package jp.lowput.todo_api_sample.staging

import TodoEntity
import app.cash.sqldelight.db.QueryResult
import app.softwork.sqldelight.postgresdriver.ListenerSupport
import app.softwork.sqldelight.postgresdriver.PostgresNativeDriver
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

val driver = PostgresNativeDriver(
    host = "db",
    port = 5432,
    user = "todo_api_sample",
    database = "todo_api_sample_development",
    password = "password",
    options = null,
    listenerSupport = ListenerSupport.Remote(CoroutineScope(Dispatchers.IO))
)

actual fun list(): List<TodoEntity> {
    val flow: Flow<TodoEntity> = driver.executeQueryAsFlow(
        identifier = null,
        sql = "SELECT * FROM todos",
        mapper = { cursor ->
            TodoEntity(
                id = cursor.getLong(0)!!.toInt(),
                content = cursor.getString(1)!!.toString(),
                completed = cursor.getBoolean(2)!!,
                deadline = cursor.getString(3)!!.toString(),
                created_at = cursor.getString(4)!!.toString(),
                updated_at = cursor.getString(5)!!.toString(),
            )
        },
        parameters = 0,
        fetchSize = 100,
        binders = null
    )
    return runBlocking(Dispatchers.IO) {
        flow.toList()
    }
}

actual fun add(
    content: String,
    completed: Boolean,
    deadline: String
): Int {
    TODO("Not yet implemented")
}

actual fun done(id: Int): Int {
    TODO("Not yet implemented")
}

actual fun modify(id: Int, content: String, deadline: String): Int {
    TODO("Not yet implemented")
}

actual fun delete(id: Int): Int {
    TODO("Not yet implemented")
}

actual fun main(args: Array<String>) {
    _main(23457)
}

actual fun modify(
    id: Int,
    content: String,
    deadline: String,
    completed: Boolean
): Int {
    TODO("Not yet implemented")
}