package ir.shariaty.notes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class AddNoteActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("Notes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val noteTitle: EditText = findViewById(R.id.noteTitle)
        val noteDescription: EditText = findViewById(R.id.noteDescription)
        val saveButton: Button = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val title = noteTitle.text.toString()
            val description = noteDescription.text.toString()
            val note = hashMapOf(
                "Title" to title,
                "Description" to description,
                "Time" to Timestamp.now()
            )
            notesCollection.add(note)
                .addOnSuccessListener {
                    setResult(RESULT_OK)
                    finish()
                }
                .addOnFailureListener {

                }
        }
    }
}
