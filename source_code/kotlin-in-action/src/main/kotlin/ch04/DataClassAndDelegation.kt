package ch04

// 4.11 일반 클래스 — toString, equals, hashCode 직접 구현
class ClientV1(val name: String, val postalCode: Int) {
    override fun toString() = "Client(name=$name, postalCode=$postalCode)"
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is ClientV1) return false
        return name == other.name && postalCode == other.postalCode
    }
    override fun hashCode(): Int = name.hashCode() * 31 + postalCode
}

// 4.12 data class — 컴파일러가 toString, equals, hashCode, copy 자동 생성
data class Client(val name: String, val postalCode: Int)

// 4.13 클래스 위임: by 키워드
class CountingSet<T>(
    val innerSet: MutableCollection<T> = HashSet()
) : MutableCollection<T> by innerSet {  // innerSet에 MutableCollection 구현 위임
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

fun main() {
    // 일반 클래스
    val c1 = ClientV1("Alice", 342562)
    val c2 = ClientV1("Alice", 342562)
    println(c1 == c2)  // true

    // data class
    val d1 = Client("Alice", 342562)
    val d2 = Client("Alice", 342562)
    println(d1 == d2)  // true
    println(d1)        // Client(name=Alice, postalCode=342562)

    val bob = Client("Bob", 973293)
    println(bob.copy(postalCode = 382555))  // Client(name=Bob, postalCode=382555)

    // 클래스 위임
    val cset = CountingSet<Int>()
    cset.addAll(listOf(1, 1, 2))
    println("${cset.objectsAdded} added, ${cset.size} remain")  // 3 added, 2 remain
}
