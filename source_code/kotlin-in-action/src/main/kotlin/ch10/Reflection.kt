package ch10

import kotlin.reflect.KFunction2
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

// 10.6 KClass: 클래스 표현
fun exploreKClass() {
    val kClass = String::class
    println(kClass.simpleName)        // String
    println(kClass.memberProperties.map { it.name })  // [length]

    val obj = "hello"
    val runtimeClass = obj.javaClass.kotlin
    println(runtimeClass.simpleName)  // String
}

// 10.7 KCallable: 함수와 프로퍼티 공통 인터페이스
fun sum(a: Int, b: Int) = a + b

fun exploreKFunction() {
    val kFunc = ::sum
    kFunc.call(1, 2).also { println(it) }  // 3

    val typedFunc: KFunction2<Int, Int, Int> = ::sum
    println(typedFunc.invoke(3, 4))  // 7
}

// 10.8 KProperty: 프로퍼티 리플렉션
var counter = 0

fun exploreKProperty() {
    val kProp = ::counter
    kProp.setter.call(21)
    println(kProp.get())  // 21

    // 멤버 프로퍼티
    data class PersonR(val name: String, val age: Int)
    val memberProp = PersonR::age
    println(memberProp.get(PersonR("Alice", 29)))  // 29
}

// 10.9 간단한 직렬화 구현
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExcludeR

@Target(AnnotationTarget.PROPERTY)
annotation class JsonNameR(val name: String)

data class PersonSerial(
    @JsonNameR("first_name") val firstName: String,
    @JsonExcludeR val privateNote: String? = null,
    val age: Int = 0
)

fun serializeSimple(obj: Any): String {
    val kClass = obj.javaClass.kotlin
    val props = kClass.memberProperties
        .filter { it.findAnnotation<JsonExcludeR>() == null }

    return props.joinToString(prefix = "{", postfix = "}") { prop ->
        val jsonKey = prop.findAnnotation<JsonNameR>()?.name ?: prop.name
        val value = prop.get(obj)
        val jsonValue = when (value) {
            is String -> "\"$value\""
            null -> "null"
            else -> value.toString()
        }
        "\"$jsonKey\": $jsonValue"
    }
}

// 10.10 callBy로 기본값 사용하며 객체 생성
data class PersonDefault(val name: String, val age: Int = 0)

fun createWithCallBy() {
    val constructor = PersonDefault::class.primaryConstructor!!
    val params = constructor.parameters
    val args = mapOf(params[0] to "Alice")  // age는 기본값 사용
    println(constructor.callBy(args))        // PersonDefault(name=Alice, age=0)
}

fun main() {
    exploreKClass()
    exploreKFunction()
    exploreKProperty()

    // 직렬화
    val person = PersonSerial("Alice", "secret", 29)
    println(serializeSimple(person))
    // {"first_name": "Alice", "age": 29}  (privateNote 제외)

    createWithCallBy()
}
