package com.example.d2.data

class Score (
    var studentId: String? = null,
    var grade: String? = null,
    var subject: String? = null,
    var finalScore: Double? = null,
    var key: String? = null
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "studentId" to studentId,
            "grade" to grade,
            "subject" to subject,
            "finalScore" to finalScore
        )
    }

}