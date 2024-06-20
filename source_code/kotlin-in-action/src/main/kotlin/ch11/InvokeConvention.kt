package ch11

// 11.4 invoke 관례: 객체를 함수처럼 호출

class Greeter(val greeting: String) {
    operator fun invoke(name: String) {
        println("$greeting, $name!")
    }
}

// 11.5 함수 타입 인터페이스 구현으로 복잡한 람다 분리
data class Issue(
    val id: String,
    val project: String,
    val type: String,
    val priority: String,
    val description: String
)

class ImportantIssuesPredicate(val project: String) : (Issue) -> Boolean {
    override fun invoke(issue: Issue): Boolean =
        issue.project == project && issue.isImportant()

    private fun Issue.isImportant(): Boolean =
        type == "Bug" && (priority == "Major" || priority == "Critical")
}

// 11.6 DSL에서 invoke 활용: 단일 호출과 블록 구조 모두 지원
class DependencyHandler {
    private val dependencies = mutableListOf<String>()

    fun compile(coordinate: String) {
        dependencies.add(coordinate)
        println("Added dependency on $coordinate")
    }

    operator fun invoke(body: DependencyHandler.() -> Unit) {
        body()
    }

    fun list() = dependencies
}

fun main() {
    // invoke 관례
    val greeter = Greeter("Hello")
    greeter("Kotlin")       // greeter.invoke("Kotlin") → Hello, Kotlin!
    greeter("World")        // Hello, World!

    // 함수 타입 구현 클래스
    val i1 = Issue("IDEA-1", "IDEA", "Bug", "Major", "Save failed")
    val i2 = Issue("KT-1", "Kotlin", "Feature", "Normal", "Convert")
    val i3 = Issue("IDEA-2", "IDEA", "Bug", "Critical", "Data loss")

    val predicate = ImportantIssuesPredicate("IDEA")
    val important = listOf(i1, i2, i3).filter(predicate)
    println(important.map { it.id })  // [IDEA-1, IDEA-2]

    // DSL에서 invoke: 단일 호출
    val deps = DependencyHandler()
    deps.compile("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")

    // DSL에서 invoke: 블록 구조
    deps {
        compile("org.jetbrains.kotlin:kotlin-reflect:1.0.0")
        compile("junit:junit:4.11")
    }

    println("All dependencies: ${deps.list()}")
}
