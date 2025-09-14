package com.example.d2

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.d2.data.Score
import com.example.d2.data.Student

class AdaptadorScore(
    private val context: Activity,
    var scores: List<Score>,
    private val studentMap: Map<String, Student>
) : ArrayAdapter<Score>(context, R.layout.item_score, scores) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View = convertView ?: context.layoutInflater.inflate(R.layout.item_score, parent, false)
        val tvStudentName = rowView.findViewById<TextView>(R.id.tvStudentName)
        val tvGradeSubject = rowView.findViewById<TextView>(R.id.tvGradeSubject)
        val tvFinalScore = rowView.findViewById<TextView>(R.id.tvFinalScore)

        val score = scores[position]
        val student = studentMap[score.studentId]
        val studentName = if (student != null) "${student.name} ${student.lastName}" else "Sin nombre"
        tvStudentName.text = studentName
        tvGradeSubject.text = "${score.grade} - ${score.subject}"
        tvFinalScore.text = "Nota Final: ${score.finalScore ?: 0.0}"

        return rowView
    }
}
