import java.io.InputStreamReader
import java.net.Socket
import org.json.JSONObject
import java.io.DataOutputStream
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

    var lastLabel = "REST"

    fun activationFound(label: String?): Boolean {
        val result = (label == "ACTIVATION") && (lastLabel != label)
        lastLabel = label!!

        return result
    }

    try {
        val outStream = DataOutputStream(socket.getOutputStream())
        InputStreamReader(socket.getInputStream()).forEachLine {
            val frame = Frame(it)

            val found = activationFound(frame.label)
            if (found) {
                outStream.writeUTF("Activation classified\n");
                outStream.flush();
            }

            println(">>>>   " + frame.timeStamp + " " + frame.data + "  " + found)

            if (count++ > maxLines) exitProcess(0)
        }

    } catch (e: Exception) {
        e.printStackTrace()

    } finally {
        socket.close()
    }
}