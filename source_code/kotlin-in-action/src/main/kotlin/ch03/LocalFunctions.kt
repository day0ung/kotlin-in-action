package ch03

class User(val id: Int, val name: String, val address: String)

// 3.12 코드 중복이 있는 초기 버전
fun saveUserV1(user: User) {
    if (user.name.isEmpty()) {
        throw IllegalArgumentException("Can't save user ${user.id}: empty Name")
    }
    if (user.address.isEmpty()) {
        throw IllegalArgumentException("Can't save user ${user.id}: empty Address")
    }
    // user를 데이터베이스에 저장...
    println("User ${user.id} saved")
}

// 3.13 로컬 함수로 코드 중복 제거
fun saveUserV2(user: User) {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            // 로컬 함수에서 바깥 함수의 파라미터에 직접 접근 가능
            throw IllegalArgumentException("Can't save user ${user.id}: empty $fieldName")
        }
    }

    validate(user.name, "Name")
    validate(user.address, "Address")
    println("User ${user.id} saved")
}

// 3.14 확장 함수로 분리하여 더 개선
fun User.validateBeforeSave() {
    fun validate(value: String, fieldName: String) {
        if (value.isEmpty()) {
            throw IllegalArgumentException("Can't save user $id: empty $fieldName")
        }
    }
    validate(name, "Name")
    validate(address, "Address")
}

fun saveUser(user: User) {
    user.validateBeforeSave()
    println("User ${user.id} saved")
}

fun main() {
    val validUser = User(1, "Alice", "Seoul")
    val invalidUser = User(2, "", "Busan")

    saveUser(validUser)  // User 1 saved

    try {
        saveUser(invalidUser)
    } catch (e: IllegalArgumentException) {
        println(e.message)  // Can't save user 2: empty Name
    }
}
