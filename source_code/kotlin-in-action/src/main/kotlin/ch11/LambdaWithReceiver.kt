package ch11

// 11.1 수신 객체 지정 람다와 확장 함수 타입

// 일반 람다 버전
fun buildString1(builderAction: (StringBuilder) -> Unit): String {
    val sb = StringBuilder()
    builderAction(sb)
    return sb.toString()
}

// 수신 객체 지정 람다 버전
fun buildString2(builderAction: StringBuilder.() -> Unit): String {
    val sb = StringBuilder()
    sb.builderAction()
    return sb.toString()
}

// apply를 활용한 표준 라이브러리 스타일
fun buildString3(builderAction: StringBuilder.() -> Unit): String =
    StringBuilder().apply(builderAction).toString()

// 확장 함수 타입 변수
val appendExcl: StringBuilder.() -> Unit = { this.append("!") }

// 11.2 apply와 with
fun demonstrateApplyWith() {
    val map = mutableMapOf(1 to "one")
    map.apply { this[2] = "two" }
    with(map) { this[3] = "three" }
    println(map)  // {1=one, 2=two, 3=three}
}

fun main() {
    // 일반 람다
    val s1 = buildString1 { it.append("Hello, "); it.append("World!") }
    println(s1)  // Hello, World!

    // 수신 객체 지정 람다: it 없이 바로 호출
    val s2 = buildString2 {
        append("Hello, ")
        append("World!")
    }
    println(s2)  // Hello, World!

    val s3 = buildString3 {
        append("Kotlin ")
        append("DSL")
    }
    println(s3)  // Kotlin DSL

    // 확장 함수 타입 변수
    val sb = StringBuilder("Hi")
    sb.appendExcl()
    println(sb)          // Hi!
    println(buildString3(appendExcl))  // !

    demonstrateApplyWith()
}
