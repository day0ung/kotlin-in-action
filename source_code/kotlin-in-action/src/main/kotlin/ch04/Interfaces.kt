package ch04

// 4.1 인터페이스 선언과 구현
interface Clickable {
    fun click()
    fun showOff() = println("I'm clickable!")  // 디폴트 구현
}

interface Focusable {
    fun setFocus(b: Boolean) = println("I ${if (b) "got" else "lost"} focus.")
    fun showOff() = println("I'm focusable!")
}

// 두 인터페이스 모두 구현 — showOff는 직접 구현 필수
class Button : Clickable, Focusable {
    override fun click() = println("I was clicked")

    override fun showOff() {
        super<Clickable>.showOff()   // 각 상위 타입의 구현 호출
        super<Focusable>.showOff()
    }
}

// 4.2 open, final, abstract
open class RichButton : Clickable {
    fun disable() {}                   // final
    open fun animate() {}              // 하위 클래스에서 override 가능
    override fun click() {}            // override한 메서드는 기본 open
    final override fun showOff() = super<Clickable>.showOff()  // 더 이상 override 불가
}

abstract class Animated {
    abstract fun animate()
    open fun stopAnimating() {}
    fun animateTwice() {}
}

// 4.3 sealed 클래스
sealed class Expr {
    class Num(val value: Int) : Expr()
    class Sum(val left: Expr, val right: Expr) : Expr()
}

fun eval(e: Expr): Int = when (e) {
    is Expr.Num -> e.value
    is Expr.Sum -> eval(e.right) + eval(e.left)
    // else 불필요
}

fun main() {
    val button = Button()
    button.showOff()       // I'm clickable! \n I'm focusable!
    button.setFocus(true)  // I got focus.
    button.click()         // I was clicked

    println(eval(Expr.Sum(Expr.Num(1), Expr.Num(2))))  // 3
}
