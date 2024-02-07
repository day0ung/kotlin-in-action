package ch03

fun main() {
    // 코틀린 컬렉션 생성 (내부적으로 자바 컬렉션 사용)
    val set = hashSetOf(1, 7, 53)
    val list = arrayListOf(1, 7, 53)
    val map = hashMapOf(1 to "one", 7 to "seven", 53 to "fifty-three")

    println(set.javaClass)   // class java.util.HashSet
    println(list.javaClass)  // class java.util.ArrayList
    println(map.javaClass)   // class java.util.HashMap

    // 코틀린 컬렉션은 자바보다 더 많은 기능 제공
    val strings = listOf("first", "second", "fourteenth")
    println(strings.last())  // fourteenth

    val numbers = setOf(1, 14, 2)
    println(numbers.max())   // 14
}
