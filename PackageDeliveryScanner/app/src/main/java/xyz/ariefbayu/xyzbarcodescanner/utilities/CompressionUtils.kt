package xyz.ariefbayu.xyzbarcodescanner.utilities

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.logging.Logger
import java.util.zip.Deflater

public class CompressionUtils {
    //private val LOG: Logger = Logger.getLogger(CompressionUtils::class.java)

    @Throws(IOException::class)
    fun compress(data: ByteArray): ByteArray? {
        val deflater = Deflater()
        deflater.setInput(data)
        val outputStream = ByteArrayOutputStream(data.size)
        deflater.finish()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count: Int = deflater.deflate(buffer) // returns the generated code... index
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        val output: ByteArray = outputStream.toByteArray()
        //LOG.info("Original: " + data.size / 1024 + " Kb")
        //LOG.info("Compressed: " + output.size / 1024 + " Kb")
        return output
    }
}