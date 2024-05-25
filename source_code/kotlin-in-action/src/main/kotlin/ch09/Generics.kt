package ch09

// 9.1 제네릭 함수와 프로퍼티

// 제네릭 확장 프로퍼티
val <T> List<T>.penultimate: T
    get() = this[size - 2]

// 타입 파라미터 제약: 상한(upper bound)
fun <T : Number> oneHalf(value: T): Double = value.toDouble() / 2.0

fun <T : Comparable<T>> max(first: T, second: T): T =
    if (first > second) first else second

// 여러 제약: where
fun <T> ensureTrailingPeriod(seq: T)
        where T : CharSequence, T : Appendable {
    if (!seq.endsWith('.')) seq.append('.')
}

// 9.2 non-null 타입 파라미터
class Processor<T> {
    fun process(value: T) {
        value?.hashCode()
    }
}

class ProcessorNonNull<T : Any> {
    fun process(value: T) {
        value.hashCode()
    }
}

// 9.3 제네릭 클래스 선언
interface MyList<T> {
    operator fun get(index: Int): T
}

class StringList : MyList<String> {
    override fun get(index: Int): String = TODO()
}

fun main() {
    // penultimate
    println(listOf(1, 2, 3, 4).penultimate)  // 3

    // 타입 제약
    println(oneHalf(3))            // 1.5
    println(max("kotlin", "java")) // kotlin

    // 여러 제약
    val sb = StringBuilder("Hello World")
    ensureTrailingPeriod(sb)
    println(sb)  // Hello World.

    // Processor
    val nullableProcessor = Processor<String?>()
    nullableProcessor.process(null)  // null.hashCode() safe call

    val nonNullProcessor = ProcessorNonNull<String>()
    nonNullProcessor.process("hello")
}
