package sj.note.app.ui.notes

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.item_note.view.*
import sj.note.app.R
import sj.note.app.ext.inflate
import sj.note.app.ext.ACTIVE
import sj.note.app.ext.DONE
import sj.note.app.ext.EXPIRED
import sj.note.app.model.Note
import java.util.*
import java.util.concurrent.TimeUnit.MILLISECONDS


class NoteListAdapter(
    private val notes: ArrayList<Note>, private val listener: NoteOnClickListener
) :
    RecyclerView.Adapter<NoteListAdapter.NoteListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteListHolder(parent.inflate(R.layout.item_note))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NoteListHolder, position: Int) {
        try {
            val note = notes[position]

            with(holder.itemView) {
                textTitle.text = note.title
                textDescription.isVisible = note.description.isNotEmpty()
                textDescription.text = note.description

                imageNote.isVisible = note.imageUrl.isNotEmpty()
                Glide.with(this)
                    .load(note.imageUrl)
                    .placeholder(null)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageNote)

                buttonAlarm.isVisible = note.alarm != null
                buttonAlarm.text = "${MILLISECONDS.toHours(note.alarm ?: 0L)} hour"

                buttonActive.text = note.status
                when (note.status) {
                    ACTIVE -> changeBackgroundButton(
                        true,
                        buttonActive,
                        "#FCFAFF",
                        "#33AB6DF8",
                        "#AB6DF8"
                    )
                    EXPIRED -> changeBackgroundButton(
                        false,
                        buttonActive,
                        "#f44336",
                        "#f44336",
                        "#ffffff"
                    )
                    DONE -> changeBackgroundButton(
                        true,
                        buttonActive,
                        "#1ECD82",
                        "#1ECD82",
                        "#ffffff"
                    )
                }

                setOnClickListener {
                    listener.onItemClick(note.id)
                }

                buttonActive.setOnClickListener {
                    listener.onStatusClick(note.id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun changeBackgroundButton(
        isClickable: Boolean,
        button: MaterialButton,
        background: String,
        strokeColor: String,
        textColor: String
    ) {
        button.isEnabled = isClickable
        button.isClickable = isClickable
        button.backgroundTintList = ColorStateList.valueOf(Color.parseColor(background))
        button.strokeColor = ColorStateList.valueOf(Color.parseColor(strokeColor))
        button.setTextColor(Color.parseColor(textColor))
    }

    override fun getItemCount() = notes.size

    fun remove(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount - position)
    }

    class NoteListHolder(view: View) : RecyclerView.ViewHolder(view)
}