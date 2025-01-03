package com.example.medicinereminder.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.medicinereminder.MainActivity
import com.example.medicinereminder.R
import com.example.medicinereminder.data.entity.Medicine

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "medicine_reminder_channel"
        const val CHANNEL_NAME = "Medicine Reminders"
        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for medicine reminders"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showMedicineNotification(
        medicine: Medicine,
        imageUri: Uri?,
        imageBitmap: Bitmap?
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notificatino)
            .setContentTitle("Time for ${medicine.name}")
            .setContentText("Take ${medicine.dosage}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Add big text style for instructions
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .setBigContentTitle("Time for ${medicine.name}")
            .bigText("Take ${medicine.dosage}\n${medicine.instructions ?: ""}")

        notificationBuilder.setStyle(bigTextStyle)

        // If image is available, show it in big picture style
        if (imageBitmap != null) {
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
                .bigPicture(imageBitmap)
                .setBigContentTitle("Time for ${medicine.name}")
                .setSummaryText("Take ${medicine.dosage}")

            notificationBuilder.setStyle(bigPictureStyle)
        }

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }
}