package jp.lowput.todo_api_sample.staging

import TodoEntity
import io.ktor.server.util.toZonedDateTime
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.BindMethods
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.sql.Date
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


actual fun main(args: Array<String>) {
    _main(12345)
}

val jdbi = Jdbi.create(
    // psql -h db -p 5432 todo_api_sample_development -U todo_api_sample -W password
    // Dockerコンテナ内からの場合ホスト名:dbでアクセス
    // Dockerコンテナ外(ホストPC)からの場合、localhost or 0.0.0.0
    "jdbc:postgresql://db:5432/todo_api_sample_development",
    "todo_api_sample",
    "password")
    .installPlugin(SqlObjectPlugin())

val todoDao: TodoDao = jdbi.onDemand(TodoDao::class.java)

interface TodoDao {
    @SqlQuery("SELECT * FROM todos")
    @RegisterRowMapper(TodoEntityMapper::class)
    fun getContentList(): List<TodoEntity>

    // http://0.0.0.0:12345/todos/add?content=aaaa&completed=false&deadline=2024-01-1T00:00:00Z
    // http://0.0.0.0:12345/todos/add?content=aaaa&completed=false&deadline=2024-01-01T00:00:00.000+09:00
    // http://0.0.0.0:12345/todos/add?content=aaaa&completed=false&deadline=2024-01-01T00:00:00.000%2B09:00
    @SqlUpdate("INSERT INTO todos (content, completed, deadline, created_at, updated_at) VALUES (?, ?, ?, ?, ?)")
    fun add(
        content: String,
        completed: Boolean,
        deadline: Timestamp,
        created_at: Timestamp,
        updated_at: Timestamp,
    ): Int

    @SqlUpdate("UPDATE todos SET completed = true, updated_at = :updated_at WHERE id = :id")
    fun done(
        @Bind("id") id: Int,
        @Bind("updated_at") updated_at:Timestamp = Timestamp.from(Instant.now())): Int

    @SqlUpdate("UPDATE todos SET content = :content, updated_at = :updated_at WHERE id = :id")
    fun modify(
        @Bind("id") id: Int,
        @Bind("content") content: String,
        @Bind("updated_at") updated_at:Timestamp = Timestamp.from(Instant.now())): Int

    @SqlUpdate("UPDATE todos SET content = :content, deadline = :deadline, updated_at = :updated_at WHERE id = :id")
    fun modify(
        @Bind("id") id: Int,
        @Bind("content") content: String,
        @Bind("deadline") deadline: Timestamp,
        @Bind("updated_at") updated_at:Timestamp = Timestamp.from(Instant.now())): Int

    @SqlUpdate("UPDATE todos SET content = :content, deadline = :deadline, completed = :completed, updated_at = :updated_at WHERE id = :id")
    fun modify(
        @Bind("id") id: Int,
        @Bind("content") content: String,
        @Bind("completed") completed: Boolean,
        @Bind("deadline") deadline: Timestamp,
        @Bind("updated_at") updated_at:Timestamp = Timestamp.from(Instant.now())): Int

    @SqlUpdate("UPDATE todos SET content = :item.content, deadline = :item.deadline, updated_at = :updated_at WHERE id = :item.id")
    fun modify(@BindMethods("item") todo: TodoEntityMapper): Int

    @SqlUpdate("DELETE FROM todos WHERE id = :id")
    fun delete(@Bind("id") id: Int): Int
}

class TodoEntityMapper : RowMapper<TodoEntity> {
    @Throws(SQLException::class)
    override fun map(rs: ResultSet?, ctx: StatementContext?): TodoEntity {
        if (rs == null) throw SQLException()
        return TodoEntity(
            rs.getInt("id"),
            rs.getString("content"),
            rs.getBoolean("completed"),
            rs.getTimestamp("deadline").toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            rs.getTimestamp("created_at").toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            rs.getTimestamp("updated_at").toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        )
    }
}

actual fun list(): List<TodoEntity> = todoDao.getContentList()

actual fun add(
    content: String,
    completed: Boolean,
    deadline: String,
): Int = todoDao.add(
    content = content,
    completed = completed,
    deadline = runCatching { Timestamp.valueOf(deadline) } // "2022-02-02 10:10:10.111"
        .recoverCatching { Timestamp.from(Instant.parse(deadline)) } // 2024-01-01T00:00:00Z // 2024-01-01T00:00:00.000+09:00
        .recoverCatching { Timestamp.valueOf(Date.valueOf(deadline).toString() + " 0:00:00") } // "2022-02-02"
        .recoverCatching { Timestamp.from(Instant.ofEpochMilli(deadline.toLong()))} // 0
        .getOrThrow(),
    created_at = Timestamp.from(Instant.now()),
    updated_at = Timestamp.from(Instant.now()),
)

actual fun done(id: Int): Int = todoDao.done(id = id)
actual fun modify(id: Int, content: String, deadline: String): Int = todoDao.modify(
    id = id,
    content = content,
    deadline = runCatching { Timestamp.valueOf(deadline) } // "2022-02-02 10:10:10.111"
        .recoverCatching { Timestamp.from(Instant.parse(deadline)) } // 2024-01-01T00:00:00Z // 2024-01-01T00:00:00.000+09:00
        .recoverCatching { Timestamp.from(Date.valueOf(deadline).toInstant()) } // "2022-02-02"
        .recoverCatching { Timestamp.from(Instant.ofEpochMilli(deadline.toLong()))} // 0
        .getOrThrow(),
    updated_at = Timestamp.from(Instant.now()))

actual fun modify(
    id: Int,
    content: String,
    deadline: String,
    completed: Boolean
): Int = todoDao.modify(
    id = id,
    content = content,
    completed = completed,
    deadline = runCatching { Timestamp.valueOf(deadline) } // "2022-02-02 10:10:10.111"
        .recoverCatching { Timestamp.from(Instant.parse(deadline)) } // 2024-01-01T00:00:00Z // 2024-01-01T00:00:00.000+09:00
        .recoverCatching { Timestamp.from(Date.valueOf(deadline).toInstant()) } // "2022-02-02"
        .recoverCatching { Timestamp.from(Instant.ofEpochMilli(deadline.toLong()))} // 0
        .getOrThrow(),
    updated_at = Timestamp.from(Instant.now()))

actual fun delete(id: Int): Int = todoDao.delete(id = id)
