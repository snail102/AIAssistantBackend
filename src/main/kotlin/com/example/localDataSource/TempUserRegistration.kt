package com.example.localDataSource

object TempUserRegistration {

    private val users: MutableMap<String,UserTemp> = mutableMapOf()


    fun getUserByLogin(login: String): UserTemp? {
        return users.remove(login)
    }

    fun addNewUser(userTemp: UserTemp) {
        users[userTemp.login] = userTemp
    }
}