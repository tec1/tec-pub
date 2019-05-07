import java.io.InputStreamReader
import java.net.Socket
import org.json.JSONObject
import java.io.File
import java.io.OutputStreamWriter
import kotlin.system.exitProcess

fun main() {
    // println("Please enter the port number...")

    // val notification = "Activation classified\n"
    val maxFrames    = 200
    val minPercent   = 25
    val maxNoise     = 2

    //val port       = readLine()!!.toInt()
    //val socket     = Socket("localhost", port)
    var numFrames  = 0
    var maxData    = Int.MIN_VALUE
    var minData    = Int.MAX_VALUE

    class Frame(json: String): JSONObject(json) {
        val timeStamp: String? = this.optString("timeStamp")
        val data:      String? = this.optString("data")
        val label:     String? = this.optString("label")
    }


    var lastData = 0
    var lastLast: Int

    fun deriv (data: Int): Pair<Int,Int> {
        val delta = data - lastData
        lastLast  = lastData
        lastData  = data
        return Pair(delta, (data - 2*lastData - lastLast))
    }


    fun percent(intData: Int): Int {
        if (maxData < intData) maxData = intData
        if (minData > intData) minData = intData

        if (maxData != minData) return 100 * (intData - minData) / (maxData - minData)

        return 0
    }


    fun activationFound(intData: Int, d1: Int, d2: Int) = percent(intData) > 25
        //(intData > 0) && (d1 > 5000) && (d2 < -10000)

    try {
        //val writer = OutputStreamWriter(socket.getOutputStream())
        val inputstream = File("some.json").inputStream()
        //val inputstream2 = socket.getInputStream()

        InputStreamReader(inputstream).forEachLine {
            val frame   = Frame(it)
            val intData = frame.data!!.toInt()

            var flag = ""
            val (d1, d2) = deriv(intData)

            if (maxData < intData) { maxData = intData }
            if (minData > intData) { minData = intData }
            var percent = 50
            if (maxData != minData) percent = 100 * (intData - minData) / (maxData - minData)

            if (activationFound(intData, d1, d2)) {
                //writer.write(notification)
                //writer.flush()
                flag = "** FOUND **"
            }

            println(frame.timeStamp + " " + intData + " " + d1 + " " + d2 + " " + frame.label +
                    "  " + minData + "  " + maxData + "  " + percent +
                    "  " + flag)

            if (numFrames++ > maxFrames) exitProcess(0)
        }

    } catch (e: Exception) {
        e.printStackTrace()

    } finally {
        //socket.close()
    }
}
