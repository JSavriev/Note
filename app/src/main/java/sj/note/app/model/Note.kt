package sj.note.app.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Note : RealmObject() {

    @PrimaryKey
    var id: Long = 0

    var title = ""
    var description = ""
    var imageUrl = ""
    var status = ""
    var deadline: Long? = null
    var createdAt = 0L
    var alarm: Long? = null
    var alarmTime: Long? = null
    var hasAlarm: Boolean = false
}