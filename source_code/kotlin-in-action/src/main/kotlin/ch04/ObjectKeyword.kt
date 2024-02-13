package ch04

import java.io.File
import java.util.Comparator

// 4.14 객체 선언 — 싱글턴
object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(file1: File, file2: File): Int =
        file1.path.compareTo(file2.path, ignoreCase = true)
}

// 4.15 클래스 안의 객체 선언
data class Person(val name: String) {
    object NameComparator : Comparator<Person> {
        override fun compare(p1: Person, p2: Person): Int =
            p1.name.compareTo(p2.name)
    }
}

// 4.16 동반 객체 (companion object) — private 생성자 + 팩토리 메서드
class User private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String) = User(email.substringBefore('@'))
        fun newSocialUser(accountId: Int) = User("social_$accountId")
    }
}

// 4.17 이름 붙인 동반 객체 + 인터페이스 구현
interface JSONFactory<T> {
    fun fromJSON(jsonText: String): T
}

class UserWithFactory(val nickname: String) {
    companion object Loader : JSONFactory<UserWithFactory> {
        override fun fromJSON(jsonText: String): UserWithFactory =
            UserWithFactory(jsonText.trim('"'))  // 단순 예시
    }
}

// 4.18 동반 객체 확장 함수
class UserExtendable(val firstName: String, val lastName: String) {
    companion object  // 이름 없는 동반 객체
}

fun UserExtendable.Companion.fromFullName(fullName: String): UserExtendable {
    val parts = fullName.split(" ")
    return UserExtendable(parts[0], parts.getOrElse(1) { "" })
}

fun main() {
    // 객체 선언
    val files = listOf(File("/Z"), File("/a"))
    println(files.sortedWith(CaseInsensitiveFileComparator))  // [/a, /Z]

    // 클래스 안 객체
    val persons = listOf(Person("Bob"), Person("Alice"))
    println(persons.sortedWith(Person.NameComparator))  // [Person(name=Alice), Person(name=Bob)]

    // 동반 객체 — 팩토리 메서드
    val subscribing = User.newSubscribingUser("bob@gmail.com")
    println(subscribing.nickname)  // bob

    // 동반 객체 — 인터페이스 구현
    val user = UserWithFactory.fromJSON("\"kotlin\"")
    println(user.nickname)  // kotlin

    // 동반 객체 확장
    val u = UserExtendable.fromFullName("John Doe")
    println("${u.firstName} ${u.lastName}")  // John Doe

    // 객체 식 (무명 객체)
    val comparator = object : Comparator<String> {
        override fun compare(s1: String, s2: String) = s1.length - s2.length
    }
    val words = listOf("banana", "apple", "kiwi")
    println(words.sortedWith(comparator))  // [kiwi, apple, banana]
}
