package ch03

// 3.2 이름 붙인 인자와 디폴트 파라미터 값을 가진 joinToString 함수
fun <T> joinToString(
    collection: Collection<T>,
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

fun main() {
    val list = listOf(1, 2, 3)

    // 기본 호출
    println(joinToString(list))                        // 1, 2, 3

    // 이름 붙인 인자 사용
    println(joinToString(list, separator = "; ", prefix = "(", postfix = ")"))  // (1; 2; 3)

    // 일부 인자만 지정
    println(joinToString(list, "; "))                  // 1; 2; 3

    // 이름 붙인 인자로 순서 무관하게 지정
    println(joinToString(collection = list, postfix = ";", prefix = "# "))  // # 1, 2, 3;
}
