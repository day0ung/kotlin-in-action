package ch03

// 3.3 String 확장 함수
fun String.lastChar(): Char = this[this.length - 1]

// 3.4 확장 프로퍼티
val String.lastChar: Char
    get() = get(length - 1)

var StringBuilder.lastChar: Char
    get() = get(length - 1)
    set(value: Char) {
        this.setCharAt(length - 1, value)
    }

// 3.5 컬렉션에 대한 확장 함수
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

// 3.6 확장 함수의 정적 디스패치 확인
open class View {
    open fun click() = println("View clicked")
}

class Button : View() {
    override fun click() = println("Button clicked")
}

fun View.showOff() = println("I'm a view!")
fun Button.showOff() = println("I'm a button!")

fun main() {
    // 확장 함수 호출
    println("Kotlin".lastChar())  // n
    println("Kotlin".lastChar)    // n

    val sb = StringBuilder("Kotlin?")
    sb.lastChar = '!'
    println(sb)  // Kotlin!

    // 컬렉션 확장 함수
    val list = listOf(1, 2, 3)
    println(list.joinToString(separator = "; ", prefix = "(", postfix = ")"))  // (1; 2; 3)

    // 멤버 함수: 동적 디스패치 (런타임 타입으로 결정)
    val view: View = Button()
    view.click()    // Button clicked

    // 확장 함수: 정적 디스패치 (컴파일 시점 타입으로 결정)
    view.showOff()  // I'm a view!
}
