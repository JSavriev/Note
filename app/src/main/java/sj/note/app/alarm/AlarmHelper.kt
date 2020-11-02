package sj.note.app.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import sj.note.app.R
import sj.note.app.ext.ALARM_REQUEST_CODE
import sj.note.app.ext.CHANNEL_ID
import sj.note.app.ext.CHANNEL_NAME
import sj.note.app.ui.notes.NoteListActivity

object AlarmHelper {

    @JvmStatic
    fun setNoteAlarm(context: Context, alarmTime: Long?) {
        val notifyIntent = Intent(context, AlarmReceiver::class.java)
        val notifyPendingIntent = PendingIntent.getBroadcast(
            context, ALARM_REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime!!, notifyPendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                alarmTime!!, notifyPendingIntent
            )
        }
    }

    @JvmStatic
    fun showNotification(context: Context, title: String?, description: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val importance = NotificationManager.IMPORTANCE_LOW
                val notificationChannel =
                    NotificationChannel(CHANNEL_NAME, CHANNEL_NAME, importance)
                notificationChannel.description = context.resources.getString(R.string.app_name)
                notificationChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                val notificationManager = context.getSystemService(
                    NotificationManager::class.java
                )
                notificationManager?.createNotificationChannel(notificationChannel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val intent = Intent(context, NoteListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_NAME)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(title)
            .setContentText(description)
            .setColor(ContextCompat.getColor(context, R.color.colorApp))
            .setSmallIcon(R.drawable.img_logo)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setShowWhen(false)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(CHANNEL_ID, builder.build())
    }
}