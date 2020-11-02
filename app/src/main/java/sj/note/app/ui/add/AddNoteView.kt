package sj.note.app.ui.add

interface AddNoteView {

    fun loadData(
        title: String,
        description: String?,
        imageUrl: String?,
        deadline: Long?,
        alarmTime: Long?
    )

    fun onCreateOrUpdateSuccess(isNew: Boolean)
}