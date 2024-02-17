## 5장, 람다로 프로그래밍

* [람다 식과 멤버 참조](#람다-식과-멤버-참조)
* [컬렉션 함수형 API](#컬렉션-함수형-api)
* [지연 계산 컬렉션 연산: 시퀀스](#지연-계산-컬렉션-연산-시퀀스)
* [자바 함수형 인터페이스 활용](#자바-함수형-인터페이스-활용)
* [수신 객체 지정 람다: with와 apply](#수신-객체-지정-람다-with와-apply)

## 람다 식과 멤버 참조

### 람다 소개: 코드 블록을 함수 인자로

람다식(lambda expression)을 사용하면 함수를 값처럼 다룰 수 있다.  
익명 클래스를 사용하는 자바 코드 대신, 람다로 훨씬 간결하게 동작을 전달할 수 있다.

```kotlin
// Java 익명 클래스 방식
button.setOnClickListener(object : OnClickListener {
    override fun onClick(view: View) { /* 클릭 시 동작 */ }
})

// 코틀린 람다 방식
button.setOnClickListener { /* 클릭 시 동작 */ }
```

### 람다와 컬렉션

람다를 활용하면 컬렉션 처리를 라이브러리 함수로 간결하게 표현할 수 있다.

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(Person("Alice", 29), Person("Bob", 31))

// 직접 구현 (장황함)
fun findTheOldest(people: List<Person>): Person? {
    var maxAge = 0
    var theOldest: Person? = null
    for (person in people) {
        if (person.age > maxAge) { maxAge = person.age; theOldest = person }
    }
    return theOldest
}

// 람다 활용 (간결함)
println(people.maxBy { it.age })         // Person(name=Bob, age=31)
println(people.maxBy(Person::age))       // 멤버 참조로 더 간결하게
```

### 람다 식의 문법

```kotlin
// 기본 람다 문법: { 파라미터 -> 본문 }
val sum = { x: Int, y: Int -> x + y }
println(sum(1, 2))  // 3

// 람다를 직접 호출
{ println(42) }()

// run으로 람다 실행 (가장 명확)
run { println(42) }
```

📌 람다 문법 간소화 단계

```kotlin
val people = listOf(Person("Alice", 29), Person("Bob", 31))

// 1단계: 기본형 (람다가 유일한 인자면 괄호 밖으로)
people.maxBy({ p: Person -> p.age })

// 2단계: 괄호 밖으로 이동
people.maxBy() { p: Person -> p.age }

// 3단계: 빈 괄호 제거
people.maxBy { p: Person -> p.age }

// 4단계: 타입 추론으로 타입 생략
people.maxBy { p -> p.age }

// 5단계: it 사용 (인자가 하나일 때 자동 생성)
people.maxBy { it.age }
```

> 람다가 여러 줄인 경우 마지막 식이 결과값이 된다.

```kotlin
val sum = { x: Int, y: Int ->
    println("Computing sum of $x and $y")
    x + y  // 마지막 식이 반환값
}
println(sum(1, 2))  // Computing sum of 1 and 2 \n 3
```

### 현재 영역에 있는 변수에 접근 (클로저)

람다를 함수 안에서 정의하면 함수의 파라미터 및 로컬 변수에 접근할 수 있다.

```kotlin
fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
    messages.forEach {
        println("$prefix $it")  // 바깥 함수의 파라미터 prefix에 접근
    }
}

// 코틀린은 자바와 달리 람다 안에서 final이 아닌 변수도 변경 가능
fun printProblemCounts(responses: Collection<String>) {
    var clientErrors = 0
    var serverErrors = 0
    responses.forEach {
        if (it.startsWith("4")) clientErrors++      // 람다 바깥 변수 변경
        else if (it.startsWith("5")) serverErrors++
    }
    println("$clientErrors client errors, $serverErrors server errors")
}

val responses = listOf("200 OK", "418 I'm a teapot", "500 Internal Server Error")
printProblemCounts(responses)  // 1 client errors, 1 server errors
```

📌 람다가 캡처한 변수(closure)
* 람다가 변수를 캡처하면, 람다 코드와 함께 해당 변수의 값(또는 래퍼)이 저장된다.
* 람다가 이벤트 핸들러나 비동기로 실행된다면, 함수 반환 후에 변수 수정이 일어남을 주의해야 한다.

### 멤버 참조

이미 정의된 함수를 값으로 넘기고 싶을 때 `::` 연산자로 멤버 참조를 사용한다.

```kotlin
// 람다와 멤버 참조 (동일한 의미)
people.maxBy { it.age }
people.maxBy(Person::age)       // 멤버 참조

// 프로퍼티 참조
val getAge = Person::age

// 최상위 함수 참조
fun salute() = println("Salute!")
run(::salute)

// 생성자 참조
data class Person(val name: String, val age: Int)
val createPerson = ::Person
val p = createPerson("Alice", 29)

// 확장 함수 참조
fun Person.isAdult() = age >= 21
val predicate = Person::isAdult
```

## 컬렉션 함수형 API

### filter와 map

```kotlin
val list = listOf(1, 2, 3, 4)
val people = listOf(Person("Alice", 29), Person("Bob", 31))

// filter: 조건을 만족하는 원소만 추출
println(list.filter { it % 2 == 0 })              // [2, 4]
println(people.filter { it.age > 30 })             // [Person(name=Bob, age=31)]

// map: 각 원소를 변환
println(list.map { it * it })                      // [1, 4, 9, 16]
println(people.map { it.name })                    // [Alice, Bob]
println(people.map(Person::name))                  // 멤버 참조로

// 체이닝
people.filter { it.age > 30 }.map(Person::name)    // [Bob]

// 주의: 람다 안에서 불필요한 반복 계산을 피해야 한다
val maxAge = people.maxBy(Person::age)!!.age
people.filter { it.age == maxAge }                 // maxAge를 한 번만 계산
```

### all, any, count, find

```kotlin
val canBeInClub27 = { p: Person -> p.age <= 27 }
val people = listOf(Person("Alice", 27), Person("Bob", 31))

println(people.all(canBeInClub27))    // false — 모두 만족하는지
println(people.any(canBeInClub27))    // true  — 하나라도 만족하는지
println(people.count(canBeInClub27))  // 1     — 만족하는 원소 개수
println(people.find(canBeInClub27))   // Person(name=Alice, age=27) — 첫 번째 만족 원소

// !all vs any: 가독성을 위해 부정 기호를 피하는 쪽을 선택
println(!people.all { it.age == 31 })  // true
println(people.any { it.age != 31 })   // true (더 읽기 쉬움)
```

📌 `count` vs `filter { }.size`
> `filter { }.size`는 조건 만족 원소를 담는 중간 컬렉션을 생성한다.  
> `count`는 개수만 추적하므로 더 효율적이다.

### groupBy: 리스트를 맵으로 변환

```kotlin
val people = listOf(
    Person("Alice", 31), Person("Bob", 29), Person("Carol", 31)
)

println(people.groupBy { it.age })
// {31=[Person(name=Alice...), Person(name=Carol...)], 29=[Person(name=Bob...)]}
// 반환 타입: Map<Int, List<Person>>

// 문자열 첫 글자로 그룹화
val list = listOf("a", "ab", "b")
println(list.groupBy(String::first))  // {a=[a, ab], b=[b]}
```

### flatMap과 flatten

```kotlin
data class Book(val title: String, val authors: List<String>)

val books = listOf(
    Book("Thursday Next", listOf("Jasper Fforde")),
    Book("Good Omens", listOf("Terry Pratchett", "Neil Gaiman"))
)

// flatMap: 변환 후 펼치기
println(books.flatMap { it.authors }.toSet())
// [Jasper Fforde, Terry Pratchett, Neil Gaiman]

val strings = listOf("abc", "def")
println(strings.flatMap { it.toList() })  // [a, b, c, d, e, f]

// flatten: 중첩 컬렉션을 단순히 펼치기
val listOfLists = listOf(listOf(1, 2), listOf(3, 4))
println(listOfLists.flatten())  // [1, 2, 3, 4]
```

## 지연 계산 컬렉션 연산: 시퀀스

컬렉션 함수(`filter`, `map` 등)는 **즉시(eagerly)** 중간 컬렉션을 만든다.  
시퀀스(Sequence)를 사용하면 **지연(lazily)** 계산으로 중간 컬렉션 생성을 피할 수 있다.

```kotlin
val people = listOf(Person("Alice", 29), Person("Bob", 31))

// 즉시 계산: 중간 리스트 두 개 생성
people.map(Person::name).filter { it.startsWith("A") }

// 지연 계산: 중간 컬렉션 없음
people.asSequence()
    .map(Person::name)
    .filter { it.startsWith("A") }
    .toList()              // 최종 연산: 이 시점에 실제 계산 수행
```

### 중간 연산과 최종 연산

```kotlin
// 중간 연산: 다른 시퀀스를 반환, 지연 실행
// 최종 연산: 결과를 반환, 실제 계산 수행

listOf(1, 2, 3, 4).asSequence()
    .map { print("map($it) "); it * it }
    .filter { print("filter($it) "); it % 2 == 0 }
    // 최종 연산 없으면 아무것도 출력 안 됨

listOf(1, 2, 3, 4).asSequence()
    .map { it * it }
    .filter { it % 2 == 0 }
    .toList()
// 원소별로 순서대로 처리: map(1) filter(1) map(2) filter(4) ...

// 원소를 하나씩 처리하므로, find와 함께 사용 시 불필요한 연산 생략
println(listOf(1, 2, 3, 4).asSequence().map { it * it }.find { it > 3 })
// 1 처리 → map(1)=1, 1>3? No
// 2 처리 → map(2)=4, 4>3? Yes → 결과 4 반환 (3, 4는 처리 안 함)
```

📌 연산 순서가 성능에 영향을 미친다
```kotlin
// map 먼저: 모든 원소 변환 후 필터
people.asSequence().map(Person::name).filter { it.length < 4 }.toList()

// filter 먼저: 짧은 이름인 사람만 변환 → 더 효율적
people.asSequence().filter { it.name.length < 4 }.map(Person::name).toList()
```

### 시퀀스 만들기

```kotlin
// generateSequence: 이전 원소로 다음 원소를 계산
val naturalNumbers = generateSequence(0) { it + 1 }
val numbersTo100 = naturalNumbers.takeWhile { it <= 100 }
println(numbersTo100.sum())  // 5050 (최종 연산 시 계산)

// 부모 디렉터리 시퀀스
fun File.isInsideHiddenDirectory() =
    generateSequence(this) { it.parentFile }.any { it.isHidden }
```

> 시퀀스는 원소가 많을 때 유리하다. 원소가 적으면 일반 컬렉션이 더 단순하다.

## 자바 함수형 인터페이스 활용

**함수형 인터페이스(SAM 인터페이스)**: 추상 메서드가 하나인 인터페이스 (예: `Runnable`, `Callable`)

코틀린에서는 SAM 인터페이스를 기대하는 자바 메서드에 람다를 넘길 수 있다.

```kotlin
// 자바 메서드
// void postponeComputation(int delay, Runnable computation)

// 람다를 Runnable로 자동 변환
postponeComputation(1000) { println(42) }

// 람다가 변수를 캡처하지 않으면 한 인스턴스 재사용
val runnable = Runnable { println(42) }  // 전역 인스턴스

// 변수를 캡처하면 호출마다 새 인스턴스 생성
fun handleComputation(id: String) {
    postponeComputation(1000) { println(id) }  // id 캡처 → 매번 새 객체
}
```

### SAM 생성자: 람다를 함수형 인터페이스로 명시적 변환

```kotlin
// 컴파일러가 자동 변환을 못 할 때 SAM 생성자를 직접 사용
fun createAllDoneRunnable(): Runnable {
    return Runnable { println("All done!") }  // SAM 생성자로 명시적 변환
}

// 리스너 인스턴스를 변수에 저장해서 재사용
val listener = OnClickListener { view ->
    val text = when (view.id) {
        R.id.button1 -> "First button"
        R.id.button2 -> "Second button"
        else -> "Unknown button"
    }
    toast(text)
}
button1.setOnClickListener(listener)
button2.setOnClickListener(listener)
```

> 람다 안에는 `this`가 없다. 이벤트 리스너 등록 해제 시에는 `object` 식을 사용해야 한다.

## 수신 객체 지정 람다: with와 apply

**수신 객체 지정 람다**: 람다 본문에서 `this` 없이 다른 객체의 메서드를 바로 호출할 수 있는 람다

### with 함수

같은 객체에 여러 메서드를 반복 호출할 때 객체 이름을 생략할 수 있다.

```kotlin
// with 없이
fun alphabet(): String {
    val result = StringBuilder()
    for (letter in 'A'..'Z') {
        result.append(letter)
    }
    result.append("\nNow I know the alphabet!")
    return result.toString()
}

// with 사용 (수신 객체: stringBuilder)
fun alphabet(): String {
    val stringBuilder = StringBuilder()
    return with(stringBuilder) {
        for (letter in 'A'..'Z') {
            this.append(letter)     // this로 명시적 호출
        }
        append("\nNow I know the alphabet!")  // this 생략 가능
        this.toString()
    }
}

// 더 간결하게
fun alphabet() = with(StringBuilder()) {
    for (letter in 'A'..'Z') { append(letter) }
    append("\nNow I know the alphabet!")
    toString()  // with의 반환값 = 람다의 마지막 식
}
```

> `with`는 두 개의 인자를 받는 함수: `with(receiver, lambda)`  
> 람다 본문에서 `this`는 첫 번째 인자(수신 객체)를 가리킨다.

### apply 함수

`with`와 거의 같지만, **항상 수신 객체를 반환**한다. 객체 초기화에 주로 사용된다.

```kotlin
// apply: 수신 객체를 그대로 반환
fun alphabet() = StringBuilder().apply {
    for (letter in 'A'..'Z') { append(letter) }
    append("\nNow I know the alphabet!")
}.toString()  // apply가 StringBuilder를 반환하므로 toString() 가능

// TextView 초기화 예시 (Android)
val textView = TextView(context).apply {
    text = "Hello"
    textSize = 20.0f
    setPadding(10, 0, 0, 0)
}

// buildString: with + StringBuilder를 감싼 표준 라이브러리 함수
fun alphabet() = buildString {
    for (letter in 'A'..'Z') { append(letter) }
    append("\nNow I know the alphabet!")
}
```

📌 with vs apply 비교

| | `with` | `apply` |
|---|--------|---------|
| 형태 | `with(obj) { ... }` | `obj.apply { ... }` |
| 반환값 | 람다의 마지막 식 | 수신 객체 자신 |
| 주 용도 | 결과값이 필요한 연산 | 객체 초기화/설정 |
