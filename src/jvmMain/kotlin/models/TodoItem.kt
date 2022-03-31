package models

import kotlinx.serialization.Serializable

@Serializable
data class TodoItem (val userId: Int, var id: Int = 0, val title: String, var completed: Boolean = false) {
    fun switchCompleted() {
        this.completed = !this.completed;
    }
    companion object {
        public val path = "/todos";
    }
}