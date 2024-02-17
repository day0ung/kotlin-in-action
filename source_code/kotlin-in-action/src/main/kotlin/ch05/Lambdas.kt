package ch05

data class Person(val name: String, val age: Int)

// 5.1 람다 문법
fun lambdaSyntax() {
    val sum = { x: Int, y: Int -> x + y }
    println(sum(1, 2))  // 3

    run { println(42) }  // 42

    val people = listOf(Person("Alice", 29), Person("Bob", 31))

    // 문법 간소화 단계
    people.maxBy({ p: Person -> p.age })
    people.maxBy { p: Person -> p.age }
    people.maxBy { p -> p.age }
    people.maxBy { it.age }         // it: 자동 생성 파라미터 이름
    people.maxBy(Person::age)       // 멤버 참조

    // 여러 줄 람다: 마지막 식이 반환값
    val multiLine = { x: Int, y: Int ->
        println("Computing sum of $x and $y")
        x + y
    }
    println(multiLine(1, 2))
}

// 5.2 클로저: 바깥 변수 접근 및 변경
fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
    messages.forEach { println("$prefix $it") }
}

fun printProblemCounts(responses: Collection<String>) {
    var clientErrors = 0
    var serverErrors = 0
    responses.forEach {
        if (it.startsWith("4")) clientErrors++
        else if (it.startsWith("5")) serverErrors++
    }
    println("$clientErrors client errors, $serverErrors server errors")
}

// 5.3 멤버 참조
fun salute() = println("Salute!")

fun memberReferences() {
    val people = listOf(Person("Alice", 29), Person("Bob", 31))

    // 프로퍼티 참조
    println(people.maxBy(Person::age))

    // 최상위 함수 참조
    run(::salute)

    // 생성자 참조
    val createPerson = ::Person
    val p = createPerson("Alice", 29)
    println(p)

    // 확장 함수 참조
    fun Person.isAdult() = age >= 21
    val predicate = Person::isAdult
    println(people.filter(predicate))
}

fun main() {
    lambdaSyntax()

    val errors = listOf("403 Forbidden", "404 Not Found")
    printMessagesWithPrefix(errors, "Error:")

    val responses = listOf("200 OK", "418 I'm a teapot", "500 Internal Server Error")
    printProblemCounts(responses)

    memberReferences()
}
