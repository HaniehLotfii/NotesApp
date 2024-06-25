package ir.shariaty.notes
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditNoteActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("Notes")
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val noteTitle: EditText = findViewById(R.id.noteTitle)
        val noteDescription: EditText = findViewById(R.id.noteDescription)
        val saveButton: Button = findViewById(R.id.saveButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)

        noteId = intent.getStringExtra("noteId")
        loadNote()

        saveButton.setOnClickListener {
            val title = noteTitle.text.toString()
            val description = noteDescription.text.toString()
            val note = hashMapOf(
                "Title" to title,
                "Description" to description,
                "Time" to com.google.firebase.Timestamp.now()
            )
            noteId?.let { id ->
                notesCollection.document(id).set(note)
                    .addOnSuccessListener {
                        setResult(RESULT_UPDATED)
                        finish()
                    }
                    .addOnFailureListener {
                    }
            }
        }

        deleteButton.setOnClickListener {
            noteId?.let { id ->
                notesCollection.document(id).delete()
                    .addOnSuccessListener {
                        setResult(RESULT_DELETED)
                        finish()
                    }
                    .addOnFailureListener {
                    }
            }
        }
    }

    private fun loadNote() {
        noteId?.let { id ->
            notesCollection.document(id).get()
                .addOnSuccessListener { doc ->
                    findViewById<EditText>(R.id.noteTitle).setText(doc.getString("Title"))
                    findViewById<EditText>(R.id.noteDescription).setText(doc.getString("Description"))
                }
                .addOnFailureListener {
                }
        }
    }

    companion object {
        const val RESULT_UPDATED = 100
        const val RESULT_DELETED = 200
    }
}
