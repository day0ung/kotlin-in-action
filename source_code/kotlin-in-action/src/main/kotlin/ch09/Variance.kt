package ch09

// 9.6 공변성(covariance): out
interface Producer<out T> {
    fun produce(): T
}

open class Animal { open fun feed() = println("Feeding animal") }
class Cat : Animal() {
    fun cleanLitter() = println("Cleaning litter")
    override fun feed() = println("Feeding cat")
}

class Herd<out T : Animal>(private val animals: List<T>) {
    val size get() = animals.size
    operator fun get(i: Int): T = animals[i]
}

fun feedAll(animals: Herd<Animal>) {
    for (i in 0 until animals.size) animals[i].feed()
}

fun takeCareOfCats(cats: Herd<Cat>) {
    for (i in 0 until cats.size) cats[i].cleanLitter()
    feedAll(cats)  // Herd가 covariant이므로 가능
}

// 9.7 반공변성(contravariance): in
class AnimalComparator : Comparator<Animal> {
    override fun compare(o1: Animal, o2: Animal): Int = 0
}

// 9.8 사용 지점 변성(use-site variance)
fun <T> copyData(source: MutableList<out T>, destination: MutableList<T>) {
    for (item in source) destination.add(item)
}

fun <T> copyDataIn(source: MutableList<T>, destination: MutableList<in T>) {
    for (item in source) destination.add(item)
}

// 9.9 스타 프로젝션
fun printFirst(list: List<*>) {
    if (list.isNotEmpty()) println(list.first())
}

// 타입 안전 컨테이너 패턴
interface FieldValidator<in T> {
    fun validate(input: T): Boolean
}

object DefaultStringValidator : FieldValidator<String> {
    override fun validate(input: String) = input.isNotEmpty()
}

object DefaultIntValidator : FieldValidator<Int> {
    override fun validate(input: Int) = input >= 0
}

fun main() {
    // 공변성
    val cats = Herd(listOf(Cat(), Cat()))
    takeCareOfCats(cats)

    // 반공변성
    val strings = listOf("b", "a", "c")
    val anyComparator = Comparator<Any> { e1, e2 ->
        e1.hashCode() - e2.hashCode()
    }
    println(strings.sortedWith(anyComparator))

    // 사용 지점 변성
    val ints = mutableListOf(1, 2, 3)
    val anyItems = mutableListOf<Any>()
    copyData(ints, anyItems)
    println(anyItems)  // [1, 2, 3]

    // 스타 프로젝션
    printFirst(listOf("Svetlana", "Dmitry"))  // Svetlana

    // FieldValidator
    println(DefaultStringValidator.validate("Kotlin"))  // true
    println(DefaultStringValidator.validate(""))        // false
    println(DefaultIntValidator.validate(42))           // true
    println(DefaultIntValidator.validate(-1))           // false
}
