package sj.note.app.di

import dagger.Module
import dagger.Provides
import sj.note.app.database.DBHelper
import javax.inject.Singleton

@Module
class DBModule {

    @Provides
    @Singleton
    fun provideDBHelper(): DBHelper = DBHelper()
}