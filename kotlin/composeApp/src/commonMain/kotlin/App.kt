import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import jp.lowput.todo_api_sample.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
@Preview
fun App() {
    MaterialTheme {
        TodoListPage()
    }
}

data class TodoListPageViewState(
    val showDialog: Boolean = false,
    val selectedItem: TodoEntity? = null,
)

@Composable
@Preview
fun TodoListPage() {
    var resultList by rememberSaveable {
        mutableStateOf(listOf<TodoEntity>())
    }
    val showDialog =  remember { mutableStateOf(TodoListPageViewState()) }

    LaunchedEffect(Unit) {
        resultList = ApiClient.list()
    }

    if(showDialog.value.showDialog) {
        CustomDialog(
            item = showDialog.value.selectedItem,
            close = {
                showDialog.value = TodoListPageViewState(showDialog=false)
            },
            add = { content, deadline ->
                kotlinx.coroutines.CoroutineScope(Dispatchers.Default).launch {
                    ApiClient.add(content, deadline, false)
                    resultList = ApiClient.list()
                }
                showDialog.value = TodoListPageViewState(showDialog=false)
            },
            modify = { content, deadline ->
                showDialog.value.selectedItem?.id?.let { id ->
                    kotlinx.coroutines.CoroutineScope(Dispatchers.Default).launch {
                        ApiClient.modify(id, content, deadline, false)
                        resultList = ApiClient.list()
                    }
                }
                showDialog.value = TodoListPageViewState(showDialog=false)
            },
            delete = {
                showDialog.value.selectedItem?.id?.let {id ->
                    kotlinx.coroutines.CoroutineScope(Dispatchers.Default).launch {
                        ApiClient.delete(id)
                        resultList = ApiClient.list()
                    }
                }
                showDialog.value = TodoListPageViewState(showDialog=false)
            })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Todo List") }
            )
        },
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = {
                    kotlinx.coroutines.CoroutineScope(Dispatchers.Default).launch {
                        showDialog.value = TodoListPageViewState(showDialog=true)
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                }
                FloatingActionButton(onClick = {
                    kotlinx.coroutines.CoroutineScope(Dispatchers.Default).launch {
                        resultList = ApiClient.list()
                    }
                }) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        }
    ) {
        LazyColumn {
            items(resultList.size) {index ->
                Column(
                    modifier = Modifier.clickable {
                        showDialog.value = TodoListPageViewState(showDialog=true, selectedItem=resultList[index])
                    }.fillMaxWidth()
                ) {
                    Text(resultList[index].content, style = MaterialTheme.typography.body1)
                    Text(Instant.parse(resultList[index].deadline).toLocalDateTime(TimeZone.currentSystemDefault()).toString().replace("-", "/").replace("T", " "), style = MaterialTheme.typography.body2, modifier = Modifier.padding(start = 5.dp))
                }
                Divider()
            }
        }
    }
}

@Preview
@Composable
fun CustomDialogContentPreview() {
    MaterialTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                CustomDialogContent(null, {}, {a,b->}, {a,b->}, {})
            }
        }
    }
}

@Composable
fun CustomDialogContent(item: TodoEntity?, close: (Boolean) -> Unit, add: (String, String) -> Unit, modify: (String, String) -> Unit, delete: () -> Unit) {
    val txtComtentFieldError = remember { mutableStateOf("") }
    val txtContentField = remember { mutableStateOf(item?.content ?: "") }
    val txtDeadlineError = remember { mutableStateOf("") }
    val txtDeadlineField = remember { mutableStateOf(item?.deadline?.let { Instant.parse(item.deadline).toLocalDateTime(TimeZone.currentSystemDefault()).toString().replace("-", "/").replace("T", " ") } ?: "") }

    Surface(
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Set value",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (item != null) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "",
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable { delete() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                color = if (txtComtentFieldError.value.isEmpty()) Color.Cyan else Color.Red
                            ),
                            shape = RoundedCornerShape(50)
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "",
                            tint = Color.Cyan,
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                        )
                    },
                    placeholder = { Text(text = "Enter content") },
                    value = txtContentField.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        txtContentField.value = it
                    })

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                color = if (txtDeadlineError.value.isEmpty()) Color.Cyan else Color.Red
                            ),
                            shape = RoundedCornerShape(50)
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "",
                            tint = Color.Cyan,
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                        )
                    },
                    placeholder = { Text(text = "Enter deadline(yyyy-mm-dd)") },
                    value = txtDeadlineField.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {
                        txtDeadlineField.value = it
                    })

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            if (txtContentField.value.isEmpty()) {
                                txtComtentFieldError.value = "何か入力してください"
                                return@Button
                            }
                            val instant: Instant? = txtDeadlineField.value.trim().replace("/", "-").replace(Regex("\\s+"), "T").let {time->
                                kotlin.runCatching { Instant.parse(time) }
                                    .recoverCatching { LocalDateTime.parse(time).toInstant(TimeZone.currentSystemDefault()) }
                                    .recoverCatching { LocalDate.parse(time).atStartOfDayIn(TimeZone.currentSystemDefault()) }
                                    .getOrNull()
                            }
                            if (instant == null) {
                                txtDeadlineError.value = "日付時刻を入力してください"
                                return@Button
                            }
                            if(item == null) {
                                add(txtContentField.value, instant.toString())
                            } else {
                                modify(txtContentField.value, instant.toString())
                            }
                            close(false)
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "Done")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomDialog(item: TodoEntity?, close: (Boolean) -> Unit, add: (String, String) -> Unit, modify: (String, String) -> Unit, delete: () -> Unit) {
    Dialog(onDismissRequest = { close(false) }) {
        CustomDialogContent(item, close, add, modify, delete)
    }
}