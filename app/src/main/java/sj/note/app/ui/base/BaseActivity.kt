package sj.note.app.ui.base

import androidx.appcompat.app.AppCompatActivity
import sj.note.app.di.AppComponent
import sj.note.app.helper.NoteApp

open class BaseActivity : AppCompatActivity() {

    protected fun appComponent(): AppComponent {
        return (application as NoteApp).appComponent
    }
}