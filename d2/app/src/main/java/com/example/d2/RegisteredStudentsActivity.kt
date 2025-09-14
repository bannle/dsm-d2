package com.example.d2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.d2.data.Score
import com.example.d2.data.Student
import com.google.firebase.database.*

class RegisteredStudentsActivity : AppCompatActivity() {

    private lateinit var lvScores: ListView
    private lateinit var btnBack: Button
    private lateinit var scoreAdapter: AdaptadorScore
    private lateinit var databaseScores: DatabaseReference
    private lateinit var databaseStudents: DatabaseReference

    private val scoreList = mutableListOf<Score>()
    private val studentMap = mutableMapOf<String, Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registered_students)

        lvScores = findViewById(R.id.lvScores)
        btnBack = findViewById(R.id.btnBack)

        scoreAdapter = AdaptadorScore(this, scoreList, studentMap)
        lvScores.adapter = scoreAdapter

        databaseScores = FirebaseDatabase.getInstance().getReference("scores")
        databaseStudents = FirebaseDatabase.getInstance().getReference("students")

        loadStudents {
            loadScores()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadStudents(onComplete: () -> Unit) {
        databaseStudents.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentMap.clear()
                for (data in snapshot.children) {
                    val student = data.getValue(Student::class.java)
                    val key = data.key
                    if (student != null && key != null) {
                        studentMap[key] = student
                    }
                }
                onComplete()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadScores() {
        databaseScores.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scoreList.clear()
                for (data in snapshot.children) {
                    val nota = data.getValue(Score::class.java)
                    nota?.let { scoreList.add(it) }
                }
                scoreAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
