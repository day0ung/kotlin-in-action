package ch08

// 8.1 함수 타입과 고차 함수 선언

// 명시적 함수 타입
val sum: (Int, Int) -> Int = { x, y -> x + y }
val action: () -> Unit = { println(42) }

// nullable 반환 타입 vs nullable 함수 타입 변수
var canReturnNull: (Int, Int) -> Int? = { null }
var funOrNull: ((Int, Int) -> Int)? = null

// 8.2 함수 타입 파라미터 호출
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

// filter 직접 구현
fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}

// 8.3 함수 타입 파라미터 기본값
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    transform: (T) -> String = { it.toString() }
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element))
    }
    result.append(postfix)
    return result.toString()
}

// nullable 함수 타입 파라미터: safe-call + invoke
fun <T> Collection<T>.joinToStringNullable(
    separator: String = ", ",
    transform: ((T) -> String)? = null
): String {
    val result = StringBuilder()
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        val str = transform?.invoke(element) ?: element.toString()
        result.append(str)
    }
    return result.toString()
}

// 8.4 함수에서 함수 반환
enum class Delivery { STANDARD, EXPEDITED }
class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }
    return { order -> 1.2 * order.itemCount }
}

// 8.5 람다로 중복 제거
data class SiteVisit(val path: String, val duration: Double, val os: OS)
enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("/signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
)

fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) =
    filter(predicate).map(SiteVisit::duration).average()

fun main() {
    // 함수 타입
    println(sum(3, 4))      // 7
    action()                // 42

    // twoAndThree
    twoAndThree { a, b -> a + b }  // The result is 5
    twoAndThree { a, b -> a * b }  // The result is 6

    // filter
    println("ab1c".filter { it in 'a'..'z' })  // abc

    // joinToString
    val letters = listOf("Alpha", "Beta")
    println(letters.joinToString())                          // Alpha, Beta
    println(letters.joinToString { it.lowercase() })         // alpha, beta
    println(letters.joinToString(separator = "! ", postfix = "! ",
        transform = { it.uppercase() }))                    // ALPHA! BETA!

    println(letters.joinToStringNullable())                  // Alpha, Beta
    println(letters.joinToStringNullable { it.uppercase() }) // ALPHA, BETA

    // 함수 반환
    val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
    println("Shipping costs ${calculator(Order(3))}")  // Shipping costs 12.3

    // 중복 제거
    println(log.averageDurationFor { it.os in setOf(OS.ANDROID, OS.IOS) })  // 12.15
    println(log.averageDurationFor { it.os == OS.IOS && it.path == "/signup" })  // 8.0
}
