package com.example.d2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.d2.data.Score
import com.example.d2.data.Student
import com.google.firebase.database.*

class RegisterStudentScoreActivity : AppCompatActivity() {

    private lateinit var spStudent: Spinner
    private lateinit var spGrade: Spinner
    private lateinit var spSubject: Spinner
    private lateinit var etFinalScore: EditText
    private lateinit var btnRegisterScore: Button
    private lateinit var btnBack: Button

    private lateinit var database: DatabaseReference
    private val studentList = mutableListOf<Student>()
    private val studentNames = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_student_score)

        spStudent = findViewById(R.id.spStudent)
        spGrade = findViewById(R.id.spGrade)
        spSubject = findViewById(R.id.spSubject)
        etFinalScore = findViewById(R.id.etFinalScore)
        btnRegisterScore = findViewById(R.id.btnRegisterScore)
        btnBack = findViewById(R.id.btnBack)

        database = FirebaseDatabase.getInstance().reference

        loadStudents()

        val grades = listOf("6° Grado", "7° Grado", "8° Grado", "9° Grado", "Bachillerato")
        spGrade.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, grades)

        val subjects = listOf("Matemáticas", "Lenguaje", "Ciencias", "Sociales", "Inglés", "DMS")
        spSubject.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjects)

        btnRegisterScore.setOnClickListener {
            registerScore()
        }
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadStudents() {
        val studentsRef = database.child("students")
        studentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                studentNames.clear()

                for (studentSnapshot in snapshot.children) {
                    val student = studentSnapshot.getValue(Student::class.java)
                    student?.let {
                        it.key = studentSnapshot.key
                        studentList.add(it)
                        studentNames.add("${it.name} ${it.lastName}")
                    }
                }

                val adapter = ArrayAdapter(this@RegisterStudentScoreActivity,
                    android.R.layout.simple_spinner_dropdown_item, studentNames)
                spStudent.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegisterStudentScoreActivity, "Error cargando estudiantes", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registerScore() {
        val selectedStudentIndex = spStudent.selectedItemPosition
        if (selectedStudentIndex == -1 || studentList.isEmpty()) {
            Toast.makeText(this, "Seleccione un estudiante", Toast.LENGTH_SHORT).show()
            return
        }

        val student = studentList[selectedStudentIndex]
        val grade = spGrade.selectedItem.toString()
        val subject = spSubject.selectedItem.toString()
        val finalScoreText = etFinalScore.text.toString()

        if (finalScoreText.isEmpty()) {
            Toast.makeText(this, "Ingrese la nota", Toast.LENGTH_SHORT).show()
            return
        }

        val finalScore = finalScoreText.toDoubleOrNull()
        if (finalScore == null || finalScore < 0 || finalScore > 10) {
            Toast.makeText(this, "La nota debe estar entre 0 y 10", Toast.LENGTH_SHORT).show()
            return
        }

        val scoresRef = database.child("scores")

        scoresRef.orderByChild("studentId").equalTo(student.key).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var existingKey: String? = null

                for (child in snapshot.children) {
                    val score = child.getValue(Score::class.java)
                    if (score != null && score.grade == grade && score.subject == subject) {
                        existingKey = child.key
                        break
                    }
                }

                if (existingKey != null) {
                    scoresRef.child(existingKey).child("finalScore").setValue(finalScore)
                        .addOnSuccessListener {
                            Toast.makeText(this@RegisterStudentScoreActivity, "Nota actualizada", Toast.LENGTH_SHORT).show()
                            etFinalScore.text.clear()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@RegisterStudentScoreActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    val scoreId = scoresRef.push().key
                    val score = Score(
                        studentId = student.key,
                        grade = grade,
                        subject = subject,
                        finalScore = finalScore,
                        key = scoreId
                    )

                    if (scoreId != null) {
                        scoresRef.child(scoreId).setValue(score)
                            .addOnSuccessListener {
                                Toast.makeText(this@RegisterStudentScoreActivity, "Nota registrada", Toast.LENGTH_SHORT).show()
                                etFinalScore.text.clear()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@RegisterStudentScoreActivity, "Error al registrar", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegisterStudentScoreActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
