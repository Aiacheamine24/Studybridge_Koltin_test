import androidx.annotation.Nullable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.net.URL

class ApiService(
    private val body: JSONObject,
    private val url: String,
    @Nullable private val headers: JSONObject? = null,
    private val type_request: String
) {
    suspend fun executeRequest(): JSONObject? {
        return withContext(Dispatchers.IO) {
            // Create OkHttp client and request
            val client = OkHttpClient()

            val requestBuilder = Request.Builder()
                .url(URL(url)) // Convert the URL string to URL
                .method(type_request, requestBody) // Set the request type

            // Add headers if present
            headers?.let {
                for (key in it.keys()) {
                    requestBuilder.addHeader(key, it.getString(key))
                }
            }

            val request = requestBuilder.build()

            // Execute the request
            val response = client.newCall(request).execute()

            // Handle the response as needed
            val responseBody = response.body?.string()
            return@withContext responseBody?.let { JSONObject(it) }
        }
    }

    private val requestBody: RequestBody = RequestBody.create(
        "application/json".toMediaTypeOrNull(),
        body.toString()
    )
}
