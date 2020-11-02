package sj.note.app.helper

import android.app.Application
import android.content.Context
import io.reactivex.plugins.RxJavaPlugins
import io.realm.Realm
import sj.note.app.di.AppComponent
import sj.note.app.di.DaggerAppComponent

class NoteApp : Application() {

    lateinit var appComponent: AppComponent

    companion object {
        private lateinit var instance: NoteApp

        fun getAppContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
        RxJavaPlugins.setErrorHandler {}
        appComponent = DaggerAppComponent.builder().build()
        Realm.init(this)
    }
}