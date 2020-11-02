package sj.note.app.ui.add

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.layout_add_alarm.view.*
import sj.note.app.R
import sj.note.app.ext.*
import sj.note.app.ext.CAMERA_REQUEST_CODE
import sj.note.app.ext.GALLERY_REQUEST_CODE
import sj.note.app.ext.IS_NEW
import sj.note.app.ext.NOTE_ID
import sj.note.app.ui.base.BaseActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AddNoteActivity : BaseActivity(), AddNoteView, View.OnClickListener {

    @Inject
    lateinit var addNotePresenter: AddNotePresenter

    private var imageFilePath: String = ""
    private var deadline: Long? = null
    private var alarmTime: Long? = null
    private var isNew = true
    private var id: Long = 0L

    private val back = ColorDrawable(Color.TRANSPARENT)
    private val inset = InsetDrawable(back, 16)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        appComponent().inject(this)
        bindingViews()
    }

    override fun onResume() {
        super.onResume()
        onPermissionGranted()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonSave -> saveOrUpdateNote()
            R.id.buttonAddPhoto -> showCameraOrGallery()
            R.id.buttonAddAlarm -> showAddAlarm()
            R.id.buttonAlarm -> showAddAlarm()
            R.id.buttonCancelDeadline -> {
                deadline = null
                textDeadline.text = ""
                buttonCancelDeadline.gone()
            }
            R.id.buttonBack -> super.onBackPressed()
            R.id.textDeadline -> showDeadline()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    setImagePathToGlide(imageFilePath)
                }
            }
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    setImagePathToGlide(data?.data.toString())
                }
            }
            else -> {
                imageFilePath = ""
            }
        }
    }

    override fun loadData(
        title: String,
        description: String?,
        imageUrl: String?,
        deadline: Long?,
        alarmTime: Long?
    ) {
        isNew = false
        buttonSave.text = getString(R.string.update)
        editTitle.text = title.toEditable()
        editDescription.text = description?.toEditable() ?: "".toEditable()

        setImagePathToGlide(imageUrl ?: "")
        updateDeadline(deadline)
        updateAlarmTime(alarmTime != null, alarmTime)
    }

    override fun onCreateOrUpdateSuccess(isNew: Boolean) {
        toast(
            if (isNew) getString(R.string.successfully_created)
            else getString(R.string.successfully_updated)
        )
        super.onBackPressed()
    }

    private fun bindingViews() {
        isNew = intent.extras!!.getBoolean(IS_NEW, true)

        addNotePresenter.setView(this)
        if (!isNew) {
            id = intent.extras!!.getLong(NOTE_ID)
            addNotePresenter.loadData(id)
        }

        buttonSave.setOnClickListener(this)
        buttonCancelDeadline.setOnClickListener(this)
        buttonAddPhoto.setOnClickListener(this)
        buttonAddAlarm.setOnClickListener(this)
        buttonAlarm.setOnClickListener(this)
        buttonBack.setOnClickListener(this)
        textDeadline.setOnClickListener(this)
    }

    private fun saveOrUpdateNote() {
        if (editTitle.text.isNotEmpty()) {
            addNotePresenter.saveOrUpdateNote(
                isNew,
                id,
                editTitle.text.toString(),
                editDescription.text.toString(),
                imageFilePath,
                deadline,
                alarmTime
            )
        } else {
            toast(getString(R.string.title_error))
        }
    }

    private fun showDeadline() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        builder.setTitleText("Select a Date")

        val picker: MaterialDatePicker<*> = builder.build()
        picker.show(supportFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener {
            updateDeadline(it as Long?)
        }
    }

    private fun updateDeadline(time: Long?) {
        deadline = time
        textDeadline.text = if (deadline != null) getDate(deadline ?: 0L) else ""
        buttonCancelDeadline.isVisible = deadline != null
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(milliSeconds: Long): String? {
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun showAddAlarm() {
        val view: View = inflate(R.layout.layout_add_alarm)
        val dialog: AlertDialog = AlertDialog.Builder(this).setView(view).create()
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.show()

        view.buttonDelete.isVisible = alarmTime != null

        var time: Long? = 3600000L

        view.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonOne -> time = 3600000L
                R.id.radioButtonTwo -> time = 7200000L
                R.id.radioButtonThree -> time = 10800000L
                R.id.radioButtonFive -> time = 18000000L
            }
        }

        view.buttonOk.setOnClickListener {
            updateAlarmTime(true, time)
            dialog.cancel()
        }

        view.buttonCancel.setOnClickListener { dialog.cancel() }

        view.buttonDelete.setOnClickListener {
            updateAlarmTime(false, null)
            dialog.cancel()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateAlarmTime(isVisible: Boolean, time: Long?) {
        alarmTime = time
        buttonAlarm.isVisible = isVisible
        buttonAlarm.text = "${TimeUnit.MILLISECONDS.toHours(time ?: 0L)} hour"
    }

    private fun showCameraOrGallery() {
        val view: View = inflate(R.layout.layout_select)
        val dialog: AlertDialog = AlertDialog.Builder(this).setView(view).create()
        dialog.window!!.setBackgroundDrawable(inset)
        dialog.show()

        view.findViewById<MaterialButton>(R.id.buttonTakePhoto).setOnClickListener {
            cameraCapture()
            dialog.cancel()
        }

        view.findViewById<MaterialButton>(R.id.buttonAddImage).setOnClickListener {
            pickImageFromGallery()
            dialog.cancel()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun cameraCapture() {
        try {
            val imageFile = createImageFile()
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (callCameraIntent.resolveActivity(packageManager) != null) {
                val authorities = "$packageName.fileprovider"
                val imageUri = FileProvider.getUriForFile(this, authorities, imageFile)

                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
            }
        } catch (e: IOException) {
            toast("Could not create file!")
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        if (!storageDir.exists()) storageDir.mkdirs()

        val file = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = file.absolutePath
        return file
    }

    private fun setImagePathToGlide(url: String) {
        imageFilePath = url
        imageNote.visible()
        Glide.with(this)
            .load(url)
            .placeholder(null)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageNote)
    }
}