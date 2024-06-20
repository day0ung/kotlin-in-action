## 11장, DSL 만들기

* [API에서 DSL로](#api에서-dsl로)
* [구조화된 API 구축: DSL에서 수신 객체 지정 람다 사용](#구조화된-api-구축-dsl에서-수신-객체-지정-람다-사용)
* [invoke 관례를 사용한 유연한 블록 중첩](#invoke-관례를-사용한-유연한-블록-중첩)

> **내부 DSL(internal DSL)**: 범용 언어(코틀린)의 문법을 그대로 사용해 특정 도메인을 위한 API를 제공하는 방식.  
> 타입 안전성을 유지하면서 선언적이고 읽기 좋은 코드를 작성할 수 있다.

## API에서 DSL로

### 코틀린 DSL을 깔끔하게 만드는 기능

| 일반 구문 | 간결한 구문 | 사용 기능 |
|-----------|------------|-----------|
| `StringUtil.capitalize(s)` | `s.capitalize()` | 확장 함수 |
| `1.to("one")` | `1 to "one"` | infix 호출 |
| `set.add(2)` | `set += 2` | 연산자 오버로딩 |
| `map.get("key")` | `map["key"]` | get 관례 |
| `file.use({ f -> f.read() })` | `file.use { it.read() }` | 괄호 밖 람다 |
| `sb.append("yes"); sb.append("no")` | `with(sb) { append("yes"); append("no") }` | 수신 객체 지정 람다 |

### DSL 구조의 핵심: 람다 중첩과 메서드 체인

```kotlin
// HTML DSL 예시: 중첩 람다로 구조 표현
fun createSimpleTable() = createHTML().table {
    tr {
        td { +"cell" }
    }
}
// 결과: <table><tr><td>cell</td></tr></table>

// Gradle 의존성 DSL: 람다 중첩으로 컨텍스트 공유
dependencies {
    compile("junit:junit:4.11")
    compile("com.google.inject:guice:4.1.0")
}

// 테스트 DSL: 메서드 체인으로 구조 표현
str should startWith("kot")
```

## 구조화된 API 구축: DSL에서 수신 객체 지정 람다 사용

### 수신 객체 지정 람다와 확장 함수 타입

```kotlin
// 일반 람다: StringBuilder를 인자로 전달
fun buildString1(builderAction: (StringBuilder) -> Unit): String {
    val sb = StringBuilder()
    builderAction(sb)
    return sb.toString()
}
val s1 = buildString1 { it.append("Hello, "); it.append("World!") }

// 수신 객체 지정 람다: StringBuilder를 수신 객체로 사용
fun buildString2(builderAction: StringBuilder.() -> Unit): String {
    val sb = StringBuilder()
    sb.builderAction()
    return sb.toString()
}
val s2 = buildString2 {
    append("Hello, ")   // this.append 대신 바로 호출
    append("World!")
}
println(s2)  // Hello, World!

// 표준 라이브러리 구현 (apply 활용)
fun buildString(builderAction: StringBuilder.() -> Unit): String =
    StringBuilder().apply(builderAction).toString()

// 확장 함수 타입 변수에 저장
val appendExcl: StringBuilder.() -> Unit = { this.append("!") }
val sb = StringBuilder("Hi")
sb.appendExcl()
println(sb)  // Hi!
```

### apply와 with 구현 원리

```kotlin
// apply: 수신 객체를 람다에 전달 후 수신 객체 반환
inline fun <T> T.apply(block: T.() -> Unit): T {
    block()          // this.block()과 동일
    return this
}

// with: 수신 객체를 첫 번째 인자로, 람다 결과 반환
inline fun <T, R> with(receiver: T, block: T.() -> R): R =
    receiver.block()

val map = mutableMapOf(1 to "one")
map.apply { this[2] = "two" }
with(map) { this[3] = "three" }
println(map)  // {1=one, 2=two, 3=three}
```

### HTML 빌더 구현

```kotlin
// 태그 클래스: 중첩 태그를 children에 저장
open class Tag(val name: String) {
    private val children = mutableListOf<Tag>()

    protected fun <T : Tag> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children.add(child)
    }

    override fun toString() =
        "<$name>${children.joinToString("")}</$name>"
}

fun table(init: TABLE.() -> Unit) = TABLE().apply(init)

class TABLE : Tag("table") {
    fun tr(init: TR.() -> Unit) = doInit(TR(), init)
}
class TR : Tag("tr") {
    fun td(init: TD.() -> Unit) = doInit(TD(), init)
}
class TD : Tag("td")

fun createTable() = table {
    tr { td { } }
}
println(createTable())  // <table><tr><td></td></tr></table>

// 동적 태그 생성
fun createAnotherTable() = table {
    for (i in 1..2) {
        tr { td { } }
    }
}
println(createAnotherTable())
// <table><tr><td></td></tr><tr><td></td></tr></table>
```

> `@DslMarker` 애노테이션으로 중첩 람다에서 외부 수신 객체 접근을 제한할 수 있다 (Kotlin 1.1+).

## invoke 관례를 사용한 유연한 블록 중첩

### invoke 관례: 객체를 함수처럼 호출

```kotlin
// operator fun invoke 정의 시 객체를 함수처럼 호출 가능
class Greeter(val greeting: String) {
    operator fun invoke(name: String) {
        println("$greeting, $name!")
    }
}

val greeter = Greeter("Hello")
greeter("Kotlin")  // greeter.invoke("Kotlin") 호출 → Hello, Kotlin!
```

### 함수 타입과 invoke 관례

```kotlin
// 람다는 FunctionN 인터페이스를 구현한 클래스로 컴파일됨
// invoke 메서드를 직접 구현해 복잡한 람다를 클래스로 분리 가능
data class Issue(
    val id: String, val project: String, val type: String,
    val priority: String, val description: String
)

class ImportantIssuesPredicate(val project: String) : (Issue) -> Boolean {
    override fun invoke(issue: Issue): Boolean {
        return issue.project == project && issue.isImportant()
    }

    private fun Issue.isImportant(): Boolean =
        type == "Bug" && (priority == "Major" || priority == "Critical")
}

val predicate = ImportantIssuesPredicate("IDEA")
val issues = listOf(
    Issue("IDEA-1", "IDEA", "Bug", "Major", "Save failed"),
    Issue("KT-1", "Kotlin", "Feature", "Normal", "Convert calls")
)
println(issues.filter(predicate).map { it.id })  // [IDEA-1]
```

### DSL에서 invoke 활용: Gradle 의존성

```kotlin
// invoke를 정의해 단일 호출과 블록 구조 모두 지원
class DependencyHandler {
    fun compile(coordinate: String) {
        println("Added dependency on $coordinate")
    }

    operator fun invoke(body: DependencyHandler.() -> Unit) {
        body()
    }
}

val dependencies = DependencyHandler()

// 단일 호출
dependencies.compile("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")

// 블록 구조 (invoke 관례)
dependencies {
    compile("org.jetbrains.kotlin:kotlin-reflect:1.0.0")
}
```

📌 DSL 구축 기법 정리

| 기법 | 역할 | 예 |
|------|------|---|
| 확장 함수 | 기존 타입에 도메인 함수 추가 | `UL.item(href, name)` |
| 수신 객체 지정 람다 | 블록 내 컨텍스트 설정 | `table { tr { td {} } }` |
| `operator fun invoke` | 객체를 함수처럼 호출 | `dependencies { … }` |
| infix 함수 | 메서드 체인 가독성 향상 | `str should startWith("kot")` |
| 연산자 오버로딩 | 간결한 DSL 문법 | `1.days.ago` |
