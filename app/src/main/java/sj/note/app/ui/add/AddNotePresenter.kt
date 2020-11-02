package sj.note.app.ui.add

import sj.note.app.database.DBHelper
import sj.note.app.model.Note
import javax.inject.Inject

class AddNotePresenter @Inject constructor() {

    private lateinit var addNoteView: AddNoteView
    private var dbHelper: DBHelper = DBHelper()

    fun setView(addNoteView: AddNoteView) {
        this.addNoteView = addNoteView
    }

    fun saveOrUpdateNote(
        isNew: Boolean,
        id: Long,
        title: String,
        description: String,
        imageUrl: String,
        deadline: Long?,
        alarmTime: Long?
    ) {
        if (isNew) {
            dbHelper.create(title, description, imageUrl, deadline, alarmTime)
        } else {
            dbHelper.update(id, title, description, imageUrl, deadline, alarmTime)
        }

        addNoteView.onCreateOrUpdateSuccess(isNew)
    }

    fun loadData(noteId: Long) {
        val note: Note? = dbHelper.find(noteId)
        if (note != null) {
            addNoteView.loadData(
                note.title,
                note.description,
                note.imageUrl,
                note.deadline,
                note.alarm
            )
        }
    }
}