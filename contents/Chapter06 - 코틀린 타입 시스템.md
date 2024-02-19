## 6장, 코틀린 타입 시스템

* [널 가능성](#널-가능성)
* [코틀린의 원시 타입](#코틀린의-원시-타입)
* [컬렉션과 배열](#컬렉션과-배열)

## 널 가능성

코틀린의 가장 중요한 특성 중 하나는 **null 안전성**이다.  
런타임에서 NullPointerException을 발생시키던 코드를 컴파일 시점에 잡아준다.

### 널이 될 수 있는 타입

타입 이름 뒤에 `?`를 붙이면 null을 허용한다. 붙이지 않으면 non-null이 기본이다.

```kotlin
fun strLen(s: String) = s.length       // null 불가, null 전달 시 컴파일 오류
fun strLenSafe(s: String?) = s?.length // null 허용

strLen(null)   // ERROR: Null can not be a value of a non-null type String
strLenSafe(null)  // OK → null 반환

// null 검사 후에는 non-null로 스마트 캐스트
fun strLenSafe2(s: String?): Int = if (s != null) s.length else 0
```

> 런타임에 nullable/non-null 타입 객체는 동일하다. 모든 검사는 컴파일 시점에 이뤄지므로 런타임 오버헤드가 없다.

### 안전한 호출 연산자: ?.

`?.`는 null 검사와 메서드 호출을 한 번에 수행한다. 수신 객체가 null이면 호출을 건너뛰고 null을 반환한다.

```kotlin
// s?.toUpperCase() == if (s != null) s.toUpperCase() else null

fun printAllCaps(s: String?) {
    val allCaps: String? = s?.toUpperCase()  // 결과 타입도 String?
    println(allCaps)
}
printAllCaps("abc")  // ABC
printAllCaps(null)   // null

// 프로퍼티에도 사용 가능
class Employee(val name: String, val manager: Employee?)
fun managerName(employee: Employee): String? = employee.manager?.name

// 연쇄 호출
class Address(val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

fun Person.countryName() = company?.address?.country ?: "Unknown"
```

### 엘비스 연산자: ?:

null 대신 사용할 디폴트 값을 지정한다. null이 아니면 그 값을, null이면 오른쪽 값을 반환한다.

```kotlin
fun strLenSafe(s: String?): Int = s?.length ?: 0

// return, throw도 식이므로 엘비스 오른쪽에 사용 가능
fun printShippingLabel(person: Person) {
    val address = person.company?.address
        ?: throw IllegalArgumentException("No address")
    // address는 이 시점부터 non-null
    println(address.city)
}
```

### 안전한 캐스트: as?

타입 캐스트를 시도하고, 타입이 맞지 않으면 null을 반환한다. (ClassCastException 방지)

```kotlin
// as?와 ?:를 함께 사용하는 패턴
class Person(val firstName: String, val lastName: String) {
    override fun equals(o: Any?): Boolean {
        val otherPerson = o as? Person ?: return false  // 타입 불일치 시 false 반환
        // as? 이후 otherPerson은 Person으로 스마트 캐스트
        return otherPerson.firstName == firstName &&
               otherPerson.lastName == lastName
    }
    override fun hashCode() = firstName.hashCode() * 37 + lastName.hashCode()
}

val p1 = Person("Alice", "Smith")
println(p1 == Person("Alice", "Smith"))  // true
println(p1.equals(42))                  // false
```

### 널 아님 단언: !!

nullable 타입을 non-null로 강제 변환한다. null이면 NullPointerException이 발생한다.

```kotlin
fun ignoreNulls(s: String?) {
    val sNotNull: String = s!!  // null이면 이 줄에서 예외 발생
    println(sNotNull.length)
}

// !! 사용 시 주의: 같은 줄에 여러 !!을 쓰면 어디서 null이었는지 알 수 없음
// person.company!!.address!!.country  // 지양할 것
```

> `!!`는 컴파일러가 null 여부를 확인할 수 없는 상황에서만 사용한다.  
> 가능하면 `?.`나 `?:`로 대체한다.

### let 함수

nullable 표현식을 non-null 함수의 인자로 전달할 때 유용하다.

```kotlin
fun sendEmailTo(email: String) { println("Sending email to $email") }

var email: String? = "kotlin@example.com"

// null 검사 방식 1: if
if (email != null) sendEmailTo(email)

// null 검사 방식 2: let (더 간결)
email?.let { sendEmailTo(it) }

// 더 긴 표현식에 유용
getTheBestPersonInTheWorld()?.let { sendEmailTo(it.email) }
```

### 나중에 초기화할 프로퍼티: lateinit

생성자가 아닌 별도 메서드에서 초기화하는 non-null 프로퍼티에 사용한다. (DI, JUnit 등)

```kotlin
// lateinit 없이: ?? 반복 사용 필요
class MyTest {
    private var myService: MyService? = null
    @Before fun setUp() { myService = MyService() }
    @Test fun testAction() {
        myService!!.performAction()  // 매번 !! 필요
    }
}

// lateinit 사용: non-null 타입 그대로 사용 가능
class MyTest {
    private lateinit var myService: MyService  // var이어야 함
    @Before fun setUp() { myService = MyService() }
    @Test fun testAction() {
        myService.performAction()  // !! 불필요
    }
}
```

> `lateinit`은 반드시 `var`이어야 한다. 초기화 전 접근 시 "lateinit property has not been initialized" 예외 발생.

### 널이 될 수 있는 타입 확장

nullable 타입의 확장 함수는 null 수신 객체에 대해서도 안전하게 동작하도록 직접 null 처리한다.

```kotlin
// 표준 라이브러리 예시
fun String?.isNullOrBlank(): Boolean = this == null || this.isBlank()

fun verifyUserInput(input: String?) {
    if (input.isNullOrBlank()) {  // 안전한 호출(?.) 없이 사용 가능
        println("Please fill in the required fields")
    }
}
verifyUserInput(null)   // "Please fill in the required fields"
verifyUserInput("  ")   // "Please fill in the required fields"
```

### 타입 파라미터의 널 가능성

타입 파라미터 `T`는 기본적으로 nullable이다 (`T`는 `Any?`로 추론될 수 있음).

```kotlin
fun <T> printHashCode(t: T) {
    println(t?.hashCode())  // T는 nullable일 수 있으므로 ?. 필요
}
printHashCode(null)  // OK, T는 Any?로 추론

// non-null upper bound로 제한
fun <T : Any> printHashCode(t: T) {
    println(t.hashCode())  // T는 non-null
}
printHashCode(null)  // ERROR
```

### 널 가능성과 자바

자바 코드에 `@Nullable`/`@NotNull` 어노테이션이 없는 타입은 코틀린에서 **플랫폼 타입**(`Type!`)으로 처리된다.  
플랫폼 타입은 nullable 또는 non-null 중 개발자가 직접 선택해서 사용한다.

```kotlin
// 자바 클래스 (어노테이션 없음)
// public class Person { public String getName() { return name; } }

fun yellAt(person: Person) {
    println(person.name.toUpperCase() + "!!!")  // name이 null이면 런타임 예외
}

fun yellAtSafe(person: Person) {
    println((person.name ?: "Anyone").toUpperCase() + "!!!")  // 안전하게 처리
}
```

## 코틀린의 원시 타입

### 원시 타입: Int, Boolean 등

코틀린은 자바와 달리 원시 타입과 래퍼 타입을 구분하지 않는다. 항상 같은 타입을 사용한다.

```kotlin
val i: Int = 1
val list: List<Int> = listOf(1, 2, 3)  // 자바에서는 List<Integer>

// 런타임에는 대부분 자바 int 원시 타입으로 컴파일
// 제네릭 타입 인자로 쓰이면 java.lang.Integer (박싱 타입)으로 컴파일
```

코틀린 타입 ↔ 자바 원시 타입 대응:
* 정수: `Byte`, `Short`, `Int`, `Long`
* 부동소수점: `Float`, `Double`
* 문자: `Char`
* 불리언: `Boolean`

### 널이 될 수 있는 원시 타입: Int?

`Int?` 같은 nullable 원시 타입은 자바의 박싱 타입(`Integer`)으로 컴파일된다.

```kotlin
data class Person(val name: String, val age: Int? = null) {
    fun isOlderThan(other: Person): Boolean? {
        if (age == null || other.age == null) return null
        return age > other.age
    }
}

println(Person("Sam", 35).isOlderThan(Person("Amy", 42)))  // false
println(Person("Sam", 35).isOlderThan(Person("Jane")))     // null
```

### 숫자 변환

코틀린은 숫자 타입 간 **자동 변환을 지원하지 않는다**. 명시적 변환 함수를 사용해야 한다.

```kotlin
val i = 1
// val l: Long = i  // ERROR: type mismatch
val l: Long = i.toLong()  // OK

// 변환 함수: toByte(), toShort(), toInt(), toLong(), toFloat(), toDouble(), toChar()
val x = 1
println(x.toLong() in listOf(1L, 2L, 3L))  // true

// 리터럴 타입 명시
val l2 = 123L      // Long
val f = 123.4f     // Float
val hex = 0xFF     // Int (16진수)
val bin = 0b1010   // Int (2진수)

// String → 숫자 변환
println("42".toInt())    // 42
println("abc".toIntOrNull())  // null (파싱 실패 시)
```

### Any와 Any?: 루트 타입

`Any`는 코틀린의 모든 non-null 타입의 최상위 타입 (자바의 `Object`에 해당).

```kotlin
val answer: Any = 42  // Int가 Any로 박싱됨

// Any: non-null 루트 타입
// Any?: nullable 포함 모든 타입의 루트 타입
// Any에는 toString(), equals(), hashCode()가 정의되어 있음
```

### Unit 타입: 코틀린의 void

`Unit`은 반환값이 없는 함수의 반환 타입으로 사용된다. 자바의 `void`와 달리 타입 인자로 쓸 수 있다.

```kotlin
fun f(): Unit { /* ... */ }
fun f() { /* ... */ }  // Unit 생략 가능 (동일)

// Unit은 타입 인자로 사용 가능 (Java의 void는 불가)
interface Processor<T> {
    fun process(): T
}
class NoResultProcessor : Processor<Unit> {
    override fun process() {
        // return Unit 생략 가능 — 컴파일러가 자동 추가
    }
}
```

### Nothing 타입: 이 함수는 정상적으로 끝나지 않는다

정상적으로 반환되지 않는 함수(예외만 던지거나 무한루프)의 반환 타입.

```kotlin
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}

// Nothing은 엘비스 연산자 오른쪽에서 유용하다
val address = company.address ?: fail("No address")
println(address.city)  // 컴파일러가 address를 non-null로 추론
```

## 컬렉션과 배열

### 널 가능성과 컬렉션

`List<Int?>`와 `List<Int>?`는 다르다.

```kotlin
// List<Int?>: 리스트 자체는 non-null, 원소는 null 가능
// List<Int>?: 리스트 자체가 null 가능, 원소는 non-null
// List<Int?>?: 둘 다 null 가능

fun readNumbers(reader: BufferedReader): List<Int?> {
    val result = ArrayList<Int?>()
    for (line in reader.lineSequence()) {
        result.add(line.toIntOrNull())  // 파싱 실패 시 null 추가
    }
    return result
}

// filterNotNull: nullable 원소 컬렉션에서 null 제거
fun addValidNumbers(numbers: List<Int?>) {
    val validNumbers = numbers.filterNotNull()  // 반환 타입: List<Int>
    println("Sum: ${validNumbers.sum()}")
    println("Invalid: ${numbers.size - validNumbers.size}")
}
```

### 읽기 전용과 변경 가능한 컬렉션

코틀린 컬렉션의 핵심 설계: **읽기 인터페이스**와 **변경 인터페이스**를 분리한다.

```kotlin
// Collection (읽기 전용): size, iterator(), contains() 등
// MutableCollection (변경 가능): add(), remove(), clear() 등

fun <T> copyElements(source: Collection<T>, target: MutableCollection<T>) {
    for (item in source) {
        target.add(item)
    }
}

val source: Collection<Int> = arrayListOf(3, 5, 7)
val target: MutableCollection<Int> = arrayListOf(1)
copyElements(source, target)
println(target)  // [1, 3, 5, 7]
```

📌 읽기 전용 컬렉션 ≠ 불변 컬렉션
> 읽기 전용 참조라도 다른 참조(MutableList)가 같은 컬렉션을 가리킬 수 있다.  
> 멀티스레드 환경에서는 읽기 전용 컬렉션도 동기화가 필요할 수 있다.

### 컬렉션 생성 함수

| 컬렉션 타입 | 읽기 전용 | 변경 가능 |
|-------------|-----------|-----------|
| List | `listOf` | `mutableListOf`, `arrayListOf` |
| Set | `setOf` | `mutableSetOf`, `hashSetOf`, `linkedSetOf`, `sortedSetOf` |
| Map | `mapOf` | `mutableMapOf`, `hashMapOf`, `linkedMapOf`, `sortedMapOf` |

### 코틀린 컬렉션과 자바

코틀린 컬렉션은 **자바 컬렉션 인터페이스 그대로**다. 변환이나 래핑 없이 자바 코드와 교환할 수 있다.

```kotlin
// 자바 메서드에 코틀린 컬렉션을 그대로 전달 가능
// 단, 자바 코드는 읽기 전용/변경 가능 구분을 모르므로 읽기 전용 컬렉션도 자바에서 변경될 수 있음

// 플랫폼 타입: 자바에서 온 컬렉션은 nullable 여부를 개발자가 판단해야 함
```

### 배열

코틀린에서 배열을 만드는 방법:

```kotlin
// arrayOf: 원소를 직접 나열
val array = arrayOf(1, 2, 3)

// arrayOfNulls: null로 초기화된 배열
val nullArray = arrayOfNulls<String>(3)  // [null, null, null]

// Array 생성자: 크기와 초기화 람다
val squares = Array(5) { i -> (i + 1) * (i + 1) }  // [1, 4, 9, 16, 25]

// 배열 이터레이션
for ((index, element) in squares.withIndex()) {
    println("$index: $element")
}

// 원시 타입 배열 (박싱 없이 효율적)
val intArray = IntArray(5)                    // [0, 0, 0, 0, 0]
val intArray2 = intArrayOf(1, 2, 3)
val intArray3 = IntArray(5) { it * 2 }        // [0, 2, 4, 6, 8]

// 컬렉션 → 배열 변환
val list = listOf(1, 2, 3)
val arr = list.toIntArray()
```
