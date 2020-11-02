package sj.note.app.ui.notes

import sj.note.app.alarm.AlarmHelper
import sj.note.app.database.DBHelper
import sj.note.app.helper.NoteApp.Companion.getAppContext
import javax.inject.Inject

class NoteListPresenter @Inject constructor() {

    private lateinit var noteListView: NoteListView
    private var dbHelper: DBHelper = DBHelper()

    fun setView(noteListView: NoteListView) {
        this.noteListView = noteListView
    }

    fun findAll() {
        try {
            noteListView.onSuccess(dbHelper.findAll())
        } catch (e: Exception) {
            noteListView.onFailed(e.message)
        }
    }

    fun deleteById(position: Int) {
        dbHelper.deleteById(position)
        noteListView.deleteNote(position)
    }

    fun updateStatus(id: Long) {
        dbHelper.updateStatus(id)
        noteListView.updateStatus()
    }

    fun filter(status: String) {
        noteListView.onSuccess(dbHelper.filter(status))
    }

    fun period(start: Long?, end: Long?) {
        noteListView.onSuccess(dbHelper.period(start, end))
    }

    fun addAlarm() {
        val note = dbHelper.findFirstAlarm()

        if (note != null) {
            AlarmHelper.setNoteAlarm(getAppContext(), note.alarmTime)
        }
    }
}