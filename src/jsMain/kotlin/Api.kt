import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

import kotlinx.browser.window
import models.ShoppingItem

val endpoint = window.location.origin // only needed until https://youtrack.jetbrains.com/issue/KTOR-453 is resolved

val jsonClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}

suspend fun getShoppingList(): List<ShoppingItem> {
    return jsonClient.get(endpoint + ShoppingItem.path)
}

suspend fun addShoppingListItem(shoppingListItem: ShoppingItem) {
    jsonClient.post<Unit>(endpoint + ShoppingItem.path) {
        contentType(ContentType.Application.Json)
        body = shoppingListItem
    }
}

suspend fun deleteShoppingListItem(shoppingListItem: ShoppingItem) {
    jsonClient.delete<Unit>(endpoint + ShoppingItem.path + "/${shoppingListItem.id}")
}