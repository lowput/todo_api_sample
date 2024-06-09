package jp.lowput.todo_api_sample
import TodoEntity
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface Api {
    @GET("todos/list")
    suspend fun list(): List<TodoEntity>

    @GET("todos/add")
    suspend fun add(@Query content: String, @Query deadline: String, @Query completed: Boolean): Int

    @GET("todos/modify")
    suspend fun modify(@Query id: Int, @Query content: String, @Query deadline: String, @Query completed: Boolean): Int

    @GET("todos/delete")
    suspend fun delete(@Query id: Int): Int
}

val ApiClient = ktorfit {
    baseUrl("http://192.168.10.103:12345/")
    httpClient(HttpClient() {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    })
    converterFactories(
        FlowConverterFactory(),
        CallConverterFactory()
    )
}.create<Api>()