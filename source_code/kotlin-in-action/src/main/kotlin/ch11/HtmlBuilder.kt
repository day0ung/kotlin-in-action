package ch11

// 11.3 HTML 빌더: 수신 객체 지정 람다로 타입 안전 DSL 구현

open class Tag(val name: String) {
    private val children = mutableListOf<Tag>()

    protected fun <T : Tag> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children.add(child)
    }

    override fun toString() = "<$name>${children.joinToString("")}</$name>"
}

fun table(init: TABLE.() -> Unit) = TABLE().apply(init)

class TABLE : Tag("table") {
    fun tr(init: TR.() -> Unit) = doInit(TR(), init)
}

class TR : Tag("tr") {
    fun td(init: TD.() -> Unit) = doInit(TD(), init)
}

class TD : Tag("td")

// 텍스트 콘텐츠를 지원하는 확장 Tag
open class TagWithText(name: String) : Tag(name) {
    operator fun String.unaryPlus() {
        // 실제 구현에서는 children에 TextNode 추가
        println("Adding text: $this")
    }
}

fun main() {
    // 기본 HTML 빌더
    val table = table {
        tr { td { } }
    }
    println(table)  // <table><tr><td></td></tr></table>

    // 동적 태그 생성
    val dynamicTable = table {
        for (i in 1..2) {
            tr { td { } }
        }
    }
    println(dynamicTable)
    // <table><tr><td></td></tr><tr><td></td></tr></table>

    // 중첩 구조 예시
    val nested = table {
        tr {
            td { }
            td { }
        }
        tr {
            td { }
        }
    }
    println(nested)
    // <table><tr><td></td><td></td></tr><tr><td></td></tr></table>
}
