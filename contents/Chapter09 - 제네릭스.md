## 9장, 제네릭스

* [제네릭 타입 파라미터](#제네릭-타입-파라미터)
* [실행 시 제네릭스의 동작: 소거된 타입 파라미터와 실체화된 타입 파라미터](#실행-시-제네릭스의-동작)
* [변성: 제네릭과 서브타입](#변성-제네릭과-서브타입)

## 제네릭 타입 파라미터

### 제네릭 함수와 프로퍼티

```kotlin
// 제네릭 함수: 타입 파라미터를 함수 이름 앞에 선언
fun <T> List<T>.slice(indices: IntRange): List<T>

val letters = ('a'..'z').toList()
println(letters.slice(0..2))   // [a, b, c] — T가 Char로 추론됨

// 제네릭 확장 프로퍼티
val <T> List<T>.penultimate: T
    get() = this[size - 2]

println(listOf(1, 2, 3, 4).penultimate)  // 3
```

### 제네릭 클래스 선언

```kotlin
// 타입 파라미터를 클래스 이름 뒤에 선언
interface List<T> {
    operator fun get(index: Int): T
}

// 구체적인 타입으로 상속
class StringList : List<String> {
    override fun get(index: Int): String = TODO()
}

// 타입 파라미터를 그대로 전달
class ArrayList<T> : List<T> {
    override fun get(index: Int): T = TODO()
}

// 자기 자신을 타입 인자로 참조 (Comparable)
interface Comparable<T> {
    fun compareTo(other: T): Int
}
```

### 타입 파라미터 제약

```kotlin
// 상한(upper bound) 제약: 콜론 뒤에 타입 지정
fun <T : Number> List<T>.sum(): T = TODO()
println(listOf(1, 2, 3).sum())  // 6

fun <T : Number> oneHalf(value: T): Double = value.toDouble() / 2.0
println(oneHalf(3))  // 1.5

// Comparable 제약: 비교 가능한 값만 허용
fun <T : Comparable<T>> max(first: T, second: T): T =
    if (first > second) first else second

println(max("kotlin", "java"))  // kotlin

// 여러 제약: where 절 사용
fun <T> ensureTrailingPeriod(seq: T)
        where T : CharSequence, T : Appendable {
    if (!seq.endsWith('.')) {
        seq.append('.')
    }
}

val sb = StringBuilder("Hello World")
ensureTrailingPeriod(sb)
println(sb)  // Hello World.
```

### 타입 파라미터를 non-null로 만들기

```kotlin
// 기본 상한은 Any? → T는 nullable일 수 있음
class Processor<T> {
    fun process(value: T) {
        value?.hashCode()  // safe call 필요
    }
}

// Any를 상한으로 지정하면 non-null 보장
class ProcessorNonNull<T : Any> {
    fun process(value: T) {
        value.hashCode()  // safe call 불필요
    }
}
```

## 실행 시 제네릭스의 동작

### 타입 소거와 스타 프로젝션

```kotlin
// JVM 제네릭스는 타입 소거(type erasure) 사용
// 런타임에는 List<String>과 List<Int>가 모두 List로만 보임

val list1: List<String> = listOf("a", "b")
val list2: List<Int> = listOf(1, 2, 3)
// 런타임: 둘 다 그냥 List

// is 검사에 타입 인자 사용 불가
// if (value is List<String>) { }  // ERROR

// 스타 프로젝션(*): 타입 인자를 모를 때
fun printList(c: Collection<*>) {
    val list = c as? List<*>
        ?: throw IllegalArgumentException("List expected")
    println(list)
}

// 컴파일 타임에 알려진 타입 인자는 is 검사 가능
fun printSum(c: Collection<Int>) {
    if (c is List<Int>) {
        println(c.sum())
    }
}
```

### 실체화된 타입 파라미터: reified

```kotlin
// 일반 제네릭 함수는 T로 is 검사 불가
// fun <T> isA(value: Any) = value is T  // ERROR

// inline + reified: 런타임에 타입 인자 사용 가능
inline fun <reified T> isA(value: Any) = value is T

println(isA<String>("abc"))  // true
println(isA<String>(123))    // false

// 표준 라이브러리: filterIsInstance
val items = listOf("one", 2, "three")
println(items.filterIsInstance<String>())  // [one, three]

// 간소화된 filterIsInstance 구현
inline fun <reified T> Iterable<*>.filterIsInstance(): List<T> {
    val destination = mutableListOf<T>()
    for (element in this) {
        if (element is T) destination.add(element)
    }
    return destination
}

// java.lang.Class 대신 reified 사용
inline fun <reified T> loadService() =
    java.util.ServiceLoader.load(T::class.java)

// reified 타입 파라미터 사용 가능 범위
// - is, !is, as, as? 검사
// - ::class 리플렉션
// - ::class.java 접근
// - 다른 reified 함수의 타입 인자
// 불가: 인스턴스 생성, 동반 객체 멤버 호출, non-inline 함수의 타입 파라미터
```

## 변성: 제네릭과 서브타입

### 클래스, 타입, 서브타입

```kotlin
// 서브타입(subtype): B가 A의 서브타입 → A가 필요한 곳에 B를 사용 가능
// Int는 Number의 서브타입, String은 CharSequence의 서브타입

// nullable: String은 String?의 서브타입 (역은 성립 안함)
val s: String = "abc"
val t: String? = s  // OK

// 불변성(invariant): MutableList<String>은 MutableList<Any>의 서브타입이 아님
// → 안전하지 않기 때문
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}
// val strings = mutableListOf("abc")
// addAnswer(strings)  // 컴파일 오류
```

### 공변성(covariance): out

```kotlin
// out 키워드: T를 out 위치(반환 타입)에서만 사용
// Producer<Cat>은 Producer<Animal>의 서브타입이 됨
interface Producer<out T> {
    fun produce(): T
}

// 읽기 전용 List는 공변적
// interface List<out T> : Collection<T>

// 직접 구현 예
open class Animal { fun feed() {} }
class Cat : Animal() { fun cleanLitter() {} }

class Herd<out T : Animal>(private val animals: List<T>) {
    val size get() = animals.size
    operator fun get(i: Int): T = animals[i]
}

fun feedAll(animals: Herd<Animal>) {
    for (i in 0 until animals.size) animals[i].feed()
}

fun takeCareOfCats(cats: Herd<Cat>) {
    for (i in 0 until cats.size) cats[i].cleanLitter()
    feedAll(cats)  // out 덕분에 가능
}
```

### 반공변성(contravariance): in

```kotlin
// in 키워드: T를 in 위치(파라미터 타입)에서만 사용
// Consumer<Animal>은 Consumer<Cat>의 서브타입이 됨
interface Comparator<in T> {
    fun compare(e1: T, e2: T): Int
}

val anyComparator = Comparator<Any> { e1, e2 ->
    e1.hashCode() - e2.hashCode()
}
val strings: List<String> = listOf("b", "a", "c")
println(strings.sortedWith(anyComparator))  // [a, b, c]

// 함수 타입: 파라미터에 반공변, 반환 타입에 공변
// (Cat) -> Number의 서브타입은 (Animal) -> Int
// interface Function1<in P, out R> { fun invoke(p: P): R }
```

📌 변성 정리

| | 공변(covariant) | 반공변(contravariant) | 무공변(invariant) |
|---|---|---|---|
| 키워드 | `out` | `in` | 없음 |
| 서브타입 관계 | 보존 | 역전 | 없음 |
| T 사용 위치 | out 위치만 | in 위치만 | 어디서나 |
| 예 | `List<out T>` | `Comparator<in T>` | `MutableList<T>` |

### 사용 지점 변성과 스타 프로젝션

```kotlin
// 사용 지점 변성(use-site variance): 특정 사용 위치에 out/in 지정
fun <T> copyData(source: MutableList<out T>,
                 destination: MutableList<T>) {
    for (item in source) destination.add(item)
}

val ints = mutableListOf(1, 2, 3)
val anyItems = mutableListOf<Any>()
copyData(ints, anyItems)
println(anyItems)  // [1, 2, 3]

// in 프로젝션
fun <T> copyDataIn(source: MutableList<T>,
                   destination: MutableList<in T>) {
    for (item in source) destination.add(item)
}

// 스타 프로젝션(*): 타입 인자를 모를 때 읽기만 허용
fun printFirst(list: List<*>) {
    if (list.isNotEmpty()) println(list.first())  // Any? 반환
}
printFirst(listOf("Svetlana", "Dmitry"))  // Svetlana
```
