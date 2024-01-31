package ch02
import  ch02.Color.*


// 2.14 enum 상수 값을 import enum 클래스 수식자 없이 enum 사용하기
fun getWarmth(color: Color) = when(color){
    RED, ORANGE, YELLO -> "warm"
    GREEN -> "neutral"
    BLUE -> "cold"
}

// 2.15 when의 분기 조건에 여러 다른 객체 사용하기
fun mix(c1: Color, c2: Color) =
    when (setOf(c1, c2)){  //setOf : Kotlin에서 고유한 요소로 이뤄진 불변의 새로운 Set을 만드는 함수
        setOf(RED, YELLO) -> ORANGE
        else -> throw Exception("Dirty color")
    }

//2.16 인자가 없는 when
fun mixOptimized(c1: Color, c2: Color) =
    when {
        (c1 ==RED && c2 ==YELLO) || (c1 ==YELLO && c2 == RED) -> ORANGE
        (c1 ==BLUE && c2 ==YELLO) || (c1 ==YELLO && c2 == BLUE) -> GREEN
        else -> throw Exception("Dirty color")
    }

