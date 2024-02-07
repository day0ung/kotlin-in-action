package ch03

// 3.7 vararg: 가변 길이 인자
fun main() {
    val list = listOf(2, 3, 5, 7, 11)

    // 스프레드 연산자(*)로 배열의 원소를 vararg에 전달
    val args = arrayOf("foo", "bar")
    val list2 = listOf("args: ", *args)
    println(list2)  // [args: , foo, bar]

    // 3.8 중위 함수 호출 (infix)
    val map = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three")
    println(map)  // {1=one, 7=seven, 53=fifty-three}

    // to 함수로 Pair 생성
    val pair = 1 to "one"
    println(pair)  // (1, one)

    // 구조 분해 선언
    val (number, name) = 1 to "one"
    println("$number -> $name")  // 1 -> one

    // withIndex 도 구조 분해 활용
    for ((index, element) in list.withIndex()) {
        println("$index: $element")
    }
}

// 사용자 정의 infix 함수 예시
infix fun Int.times(str: String) = str.repeat(this)

fun customInfixDemo() {
    println(2 times "Bye ")  // Bye Bye
}
