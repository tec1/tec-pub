import java.io.InputStreamReader
import java.net.Socket
import org.json.JSONObject
import kotlin.system.exitProcess

fun main() {
    println("Please enter the port number...")

    val port = readLine()!!.toInt()
    val socket = Socket("localhost", port)
    val maxLines = 20
    var count = 0

    class Frame(json: String) : JSONObject(json) {
        val timeStamp: String? = this.optString("timeStamp")
        val data:      String? = this.optString("data")
        val label:     String? = this.optString("label")
    }

        try {
            InputStreamReader(socket.getInputStream()).forEachLine {
//                println(it)
                val frame = Frame(it)
                println(">>>>   " + frame.timeStamp + " " + frame.data)
                if (count++ > maxLines) exitProcess(0)
            }

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            socket.close()
        }
    }