package sj.note.app.di

import dagger.Component
import sj.note.app.ui.notes.NoteListActivity
import sj.note.app.ui.add.AddNoteActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [PresenterModule::class, DBModule::class])
interface AppComponent {

    fun inject(target: NoteListActivity)

    fun inject(target: AddNoteActivity)
}