package ch09

// 9.4 타입 소거와 스타 프로젝션

fun printCollection(c: Collection<*>) {
    val list = c as? List<*>
        ?: throw IllegalArgumentException("List expected")
    println(list)
}

fun printSumKnown(c: Collection<Int>) {
    if (c is List<Int>) {
        println(c.sum())
    }
}

// 9.5 실체화된 타입 파라미터: reified
inline fun <reified T> isA(value: Any) = value is T

// filterIsInstance 간소화 구현
inline fun <reified T> Iterable<*>.filterIsInstanceCustom(): List<T> {
    val destination = mutableListOf<T>()
    for (element in this) {
        if (element is T) destination.add(element)
    }
    return destination
}

// java.lang.Class 대신 reified 사용
inline fun <reified T> loadServiceSimple(): String =
    "Would load: ${T::class.java.simpleName}"

fun main() {
    // 스타 프로젝션
    printCollection(listOf(1, 2, 3))   // [1, 2, 3]
    printSumKnown(listOf(1, 2, 3))     // 6

    // reified
    println(isA<String>("abc"))  // true
    println(isA<String>(123))    // false

    // filterIsInstance (표준 라이브러리)
    val items = listOf("one", 2, "three")
    println(items.filterIsInstance<String>())          // [one, three]
    println(items.filterIsInstanceCustom<String>())    // [one, three]

    // T::class.java
    println(loadServiceSimple<String>())  // Would load: String
}
