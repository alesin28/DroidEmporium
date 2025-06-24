package org.alessandrosinibaldi.droidemporium.adminProduct.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.hex
import io.ktor.util.sha1
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.alessandrosinibaldi.droidemporium.core.config.CloudinaryConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files

@Serializable
data class CloudinaryUploadResponse(
    @SerialName("public_id")
    val publicId: String,
    @SerialName("secure_url")
    val secureUrl: String
)

class CloudinaryUploader(
    private val client: HttpClient,
    private val config: CloudinaryConfig
) {

    suspend fun uploadImage(file: File): CloudinaryUploadResponse {
        val url = "https://api.cloudinary.com/v1_1/${config.cloudName}/image/upload"
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val signature = generateSignature(timestamp)

        val boundary = "Boundary-${System.currentTimeMillis()}"
        val CRLF = "\r\n"

        val outputStream = ByteArrayOutputStream()

        fun addFormField(name: String, value: String) {
            outputStream.write("--$boundary$CRLF".toByteArray())
            outputStream.write("Content-Disposition: form-data; name=\"$name\"$CRLF".toByteArray())
            outputStream.write("Content-Type: text/plain; charset=UTF-8$CRLF".toByteArray())
            outputStream.write(CRLF.toByteArray())
            outputStream.write(value.toByteArray())
            outputStream.write(CRLF.toByteArray())
        }

        addFormField("api_key", config.apiKey)
        addFormField("timestamp", timestamp)
        addFormField("upload_preset", config.preset)
        addFormField("signature", signature)

        val contentType = Files.probeContentType(file.toPath()) ?: "application/octet-stream"
        outputStream.write("--$boundary$CRLF".toByteArray())
        outputStream.write("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"$CRLF".toByteArray())
        outputStream.write("Content-Type: $contentType$CRLF".toByteArray())
        outputStream.write(CRLF.toByteArray())
        outputStream.write(file.readBytes())
        outputStream.write(CRLF.toByteArray())

        outputStream.write("--$boundary--$CRLF".toByteArray())

        val requestBodyBytes = outputStream.toByteArray()

        try {
            val response: CloudinaryUploadResponse = client.post(url) {
                setBody(requestBodyBytes)
                contentType(ContentType("multipart", "form-data").withParameter("boundary", boundary))
            }.body()

            println("Cloudinary upload SUCCEEDED. Secure URL: ${response.secureUrl}")
            return response

        } catch (e: Exception) {
            println("Cloudinary upload failed (MANUAL BODY): ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    private fun generateSignature(timestamp: String): String {
        val params = sortedMapOf<String, String>()
        params["timestamp"] = timestamp
        params["upload_preset"] = config.preset

        val paramsToSign = params.map { (key, value) -> "$key=$value" }.joinToString("&")

        val finalStringToSign = "$paramsToSign${config.apiSecret}"

        val hashBytes = sha1(finalStringToSign.toByteArray(Charsets.UTF_8))
        return hex(hashBytes)
    }
}
