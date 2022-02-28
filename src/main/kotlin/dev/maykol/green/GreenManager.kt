package dev.maykol.green

import com.google.gson.Gson
import com.google.gson.JsonObject
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import java.util.concurrent.ForkJoinPool


/**
 * @author Maykol Morales Morante (zSirSpectro)
 * Saturday, February 12, 2022
 */

class GreenManager(
    private val gson: Gson,
    private val redisPool: JedisPool
) {

    val id = "Green"
    val storage = HashSet<GreenData>()

    init {
        ForkJoinPool.commonPool().execute {
            redisPool.resource.use {
                it.subscribe(object : JedisPubSub() {
                    override fun onMessage(channel: String, message: String) {
                        if (channel.equals(id, true)) {
                            val args = message.split("~")

                            for (greenData in storage) {
                                if (greenData.id.equals(args[0], true)) {
                                    val json = gson.fromJson(args[1], greenData.clazz)

                                    if (json != null) {
                                        greenData.method.invoke(greenData.instance, json)
                                    }
                                }
                            }
                        }
                    }
                }, id)
            }
        }
    }

    fun sendPacket(channel: String, any: Any) {
        try {
            val jsonObject: String = gson.toJson(any) ?: throw IllegalStateException("JsonObject throw null.")

            redisPool.resource.use {
                it.publish(id, "$channel~$jsonObject")
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun sendPacket(channel: String, jsonObject: JsonObject) {
        try {
            redisPool.resource.use {
                it.publish(id, "$channel~$jsonObject")
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun addListener(listener: GreenListener) {
        for (method in listener::class.java.declaredMethods) {
            val annotation = method.getDeclaredAnnotation(GreenHandler::class.java)

            if (annotation != null) {
                val packetId = annotation.id
                var packetClass: Class<*>? = null

                if (method.parameters.size == 1) {
                    packetClass = method.parameters[0].type
                }
                if (packetClass != null) {
                    storage.add(GreenData(packetId, listener, method, packetClass))
                }
            }
        }
    }
}