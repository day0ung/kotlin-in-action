package ch07

import java.time.LocalDate

// 7.6 인덱스 접근: get / set
data class PointIdx(val x: Int, val y: Int)

operator fun PointIdx.get(index: Int): Int = when (index) {
    0 -> x
    1 -> y
    else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
}

data class MutablePoint(var x: Int, var y: Int)

operator fun MutablePoint.set(index: Int, value: Int) {
    when (index) {
        0 -> x = value
        1 -> y = value
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

// 7.7 in 관례
data class Rectangle(val upperLeft: PointIdx, val lowerRight: PointIdx)

operator fun Rectangle.contains(p: PointIdx): Boolean =
    p.x in upperLeft.x until lowerRight.x &&
    p.y in upperLeft.y until lowerRight.y

// 7.8 rangeTo 관례
// Comparable 구현체는 표준 라이브러리 rangeTo 확장 함수 사용 가능

// 7.9 iterator 관례: for 루프
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    object : Iterator<LocalDate> {
        var current = start
        override fun hasNext() = current <= endInclusive
        override fun next() = current.apply { current = plusDays(1) }
    }

fun main() {
    // get
    val p = PointIdx(10, 20)
    println(p[0])  // 10
    println(p[1])  // 20

    // set
    val mp = MutablePoint(10, 20)
    mp[1] = 42
    println(mp)    // MutablePoint(x=10, y=42)

    // in
    val rect = Rectangle(PointIdx(10, 20), PointIdx(50, 50))
    println(PointIdx(20, 30) in rect)  // true
    println(PointIdx(5, 5) in rect)    // false

    // rangeTo
    val now = LocalDate.now()
    val vacation = now..now.plusDays(10)
    println(now.plusWeeks(1) in vacation)  // true

    val n = 9
    println(0..(n + 1))                   // 0..10
    (0..n).forEach { print(it) }          // 0123456789
    println()

    // iterator: LocalDate 범위 for 루프
    val newYear = LocalDate.ofYearDay(2017, 1)
    val daysOff = newYear.minusDays(1)..newYear
    for (dayOff in daysOff) { println(dayOff) }
    // 2016-12-31
    // 2017-01-01
}
