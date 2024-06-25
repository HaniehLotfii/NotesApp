package ir.shariaty.notes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddNoteActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("Notes")
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        auth = FirebaseAuth.getInstance()

        val noteTitle: EditText = findViewById(R.id.noteTitle)
        val noteDescription: EditText = findViewById(R.id.noteDescription)
        val saveButton: Button = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val title = noteTitle.text.toString()
                val description = noteDescription.text.toString()
                val note = hashMapOf(
                    "Title" to title,
                    "Description" to description,
                    "Time" to Timestamp.now(),
                    "userId" to currentUser.uid
                )
                notesCollection.add(note)
                    .addOnSuccessListener {
                        setResult(RESULT_OK)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error saving note: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
