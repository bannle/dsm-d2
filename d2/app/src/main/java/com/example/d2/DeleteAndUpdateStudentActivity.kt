package com.example.d2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.d2.data.Student
import com.google.firebase.database.*

class DeleteAndUpdateStudentActivity : AppCompatActivity() {

    private lateinit var spinnerStudents: Spinner
    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etAge: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnBack: Button

    private val database = FirebaseDatabase.getInstance().getReference("students")

    private val studentList = mutableListOf<Student>()
    private val studentNames = mutableListOf<String>()
    private val studentKeys = mutableListOf<String>()

    private var selectedKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_and_update_student)

        spinnerStudents = findViewById(R.id.spinnerStudents)
        etName = findViewById(R.id.etName)
        etLastName = findViewById(R.id.etLastName)
        etAge = findViewById(R.id.etAge)
        etAddress = findViewById(R.id.etAddress)
        etPhone = findViewById(R.id.etPhone)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack = findViewById(R.id.btnBack)

        loadStudents()

        spinnerStudents.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedKey = studentKeys[position]
                val student = studentList[position]

                etName.setText(student.name)
                etLastName.setText(student.lastName)
                etAge.setText(student.age?.toString())
                etAddress.setText(student.address)
                etPhone.setText(student.phone)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSave.setOnClickListener {
            selectedKey?.let { key ->
                val name = etName.text.toString().trim()
                val lastName = etLastName.text.toString().trim()
                val ageText = etAge.text.toString().trim()
                val address = etAddress.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                if (name.isEmpty() || lastName.isEmpty() || ageText.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val regexName = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$")
                if (!regexName.matches(name)) {
                    Toast.makeText(this, "Nombre inválido (solo letras y mínimo 2 caracteres)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (!regexName.matches(lastName)) {
                    Toast.makeText(this, "Apellido inválido (solo letras y mínimo 2 caracteres)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val age = try {
                    ageText.toInt()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (age <= 3 || age > 100) {
                    Toast.makeText(this, "Edad inválida (debe ser entre 4 y 100)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (address.length < 5) {
                    Toast.makeText(this, "Dirección demasiado corta", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val regexPhone = Regex("^[0-9]{4}-[0-9]{4}$")
                if (!regexPhone.matches(phone)) {
                    Toast.makeText(this, "Teléfono inválido (formato 2222-2222)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updatedStudent = Student(
                    name = name,
                    lastName = lastName,
                    age = age,
                    address = address,
                    phone = phone,
                    key = key
                )

                database.child(key).updateChildren(updatedStudent.toMap())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Estudiante actualizado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnDelete.setOnClickListener {
            selectedKey?.let { key ->
                database.child(key).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Estudiante eliminado", Toast.LENGTH_SHORT).show()
                        loadStudents()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadStudents() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
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
                    this@DeleteAndUpdateStudentActivity,
                    android.R.layout.simple_spinner_item,
                    studentNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerStudents.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DeleteAndUpdateStudentActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
