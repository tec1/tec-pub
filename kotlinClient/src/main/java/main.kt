import org.json.JSONObject
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import kotlin.system.exitProcess

/**
 * Challenge exercise.
 *
 * This function reads frames from an input socket and classifies activations.  When an activation is found it notifies
 * the server via the output socket.
 *
 * Classification is done by sampling the input data. When the data value exceeds MIN_PERCENT it is considered to have
 * found an activation.  The algorithm allows 1 data point (configurable) to drop below the threshold.
 *
 * An earlier heuristic was considered based on the 1st and 2nd derivative of the input data.
 *
 * @author Eric Christiansen
 */
fun main(
) {
    val NOTIFICATION = "Activation classified\n"
    val MAX_FRAMES   = 200
    val MIN_PERCENT  = 28
    val MAX_NOISE    = 3
    
    var numFrames = 0
    var maxData = Int.MIN_VALUE
    var minData = Int.MAX_VALUE
    
    class Frame(
        json: String
    ) : JSONObject(json) {
        val timeStamp: String? = this.optString("timeStamp")
        val data: String? = this.optString("data")
        val label: String? = this.optString("label")
    }
    
    
/**
 * This (now unused) function calculates the derivatives of the input data.
 * @return Pair containing the 1st and 2nd derivatives.
 */
var lastData = 0
var lastLast: Int

fun deriv(
    data: Int
): Pair<Int, Int> {
    val delta = data - lastData
    lastLast = lastData
    lastData = data

    return Pair(delta, (data - 2*lastData - lastLast))
}
    
    
/**
 * Calculates the range of data values and returns the percent of that range for the current data point.
 * @return the percent of range for the current data point.
 */
val VALID_RANGE = 25000
var range: Int

fun percent(
    intData: Int
): Int {
    maxData = Math.max(intData, maxData)
    minData = Math.min(intData, minData)
    range = Math.max((maxData - minData), 1)
    if (range < VALID_RANGE) return 0
    
    return 100 * (intData - minData) / range
}


/**
 * Based on the percent of range, determines if the current data point indicates an activation event.
 * @return true if the current data point indicates an activation.
 */
var numNoise = MAX_NOISE
var percent = 0
var lastFound = false

fun activationFound(
    intData: Int
): Boolean {
    percent = percent(intData)
    var found = (percent > MIN_PERCENT)
    
    if (found != lastFound) numNoise = 0
    else numNoise++
    
    found = (numNoise < MAX_NOISE) || found
    lastFound = found
    
    return found
}


/* Main block */
    println("Please enter the port number...")
    val port= readLine()!!.toInt()
    val socket   = Socket("localhost", port)

    try {
        val writer = OutputStreamWriter(socket.getOutputStream())
//      val inputStream = File("some.json").inputStream()
        val inputStream = socket.getInputStream()
        
        InputStreamReader(inputStream).forEachLine {
            val frame = Frame(it)
            val intData = frame.data!!.toInt()
            
            var flag = ""
            
            if (activationFound(intData)) {
                writer.write(NOTIFICATION)
                writer.flush()
                flag = "** FOUND **"
            }
            
            println(
                frame.timeStamp + " " + intData + " " + frame.label + "  " + percent + "  " + flag
            )
            
            if (numFrames++ > MAX_FRAMES) exitProcess(0)
        }
        
    } catch (e: Exception) {
        e.printStackTrace()
        
    } finally {
        socket.close()
    }
}
