package ch10

// 10.1 애노테이션 선언
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude

@Target(AnnotationTarget.PROPERTY)
annotation class JsonName(val name: String)

// 10.2 애노테이션 적용
data class PersonJSON(
    @JsonName("first_name") val firstName: String,
    @JsonExclude val age: Int? = null
)

// 10.3 애노테이션 선언: 파라미터 있는 애노테이션
annotation class DeserializeInterface(val targetClass: kotlin.reflect.KClass<out Any>)

// 10.4 메타 애노테이션
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class BindingAnnotation

@BindingAnnotation
annotation class MyBinding

// 10.5 @Deprecated 활용
@Deprecated("Use newFunction() instead.", ReplaceWith("newFunction()"))
fun oldFunction() = println("old")

fun newFunction() = println("new")

fun main() {
    // 애노테이션이 붙은 data class 사용
    val person = PersonJSON("Alice")
    println(person)  // PersonJSON(firstName=Alice, age=null)

    // 리플렉션으로 애노테이션 읽기
    val prop = PersonJSON::firstName
    val jsonName = prop.annotations.filterIsInstance<JsonName>().firstOrNull()
    println(jsonName?.name)  // first_name

    val ageProp = PersonJSON::age
    val excluded = ageProp.annotations.filterIsInstance<JsonExclude>().isNotEmpty()
    println("age excluded: $excluded")  // age excluded: true

    oldFunction()   // old (deprecated)
    newFunction()   // new
}
