# 📖 KotlinInAction
---

#### How to study
* 매주 수요일 (14시)
    * 한시간 가량 일주일동안 정해진 분량을 발표
    * (본인이 재미있었던 부분에 대한 3가지 이상)


### 예제 코드 - New Project
* 소스코드는 프로젝트를 생성하여 '/source_code' 폴더안에 작성

---

## 목차

| 챕터 | 정리  | 소스 코드 |
|------|---------|-----------|
| 1장. 코틀린이란 무엇이며, 왜 필요한가? | [notes](<./contents/Chapter01.md>) | - |
| 2장. 코틀린 기초 | [notes](<./contents/Chapter02 - 코틀린 기초.md>) | 2.1 기본 요소 · 함수와 변수: [Chapter02.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch02/Chapter02.kt>) <br> 2.2 enum과 when: [Color.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch02/Color.kt>) |
| 3장. 함수 정의와 호출 | [notes](<./contents/Chapter03 - 함수 정의와 호출.md>) | 3.1 컬렉션 만들기: [Collections.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/Collections.kt>) <br> 3.2 함수 호출 · joinToString: [JoinToString.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/JoinToString.kt>) <br> 3.3 확장 함수와 프로퍼티: [Extensions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/Extensions.kt>) <br> 3.4 vararg · infix: [VarargAndInfix.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/VarargAndInfix.kt>) <br> 3.5 문자열과 정규식: [StringsAndRegex.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/StringsAndRegex.kt>) <br> 3.6 로컬 함수: [LocalFunctions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/LocalFunctions.kt>) |
| 4장. 클래스, 객체, 인터페이스 | [notes](<./contents/Chapter04 - 클래스, 객체, 인터페이스.md>) | 4.1 인터페이스 · sealed: [Interfaces.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch04/Interfaces.kt>) <br> 4.2 생성자 · 프로퍼티: [Constructors.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch04/Constructors.kt>) <br> 4.3 데이터 클래스 · 위임: [DataClassAndDelegation.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch04/DataClassAndDelegation.kt>) <br> 4.4 object 키워드: [ObjectKeyword.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch04/ObjectKeyword.kt>) |
| 5장. 람다로 프로그래밍 | [notes](<./contents/Chapter05 - 람다로 프로그래밍.md>) | 5.1 람다 식 · 멤버 참조: [Lambdas.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch05/Lambdas.kt>) <br> 5.2 컬렉션 함수형 API: [CollectionFunctions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch05/CollectionFunctions.kt>) <br> 5.3 지연 계산 · 시퀀스: [Sequences.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch05/Sequences.kt>) <br> 5.4 with · apply: [WithAndApply.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch05/WithAndApply.kt>) |
| 6장. 코틀린 타입 시스템 | [notes](<./contents/Chapter06 - 코틀린 타입 시스템.md>) | 6.1 널 가능성: [Nullability.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch06/Nullability.kt>) <br> 6.2 원시 타입 · Any · Unit · Nothing: [PrimitiveTypes.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch06/PrimitiveTypes.kt>) <br> 6.3 컬렉션과 배열: [Collections.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch06/Collections.kt>) |
| 7장. 연산자 오버로딩과 기타 관례 | [notes](<./contents/Chapter07 - 연산자 오버로딩과 기타 관례.md>) | 7.1 산술 연산자: [ArithmeticOperators.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch07/ArithmeticOperators.kt>) <br> 7.2 비교 연산자: [ComparisonOperators.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch07/ComparisonOperators.kt>) <br> 7.3 컬렉션 · 범위 관례: [CollectionConventions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch07/CollectionConventions.kt>) <br> 7.4 구조 분해 · 위임 프로퍼티: [DestructuringAndDelegation.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch07/DestructuringAndDelegation.kt>) |
| 8장. 고차 함수 | [notes](<./contents/Chapter08 - 고차 함수: 파라미터와 반환 값으로 람다 사용.md>) | 8.1 고차 함수 선언: [HigherOrderFunctions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch08/HigherOrderFunctions.kt>) <br> 8.2 인라인 함수: [InlineFunctions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch08/InlineFunctions.kt>) <br> 8.3 흐름 제어 · return: [ControlFlow.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch08/ControlFlow.kt>) |
| 9장. 제네릭스 | [notes](<./contents/Chapter09 - 제네릭스.md>) | 9.1 제네릭 타입 파라미터: [Generics.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch09/Generics.kt>) <br> 9.2 타입 소거 · reified: [TypeErasureAndReified.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch09/TypeErasureAndReified.kt>) <br> 9.3 변성 · in · out: [Variance.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch09/Variance.kt>) |
| 10장. 애노테이션과 리플렉션 | [notes](<./contents/Chapter10 - 애노테이션과 리플렉션.md>) | 10.1 애노테이션 선언 · 적용: [Annotations.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch10/Annotations.kt>) <br> 10.2 리플렉션 · KClass · KProperty: [Reflection.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch10/Reflection.kt>) |
| 11장. DSL 만들기 | [notes](<./contents/Chapter11 - DSL 만들기.md>) | 11.1 수신 객체 지정 람다: [LambdaWithReceiver.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch11/LambdaWithReceiver.kt>) <br> 11.2 HTML 빌더: [HtmlBuilder.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch11/HtmlBuilder.kt>) <br> 11.3 invoke 관례: [InvokeConvention.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch11/InvokeConvention.kt>) |
