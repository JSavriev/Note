package sj.note.app.ui.notes

import sj.note.app.model.Note

interface NoteListView {

    fun onSuccess(notes: ArrayList<Note>)

    fun onFailed(error: String?)

    fun deleteNote(position: Int)

    fun updateStatus()
}