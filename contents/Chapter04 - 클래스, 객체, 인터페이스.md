## 4장, 클래스, 객체, 인터페이스

* [클래스 계층 정의](#클래스-계층-정의)
* [뻔하지 않은 생성자와 프로퍼티를 갖는 클래스 선언](#뻔하지-않은-생성자와-프로퍼티를-갖는-클래스-선언)
* [컴파일러가 생성한 메서드: 데이터 클래스와 클래스 위임](#컴파일러가-생성한-메서드-데이터-클래스와-클래스-위임)
* [object 키워드: 클래스 선언과 인스턴스 생성](#object-키워드-클래스-선언과-인스턴스-생성)

## 클래스 계층 정의

### 인터페이스

코틀린 인터페이스는 자바 8의 인터페이스와 비슷하다. 추상 메서드뿐 아니라 구현이 있는 메서드도 정의할 수 있다.  
단, 인터페이스에는 아무런 상태(필드)도 들어갈 수 없다.

```kotlin
interface Clickable {
    fun click()                                     // 추상 메서드
    fun showOff() = println("I'm clickable!")       // 디폴트 구현이 있는 메서드
}

class Button : Clickable {                          // 인터페이스 구현 (콜론 사용)
    override fun click() = println("I was clicked") // override 키워드 필수
}
```

📌 Java와 다른점
* 코틀린은 `extends`, `implements` 대신 `:` (콜론)을 사용한다.
* `override` 변경자 사용이 **필수**다. 실수로 override 하는 일을 방지한다.
* 메서드 본문이 있으면 디폴트 구현이 된다. (Java 8의 `default` 키워드 불필요)

**두 인터페이스에 같은 이름의 디폴트 메서드가 있을 때**

```kotlin
interface Focusable {
    fun showOff() = println("I'm focusable!")
}

class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")
    // 두 인터페이스 모두 showOff() 디폴트 구현이 있어 직접 구현 필수
    override fun showOff() {
        super<Clickable>.showOff()   // 상위 타입 이름을 <> 안에 지정
        super<Focusable>.showOff()
    }
}
```

### open, final, abstract 변경자: 기본적으로 final

자바의 클래스와 메서드는 기본적으로 **상속 가능**하지만, 코틀린은 기본적으로 **final**이다.  
상속을 허용하려면 `open` 변경자를 붙여야 한다.

```kotlin
open class RichButton : Clickable {    // open: 상속 가능
    fun disable() {}                   // final: 하위 클래스에서 override 불가
    open fun animate() {}              // open: 하위 클래스에서 override 가능
    override fun click() {}            // override한 메서드는 기본적으로 open
    final override fun click2() {}     // override를 금지하려면 final 명시
}
```

| 변경자 | 해당 멤버 | 설명 |
|--------|-----------|------|
| `final` | override 불가 | 클래스 멤버의 기본값 |
| `open` | override 가능 | 반드시 명시해야 함 |
| `abstract` | 반드시 override | 추상 클래스에서만 사용, 구현 없음 |
| `override` | 상위 클래스/인터페이스를 override | override된 멤버는 기본적으로 open |

```kotlin
abstract class Animated {
    abstract fun animate()              // 추상 함수: 구현 없음, 반드시 override
    open fun stopAnimating() {}         // open: override 가능
    fun animateTwice() {}               // 비추상 함수: 기본 final
}
```

### 가시성 변경자: 기본적으로 public

코틀린의 기본 가시성은 `public`이다. 자바의 `package-private`는 존재하지 않는다.  
대신 `internal`이라는 새로운 가시성이 있다.

| 변경자 | 클래스 멤버 | 최상위 선언 |
|--------|-------------|-------------|
| `public` (기본) | 모든 곳에서 접근 가능 | 모든 곳에서 접근 가능 |
| `internal` | 같은 모듈 안에서만 | 같은 모듈 안에서만 |
| `protected` | 클래스와 하위 클래스 | 최상위 선언에 적용 불가 |
| `private` | 같은 클래스 안에서만 | 같은 파일 안에서만 |

> **모듈(module)**: IntelliJ IDEA 모듈, Maven/Gradle 프로젝트처럼 한꺼번에 컴파일되는 코틀린 파일의 집합

📌 자바와의 차이
* 코틀린의 `protected`는 자바와 달리 **같은 패키지**에서 접근 불가. 오직 하위 클래스만 접근 가능.
* 외부 클래스는 자신에게 속한 내부 클래스의 `private` 멤버에 접근 불가.

### 내부 클래스와 중첩된 클래스: 기본적으로 중첩 클래스

코틀린의 중첩 클래스는 **바깥쪽 클래스에 대한 참조를 저장하지 않는다** (자바 `static` 중첩 클래스와 동일).  
바깥 클래스 참조가 필요하면 `inner` 변경자를 붙여야 한다.

```kotlin
// 코틀린에서 중첩 클래스 = 자바의 static 중첩 클래스
class Outer {
    class Nested {           // 바깥 클래스 참조 없음 (Java의 static class)
        // ...
    }
    inner class Inner {      // 바깥 클래스 참조 있음 (Java의 inner class)
        fun getOuterRef(): Outer = this@Outer  // 바깥 클래스 참조 방법
    }
}
```

| 클래스 B 안에 정의된 클래스 A | Java | Kotlin |
|-------------------------------|------|--------|
| 중첩 클래스 (바깥 참조 없음) | `static class A` | `class A` |
| 내부 클래스 (바깥 참조 있음) | `class A` | `inner class A` |

### 봉인된 클래스: 클래스 계층 정의 시 계층 확장 제한

`sealed` 클래스는 하위 클래스를 제한한다. 직접 하위 클래스는 반드시 `sealed` 클래스 안에 중첩되어야 한다.

```kotlin
// sealed 없이 when 사용 시 else가 필수
interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr
fun eval(e: Expr): Int = when (e) {
    is Num -> e.value
    is Sum -> eval(e.right) + eval(e.left)
    else -> throw IllegalArgumentException("Unknown expression")  // else 필수
}

// sealed 클래스 사용 시 else 불필요
sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr) : Expr()
}
fun eval(e: Expr): Int = when (e) {
    is Expr.Num -> e.value
    is Expr.Sum -> eval(e.right) + eval(e.left)
    // else 불필요 — 모든 하위 클래스를 커버하므로
}
```

> `sealed`는 `open`을 내포한다. 별도로 `open`을 붙일 필요가 없다.  
> `when`에 새 하위 클래스를 추가하면 컴파일 오류가 발생해 빠짐없이 처리를 강제할 수 있다.

## 뻔하지 않은 생성자와 프로퍼티를 갖는 클래스 선언

### 주 생성자와 초기화 블록

코틀린은 **주 생성자**와 **부 생성자**를 구분한다.

```kotlin
// 가장 간결한 주 생성자 선언
class User(val nickname: String)

// 풀어서 쓴 형태 (위와 동일)
class User constructor(_nickname: String) {
    val nickname: String
    init {                       // 초기화 블록
        nickname = _nickname
    }
}

// 디폴트 값 지정
class User(val nickname: String, val isSubscribed: Boolean = true)

val alice = User("Alice")            // isSubscribed = true (디폴트)
val bob   = User("Bob", false)
val carol = User("Carol", isSubscribed = false)

// 상위 클래스 생성자 호출
open class User(val nickname: String)
class TwitterUser(nickname: String) : User(nickname)  // 상위 클래스 생성자 호출

// 인스턴스 생성은 new 키워드 없이
val user = User("Alice")
```

📌 주 생성자를 private으로 만들기
```kotlin
class Secretive private constructor()  // 외부에서 인스턴스 생성 불가
```

### 부 생성자

주로 자바 프레임워크와의 상호운용성 때문에 부 생성자가 필요한 경우가 있다.

```kotlin
open class View {
    constructor(ctx: Context) { /* ... */ }
    constructor(ctx: Context, attr: AttributeSet) { /* ... */ }
}

class MyButton : View {
    constructor(ctx: Context) : super(ctx)                       // 상위 클래스 생성자 위임
    constructor(ctx: Context, attr: AttributeSet) : super(ctx, attr)
}

// 자신의 다른 생성자에게 위임
class MyButton2 : View {
    constructor(ctx: Context) : this(ctx, MY_STYLE)  // 같은 클래스의 다른 생성자 위임
    constructor(ctx: Context, attr: AttributeSet) : super(ctx, attr)
}
```

> 클래스에 주 생성자가 없으면 모든 부 생성자는 반드시 상위 클래스를 초기화하거나 다른 생성자에게 위임해야 한다.

### 인터페이스에 선언된 프로퍼티 구현

```kotlin
interface User {
    val nickname: String  // 추상 프로퍼티 선언
}

// 구현 방법 1: 주 생성자 프로퍼티
class PrivateUser(override val nickname: String) : User

// 구현 방법 2: 커스텀 getter (backing field 없음, 매번 계산)
class SubscribingUser(val email: String) : User {
    override val nickname: String
        get() = email.substringBefore('@')
}

// 구현 방법 3: 프로퍼티 초기화 (backing field 있음, 한 번만 계산)
class FacebookUser(val accountId: Int) : User {
    override val nickname = getFacebookName(accountId)
}
```

### 게터와 세터에서 뒷받침하는 필드에 접근

프로퍼티의 값을 저장하면서 추가 로직을 실행하려면 `field` 식별자로 backing field에 접근한다.

```kotlin
class User(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println("Address was changed: \"$field\" -> \"$value\"")
            field = value  // field: backing field 접근
        }
}

val user = User("Alice")
user.address = "Seoul"  // setter 호출 → 로그 출력 후 저장
```

### 접근자의 가시성 변경

```kotlin
class LengthCounter {
    var counter: Int = 0
        private set  // setter를 private으로 변경 (getter는 public 유지)

    fun addWord(word: String) {
        counter += word.length
    }
}

val lc = LengthCounter()
lc.addWord("Hi!")
println(lc.counter)  // 3 — 읽기는 가능
// lc.counter = 0   // 컴파일 오류 — 외부에서 쓰기 불가
```

## 컴파일러가 생성한 메서드: 데이터 클래스와 클래스 위임

### 모든 클래스가 정의해야 하는 메서드

코틀린은 `toString`, `equals`, `hashCode`를 오버라이드해야 하는 경우를 자동으로 처리해준다.

```kotlin
// 일반 클래스: 직접 구현 필요
class Client(val name: String, val postalCode: Int) {
    override fun toString() = "Client(name=$name, postalCode=$postalCode)"
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Client) return false
        return name == other.name && postalCode == other.postalCode
    }
    override fun hashCode(): Int = name.hashCode() * 31 + postalCode
}
```

📌 코틀린의 `==` vs `===`
* `==`: `equals()`를 호출하여 **값**을 비교 (자바의 `equals()`)
* `===`: 참조 동일성 비교 (자바의 `==`)

### 데이터 클래스: 모든 클래스가 정의해야 하는 메서드 자동 생성

`data` 변경자를 붙이면 `toString`, `equals`, `hashCode`를 컴파일러가 자동 생성한다.

```kotlin
data class Client(val name: String, val postalCode: Int)
// toString, equals, hashCode 자동 생성
// 주 생성자에 선언된 프로퍼티만 고려됨

val client1 = Client("Alice", 342562)
val client2 = Client("Alice", 342562)
println(client1 == client2)  // true (equals 자동 생성)
println(client1)             // Client(name=Alice, postalCode=342562) (toString 자동 생성)

// copy(): 일부 프로퍼티만 변경하여 복사 (불변성 유지에 편리)
val bob = Client("Bob", 973293)
println(bob.copy(postalCode = 382555))  // Client(name=Bob, postalCode=382555)
```

> 데이터 클래스의 프로퍼티는 가능하면 `val`을 사용해 **불변**으로 만드는 것이 권장된다.  
> 불변 객체는 HashMap의 키 등으로 안전하게 사용할 수 있고, 다중 스레드 환경에서도 안전하다.

### 클래스 위임: by 키워드

데코레이터 패턴을 구현할 때 인터페이스 메서드를 일일이 위임하는 보일러플레이트를 `by` 키워드로 제거한다.

```kotlin
// by 키워드 없이 직접 구현 (보일러플레이트 많음)
class DelegatingCollection<T> : Collection<T> {
    private val innerList = arrayListOf<T>()
    override val size get() = innerList.size
    override fun isEmpty() = innerList.isEmpty()
    override fun iterator() = innerList.iterator()
    override fun contains(element: T) = innerList.contains(element)
    override fun containsAll(elements: Collection<T>) = innerList.containsAll(elements)
}

// by 키워드 사용: 컴파일러가 위임 메서드를 자동 생성
class DelegatingCollection<T>(
    innerList: Collection<T> = ArrayList()
) : Collection<T> by innerList  // innerList에 Collection 구현 위임

// 일부 메서드만 오버라이드하고 나머지는 위임
class CountingSet<T>(
    val innerSet: MutableCollection<T> = HashSet()
) : MutableCollection<T> by innerSet {
    var objectsAdded = 0

    override fun add(element: T): Boolean {
        objectsAdded++
        return innerSet.add(element)
    }
    override fun addAll(c: Collection<T>): Boolean {
        objectsAdded += c.size
        return innerSet.addAll(c)
    }
}

val cset = CountingSet<Int>()
cset.addAll(listOf(1, 1, 2))
println("${cset.objectsAdded} added, ${cset.size} remain")  // 3 added, 2 remain
```

## object 키워드: 클래스 선언과 인스턴스 생성

`object` 키워드는 클래스 선언과 인스턴스 생성을 동시에 한다. 세 가지 경우에 사용된다.

### 객체 선언: 싱글턴 쉽게 만들기

```kotlin
object Payroll {
    val allEmployees = arrayListOf<Person>()

    fun calculateSalary() {
        for (person in allEmployees) { /* ... */ }
    }
}

// 사용: 클래스 이름으로 직접 접근
Payroll.allEmployees.add(Person("Alice"))
Payroll.calculateSalary()

// 인터페이스나 클래스를 상속할 수 있다
object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(file1: File, file2: File): Int =
        file1.path.compareTo(file2.path, ignoreCase = true)
}

val files = listOf(File("/Z"), File("/a"))
println(files.sortedWith(CaseInsensitiveFileComparator))  // [/a, /Z]
```

> 객체 선언은 생성자를 가질 수 없다. (생성자 호출 없이 즉시 생성되기 때문)  
> 자바에서 사용 시 `CaseInsensitiveFileComparator.INSTANCE.compare(f1, f2)` 형태로 접근

### 동반 객체: 팩토리 메서드와 정적 멤버가 들어갈 장소

코틀린에는 `static` 키워드가 없다. 대신 최상위 함수나 `companion object`를 사용한다.  
`companion object`는 클래스의 `private` 멤버에 접근할 수 있다.

```kotlin
class User private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String) = User(email.substringBefore('@'))
        fun newFacebookUser(accountId: Int) = User(getFacebookName(accountId))
    }
}

// 팩토리 메서드 사용
val subscribingUser = User.newSubscribingUser("bob@gmail.com")
val facebookUser = User.newFacebookUser(4)
println(subscribingUser.nickname)  // bob
```

**동반 객체에 이름 붙이기와 인터페이스 구현**

```kotlin
class Person(val name: String) {
    companion object Loader {  // 이름 붙인 동반 객체
        fun fromJSON(jsonText: String): Person = /* ... */
    }
}

// 이름으로 접근 (이름 없으면 Companion)
Person.Loader.fromJSON("...")
Person.fromJSON("...")

// 동반 객체도 인터페이스를 구현할 수 있다
interface JSONFactory<T> {
    fun fromJSON(jsonText: String): T
}
class Person(val name: String) {
    companion object : JSONFactory<Person> {
        override fun fromJSON(jsonText: String): Person = /* ... */
    }
}
```

> 동반 객체는 자바의 `static` 멤버와 완전히 동일하지는 않다. 동반 객체는 클래스에 대한 **실제 객체 인스턴스**다.  
> `@JvmStatic` 어노테이션을 붙이면 자바에서 정적 메서드처럼 호출할 수 있다.

### 객체 식: 무명 내부 클래스를 다른 방식으로 작성

`object` 식은 자바의 무명 내부 클래스를 대신한다. 이름이 없으며, 변수에 저장하거나 직접 전달할 수 있다.

```kotlin
// 자바 무명 내부 클래스 대체
window.addMouseListener(object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) { /* ... */ }
    override fun mouseEntered(e: MouseEvent) { /* ... */ }
})

// 변수에 저장
val listener = object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) { /* ... */ }
}

// object 식은 바깥 함수의 변수를 읽고 변경할 수 있다 (자바와 달리 final 불필요)
fun countClicks(window: Window) {
    var clickCount = 0
    window.addMouseListener(object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            clickCount++  // 바깥 함수의 변수 변경 가능
        }
    })
}
```

📌 객체 선언 vs 동반 객체 vs 객체 식

| | 객체 선언 | 동반 객체 | 객체 식 |
|---|-----------|-----------|---------|
| 이름 | 있음 | 선택 | 없음 |
| 싱글턴 | O | O | X (매번 새 인스턴스) |
| 용도 | 싱글턴 패턴 | static 멤버 대체 | 무명 내부 클래스 대체 |
