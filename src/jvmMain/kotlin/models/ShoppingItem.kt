package models

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingItem (val desc: String, val priority: Int) {
    val id: Int = desc.hashCode()

    companion object {
        val path = "/shoppingList"
    }
}