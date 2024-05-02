package ch08

// 8.9 고차 함수 안에서의 흐름 제어

data class PersonCF(val name: String, val age: Int)

val people = listOf(PersonCF("Alice", 29), PersonCF("Bob", 31))

// 비지역 return: 람다에서 return → 바깥 함수(lookForAlice)에서 반환
fun lookForAlice(people: List<PersonCF>) {
    people.forEach {
        if (it.name == "Alice") {
            println("Found!")
            return  // forEach가 inline이므로 비지역 return 가능
        }
    }
    println("Alice is not found")
}

// 레이블 지정 return: 람다 자체에서만 반환
fun lookForAliceLabel(people: List<PersonCF>) {
    people.forEach label@{
        if (it.name == "Alice") return@label
    }
    println("Alice might be somewhere")  // 항상 출력
}

// 함수 이름을 레이블로 사용
fun lookForAliceForEach(people: List<PersonCF>) {
    people.forEach {
        if (it.name == "Alice") return@forEach
    }
    println("Alice might be somewhere")
}

// 무명 함수: return은 무명 함수 자체에서 반환 (지역 return)
fun lookForAliceAnon(people: List<PersonCF>) {
    people.forEach(fun(person) {
        if (person.name == "Alice") return  // 무명 함수에서만 반환
        println("${person.name} is not Alice")
    })
}

fun main() {
    // 비지역 return
    lookForAlice(people)       // Found!

    // 레이블 return
    lookForAliceLabel(people)  // Alice might be somewhere
    lookForAliceForEach(people) // Alice might be somewhere

    // 무명 함수
    lookForAliceAnon(people)   // Bob is not Alice

    // 표현식 본문 무명 함수
    val under30 = people.filter(fun(person) = person.age < 30)
    println(under30)           // [PersonCF(name=Alice, age=29)]
}
