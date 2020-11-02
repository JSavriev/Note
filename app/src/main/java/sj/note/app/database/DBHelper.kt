package sj.note.app.database

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.Sort
import sj.note.app.ext.ACTIVE
import sj.note.app.ext.DONE
import sj.note.app.ext.EXPIRED
import sj.note.app.model.Note
import java.util.*
import kotlin.collections.ArrayList

class DBHelper {

    private val realm: Realm by lazy {
        val config = RealmConfiguration.Builder()
            .name("note.realm")
            .schemaVersion(2)
            .build()
        Realm.getInstance(config)
    }

    fun create(
        title: String,
        description: String,
        imageUrl: String,
        deadline: Long?,
        alarmTime: Long?
    ) {
        val calendar: Calendar = Calendar.getInstance()
        val millis: Long = calendar.timeInMillis

        realm.beginTransaction()
        var newId: Long = 1

        if (realm.where(Note::class.java).max("id") != null) {
            newId = realm.where(Note::class.java).max("id") as Long + 1
        }

        val note = realm.createObject(Note::class.java, newId)
        note.title = title
        note.description = description
        note.imageUrl = imageUrl
        note.deadline = deadline
        note.alarm = alarmTime
        note.hasAlarm = alarmTime != null
        note.alarmTime = if (alarmTime != null) alarmTime + millis else alarmTime
        note.status = if (deadline != null && millis > deadline) EXPIRED else ACTIVE
        note.createdAt = millis
        realm.commitTransaction()
    }

    fun update(
        id: Long,
        title: String,
        description: String,
        imageUrl: String,
        deadline: Long?,
        alarmTime: Long?
    ) {
        val calendar: Calendar = Calendar.getInstance()
        val millis: Long = calendar.timeInMillis

        realm.beginTransaction()
        val note = find(id)
        if (note != null) {
            note.title = title
            note.description = description
            note.imageUrl = imageUrl
            note.deadline = deadline
            note.alarm = alarmTime
            note.hasAlarm = alarmTime != null
            note.alarmTime = if (alarmTime != null) alarmTime + millis else alarmTime
            note.status = if (deadline != null && millis > deadline) EXPIRED else ACTIVE
            note.createdAt = millis
        }
        realm.commitTransaction()
    }

    fun updateStatus(
        id: Long
    ) {
        realm.beginTransaction()
        val note = find(id)
        if (note != null) {
            note.status = if (note.status == ACTIVE) DONE else ACTIVE
            note.deadline = null
        }
        realm.commitTransaction()
    }

    fun updateAlarm(
        id: Long
    ) {
        realm.beginTransaction()
        val note = find(id)
        if (note != null) {
            note.alarm = null
            note.hasAlarm = false
            note.alarmTime = null
        }
        realm.commitTransaction()
    }

    fun find(id: Long): Note? {
        return realm.where(Note::class.java).equalTo("id", id)
            .findFirst()
    }

    fun findAll(): ArrayList<Note> {
        return ArrayList(
            realm.where(Note::class.java)
                .sort("createdAt", Sort.DESCENDING)
                .findAll()
        )
    }

    fun deleteById(position: Int) {
        realm.beginTransaction()
        realm.where(Note::class.java)
            .sort("createdAt", Sort.DESCENDING)
            .findAll()[position]?.deleteFromRealm()
        realm.commitTransaction()
    }

    fun filter(status: String): ArrayList<Note> {
        return ArrayList(
            realm.where(Note::class.java)
                .equalTo("status", status)
                .sort("createdAt", Sort.DESCENDING)
                .findAll()
        )
    }

    fun period(start: Long?, end: Long?): ArrayList<Note> {
        return ArrayList(
            realm.where(Note::class.java)
                .between("createdAt", start ?: 0L, end ?: 0L)
                .sort("createdAt", Sort.DESCENDING)
                .findAll()
        )
    }

    fun findFirstAlarm(): Note? {
        return realm.where(Note::class.java)
            .equalTo("hasAlarm", true)
            .sort("alarmTime", Sort.ASCENDING)
            .findFirst()
    }
}