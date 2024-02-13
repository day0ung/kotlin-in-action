package ch04

// 4.4 주 생성자
class User1(val nickname: String)  // 가장 간결한 형태

class User2(val nickname: String, val isSubscribed: Boolean = true)  // 디폴트 값

// 4.5 초기화 블록
class User3 constructor(_nickname: String) {
    val nickname: String
    init {
        nickname = _nickname
    }
}

// 4.6 상위 클래스 생성자 호출
open class BaseUser(val nickname: String)
class TwitterUser(nickname: String) : BaseUser(nickname)

// 4.7 private 생성자
class Secretive private constructor()

// 4.8 인터페이스 프로퍼티 구현
interface UserInterface {
    val nickname: String
}

class PrivateUser(override val nickname: String) : UserInterface  // 주 생성자 프로퍼티

class SubscribingUser(val email: String) : UserInterface {
    override val nickname: String
        get() = email.substringBefore('@')  // 커스텀 getter, 매번 계산
}

class FacebookUser(val accountId: Int) : UserInterface {
    override val nickname = "fb_user_$accountId"  // 초기화 시 한 번만 계산
}

// 4.9 backing field 접근 (field 식별자)
class LoggingUser(val name: String) {
    var address: String = "unspecified"
        set(value: String) {
            println("Address changed for $name: \"$field\" -> \"$value\"")
            field = value
        }
}

// 4.10 접근자 가시성 변경
class LengthCounter {
    var counter: Int = 0
        private set  // 외부에서 쓰기 불가

    fun addWord(word: String) {
        counter += word.length
    }
}

fun main() {
    val alice = User2("Alice")
    println("${alice.nickname}, subscribed: ${alice.isSubscribed}")  // Alice, subscribed: true

    println(PrivateUser("test@kotlin.org").nickname)    // test@kotlin.org
    println(SubscribingUser("test@kotlin.org").nickname) // test
    println(FacebookUser(4).nickname)                   // fb_user_4

    val user = LoggingUser("Alice")
    user.address = "Seoul"
    // Address changed for Alice: "unspecified" -> "Seoul"

    val lc = LengthCounter()
    lc.addWord("Hi!")
    println(lc.counter)  // 3
}
