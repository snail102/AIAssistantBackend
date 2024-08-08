package com.example.localDataSource

object TempUserRegistration {

    private val users: MutableMap<String, UserTemp> = mutableMapOf()


    fun getUserByLogin(login: String): UserTemp? {
        return users.remove(login)
    }

    fun replaceCode(login: String, newCode: String): UserTemp? {
        val userTemp = users[login]
        userTemp?.let {
            users[login] = it.copy(code = newCode)
        }
        return userTemp
    }

    fun addNewUser(userTemp: UserTemp) {
        users[userTemp.login] = userTemp
    }
}