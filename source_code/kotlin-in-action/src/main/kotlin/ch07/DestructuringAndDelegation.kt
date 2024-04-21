package ch07

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

// 7.10 구조 분해 선언
data class PointD(val x: Int, val y: Int)

// data class 아닌 경우 직접 정의
class NameComponents(val name: String, val extension: String) {
    operator fun component1() = name
    operator fun component2() = extension
}

fun splitFilename(fullName: String): NameComponents {
    val (name, extension) = fullName.split('.', limit = 2)
    return NameComponents(name, extension)
}

fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}

// 7.11 위임 프로퍼티
class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        println("${property.name} getValue")
        return "delegated value"
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("${property.name} setValue = $value")
    }
}

class Foo {
    var p: String by Delegate()
}

// 7.12 by lazy: 지연 초기화
fun loadEmails(person: PersonLazy): List<String> {
    println("Loading emails for ${person.name}")
    return listOf("${person.name}@example.com")
}

class PersonLazy(val name: String) {
    val emails by lazy { loadEmails(this) }
}

// 7.13 Delegates.observable: 변경 통지
class PersonObs(val name: String, age: Int, salary: Int) {
    var age: Int by Delegates.observable(age) { prop, old, new ->
        println("${prop.name}: $old → $new")
    }
    var salary: Int by Delegates.observable(salary) { prop, old, new ->
        println("${prop.name}: $old → $new")
    }
}

// 7.14 맵에 프로퍼티 값 저장
class PersonMap {
    private val _attributes = hashMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    val name: String by _attributes
}

fun main() {
    // 구조 분해 기본
    val p = PointD(10, 20)
    val (x, y) = p
    println("x=$x, y=$y")  // x=10, y=20

    // 여러 값 반환
    val (name, ext) = splitFilename("example.kt")
    println(name)  // example
    println(ext)   // kt

    // Map 순회
    val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
    printEntries(map)
    // Oracle -> Java
    // JetBrains -> Kotlin

    // withIndex
    val list = listOf("a", "b", "c")
    for ((index, element) in list.withIndex()) {
        println("$index: $element")
    }

    println("---")

    // 위임 프로퍼티
    val foo = Foo()
    println(foo.p)
    foo.p = "new value"

    println("---")

    // lazy
    val alice = PersonLazy("Alice")
    println(alice.emails)  // Loading emails... → [Alice@example.com]
    println(alice.emails)  // 캐시 사용, 로딩 없음

    println("---")

    // observable
    val obs = PersonObs("Alice", 29, 3000)
    obs.age = 30      // age: 29 → 30
    obs.salary = 3500 // salary: 3000 → 3500

    println("---")

    // 맵 위임
    val pm = PersonMap()
    pm.setAttribute("name", "Dmitry")
    println(pm.name)  // Dmitry
}
