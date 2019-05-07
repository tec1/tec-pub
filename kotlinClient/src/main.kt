import java.io.InputStreamReader
import java.net.Socket
import kotlin.system.exitProcess

fun main() {
    val port = readLine()!!.toInt()
    val socket = Socket("localhost", port)
    val maxLines = 20
    var count = 0

    try {
            InputStreamReader(socket.getInputStream()).forEachLine {
                println(it)
                if (count++ > maxLines) exitProcess(0)
            }

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            socket.close()
        }
    }
