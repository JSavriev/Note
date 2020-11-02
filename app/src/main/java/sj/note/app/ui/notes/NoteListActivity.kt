package sj.note.app.ui.notes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_note_list.*
import kotlinx.android.synthetic.main.layout_filter.view.*
import sj.note.app.R
import sj.note.app.ext.*
import sj.note.app.helper.*
import sj.note.app.model.Note
import sj.note.app.ui.add.AddNoteActivity
import sj.note.app.ui.base.BaseActivity
import java.util.*
import javax.inject.Inject


class NoteListActivity : BaseActivity(), NoteListView, NoteOnClickListener, NoteTouchHelperListener {

    @Inject
    lateinit var noteListPresenter: NoteListPresenter

    private var noteListAdapter: NoteListAdapter? = null

    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        appComponent().inject(this)
        bindingViews()
        setPopUpWindow()
    }

    override fun onResume() {
        super.onResume()
        noteListPresenter.findAll()
        noteListPresenter.addAlarm()
    }

    override fun onItemClick(id: Long) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra(NOTE_ID, id)
        intent.putExtra(IS_NEW, false)
        startActivity(intent)
    }

    override fun onStatusClick(id: Long) {
        noteListPresenter.updateStatus(id)
    }

    override fun onSuccess(notes: ArrayList<Note>) {
        noteListAdapter = NoteListAdapter(notes, this)
        recyclerView.adapter = noteListAdapter
    }

    override fun onFailed(error: String?) {
        toast(error!!)
    }

    override fun deleteNote(position: Int) {
        noteListAdapter!!.remove(position)
    }

    override fun updateStatus() {
        noteListPresenter.findAll()
    }

    override fun onItemMove(position: Int) {
        noteListPresenter.deleteById(position)
    }

    private fun bindingViews() {
        recyclerView.isFocusable = false
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager()

        val touchHandler = ItemTouchHelper(SwipeHandler(this))
        touchHandler.attachToRecyclerView(recyclerView)

        buttonAddNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra(IS_NEW, true)
            startActivity(intent)
        }

        buttonFilter.setOnClickListener {
            popupWindow!!.showAsDropDown(buttonFilter, 0, -36)
        }

        noteListPresenter.setView(this)
    }

    @SuppressLint("InflateParams")
    private fun setPopUpWindow() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.layout_filter, null)

        popupWindow = PopupWindow(
            view, RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT, true
        )
        popupWindow!!.elevation = 2.toFloat()
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        view.textActive.setOnClickListener {
            noteListPresenter.filter(ACTIVE)
            popupWindow!!.dismiss()
        }

        view.textDone.setOnClickListener {
            noteListPresenter.filter(DONE)
            popupWindow!!.dismiss()
        }

        view.textExpired.setOnClickListener {
            noteListPresenter.filter(EXPIRED)
            popupWindow!!.dismiss()
        }

        view.textPeriod.setOnClickListener {
            rangeDatePicker()
            popupWindow!!.dismiss()
        }
    }

    private fun rangeDatePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()

        val now = Calendar.getInstance()
        builder.setSelection(androidx.core.util.Pair(now.timeInMillis, now.timeInMillis))

        val picker = builder.build()
        picker.show(supportFragmentManager, picker.toString())

        picker.addOnNegativeButtonClickListener { picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            noteListPresenter.period(it.first, it.second)
        }
    }
}