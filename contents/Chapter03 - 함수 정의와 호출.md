## 3장, 함수 정의와 호출

* [코틀린에서 컬렉션 만들기](#코틀린에서-컬렉션-만들기)
* [함수를 호출하기 쉽게 만들기](#함수를-호출하기-쉽게-만들기)
* [확장 함수와 확장 프로퍼티](#확장-함수와-확장-프로퍼티)
* [컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원](#컬렉션-처리-가변-길이-인자-중위-함수-호출-라이브러리-지원)
* [문자열과 정규식 다루기](#문자열과-정규식-다루기)
* [코드 다듬기: 로컬 함수와 확장](#코드-다듬기-로컬-함수와-확장)

## 코틀린에서 컬렉션 만들기

코틀린은 자체 컬렉션을 제공하지 않고, 자바의 표준 컬렉션을 그대로 사용한다.  
단, `setOf()`, `listOf()`, `mapOf()` 등의 함수로 더 간결하게 생성할 수 있다.

```kotlin
val set = hashSetOf(1, 7, 53)
val list = arrayListOf(1, 7, 53)
val map = hashMapOf(1 to "one", 7 to "seven", 53 to "fifty-three")

println(set.javaClass)  // class java.util.HashSet
println(list.javaClass) // class java.util.ArrayList
println(map.javaClass)  // class java.util.HashMap
```

> 코틀린이 자체 컬렉션을 만들지 않는 이유는, 표준 자바 컬렉션을 활용하면 자바 코드와 상호작용하기 훨씬 쉽기 때문이다.  
> 코틀린 컬렉션은 자바 컬렉션과 같은 클래스지만, 코틀린에서는 더 많은 기능을 사용할 수 있다.

```kotlin
val strings = listOf("first", "second", "fourteenth")
println(strings.last())    // fourteenth

val numbers = setOf(1, 14, 2)
println(numbers.max())     // 14
```

## 함수를 호출하기 쉽게 만들기

### 이름 붙인 인자 (Named Arguments)

코틀린으로 작성한 함수를 호출할 때는 함수에 전달하는 인자 중 일부(또는 전부)의 이름을 명시할 수 있다.

```kotlin
fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

// 이름 붙인 인자 사용
println(joinToString(collection = listOf(1, 2, 3), separator = "; ", prefix = "(", postfix = ")"))
// (1; 2; 3)
```

📌 주의사항
> 자바로 작성한 함수를 호출할 때는 이름 붙인 인자를 사용할 수 없다.  
> JDK가 제공하는 함수나 안드로이드 프레임워크 함수는 모두 자바로 작성됐으므로 이름 붙인 인자를 사용할 수 없다.

### 디폴트 파라미터 값 (Default Parameter Values)

코틀린에서는 함수 선언에서 파라미터의 디폴트 값을 지정할 수 있어, 오버로드 중 상당수를 피할 수 있다.

```kotlin
fun <T> joinToString(
    collection: Collection<T>,
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String { /* ... */ }

// 다양한 호출 방식
joinToString(list)                          // separator, prefix, postfix 모두 디폴트
joinToString(list, "; ")                    // separator만 변경
joinToString(list, prefix = "(", postfix = ")")  // 이름 붙인 인자로 일부만 지정
```

📌 자바에서 코틀린 디폴트 파라미터 함수 호출
> 자바에서는 디폴트 파라미터 값이라는 개념이 없어서, 코틀린 함수를 자바에서 호출할 때는 모든 인자를 명시해야 한다.  
> 자바에서 좀 더 편하게 코틀린 함수를 호출하고 싶다면 `@JvmOverloads` 어노테이션을 함수에 추가하면 된다.

```kotlin
@JvmOverloads
fun <T> joinToString(
    collection: Collection<T>,
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String { /* ... */ }
```

### 최상위 함수와 프로퍼티 (Top-level Functions and Properties)

자바에서는 모든 코드를 클래스의 메서드로 작성해야 하지만, 코틀린에서는 함수를 클래스 밖에 선언할 수 있다.

```kotlin
// strings/join.kt 파일
package strings

fun joinToString(/* ... */): String { /* ... */ }
```

> JVM은 클래스 안에 있는 코드만을 실행할 수 있어서, 컴파일러는 최상위 함수를 해당 파일 이름에 해당하는 클래스의 정적 메서드로 변환한다.  
> 코틀린 컴파일러가 생성하는 클래스의 이름은 최상위 함수가 들어있던 코틀린 소스 파일의 이름과 대응한다.

```kotlin
// 자바에서 호출 시 → JoinKt.joinToString(list, ", ", "", "")

// 파일 이름과 클래스 이름을 다르게 하고 싶다면
@file:JvmName("StringFunctions")
package strings
fun joinToString(/* ... */): String { /* ... */ }
// 자바에서 → StringFunctions.joinToString(list, ", ", "", "")
```

**최상위 프로퍼티**

```kotlin
// 최상위 프로퍼티
var opCount = 0

fun performOperation() {
    opCount++
}

// 상수 선언 (자바의 public static final 필드에 해당)
const val UNIX_LINE_SEPARATOR = "\n"
```

## 확장 함수와 확장 프로퍼티

### 확장 함수 (Extension Functions)

**확장 함수**는 어떤 클래스의 멤버 메서드인 것처럼 호출할 수 있지만, 그 클래스의 밖에 선언된 함수다.

```kotlin
// String 클래스에 확장 함수 추가
fun String.lastChar(): Char = this[this.length - 1]
//   수신 객체 타입                수신 객체

println("Kotlin".lastChar()) // n
```

* **수신 객체 타입(receiver type)**: 확장이 정의될 클래스의 타입 (`String`)
* **수신 객체(receiver object)**: 확장 함수가 호출되는 대상 값 (`this`)

📌 확장 함수 안에서는 클래스 내부에서만 사용할 수 있는 private, protected 멤버를 사용할 수 없다.

```kotlin
// 자바 컬렉션에 확장 함수 추가
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = ""
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }
    result.append(postfix)
    return result.toString()
}

val list = listOf(1, 2, 3)
println(list.joinToString(separator = "; ", prefix = "(", postfix = ")"))
// (1; 2; 3)
```

### 확장 함수는 오버라이드할 수 없다

확장 함수는 클래스의 일부가 아니라서, 오버라이드가 불가능하다.  
코틀린은 호출될 확장 함수를 **정적**으로 결정한다. (컴파일 시점에 결정)

```kotlin
open class View {
    open fun click() = println("View clicked")
}

class Button : View() {
    override fun click() = println("Button clicked")
}

// 멤버 함수: 동적 디스패치 (런타임 타입에 따라 결정)
val view: View = Button()
view.click()  // "Button clicked" (오버라이드 가능)

// 확장 함수: 정적 디스패치 (컴파일 시점 타입에 따라 결정)
fun View.showOff() = println("I'm a view!")
fun Button.showOff() = println("I'm a button!")

val view2: View = Button()
view2.showOff()  // "I'm a view!" (확장 함수는 오버라이드 불가)
```

> 어떤 클래스를 확장한 함수와 그 클래스의 멤버 함수의 이름과 시그니처가 같다면 **멤버 함수가 우선순위가 높다**.

### 확장 프로퍼티 (Extension Properties)

확장 프로퍼티는 기존 클래스 객체에 대한 프로퍼티 형식의 구문으로 사용할 수 있는 API를 추가할 수 있다.  
실제로 확장 프로퍼티는 아무 상태도 가질 수 없다. (기존 클래스에 필드를 추가할 방법이 없으므로)

```kotlin
val String.lastChar: Char
    get() = get(length - 1)

var StringBuilder.lastChar: Char
    get() = get(length - 1)
    set(value: Char) {
        this.setCharAt(length - 1, value)
    }

println("Kotlin".lastChar)  // n

val sb = StringBuilder("Kotlin?")
sb.lastChar = '!'
println(sb)  // Kotlin!
```

## 컬렉션 처리: 가변 길이 인자, 중위 함수 호출, 라이브러리 지원

### 가변 길이 인자 (vararg)

`vararg` 키워드를 사용하면 호출 시 인자 개수가 달라질 수 있는 함수를 정의할 수 있다.

```kotlin
fun listOf<T>(vararg values: T): List<T> { /* ... */ }

val list = listOf(2, 3, 5, 7, 11)

// 이미 배열에 들어있는 원소를 vararg 인자로 넘길 때: 스프레드 연산자(*) 사용
val args = arrayOf("foo", "bar")
val list2 = listOf("args: ", *args)
println(list2)  // [args: , foo, bar]
```

> 자바에서는 배열을 그냥 넘기면 되지만, 코틀린에서는 배열을 명시적으로 풀어서 배열의 각 원소가 인자로 전달되게 해야 한다.

### 중위 함수 호출 (Infix Calls)

`infix` 변경자를 함수 앞에 추가하면 중위 호출 방식으로 함수를 호출할 수 있다.

```kotlin
// to는 코틀린 표준 라이브러리의 infix 함수
val map = mapOf(1 to "one", 7 to "seven", 53 to "fifty-three")

// to 함수의 실제 정의
infix fun Any.to(other: Any) = Pair(this, other)

// 두 가지 호출 방식
1.to("one")      // 일반적인 방식
1 to "one"       // 중위 호출 방식

// 구조 분해 선언(destructuring declaration)
val (number, name) = 1 to "one"
```

## 문자열과 정규식 다루기

### 문자열 나누기

코틀린에서는 자바의 `split()`과 달리 여러 구분 문자열을 명시할 수 있다.

```kotlin
// 자바의 split은 정규식을 인자로 받는다 (혼동의 소지)
// "12.345-6.A".split(".")  → 자바에서는 빈 배열 반환

// 코틀린의 split - 정규식을 명시적으로 사용
println("12.345-6.A".split("\\.|-".toRegex()))  // [12, 345, 6, A]

// 코틀린의 split - 여러 구분 문자열 사용 (간단한 경우 권장)
println("12.345-6.A".split(".", "-"))            // [12, 345, 6, A]
```

### 정규식과 3중 따옴표 문자열

```kotlin
// 3중 따옴표 문자열에서는 역슬래시를 추가로 이스케이프할 필요가 없다
fun parsePath(path: String) {
    val regex = """(.+)/(.+)\.(.+)""".toRegex()
    val matchResult = regex.matchEntire(path)
    if (matchResult != null) {
        val (directory, filename, extension) = matchResult.destructured
        println("Dir: $directory, name: $filename, ext: $extension")
    }
}

parsePath("/Users/yole/kotlin-book/chapter.adoc")
// Dir: /Users/yole/kotlin-book, name: chapter, ext: adoc
```

### 여러 줄 3중 따옴표 문자열

```kotlin
val kotlinLogo = """| //
                   .|//
                   .|/ \"""

println(kotlinLogo.trimMargin("."))
// | //
// |//
// |/ \
```

> 3중 따옴표 문자열 안에는 줄 바꿈을 표현하는 `\n` 같은 이스케이프가 필요 없다.  
> 하지만 `\n`을 사용할 수 없다는 뜻이기도 하다. 일반 문자열로 넣고 싶다면 `\` 앞에 `${'$'}`를 사용해야 한다.

## 코드 다듬기: 로컬 함수와 확장

**로컬 함수**: 함수에서 추출한 함수를 원래 함수 내부에 중첩시켜, 코드 중복을 줄이면서 깔끔하게 코드를 구성한다.

```kotlin
class User(val id: Int, val name: String, val address: String)

// 로컬 함수를 사용한 코드 중복 제거
fun saveUser(user: User) {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException(
                "Can't save user ${user.id}: empty $fieldName"  // 바깥 함수의 파라미터에 직접 접근 가능
            )
        }
    }

    validate(user.name, "Name")
    validate(user.address, "Address")
    // user를 데이터베이스에 저장
}
```

로컬 함수는 자신이 속한 바깥 함수의 모든 파라미터와 변수를 사용할 수 있다.

```kotlin
// 확장 함수로 더 개선하기
fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can't save user $id: empty $fieldName")
        }
    }
    validate(name, "Name")
    validate(address, "Address")
}

fun saveUser(user: User) {
    user.validateBeforeSave()
    // user를 데이터베이스에 저장
}
```

📌 로컬 함수는 한 단계만 중첩시키는 것을 권장한다. 그 이상 중첩되면 코드가 읽기 어려워진다.
