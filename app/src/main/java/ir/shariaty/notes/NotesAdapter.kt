package ir.shariaty.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class NotesAdapter(private val noteClickListener: NoteClickListener) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(DiffCallback()) {

    interface NoteClickListener {
        fun onNoteClick(note: Note)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val noteTitle: TextView = itemView.findViewById(R.id.noteTitle)
        private val noteDescription: TextView = itemView.findViewById(R.id.noteDescription)
        private val noteTime: TextView = itemView.findViewById(R.id.noteTime)

        fun bind(note: Note) {
            noteTitle.text = note.title

            val description = if (note.description.length > 40) {
                note.description.substring(0, 40) + "..."
            } else {
                note.description
            }
            noteDescription.text = description

            noteTime.text = note.time?.let { SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(it) }
            itemView.setOnClickListener { noteClickListener.onNoteClick(note) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = getItem(position)
        holder.bind(currentNote)
    }
}
