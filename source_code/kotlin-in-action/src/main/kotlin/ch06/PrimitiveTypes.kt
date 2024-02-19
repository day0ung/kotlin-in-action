package ch06

// 6.8 원시 타입과 래퍼 타입

fun showProgress(progress: Int) {
    val percent = progress.coerceIn(0, 100)
    println("We're ${percent}% done!")
}

// 6.9 nullable 원시 타입
data class PersonAge(val name: String, val age: Int? = null) {
    fun isOlderThan(other: PersonAge): Boolean? {
        if (age == null || other.age == null) return null
        return age > other.age
    }
}

// 6.10 숫자 변환: 명시적 변환 필요
fun numberConversions() {
    val i = 1
    // val l: Long = i  // ERROR
    val l: Long = i.toLong()  // 명시적 변환

    // 산술 연산에서는 자동 적용
    val b: Byte = 1
    val result = b + 1L  // Byte + Long = Long 자동 처리
    println(result)      // 2

    // 문자열 → 숫자
    println("42".toInt())         // 42
    println("abc".toIntOrNull())  // null

    // 리터럴 타입 명시
    val longVal = 123L
    val floatVal = 123.4f
    val hex = 0xFF
    val binary = 0b1010
    println("$longVal $floatVal $hex $binary")  // 123 123.4 255 10
}

// 6.11 Any: 모든 non-null 타입의 루트
fun anyExample() {
    val answer: Any = 42  // Int → Any (박싱)
    println(answer)

    // Any에는 toString, equals, hashCode 정의
    println(42.toString())
    println(42 == 42)
}

// 6.12 Unit: 반환값 없음
fun unitExample(): Unit {
    println("done")
    // return Unit 자동 추가
}

interface Processor<T> {
    fun process(): T
}

class NoResultProcessor : Processor<Unit> {
    override fun process() {
        println("Processing...")
        // return Unit 자동 추가
    }
}

// 6.13 Nothing: 정상 반환 없음
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}

fun nothingExample(company: String?) {
    val name = company ?: fail("No company")  // name은 String으로 추론
    println(name.length)
}

fun main() {
    showProgress(146)  // We're 100% done!
    showProgress(50)   // We're 50% done!

    println(PersonAge("Sam", 35).isOlderThan(PersonAge("Amy", 42)))  // false
    println(PersonAge("Sam", 35).isOlderThan(PersonAge("Jane")))     // null

    numberConversions()
    anyExample()
    unitExample()

    NoResultProcessor().process()

    try {
        nothingExample(null)
    } catch (e: IllegalStateException) {
        println(e.message)  // No company
    }
    nothingExample("JetBrains")  // 8
}
