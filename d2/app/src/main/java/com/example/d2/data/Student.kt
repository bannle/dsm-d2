package com.example.d2.data

class Student(
    var name: String? = null,
    var lastName: String? = null,
    var age: Int? = null,
    var address: String? = null,
    var phone: String? = null,
    var key: String? = null,
    var per: MutableMap<String, Boolean> = mutableMapOf()
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "lastName" to lastName,
            "age" to age,
            "address" to address,
            "phone" to phone,
            "per" to per
        )
    }
}
