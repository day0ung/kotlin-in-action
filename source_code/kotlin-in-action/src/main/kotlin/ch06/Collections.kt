package ch06

import java.io.BufferedReader
import java.io.StringReader

// 6.14 컬렉션의 널 가능성

// List<Int?>: 리스트는 non-null, 원소는 nullable
fun readNumbers(reader: BufferedReader): List<Int?> {
    val result = ArrayList<Int?>()
    for (line in reader.lineSequence()) {
        result.add(line.toIntOrNull())  // 파싱 실패 시 null
    }
    return result
}

fun addValidNumbers(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull()  // List<Int>로 반환
    println("Sum of valid numbers: ${validNumbers.sum()}")
    println("Invalid numbers: ${numbers.size - validNumbers.size}")
}

// 6.15 읽기 전용과 변경 가능한 컬렉션
fun <T> copyElements(source: Collection<T>, target: MutableCollection<T>) {
    for (item in source) {
        target.add(item)
    }
}

// 6.16 배열
fun arrayExamples() {
    // arrayOf: 원소 직접 지정
    val letters = Array(26) { i -> ('a' + i).toString() }
    println(letters.joinToString(""))  // abc...z

    // arrayOfNulls
    val nullArray = arrayOfNulls<String>(3)  // [null, null, null]

    // 원시 타입 배열 (박싱 없음)
    val squares = IntArray(5) { (it + 1) * (it + 1) }
    println(squares.toList())  // [1, 4, 9, 16, 25]

    val fiveZeros = IntArray(5)         // [0, 0, 0, 0, 0]
    val fiveZeros2 = intArrayOf(0, 0, 0, 0, 0)

    // 배열과 vararg: 스프레드 연산자
    val args = arrayOf("opt1", "opt2")
    val list = listOf("start", *args, "end")
    println(list)  // [start, opt1, opt2, end]

    // withIndex
    for ((index, element) in squares.withIndex()) {
        println("$index: $element")
    }

    // 컬렉션 ↔ 배열 변환
    val intList = listOf(1, 2, 3)
    val intArr = intList.toIntArray()
    val backToList = intArr.toList()
}

fun main() {
    // 컬렉션 널 가능성
    val reader = BufferedReader(StringReader("1\nabc\n42"))
    val numbers = readNumbers(reader)
    println(numbers)  // [1, null, 42]
    addValidNumbers(numbers)
    // Sum of valid numbers: 43
    // Invalid numbers: 1

    // 읽기 전용 / 변경 가능
    val source: Collection<Int> = arrayListOf(3, 5, 7)
    val target: MutableCollection<Int> = arrayListOf(1)
    copyElements(source, target)
    println(target)  // [1, 3, 5, 7]

    // 컬렉션 생성 함수
    val readOnlyList = listOf(1, 2, 3)         // 읽기 전용
    val mutableList = mutableListOf(1, 2, 3)   // 변경 가능
    val readOnlySet = setOf("a", "b", "c")
    val mutableMap = mutableMapOf("key" to 1)

    arrayExamples()
}
