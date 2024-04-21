package ch07

// 7.4 동등성 연산자: equals
// data class는 equals 자동 생성
class PointEq(val x: Int, val y: Int) {
    override fun equals(obj: Any?): Boolean {
        if (obj === this) return true
        if (obj !is PointEq) return false
        return obj.x == x && obj.y == y
    }
    override fun hashCode(): Int = 31 * x + y
}

// 7.5 순서 연산자: compareTo
class Person(val firstName: String, val lastName: String) : Comparable<Person> {
    override fun compareTo(other: Person): Int =
        compareValuesBy(this, other, Person::lastName, Person::firstName)
}

fun main() {
    // equals
    println(PointEq(10, 20) == PointEq(10, 20))  // true
    println(PointEq(10, 20) == PointEq(5, 5))    // false
    println(null == PointEq(1, 2))               // false

    // compareTo
    val p1 = Person("Alice", "Smith")
    val p2 = Person("Bob", "Johnson")
    println(p1 < p2)    // false (Johnson < Smith 순서)
    println(p1 > p2)    // true

    println("abc" < "bac")  // true
    println("abc" >= "abc") // true
}
