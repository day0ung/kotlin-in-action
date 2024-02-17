package ch05

import java.io.File

// 5.5 시퀀스: 지연 계산 컬렉션 연산
fun main() {
    val people = listOf(
        Person("Alice", 29), Person("Bob", 31),
        Person("Charles", 31), Person("Dan", 21)
    )

    // 즉시 계산: 중간 리스트 2개 생성
    people.map(Person::name).filter { it.startsWith("A") }

    // 지연 계산: 중간 컬렉션 없음
    val result = people.asSequence()
        .map(Person::name)
        .filter { it.startsWith("A") }
        .toList()
    println(result)  // [Alice]

    // 연산 순서 확인: 원소별로 순차 처리
    println(listOf(1, 2, 3, 4).asSequence()
        .map { print("map($it) "); it * it }
        .filter { print("filter($it) "); it % 2 == 0 }
        .toList())
    // map(1) filter(1) map(2) filter(4) map(3) filter(9) map(4) filter(16)
    // [4, 16]

    // find와 함께: 결과를 찾으면 이후 원소는 처리하지 않음
    println(listOf(1, 2, 3, 4).asSequence()
        .map { it * it }
        .find { it > 3 })  // 4 (3, 4는 처리 안 함)

    // filter 먼저가 더 효율적
    people.asSequence().filter { it.name.length < 4 }.map(Person::name).toList()

    // generateSequence: 이전 값으로 다음 값 생성
    val naturalNumbers = generateSequence(0) { it + 1 }
    val numbersTo100 = naturalNumbers.takeWhile { it <= 100 }
    println(numbersTo100.sum())  // 5050

    // 파일 시스템 탐색
    fun File.isInsideHiddenDirectory() =
        generateSequence(this) { it.parentFile }.any { it.isHidden }
}
