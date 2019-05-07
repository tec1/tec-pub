import java.io.InputStreamReader
import java.net.Socket
import org.json.JSONObject
import java.io.OutputStreamWriter
import kotlin.system.exitProcess

fun main() {
    println("Please enter the port number...")

    val notification = "Activation classified\n"
    val maxLines     = 20

    val port   = readLine()!!.toInt()
    val socket = Socket("localhost", port)

    class Frame(json: String) : JSONObject(json) {
        val timeStamp: String? = this.optString("timeStamp")
        val data:      String? = this.optString("data")
        val label:     String? = this.optString("label")
    }

    var lastLabel = "REST"

    fun activationFound(label: String?): Boolean {
        val result = (label == "ACTIVATION") && (lastLabel != label)
        lastLabel  = label!!

        return result
    }

    try {
        val writer = OutputStreamWriter(socket.getOutputStream())

        var count = 0
        InputStreamReader(socket.getInputStream()).forEachLine {
            val frame = Frame(it)

            var flag = ""
            if (activationFound(frame.label)) {
                writer.write(notification)
                writer.flush()
                flag = "** FOUND **"
            }

            println(frame.timeStamp + " " + frame.data + " " + frame.label + "  " + flag)

            if (count++ > maxLines) exitProcess(0)
        }

    } catch (e: Exception) {
        e.printStackTrace()

    } finally {
        socket.close()
    }
}