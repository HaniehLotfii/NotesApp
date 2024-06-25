package ir.shariaty.notes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity(), NotesAdapter.NoteClickListener {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("Notes")
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)


        val customTitle = layoutInflater.inflate(R.layout.action_bar_title, null)
        supportActionBar?.customView = customTitle
        supportActionBar?.setDisplayShowCustomEnabled(true)

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

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
        notesCollection.orderBy("Time", Query.Direction.DESCENDING)
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
            .addOnFailureListener {
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
