## 10장, 애노테이션과 리플렉션

* [애노테이션 선언과 적용](#애노테이션-선언과-적용)
* [리플렉션: 실행 시점에 코틀린 객체 내부 관찰](#리플렉션-실행-시점에-코틀린-객체-내부-관찰)

> 애노테이션과 리플렉션을 사용하면 미리 알 수 없는 임의의 클래스를 다루는 코드를 작성할 수 있다.

## 애노테이션 선언과 적용

### 애노테이션 적용

```kotlin
// @애노테이션이름 형태로 선언 앞에 붙임
import org.junit.*

class MyTest {
    @Test fun testTrue() {
        Assert.assertTrue(true)
    }
}

// @Deprecated: replaceWith로 대체 패턴 제공
@Deprecated("Use removeAt(index) instead.", ReplaceWith("removeAt(index)"))
fun remove(index: Int) { /* ... */ }

// 애노테이션 인자: 원시 타입, 문자열, enum, 클래스 참조, 다른 애노테이션, 배열
@MyAnnotation(MyClass::class)            // 클래스 참조: ::class
@MyAnnotation(arrayOf(1, 2, 3))         // 배열
const val TEST = "test"                  // const val만 애노테이션 인자로 사용 가능
```

### 애노테이션 대상(target)

```kotlin
// 코틀린 선언 하나가 여러 자바 요소에 대응할 수 있으므로 대상을 명시
class HasTempFolder {
    @get:Rule  // getter에 애노테이션 적용
    val folder = TemporaryFolder()

    @Test
    fun testUsingTempFolder() {
        val file = folder.newFile("myFile.txt")
    }
}

// 사용 지점 대상(use-site target) 목록
// property, field, get, set, receiver, param, setparam, delegate, file
```

### 애노테이션 선언

```kotlin
// annotation class 키워드
annotation class JsonName(val name: String)

// 자바 @interface와 동일 역할
// 파라미터: val 프로퍼티만 가능

// 메타 애노테이션: 애노테이션 클래스에 적용되는 애노테이션
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude

// @Retention: SOURCE, BINARY, RUNTIME (기본: RUNTIME)
```

### JSON 직렬화 커스터마이징 예시

```kotlin
data class Person(
    @JsonName("alias") val firstName: String,  // JSON 키를 "alias"로
    @JsonExclude val age: Int? = null          // 직렬화 제외
)
```

## 리플렉션: 실행 시점에 코틀린 객체 내부 관찰

### KClass, KCallable, KFunction, KProperty

```kotlin
import kotlin.reflect.full.*

// KClass: 클래스 표현
val kClass = MyClass::class          // 코틀린 KClass
val jClass = MyClass::class.java     // 자바 Class

// 런타임 객체의 KClass
val obj = "hello"
val kc = obj.javaClass.kotlin        // KClass<String>
println(kc.simpleName)               // String
println(kc.memberProperties.map { it.name })  // 프로퍼티 목록

// KCallable: 함수와 프로퍼티의 공통 상위 인터페이스 → call()로 호출
fun foo(x: Int) = println(x)
val kFunc = ::foo
kFunc.call(42)  // 42

// KFunction: 파라미터 타입 안전 호출
import kotlin.reflect.KFunction2
val sum: KFunction2<Int, Int, Int> = ::sum2
println(sum.invoke(1, 2))  // 3
fun sum2(a: Int, b: Int) = a + b

// KProperty: 프로퍼티 접근
var counter = 0
val kProp = ::counter
kProp.setter.call(21)
println(kProp.get())  // 21

// 멤버 프로퍼티
data class PersonR(val name: String, val age: Int)
val memberProp = PersonR::age
println(memberProp.get(PersonR("Alice", 29)))  // 29
```

### 리플렉션을 이용한 직렬화

```kotlin
// 객체를 JSON으로 직렬화하는 간단한 구현
fun serialize(obj: Any): String = buildString { serializeObject(obj) }

private fun StringBuilder.serializeObject(obj: Any) {
    val kClass = obj.javaClass.kotlin
    val properties = kClass.memberProperties

    properties.joinToString(prefix = "{", postfix = "}") { prop ->
        val jsonName = prop.findAnnotation<JsonName>()?.name ?: prop.name
        val value = prop.get(obj)
        "\"$jsonName\": ${serialize(value!!)}"
    }.also { append(it) }
}

// 애노테이션으로 커스터마이징
data class PersonJSON(
    @JsonName("first_name") val firstName: String,
    @JsonExclude val privateInfo: String? = null
)
```

📌 리플렉션 API 주요 타입

| 타입 | 역할 |
|------|------|
| `KClass<T>` | 클래스 표현, `memberProperties`, `memberFunctions` 등 |
| `KCallable<R>` | 함수/프로퍼티 공통 인터페이스, `call()` 제공 |
| `KFunction<R>` | 함수 표현, 타입 안전 `invoke()` |
| `KProperty<V>` | 프로퍼티, `get()` / `set()` |
| `KParameter` | 파라미터 정보, `callBy()` 사용 시 활용 |

### callBy로 객체 생성

```kotlin
// callBy: 파라미터 이름으로 지정, 기본값 있는 파라미터 생략 가능
data class PersonDefault(val name: String, val age: Int = 0)

val constructor = PersonDefault::class.primaryConstructor!!
val params = constructor.parameters

val args = mapOf(
    params[0] to "Alice"  // age는 기본값 사용
)
println(constructor.callBy(args))  // PersonDefault(name=Alice, age=0)
```
