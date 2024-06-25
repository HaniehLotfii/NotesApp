package ir.shariaty.notes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditNoteActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("Notes")
    private lateinit var auth: FirebaseAuth
    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        auth = FirebaseAuth.getInstance()

        val noteTitle: EditText = findViewById(R.id.noteTitle)
        val noteDescription: EditText = findViewById(R.id.noteDescription)
        val saveButton: Button = findViewById(R.id.saveButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)

        noteId = intent.getStringExtra("noteId")
        loadNote()

        saveButton.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val title = noteTitle.text.toString()
                val description = noteDescription.text.toString()
                val note = hashMapOf(
                    "Title" to title,
                    "Description" to description,
                    "Time" to com.google.firebase.Timestamp.now(),
                    "userId" to currentUser.uid
                )
                noteId?.let { id ->
                    notesCollection.document(id).set(note)
                        .addOnSuccessListener {
                            setResult(RESULT_UPDATED)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error saving note: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            noteId?.let { id ->
                notesCollection.document(id).delete()
                    .addOnSuccessListener {
                        setResult(RESULT_DELETED)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error deleting note: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun loadNote() {
        val currentUser = auth.currentUser
        if (currentUser != null && noteId != null) {
            notesCollection.document(noteId!!).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists() && doc.getString("userId") == currentUser.uid) {
                        findViewById<EditText>(R.id.noteTitle).setText(doc.getString("Title"))
                        findViewById<EditText>(R.id.noteDescription).setText(doc.getString("Description"))
                    } else {
                        Toast.makeText(this, "Note not found or you don't have permission to edit this note.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error loading note: ${exception.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } else {
            Toast.makeText(this, "Error: User not logged in or invalid note ID.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    companion object {
        const val RESULT_UPDATED = 100
        const val RESULT_DELETED = 200
    }
}
