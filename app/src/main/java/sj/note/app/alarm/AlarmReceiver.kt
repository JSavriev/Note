package sj.note.app.alarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import sj.note.app.alarm.AlarmHelper.setNoteAlarm
import sj.note.app.alarm.AlarmHelper.showNotification
import sj.note.app.database.DBHelper

class AlarmReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val dbHelper = DBHelper()
        val note = dbHelper.findFirstAlarm()

        if (note != null) {
            showNotification(context, note.title, note.description)
            setNoteAlarm(context, note.alarmTime)

            dbHelper.updateAlarm(note.id)
        }

        val noteNew = dbHelper.findFirstAlarm()
        if (noteNew != null) {
            setNoteAlarm(context, noteNew.alarmTime)
        }
    }
}