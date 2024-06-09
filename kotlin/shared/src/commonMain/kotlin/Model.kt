import kotlinx.serialization.Serializable

@Serializable
data class TodoEntity(
    val id: Int,
    val content: String,
    val completed: Boolean,
    val deadline: String,
    val created_at: String,
    val updated_at: String,
)