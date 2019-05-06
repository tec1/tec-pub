import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

    fun main() {

        val socket = Socket("localhost", 51366)
        var buf = ""
        var len = 0

        try {
            val outStream = DataOutputStream(socket.getOutputStream())
            outStream.writeUTF("Hello Pison.");
            outStream.flush();

            val inStream = DataInputStream(socket.getInputStream())
            buf = inStream.readUTF()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            println(len)
            println(buf)
            socket.close()
        }
    }