## 8장, 고차 함수: 파라미터와 반환 값으로 람다 사용

* [고차 함수 선언](#고차-함수-선언)
* [인라인 함수: 람다의 부가 비용 없애기](#인라인-함수-람다의-부가-비용-없애기)
* [고차 함수 안에서의 흐름 제어](#고차-함수-안에서의-흐름-제어)

> **고차 함수(higher-order function)**: 다른 함수를 인자로 받거나 함수를 반환하는 함수.  
> 람다나 함수 참조를 인자로 넘기거나 반환값으로 사용할 수 있으면 고차 함수다.

## 고차 함수 선언

### 함수 타입

함수 타입은 `(파라미터 타입) -> 반환 타입` 형태로 선언한다.

```kotlin
// 타입 추론
val sum = { x: Int, y: Int -> x + y }
val action = { println(42) }

// 명시적 함수 타입
val sum: (Int, Int) -> Int = { x, y -> x + y }
val action: () -> Unit = { println(42) }

// nullable 반환 타입
var canReturnNull: (Int, Int) -> Int? = { null }

// nullable 함수 타입 변수 (전체를 괄호로 감싼 뒤 ?)
var funOrNull: ((Int, Int) -> Int)? = null

// 파라미터 이름 지정 (가독성 향상, IDE 자동완성 지원)
fun performRequest(
    url: String,
    callback: (code: Int, content: String) -> Unit
) { /* ... */ }
```

### 함수 타입 파라미터 호출

```kotlin
// 고차 함수 정의 및 호출
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

twoAndThree { a, b -> a + b }  // The result is 5
twoAndThree { a, b -> a * b }  // The result is 6

// filter 직접 구현
fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}

println("ab1c".filter { it in 'a'..'z' })  // abc
```

### 함수 타입 파라미터의 기본값과 null

```kotlin
// 람다를 기본값으로 지정
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    transform: (T) -> String = { it.toString() }  // 기본 람다
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element))
    }
    result.append(postfix)
    return result.toString()
}

val letters = listOf("Alpha", "Beta")
println(letters.joinToString())                  // Alpha, Beta
println(letters.joinToString { it.lowercase() }) // alpha, beta
println(letters.joinToString(separator = "! ", postfix = "! ",
    transform = { it.uppercase() }))             // ALPHA! BETA!

// nullable 함수 타입 파라미터
fun <T> Collection<T>.joinToStringNullable(
    separator: String = ", ",
    transform: ((T) -> String)? = null
): String {
    val result = StringBuilder()
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        val str = transform?.invoke(element) ?: element.toString()
        result.append(str)
    }
    return result.toString()
}
```

### 함수에서 함수 반환

```kotlin
enum class Delivery { STANDARD, EXPEDITED }
class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }
    return { order -> 1.2 * order.itemCount }
}

val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
println("Shipping costs ${calculator(Order(3))}")  // Shipping costs 12.3
```

### 람다로 중복 제거

```kotlin
data class SiteVisit(val path: String, val duration: Double, val os: OS)
enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("/signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
)

// 고차 함수로 중복 제거
fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) =
    filter(predicate).map(SiteVisit::duration).average()

println(log.averageDurationFor { it.os in setOf(OS.ANDROID, OS.IOS) })  // 12.15
println(log.averageDurationFor { it.os == OS.IOS && it.path == "/signup" })  // 8.0
```

## 인라인 함수: 람다의 부가 비용 없애기

람다는 보통 익명 클래스로 컴파일되어 호출마다 객체 생성 비용이 발생한다.  
`inline` 변경자를 붙이면 컴파일러가 함수 호출 대신 함수 본문 코드를 호출 지점에 직접 삽입한다.

### 인라이닝 작동 방식

```kotlin
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

val l = ReentrantLock()
synchronized(l) {
    println("Action")
}
// 컴파일 결과: synchronized 본문 + 람다 본문이 호출 위치에 직접 삽입됨
```

> `inline`으로 선언된 함수의 본문과 전달된 람다의 코드가 호출 지점에 직접 복사된다.  
> 별도의 익명 클래스나 객체가 생성되지 않는다.

### 인라인 함수 제약

람다를 변수에 저장하거나 다른 컨텍스트에서 호출하는 경우 인라이닝할 수 없다.  
특정 람다만 인라이닝하지 않으려면 `noinline`을 사용한다.

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    inlined()
    notInlined()  // 인라이닝되지 않음
}
```

### 자원 관리에 인라인된 람다 사용

```kotlin
// withLock: Lock 인터페이스의 확장 함수
val lock: Lock = ReentrantLock()
lock.withLock {
    // 락으로 보호되는 자원에 접근
}

// use: Closeable 자원의 자동 닫기 (try-with-resources)
fun readFirstLineFromFile(path: String): String {
    java.io.BufferedReader(java.io.FileReader(path)).use { br ->
        return br.readLine()  // 비지역 return: readFirstLineFromFile 함수에서 반환
    }
}
```

📌 인라인 함수 활용 정리

| 상황 | 효과 |
|------|------|
| 람다를 인자로 받는 함수 | 익명 클래스/객체 생성 비용 제거 |
| 컬렉션 연산 (filter, map 등) | 표준 라이브러리가 이미 inline으로 선언 |
| `noinline` | 특정 람다 파라미터만 인라이닝 제외 |
| 일반 함수 (람다 없음) | JVM이 이미 최적화, inline 불필요 |

## 고차 함수 안에서의 흐름 제어

### 람다 안의 return: 비지역 return

인라인 함수에 넘긴 람다에서 `return`을 사용하면 람다를 호출한 바깥 함수에서 반환된다.

```kotlin
data class Person(val name: String, val age: Int)
val people = listOf(Person("Alice", 29), Person("Bob", 31))

fun lookForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") {
            println("Found!")
            return  // 비지역 return: lookForAlice 함수에서 반환
        }
    }
    println("Alice is not found")  // forEach가 inline이므로 가능
}

lookForAlice(people)  // Found!
```

> 비지역 return은 람다를 인자로 받는 함수가 **인라인**일 때만 가능하다.

### 람다로부터 반환: 레이블 지정 return

람다 안에서만 반환하려면 레이블을 사용한다.

```kotlin
fun lookForAliceLabel(people: List<Person>) {
    people.forEach label@{
        if (it.name == "Alice") return@label  // 람다에서만 반환
    }
    println("Alice might be somewhere")  // 항상 출력됨
}

// 함수 이름을 레이블로 사용
fun lookForAliceForEach(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") return@forEach
    }
    println("Alice might be somewhere")
}
```

### 무명 함수: 기본적으로 지역 return

무명 함수 안의 `return`은 가장 가까운 `fun`(무명 함수 자체)에서 반환한다.

```kotlin
fun lookForAliceAnon(people: List<Person>) {
    people.forEach(fun(person) {
        if (person.name == "Alice") return  // 무명 함수에서 반환 (지역 return)
        println("${person.name} is not Alice")
    })
}

lookForAliceAnon(people)  // Bob is not Alice

// 표현식 본문 무명 함수
people.filter(fun(person) = person.age < 30)
```

📌 람다/무명 함수에서의 return 비교

| 구문 | return 범위 |
|------|------------|
| 람다 (인라인 함수에 전달) | 비지역 return — 바깥 함수에서 반환 |
| 람다 + 레이블 (`return@label`) | 지역 return — 람다 자체에서 반환 |
| 무명 함수 | 지역 return — 무명 함수 자체에서 반환 |
