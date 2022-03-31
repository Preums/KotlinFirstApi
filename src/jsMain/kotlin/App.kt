import components.inputComponent
import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.coroutines.*
import models.ShoppingItem

private val scope = MainScope()



val division = {x: Int, y: Int -> x / y}
val multiplication = {x: Int, y: Int -> x * y}
val addition = {x: Int, y: Int -> x + y}
val soustraction = {x: Int, y: Int -> x - y}

inline fun executeOperation(x: Int, y: Int, operation: (Int, Int) -> Int) = operation(x,y)

val app = functionComponent<Props> { _ ->
    var shoppingList by useState(emptyList<ShoppingItem>())

    useEffectOnce {
        scope.launch {
            shoppingList = getShoppingList()
        }
    }

    h1 {
        +"Full-Stack Shopping List"
    }
    p {
        + "Test Opération:  [${executeOperation(4,3, division)}]"
    }
    p {
        + "Test Opération:  [${executeOperation(4,3, multiplication)}]"
    }
    p {
        + "Test Opération:  [${executeOperation(4,3, addition)}]"
    }
    p {
        + "Test Opération:  [${executeOperation(4,3, soustraction)}]"
    }
    ul {
        shoppingList.sortedByDescending(ShoppingItem::priority).forEach { item ->
            li {
                key = item.toString()
                +"[${item.priority}] ${item.desc} "
                attrs.onClickFunction = {
                    scope.launch {
                        deleteShoppingListItem(item)
                        shoppingList = getShoppingList()
                    }
                }
            }
        }
    }

    child(inputComponent) {
        attrs.onSubmit = { input ->
            val cartItem = ShoppingItem(input.replace("!", ""), input.count {
                it == '!'
            })
            scope.launch {
                addShoppingListItem(cartItem)
                shoppingList = getShoppingList()
            }
        }
    }
}