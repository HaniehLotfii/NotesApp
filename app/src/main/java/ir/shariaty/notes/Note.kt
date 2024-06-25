package ir.shariaty.notes

import java.util.*
data class Note(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: Date? = null
)
