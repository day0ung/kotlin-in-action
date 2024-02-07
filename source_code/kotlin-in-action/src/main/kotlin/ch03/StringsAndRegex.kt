package ch03

fun main() {
    // 3.9 문자열 나누기
    // 자바의 split은 정규식을 인자로 받아 혼동의 소지가 있음
    // 코틀린에서는 toRegex()로 명시적으로 정규식 사용
    println("12.345-6.A".split("\\.|-".toRegex()))  // [12, 345, 6, A]

    // 여러 구분 문자열을 직접 전달하는 방법 (간단한 경우 권장)
    println("12.345-6.A".split(".", "-"))            // [12, 345, 6, A]

    // 3.10 정규식과 3중 따옴표 문자열
    parsePath("/Users/yole/kotlin-book/chapter.adoc")

    // 3.11 여러 줄 3중 따옴표 문자열
    val kotlinLogo = """| //
                       .|//
                       .|/ \"""

    println(kotlinLogo.trimMargin("."))
    // | //
    // |//
    // |/ \

    // 3중 따옴표 안에서 문자열 템플릿 사용
    val price = """${'$'}99.9"""
    println(price)  // $99.9
}

// 3중 따옴표 정규식으로 경로 파싱
fun parsePath(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if (matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured
        println("Dir: $directory, name: $filename, ext: $extension")
        // Dir: /Users/yole/kotlin-book, name: chapter, ext: adoc
    }
}
