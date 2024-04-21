package ch07

import java.math.BigDecimal

// 7.1 이항 산술 연산자
data class Point(val x: Int, val y: Int)

operator fun Point.plus(other: Point): Point = Point(x + other.x, y + other.y)
operator fun Point.minus(other: Point): Point = Point(x - other.x, y - other.y)
operator fun Point.times(scale: Double): Point = Point((x * scale).toInt(), (y * scale).toInt())

// 결과 타입이 달라도 됨
operator fun Char.times(count: Int): String = toString().repeat(count)

// 7.2 복합 대입 연산자
// plus를 정의하면 += 자동 지원

// 7.3 단항 연산자
operator fun Point.unaryMinus(): Point = Point(-x, -y)

operator fun BigDecimal.inc(): BigDecimal = this + BigDecimal.ONE

fun main() {
    // 이항 연산자
    val p1 = Point(10, 20)
    val p2 = Point(30, 40)
    println(p1 + p2)       // Point(x=40, y=60)
    println(p1 - p2)       // Point(x=-20, y=-20)
    println(p1 * 1.5)      // Point(x=15, y=30)
    println('a' * 3)       // aaa

    // 복합 대입
    var point = Point(1, 2)
    point += Point(3, 4)
    println(point)         // Point(x=4, y=6)

    // 컬렉션 복합 대입
    val numbers = ArrayList<Int>()
    numbers += 42
    println(numbers[0])    // 42

    val list = arrayListOf(1, 2)
    list += 3
    val newList = list + listOf(4, 5)
    println(newList)       // [1, 2, 3, 4, 5]

    // 단항 연산자
    val p = Point(10, 20)
    println(-p)            // Point(x=-10, y=-20)

    var bd = BigDecimal.ZERO
    println(bd++)          // 0
    println(++bd)          // 2
}
