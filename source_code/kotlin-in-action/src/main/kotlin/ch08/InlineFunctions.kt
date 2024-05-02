package ch08

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

// 8.6 인라인 함수 기본
inline fun <T> synchronizedCustom(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

// 8.7 noinline: 특정 파라미터만 인라이닝 제외
inline fun inlineExample(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    inlined()
    notInlined()
}

// 8.8 자원 관리: use 함수 (표준 라이브러리의 Closeable 확장)
fun readFirstLineFromFile(path: String): String {
    return java.io.BufferedReader(java.io.FileReader(path)).use { br ->
        br.readLine()
    }
}

fun main() {
    val lock: Lock = ReentrantLock()

    // inline 함수: 람다 본문이 호출 위치에 직접 삽입됨
    synchronizedCustom(lock) {
        println("Critical section")
    }

    // noinline 예시
    inlineExample(
        inlined = { println("inlined lambda") },
        notInlined = { println("not inlined lambda") }
    )

    // withLock (표준 라이브러리)
    lock.withLock {
        println("Under lock")
    }
}
