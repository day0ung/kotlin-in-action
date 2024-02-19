package ch06

// 6.1 널 가능성

fun strLen(s: String) = s.length
fun strLenSafe(s: String?): Int = s?.length ?: 0

// 6.2 안전한 호출 ?. 과 엘비스 ?:
class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

fun Person.countryName() = company?.address?.country ?: "Unknown"

fun printShippingLabel(person: Person) {
    val address = person.company?.address
        ?: throw IllegalArgumentException("No address")
    with(address) {
        println(streetAddress)
        println("$zipCode $city, $country")
    }
}

// 6.3 안전한 캐스트 as?
class PersonEq(val firstName: String, val lastName: String) {
    override fun equals(other: Any?): Boolean {
        val otherPerson = other as? PersonEq ?: return false
        return otherPerson.firstName == firstName && otherPerson.lastName == lastName
    }
    override fun hashCode() = firstName.hashCode() * 37 + lastName.hashCode()
}

// 6.4 let 함수
fun sendEmailTo(email: String) = println("Sending email to $email")

// 6.5 lateinit
class MyService {
    fun performAction(): String = "foo"
}

class MyTest {
    private lateinit var myService: MyService  // non-null 타입, 나중에 초기화

    fun setUp() {
        myService = MyService()
    }

    fun testAction(): String = myService.performAction()
}

// 6.6 nullable 타입 확장 함수
fun String?.isNullOrBlankCustom(): Boolean = this == null || this.isBlank()

// 6.7 타입 파라미터의 널 가능성
fun <T> printHashCode(t: T) = println(t?.hashCode())         // T는 기본 nullable
fun <T : Any> printHashCodeNonNull(t: T) = println(t.hashCode())  // non-null 제한

fun main() {
    println(strLen("abc"))      // 3
    println(strLenSafe(null))   // 0
    println(strLenSafe("abc"))  // 3

    val person = Person("Dmitry", Company("JetBrains",
        Address("Elsestr. 47", 80687, "Munich", "Germany")))
    println(person.countryName())         // Germany
    println(Person("No company", null).countryName())  // Unknown

    printShippingLabel(person)
    // Elsestr. 47
    // 80687 Munich, Germany

    val p1 = PersonEq("Alice", "Smith")
    println(p1 == PersonEq("Alice", "Smith"))  // true
    println(p1.equals(42))                     // false

    // let
    var email: String? = "kotlin@example.com"
    email?.let { sendEmailTo(it) }  // Sending email to kotlin@example.com
    email = null
    email?.let { sendEmailTo(it) }  // 실행 안 됨

    // lateinit
    val test = MyTest()
    test.setUp()
    println(test.testAction())  // foo

    // nullable 확장 함수
    println("  ".isNullOrBlankCustom())  // true
    val s: String? = null
    println(s.isNullOrBlankCustom())     // true

    // 타입 파라미터
    printHashCode(null)  // null
    printHashCodeNonNull(42)  // 42의 hashCode
}
