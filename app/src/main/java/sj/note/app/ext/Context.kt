package sj.note.app.ext

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tbruyelle.rxpermissions2.RxPermissions

fun layoutManager(): RecyclerView.LayoutManager {
    return StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
}

fun Context.toast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

fun Activity.pickImageFromGallery() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    startActivityForResult(intent, GALLERY_REQUEST_CODE)
}

@SuppressLint("CheckResult")
fun AppCompatActivity.onPermissionGranted() {
    RxPermissions(this)
        .request(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        .subscribe {}
}
