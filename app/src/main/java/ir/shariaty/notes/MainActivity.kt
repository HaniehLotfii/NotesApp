package ir.shariaty.notes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity(), NotesAdapter.NoteClickListener {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("Notes")
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()


        val currentUser = auth.currentUser
        if (currentUser == null) {
            // No user is signed in, redirect to AuthActivity
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        logOutButton = findViewById(R.id.logOutButton)

        logOutButton.setOnClickListener {
            auth.signOut()
            // Redirect to AuthActivity
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)

        val customTitle = layoutInflater.inflate(R.layout.action_bar_title, null)
        supportActionBar?.customView = customTitle
        supportActionBar?.setDisplayShowCustomEnabled(true)

        notesAdapter = NotesAdapter(this)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = notesAdapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivityForResult(Intent(this, AddNoteActivity::class.java), ADD_NOTE_REQUEST)
        }

        loadNotes()
    }

    private fun loadNotes() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            notesCollection
                .whereEqualTo("userId", currentUser.uid)  // Filter by userId
                .orderBy("Time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val notes = result.map { doc ->
                        Note(
                            id = doc.id,
                            title = doc.getString("Title") ?: "",
                            description = doc.getString("Description") ?: "",
                            time = doc.getTimestamp("Time")?.toDate()
                        )
                    }
                    notesAdapter.submitList(notes)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error loading notes: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            loadNotes()
        } else if ((requestCode == EDIT_NOTE_REQUEST) && (resultCode == EditNoteActivity.RESULT_UPDATED || resultCode == EditNoteActivity.RESULT_DELETED)) {
            loadNotes()
        }
    }

    override fun onNoteClick(note: Note) {
        val intent = Intent(this, EditNoteActivity::class.java)
        intent.putExtra("noteId", note.id)
        startActivityForResult(intent, EDIT_NOTE_REQUEST)
    }

    companion object {
        const val ADD_NOTE_REQUEST = 1
        const val EDIT_NOTE_REQUEST = 2
    }
}
