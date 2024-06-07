package jp.classi.portal.staging

import Greeting
import SERVER_PORT
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun _main(port: Int) {
    embeddedServer(CIO, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
    }
}
expect fun main(args: Array<String>)