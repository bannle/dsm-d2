package com.example.d2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

//librerias para implementar el cierre de sesión
import com.google.firebase.auth.FirebaseAuth

private lateinit var tvRedirectRegisterStudent: TextView
private lateinit var tvRedirectRegisterStudentScore: TextView
private lateinit var tvRedirectRegisteredStudents: TextView
private lateinit var tvRedirectDeleteAndUpdateStudent: TextView
private lateinit var tvRedirectDeleteAndUpdateScore: TextView
private lateinit var btnLogOut: Button
private lateinit var auth: FirebaseAuth


class MainActivity : AppCompatActivity() {

    //redirige a cada pantalla
    private fun redirectTo(view: View, destination: Class<*>) {
        view.setOnClickListener {
            val intent = Intent(this, destination)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        tvRedirectRegisterStudent = findViewById(R.id.tvRegisterStudent)
        tvRedirectRegisterStudentScore = findViewById(R.id.tvRegisterStudentScore)
        tvRedirectRegisteredStudents = findViewById(R.id.tvRegisteredStudents)
        tvRedirectDeleteAndUpdateStudent = findViewById(R.id.tvDeleteAndUpdateStudent)
        tvRedirectDeleteAndUpdateScore = findViewById(R.id.tvDeleteAndUpdateScore)
        btnLogOut = findViewById(R.id.btnLogOut)

        redirectTo(tvRedirectRegisterStudent, RegisterStudentActivity::class.java)
        redirectTo(tvRedirectRegisterStudentScore, RegisterStudentScoreActivity::class.java)
        redirectTo(tvRedirectRegisteredStudents, RegisteredStudentsActivity::class.java)
        redirectTo(tvRedirectDeleteAndUpdateStudent, DeleteAndUpdateStudentActivity::class.java)
        redirectTo(tvRedirectDeleteAndUpdateScore, DeleteAndUpdateScoreActivity::class.java)

        btnLogOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}