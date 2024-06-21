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

| 챕터 | 정리 노트 | 소스 코드 |
|------|-----------|-----------|
| 1장. 코틀린이란 무엇이며, 왜 필요한가? | [📝 notes](<./contents/Chapter01.md>) | [Chapter02.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch02/Chapter02.kt>) |
| 2장. 코틀린 기초 | [📝 notes](<./contents/Chapter02 - 코틀린 기초.md>) | [Color.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch02/Color.kt>) |
| 3장. 함수 정의와 호출 | [📝 notes](<./contents/Chapter03 - 함수 정의와 호출.md>) | [Collections.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/Collections.kt>) · [Extensions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/Extensions.kt>) · [JoinToString.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/JoinToString.kt>) · [LocalFunctions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/LocalFunctions.kt>) · [StringsAndRegex.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/StringsAndRegex.kt>) · [VarargAndInfix.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch03/VarargAndInfix.kt>) |
| 4장. 클래스, 객체, 인터페이스 | [📝 notes](<./contents/Chapter04 - 클래스, 객체, 인터페이스.md>) | [Interfaces.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch04/Interfaces.kt>) · [Constructors.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch04/Constructors.kt>) · [DataClassAndDelegation.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch04/DataClassAndDelegation.kt>) · [ObjectKeyword.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch04/ObjectKeyword.kt>) |
| 5장. 람다로 프로그래밍 | [📝 notes](<./contents/Chapter05 - 람다로 프로그래밍.md>) | [Lambdas.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch05/Lambdas.kt>) · [CollectionFunctions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch05/CollectionFunctions.kt>) · [Sequences.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch05/Sequences.kt>) · [WithAndApply.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch05/WithAndApply.kt>) |
| 6장. 코틀린 타입 시스템 | [📝 notes](<./contents/Chapter06 - 코틀린 타입 시스템.md>) | [Nullability.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch06/Nullability.kt>) · [PrimitiveTypes.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch06/PrimitiveTypes.kt>) · [Collections.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch06/Collections.kt>) |
| 7장. 연산자 오버로딩과 기타 관례 | [📝 notes](<./contents/Chapter07 - 연산자 오버로딩과 기타 관례.md>) | [ArithmeticOperators.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch07/ArithmeticOperators.kt>) · [ComparisonOperators.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch07/ComparisonOperators.kt>) · [CollectionConventions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch07/CollectionConventions.kt>) · [DestructuringAndDelegation.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch07/DestructuringAndDelegation.kt>) |
| 8장. 고차 함수 | [📝 notes](<./contents/Chapter08 - 고차 함수: 파라미터와 반환 값으로 람다 사용.md>) | [HigherOrderFunctions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch08/HigherOrderFunctions.kt>) · [InlineFunctions.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch08/InlineFunctions.kt>) · [ControlFlow.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch08/ControlFlow.kt>) |
| 9장. 제네릭스 | [📝 notes](<./contents/Chapter09 - 제네릭스.md>) | [Generics.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch09/Generics.kt>) · [TypeErasureAndReified.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch09/TypeErasureAndReified.kt>) · [Variance.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch09/Variance.kt>) |
| 10장. 애노테이션과 리플렉션 | [📝 notes](<./contents/Chapter10 - 애노테이션과 리플렉션.md>) | [Annotations.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch10/Annotations.kt>) · [Reflection.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch10/Reflection.kt>) |
| 11장. DSL 만들기 | [📝 notes](<./contents/Chapter11 - DSL 만들기.md>) | [LambdaWithReceiver.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch11/LambdaWithReceiver.kt>) · [HtmlBuilder.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch11/HtmlBuilder.kt>) · [InvokeConvention.kt](<./source_code/kotlin-in-action/src/main/kotlin/ch11/InvokeConvention.kt>) |

---

### 소스 코드 구조

```
source_code/kotlin-in-action/src/main/kotlin/
├── ch02/   # 2장 코틀린 기초
├── ch03/   # 3장 함수 정의와 호출
├── ch04/   # 4장 클래스, 객체, 인터페이스
├── ch05/   # 5장 람다로 프로그래밍
├── ch06/   # 6장 코틀린 타입 시스템
├── ch07/   # 7장 연산자 오버로딩과 기타 관례
├── ch08/   # 8장 고차 함수
├── ch09/   # 9장 제네릭스
├── ch10/   # 10장 애노테이션과 리플렉션
└── ch11/   # 11장 DSL 만들기
```
