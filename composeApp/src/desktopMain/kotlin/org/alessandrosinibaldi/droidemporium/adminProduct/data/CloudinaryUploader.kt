package org.alessandrosinibaldi.droidemporium.adminProduct.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File

@Serializable
data class CloudinaryUploadResponse(
    @SerialName("public_id")
    val publicId: String,
    @SerialName("secure_url")
    val secureUrl: String
)

class CloudinaryUploader {

    val cloudName = "dovupsygm"
    private val uploadPreset = "testing"
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.DEFAULT
        }
        expectSuccess = true
    }

    suspend fun uploadImage(file: File): String {
        val url = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

        val boundary = "Boundary-${System.currentTimeMillis()}"
        val CRLF = "\r\n"

        val outputStream = ByteArrayOutputStream()

        outputStream.write("--$boundary$CRLF".toByteArray())
        outputStream.write("Content-Disposition: form-data; name=\"upload_preset\"$CRLF".toByteArray())
        outputStream.write("Content-Type: text/plain; charset=UTF-8$CRLF".toByteArray())
        outputStream.write(CRLF.toByteArray())
        outputStream.write(uploadPreset.toByteArray())
        outputStream.write(CRLF.toByteArray())

        outputStream.write("--$boundary$CRLF".toByteArray())
        outputStream.write("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"$CRLF".toByteArray())
        outputStream.write("Content-Type: image/jpeg$CRLF".toByteArray())
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

            println("Cloudinary upload SUCCEEDED. Public ID: ${response.publicId}")
            return response.publicId

        } catch (e: Exception) {
            println("Cloudinary upload failed (MANUAL BODY): ${e.message}")
            throw e
        }
    }
}