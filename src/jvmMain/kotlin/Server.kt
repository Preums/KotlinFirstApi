import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import models.ShoppingItem
import models.TodoItem

fun main() {
    val shoppingList = mutableListOf(
        ShoppingItem("Cucumbers 🥒", 1),
        ShoppingItem("Tomatoes 🍅", 2),
        ShoppingItem("Orange Juice 🍊", 3)
    )

    val todoList = mutableListOf<TodoItem>();

    embeddedServer(Netty, 9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Put)
            method(HttpMethod.Post)
            method(HttpMethod.Delete)
            header(HttpHeaders.ContentType)
            header(HttpHeaders.Authorization)
            header(HttpHeaders.AccessControlAllowOrigin)
            host("localhost:3000")
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
            route(ShoppingItem.path) {
                get {
                    call.respond(shoppingList)
                }
                post {
                    shoppingList += call.receive<ShoppingItem>()
                    call.respond(HttpStatusCode.OK)
                }
                delete("{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    if (shoppingList.removeIf { it.id == id }) {
                        call.respond(HttpStatusCode.OK)
                    } else call.respond(HttpStatusCode.NotFound)
                }
            }
            route(TodoItem.path) {
                get {
                    call.respond(todoList);
                }
                post {
                    var newTodo: TodoItem = call.receive<TodoItem>();
                    val highestId = todoList.fold(0) {
                            acc, todoItem -> if(todoItem.id >= acc) todoItem.id else acc
                    } + 1;
                    newTodo.id = highestId ;
                    todoList += newTodo;
                    call.respond(newTodo);
                }
                put("{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid change completed value request");
                    val todo = todoList.find { it.id == id };
                    val todoToCompleteIndex = todoList.indexOf(todo);
                    val todoToComplete = todoList[todoToCompleteIndex];
                    todoToComplete.switchCompleted();
                    todoList[todoToCompleteIndex] = todoToComplete;
                    call.respond(todoToComplete);
                }
                delete("{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    if (todoList.removeIf { it.id == id }) {
                        call.respond(HttpStatusCode.OK)
                    } else call.respond(HttpStatusCode.NotFound)
                }
            }
            get ("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
        }
    }.start(wait = true)
}