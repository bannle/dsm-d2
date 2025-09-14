package com.example.d2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.d2.data.Student
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterStudentActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etAge: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnBack: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_student)

        etName = findViewById(R.id.etName)
        etLastName = findViewById(R.id.etLastName)
        etAge = findViewById(R.id.etAge)
        etAddress = findViewById(R.id.etAddress)
        etPhone = findViewById(R.id.etPhone)
        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBack)

        database = FirebaseDatabase.getInstance().getReference("students")

        btnRegister.setOnClickListener {
            saveStudent()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveStudent() {
        val name = etName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val ageText = etAge.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (name.isEmpty() || lastName.isEmpty() || ageText.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val regexName = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$")
        if (!regexName.matches(name)) {
            Toast.makeText(this, "Nombre inválido (solo letras y mínimo 2 caracteres)", Toast.LENGTH_SHORT).show()
            return
        }
        if (!regexName.matches(lastName)) {
            Toast.makeText(this, "Apellido inválido (solo letras y mínimo 2 caracteres)", Toast.LENGTH_SHORT).show()
            return
        }

        val age = try {
            ageText.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show()
            return
        }
        if (age <= 3 || age > 100) {
            Toast.makeText(this, "Edad inválida (la edad no puede ser negativa ni pasarse de 100", Toast.LENGTH_SHORT).show()
            return
        }

        if (address.length < 5) {
            Toast.makeText(this, "Dirección demasiado corta", Toast.LENGTH_SHORT).show()
            return
        }

        val regexPhone = Regex("^[0-9]{4}-[0-9]{4}$")
        if (!regexPhone.matches(phone)) {
            Toast.makeText(this, "Teléfono inválido (formato 2222-2222)", Toast.LENGTH_SHORT).show()
            return
        }

        val key = database.push().key ?: return
        val student = Student(name, lastName, age, address, phone, key)
        database.child(key).setValue(student)
            .addOnSuccessListener {
                Toast.makeText(this, "Estudiante registrado con éxito", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al registrar estudiante", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        etName.text.clear()
        etLastName.text.clear()
        etAge.text.clear()
        etAddress.text.clear()
        etPhone.text.clear()
    }
}
