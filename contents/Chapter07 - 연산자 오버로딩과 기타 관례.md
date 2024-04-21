## 7장, 연산자 오버로딩과 기타 관례

* [산술 연산자 오버로딩](#산술-연산자-오버로딩)
* [비교 연산자 오버로딩](#비교-연산자-오버로딩)
* [컬렉션과 범위에 대해 쓸 수 있는 관례](#컬렉션과-범위에-대해-쓸-수-있는-관례)
* [구조 분해 선언과 component 함수](#구조-분해-선언과-component-함수)
* [프로퍼티 접근자 로직 재활용: 위임 프로퍼티](#프로퍼티-접근자-로직-재활용-위임-프로퍼티)

> **관례(convention)**: 특정 이름의 함수를 정의하면 언어 기능(연산자 등)과 자동으로 연결되는 코틀린의 원칙.  
> 타입 시스템보다 관례에 의존하므로, 기존 자바 클래스에 확장 함수를 추가해 코틀린 기능을 적용할 수 있다.

## 산술 연산자 오버로딩

### 이항 산술 연산자

`operator` 변경자를 붙인 함수로 정의한다. 멤버 함수 또는 확장 함수 모두 가능.

```kotlin
data class Point(val x: Int, val y: Int)

// 멤버 함수로 정의
operator fun Point.plus(other: Point): Point =
    Point(x + other.x, y + other.y)

val p1 = Point(10, 20)
val p2 = Point(30, 40)
println(p1 + p2)  // Point(x=40, y=60) — plus 함수 호출

// 피연산자 타입이 달라도 됨
operator fun Point.times(scale: Double): Point =
    Point((x * scale).toInt(), (y * scale).toInt())
println(Point(10, 20) * 1.5)  // Point(x=15, y=30)

// 결과 타입도 달라도 됨
operator fun Char.times(count: Int): String = toString().repeat(count)
println('a' * 3)  // aaa
```

| 식 | 함수 이름 |
|----|-----------|
| `a * b` | `times` |
| `a / b` | `div` |
| `a % b` | `rem` |
| `a + b` | `plus` |
| `a - b` | `minus` |

> 코틀린은 직접 연산자를 정의할 수 없고, 미리 정해진 이름의 함수만 오버로딩 가능하다.  
> 코틀린은 교환 법칙(commutativity)을 자동으로 지원하지 않는다. `1.5 * p`를 지원하려면 별도로 정의해야 한다.

### 복합 대입 연산자

`plus`를 정의하면 `+=`도 자동으로 지원된다. 직접 `plusAssign`을 정의할 수도 있다.

```kotlin
var point = Point(1, 2)
point += Point(3, 4)   // point = point + Point(3, 4)와 동일
println(point)         // Point(x=4, y=6)

// 변경 가능한 컬렉션: plusAssign으로 원소 추가
val numbers = ArrayList<Int>()
numbers += 42          // plusAssign 호출
println(numbers[0])    // 42

// +와 -: 새 컬렉션 반환 / +=와 -=: 컬렉션 자체를 변경
val list = arrayListOf(1, 2)
list += 3              // list 자체를 변경
val newList = list + listOf(4, 5)  // 새 리스트 반환
println(newList)       // [1, 2, 3, 4, 5]
```

### 단항 연산자

```kotlin
operator fun Point.unaryMinus(): Point = Point(-x, -y)

val p = Point(10, 20)
println(-p)  // Point(x=-10, y=-20)

// BigDecimal 증가 연산자
operator fun BigDecimal.inc() = this + BigDecimal.ONE
var bd = BigDecimal.ZERO
println(bd++)  // 0 (후위: 출력 후 증가)
println(++bd)  // 2 (전위: 증가 후 출력)
```

| 식 | 함수 이름 |
|----|----|
| `+a` | `unaryPlus` |
| `-a` | `unaryMinus` |
| `!a` | `not` |
| `++a`, `a++` | `inc` |
| `--a`, `a--` | `dec` |

## 비교 연산자 오버로딩

### 동등성 연산자: equals

`==`는 `equals()` 호출로 변환된다. `===`는 참조 동일성 비교로 오버로딩 불가.

```kotlin
// data class는 equals 자동 생성
// 직접 구현 시:
class Point(val x: Int, val y: Int) {
    override fun equals(obj: Any?): Boolean {
        if (obj === this) return true   // 동일 참조 최적화
        if (obj !is Point) return false
        return obj.x == x && obj.y == y
    }
}

// a == b  →  a?.equals(b) ?: (b == null)
// a != b  →  !(a?.equals(b) ?: (b == null))
println(Point(10, 20) == Point(10, 20))  // true
println(null == Point(1, 2))             // false
```

> `equals`는 `Any`에 정의된 메서드를 override하므로 `operator` 키워드가 없어도 된다.  
> 확장 함수로 정의할 수 없다 (`Any`의 멤버가 항상 우선순위를 가짐).

### 순서 연산자: compareTo

`<`, `>`, `<=`, `>=`는 `compareTo()` 호출로 변환된다.

```kotlin
class Person(val firstName: String, val lastName: String) : Comparable<Person> {
    override fun compareTo(other: Person): Int =
        compareValuesBy(this, other, Person::lastName, Person::firstName)
        // 성(lastName) 먼저 비교 → 같으면 이름(firstName) 비교
}

val p1 = Person("Alice", "Smith")
val p2 = Person("Bob", "Johnson")
println(p1 < p2)   // false (Johnson < Smith)
println("abc" < "bac")  // true (String도 Comparable 구현)

// a >= b  →  a.compareTo(b) >= 0
```

## 컬렉션과 범위에 대해 쓸 수 있는 관례

### 인덱스로 원소 접근: get과 set

```kotlin
data class Point(val x: Int, val y: Int)

operator fun Point.get(index: Int): Int = when (index) {
    0 -> x
    1 -> y
    else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
}

val p = Point(10, 20)
println(p[0])  // 10
println(p[1])  // 20  — p.get(1) 호출

// x[a, b]  →  x.get(a, b)   (다차원 인덱스도 가능)

// set: 변경 가능한 경우
data class MutablePoint(var x: Int, var y: Int)

operator fun MutablePoint.set(index: Int, value: Int) {
    when (index) {
        0 -> x = value
        1 -> y = value
        else -> throw IndexOutOfBoundsException("Invalid coordinate $index")
    }
}

val mp = MutablePoint(10, 20)
mp[1] = 42     // mp.set(1, 42) 호출
println(mp)    // MutablePoint(x=10, y=42)
```

### in 관례

```kotlin
data class Rectangle(val upperLeft: Point, val lowerRight: Point)

operator fun Rectangle.contains(p: Point): Boolean =
    p.x in upperLeft.x until lowerRight.x &&
    p.y in upperLeft.y until lowerRight.y

val rect = Rectangle(Point(10, 20), Point(50, 50))
println(Point(20, 30) in rect)  // true
println(Point(5, 5) in rect)    // false

// a in c  →  c.contains(a)
```

> `until`로 만드는 열린 범위(open range)는 끝 값을 포함하지 않는다.

### rangeTo 관례

```kotlin
// start..end  →  start.rangeTo(end)
// Comparable을 구현한 클래스는 표준 라이브러리의 rangeTo 확장 함수 사용 가능

val now = LocalDate.now()
val vacation = now..now.plusDays(10)          // rangeTo 호출
println(now.plusWeeks(1) in vacation)         // true

val n = 9
println(0..(n + 1))                           // 0..10
(0..n).forEach { print(it) }                  // 0123456789
```

### iterator 관례: for 루프

```kotlin
// for (x in obj) { }  →  obj.iterator() 호출 후 hasNext()/next() 반복

// 표준 라이브러리: String은 iterator 확장 함수가 정의되어 있어 바로 for 사용 가능
for (c in "abc") { print(c) }

// LocalDate 범위에 대한 iterator 정의
operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
    object : Iterator<LocalDate> {
        var current = start
        override fun hasNext() = current <= endInclusive
        override fun next() = current.apply { current = plusDays(1) }
    }

val newYear = LocalDate.ofYearDay(2017, 1)
val daysOff = newYear.minusDays(1)..newYear
for (dayOff in daysOff) { println(dayOff) }
// 2016-12-31
// 2017-01-01
```

## 구조 분해 선언과 component 함수

### 구조 분해 선언 기초

```kotlin
data class Point(val x: Int, val y: Int)

val p = Point(10, 20)
val (x, y) = p          // component1(), component2() 호출
println(x)              // 10
println(y)              // 20

// data class는 주 생성자 프로퍼티에 대해 componentN 자동 생성
// 직접 정의 시:
class Point2(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}
```

### 구조 분해로 여러 값 반환

```kotlin
data class NameComponents(val name: String, val extension: String)

fun splitFilename(fullName: String): NameComponents {
    val (name, extension) = fullName.split('.', limit = 2)
    return NameComponents(name, extension)
}

val (name, ext) = splitFilename("example.kt")
println(name)  // example
println(ext)   // kt
```

### 구조 분해와 루프

```kotlin
// Map 순회: Map.Entry에 component1(), component2() 확장 함수가 정의됨
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
printEntries(map)
// Oracle -> Java
// JetBrains -> Kotlin

// withIndex와 함께
val list = listOf("a", "b", "c")
for ((index, element) in list.withIndex()) {
    println("$index: $element")
}
```

## 프로퍼티 접근자 로직 재활용: 위임 프로퍼티

### 위임 프로퍼티 기본

```kotlin
// 기본 문법: by 키워드로 접근자 로직을 다른 객체에 위임
class Foo {
    var p: Type by Delegate()
}

// 컴파일러가 내부적으로 생성하는 코드:
// class Foo {
//     private val delegate = Delegate()
//     var p: Type
//         get() = delegate.getValue(this, ::p)
//         set(value) = delegate.setValue(this, ::p, value)
// }

// 위임 클래스는 getValue/setValue(mutable인 경우) 를 구현해야 함
class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = "value"
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) { }
}
```

### 지연 초기화: by lazy()

```kotlin
// 백킹 프로퍼티를 직접 구현하는 방식 (번거로움)
class Person(val name: String) {
    private var _emails: List<String>? = null
    val emails: List<String>
        get() {
            if (_emails == null) _emails = loadEmails(this)
            return _emails!!
        }
}

// by lazy()로 간결하게
class Person(val name: String) {
    val emails by lazy { loadEmails(this) }
    // 처음 접근 시 한 번만 loadEmails 호출, 이후 캐시된 값 반환
    // 기본적으로 thread-safe
}
```

> `lazy()`는 람다를 인자로 받아 `getValue`가 구현된 객체를 반환한다.

### Delegates.observable: 프로퍼티 변경 통지

```kotlin
import kotlin.properties.Delegates

class Person(val name: String, age: Int, salary: Int) {
    // 변경 시 자동으로 리스너에 통지
    var age: Int by Delegates.observable(age) { prop, oldValue, newValue ->
        println("${prop.name}: $oldValue → $newValue")
    }
    var salary: Int by Delegates.observable(salary) { prop, oldValue, newValue ->
        println("${prop.name}: $oldValue → $newValue")
    }
}

val p = Person("Alice", 29, 3000)
p.age = 30      // age: 29 → 30
p.salary = 3500 // salary: 3000 → 3500
```

### 맵에 프로퍼티 값 저장

```kotlin
// 맵 자체를 위임 객체로 사용 (Map/MutableMap에 getValue/setValue 확장 함수 정의됨)
class Person {
    private val _attributes = hashMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    // 맵을 위임 객체로 사용
    val name: String by _attributes
}

val p = Person()
p.setAttribute("name", "Dmitry")
println(p.name)  // Dmitry
```

📌 위임 프로퍼티 활용 정리

| 용도 | 방법 |
|------|------|
| 지연 초기화 | `by lazy { ... }` |
| 변경 통지 | `by Delegates.observable(...)` |
| null → 예외 | `by Delegates.notNull()` |
| 맵으로 동적 저장 | `by map` |
| 커스텀 로직 | `getValue`/`setValue` 직접 구현 |
