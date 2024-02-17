package ch05

// 5.6 수신 객체 지정 람다: with, apply

// with: 람다 결과를 반환
fun alphabetWith(): String {
    val stringBuilder = StringBuilder()
    return with(stringBuilder) {
        for (letter in 'A'..'Z') {
            append(letter)          // this.append(letter)의 생략형
        }
        append("\nNow I know the alphabet!")
        toString()                  // 마지막 식 = with의 반환값
    }
}

// 더 간결하게
fun alphabetWithConcise() = with(StringBuilder()) {
    for (letter in 'A'..'Z') { append(letter) }
    append("\nNow I know the alphabet!")
    toString()
}

// apply: 수신 객체를 반환
fun alphabetApply() = StringBuilder().apply {
    for (letter in 'A'..'Z') { append(letter) }
    append("\nNow I know the alphabet!")
}.toString()

// buildString: StringBuilder + with를 감싼 표준 라이브러리
fun alphabetBuildString() = buildString {
    for (letter in 'A'..'Z') { append(letter) }
    append("\nNow I know the alphabet!")
}

// apply 활용: 객체 초기화 패턴
data class Config(var host: String = "", var port: Int = 0, var debug: Boolean = false)

fun createConfig() = Config().apply {
    host = "localhost"
    port = 8080
    debug = true
}

fun main() {
    println(alphabetWith())
    println(alphabetWithConcise())
    println(alphabetApply())
    println(alphabetBuildString())

    val config = createConfig()
    println(config)  // Config(host=localhost, port=8080, debug=true)

    // with vs apply 차이
    val result1 = with(StringBuilder()) {
        append("Hello")
        append(" World")
        toString()   // with는 마지막 식 반환
    }

    val result2 = StringBuilder().apply {
        append("Hello")
        append(" World")
    }.toString()     // apply는 StringBuilder 반환, toString() 추가 호출

    println(result1)  // Hello World
    println(result2)  // Hello World
}
