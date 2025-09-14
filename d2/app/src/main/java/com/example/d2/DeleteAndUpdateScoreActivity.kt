package com.example.d2

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.d2.data.Score
import com.example.d2.data.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeleteAndUpdateScoreActivity : AppCompatActivity() {

    private lateinit var spinnerStudents: Spinner
    private lateinit var spinnerScores: Spinner
    private lateinit var etGrade: EditText
    private lateinit var etSubject: EditText
    private lateinit var etFinalScore: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnBack: Button

    private val dbStudents = FirebaseDatabase.getInstance().getReference("students")
    private val dbScores = FirebaseDatabase.getInstance().getReference("scores")

    private val studentList = mutableListOf<Student>()
    private val studentNames = mutableListOf<String>()
    private val studentKeys = mutableListOf<String>()

    private val scoreList = mutableListOf<Score>()
    private val scoreLabels = mutableListOf<String>()
    private val scoreKeys = mutableListOf<String>()

    private var selectedStudentId: String? = null
    private var selectedScoreKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_and_update_score)

        spinnerStudents = findViewById(R.id.spinnerStudents)
        spinnerScores = findViewById(R.id.spinnerScores)
        etGrade = findViewById(R.id.etGrade)
        etSubject = findViewById(R.id.etSubject)
        etFinalScore = findViewById(R.id.etFinalScore)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack = findViewById(R.id.btnBack)

        loadStudents()

        spinnerStudents.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedStudentId = studentKeys[position]
                loadScoresForStudent(selectedStudentId!!)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerScores.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedScoreKey = scoreKeys[position]
                val score = scoreList[position]

                etGrade.setText(score.grade)
                etSubject.setText(score.subject)
                etFinalScore.setText(score.finalScore?.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSave.setOnClickListener {
            selectedScoreKey?.let { key ->
                val updatedScore = Score(
                    studentId = selectedStudentId,
                    grade = etGrade.text.toString(),
                    subject = etSubject.text.toString(),
                    finalScore = etFinalScore.text.toString().toDoubleOrNull(),
                    key = key
                )

                dbScores.child(key).updateChildren(updatedScore.toMap())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnDelete.setOnClickListener {
            selectedScoreKey?.let { key ->
                dbScores.child(key).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Nota eliminada", Toast.LENGTH_SHORT).show()
                        selectedStudentId?.let { loadScoresForStudent(it) }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun loadStudents() {
        dbStudents.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                studentNames.clear()
                studentKeys.clear()

                for (child in snapshot.children) {
                    val student = child.getValue(Student::class.java)
                    student?.let {
                        studentList.add(it)
                        studentKeys.add(child.key!!)
                        studentNames.add("${it.name} ${it.lastName}")
                    }
                }

                val adapter = ArrayAdapter(
                    this@DeleteAndUpdateScoreActivity,
                    android.R.layout.simple_spinner_item,
                    studentNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerStudents.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DeleteAndUpdateScoreActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadScoresForStudent(studentId: String) {
        dbScores.orderByChild("studentId").equalTo(studentId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scoreList.clear()
                    scoreLabels.clear()
                    scoreKeys.clear()

                    for (child in snapshot.children) {
                        val score = child.getValue(Score::class.java)
                        score?.let {
                            scoreList.add(it)
                            scoreKeys.add(child.key!!)
                            scoreLabels.add("${it.subject} - ${it.finalScore}")
                        }
                    }

                    val adapter = ArrayAdapter(
                        this@DeleteAndUpdateScoreActivity,
                        android.R.layout.simple_spinner_item,
                        scoreLabels
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerScores.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DeleteAndUpdateScoreActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
