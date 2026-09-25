# Green

Librería en **Kotlin** para enviar y recibir mensajes entre servidores mediante **Redis Pub/Sub**. Los mensajes son objetos que se serializan a JSON con Gson y se entregan a los métodos marcados con `@GreenHandler`.

Pensada para comunicar varios servidores de Minecraft (por ejemplo, un proxy y sus servidores), aunque no depende de ninguna API de Minecraft.

## Uso

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
green.sendPacket("chat", ChatPacket("Maykol", "Hola"))
```

## Cómo funciona

- `GreenManager` se suscribe al canal de Redis `Green` en un hilo aparte (`ForkJoinPool`).
- `sendPacket(id, objeto)` publica el mensaje con el formato `id~json`.
- `addListener()` registra por reflexión los métodos con `@GreenHandler(id)` que reciben un solo parámetro. Al llegar un mensaje con ese `id`, el JSON se convierte al tipo de ese parámetro y se invoca el método.

## Compilar

Requiere Java 8 y Maven.

```bash
mvn package
```

## Stack

- Kotlin 1.5.31 · Java 8
- Jedis 3.5.1 (Redis) · Gson 2.8.9

## Limitaciones

- El separador `~` no se escapa: si el `id` o el contenido del mensaje (por ejemplo, un texto dentro del JSON) contiene `~`, el mensaje se corta y no se entrega.
- Todos los mensajes viajan por un único canal de Redis y se filtran por `id` en cada servidor.
