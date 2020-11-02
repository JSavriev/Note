package sj.note.app.di

import dagger.Module
import dagger.Provides
import sj.note.app.ui.add.AddNotePresenter
import sj.note.app.ui.notes.NoteListPresenter
import javax.inject.Singleton

@Module
class PresenterModule {

    @Provides
    @Singleton
    fun provideSurahsPresenter(): NoteListPresenter = NoteListPresenter()

    @Provides
    @Singleton
    fun provideDownloadPresenter(): AddNotePresenter = AddNotePresenter()
}