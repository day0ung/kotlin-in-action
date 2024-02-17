package ch05

// 5.4 컬렉션 함수형 API
data class Book(val title: String, val authors: List<String>)

fun main() {
    val people = listOf(Person("Alice", 29), Person("Bob", 31), Person("Carol", 31))

    // filter: 조건을 만족하는 원소만 반환
    println(people.filter { it.age > 30 })           // [Bob, Carol]

    // map: 각 원소를 변환
    println(people.map { it.name })                  // [Alice, Bob, Carol]
    println(people.map(Person::name))                // 멤버 참조로

    // 체이닝
    println(people.filter { it.age > 30 }.map(Person::name))  // [Bob, Carol]

    // 주의: maxBy를 람다 안에서 반복 호출하면 비효율 (매번 재계산)
    val maxAge = people.maxBy(Person::age)!!.age
    println(people.filter { it.age == maxAge })      // 효율적

    // all, any, count, find
    val canBeInClub27 = { p: Person -> p.age <= 27 }
    println(people.all(canBeInClub27))   // false
    println(people.any(canBeInClub27))   // false (29세도 27 초과)
    println(people.count { it.age > 30 })  // 2
    println(people.find { it.age > 30 })   // Person(name=Bob, age=31)

    // groupBy
    println(people.groupBy { it.age })
    // {29=[Alice], 31=[Bob, Carol]}

    // flatMap
    val books = listOf(
        Book("Thursday Next", listOf("Jasper Fforde")),
        Book("Good Omens", listOf("Terry Pratchett", "Neil Gaiman"))
    )
    println(books.flatMap { it.authors }.toSet())
    // [Jasper Fforde, Terry Pratchett, Neil Gaiman]

    val strings = listOf("abc", "def")
    println(strings.flatMap { it.toList() })  // [a, b, c, d, e, f]

    // flatten
    val nested = listOf(listOf(1, 2), listOf(3, 4))
    println(nested.flatten())  // [1, 2, 3, 4]
}
