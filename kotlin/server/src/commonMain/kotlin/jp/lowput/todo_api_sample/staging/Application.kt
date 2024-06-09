package jp.lowput.todo_api_sample.staging

import Greeting
import TodoEntity
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun _main(port: Int) {

    embeddedServer(CIO, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")

        }
        get("/todos/list") {
            call.respond(list())
        }
        get("/todos/add") {
            val query: Parameters = context.request.queryParameters
            val content: String = query["content"] ?: return@get
            val completed: String = query["completed"] ?: return@get
            val deadline: String = query["deadline"] ?: return@get
            call.respond(add(
                content = content,
                completed = completed=="true",
                deadline = deadline,
            ).toString())
        }
        get("/todos/done") {
            val query = context.request.queryParameters
            query["id"]?.toIntOrNull()?.let { id ->
                call.respond(done(
                    id,
                ).toString())
            }
        }
        get("/todos/modify") {
            val query = context.request.queryParameters
            query["id"]?.toIntOrNull()?.let { id ->
                val content = query["content"] ?: return@get
                val deadline = query["deadline"] ?: return@get
                val completed = query["completed"] ?: return@get
                call.respond(modify(
                    id = id, content = content, deadline = deadline, completed = completed=="true"
                ).toString())
            }
        }
        get("/todos/delete") {
            val query = context.request.queryParameters
            query["id"]?.toIntOrNull()?.let { id ->
                call.respond(delete(
                    id = id
                ).toString())
            }
        }
    }
}

expect fun list(): List<TodoEntity>
expect fun add(content: String, completed: Boolean, deadline: String): Int
expect fun done(id: Int): Int
expect fun modify(id: Int, content: String, deadline: String): Int
expect fun modify(id: Int, content: String, deadline: String, completed: Boolean): Int
expect fun delete(id: Int): Int
expect fun main(args: Array<String>)