# Green

A **Kotlin** library for sending and receiving messages between servers over **Redis Pub/Sub**. Messages are objects serialized to JSON with Gson and delivered to methods annotated with `@GreenHandler`.

It was designed for communication between Minecraft servers (for example, a proxy and its backend servers), but it doesn't depend on any Minecraft API.

## Usage

```kotlin
data class ChatPacket(val player: String, val message: String)

class ChatListener : GreenListener {

    @GreenHandler("chat")
    fun onChat(packet: ChatPacket) {
        println("${packet.player}: ${packet.message}")
    }
}

val green = GreenManager(Gson(), JedisPool("localhost", 6379))

green.addListener(ChatListener())
green.sendPacket("chat", ChatPacket("Maykol", "Hello"))
```

## How it works

- `GreenManager` subscribes to the `Green` Redis channel on a separate thread (`ForkJoinPool`).
- `sendPacket(id, object)` publishes the message as `id~json`; on receipt, only the first `~` separates the `id` from the JSON.
- `addListener()` uses reflection to register methods annotated with `@GreenHandler(id)` that take a single parameter. When a message with that `id` arrives, the JSON is converted to the parameter's type and the method is invoked.

## Building

Requires Java 8 and Maven.

```bash
mvn package
```

## Stack

- Kotlin 1.5.31 · Java 8
- Jedis 3.5.1 (Redis) · Gson 2.8.9

## Notes

- A packet `id` cannot contain `~` (it's used as the separator); `addListener` and `sendPacket` reject it. The message content can include it.
- All messages travel over a single Redis channel and are filtered by `id` on each server.
