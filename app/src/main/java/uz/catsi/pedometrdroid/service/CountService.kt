package uz.catsi.pedometrdroid.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import uz.catsi.pedometrdroid.R
import uz.catsi.pedometrdroid.ui.MainActivity

class CountService :Service()  {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount: Float = 0f

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "StepCounterChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        startForegroundService()
        startStepSensor()
    }

    private fun startForegroundService() {
        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_MUTABLE
//        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Piyoda")
            .setContentText("Orqa fonda qadamlarni hisoblash")
            .setSmallIcon(R.mipmap.ic_launcher)
          //  .setContentIntent(pendingIntent)
            .build()

        createNotificationChannel()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Step Counter Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startStepSensor() {
        if (stepSensor != null) {
            sensorManager.registerListener(
                stepSensorListener,
                stepSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

//    override fun onDestroy() {
//        stopStepSensor()
//        super.onDestroy()
//    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.let {
            val command=it.getSerializable("COMMAND")
        }
        return START_NOT_STICKY
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        // Bu metodda Service qayta ishlatish uchun kerakli qadamlarni bajaring
        val restartServiceIntent = Intent(applicationContext, javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun stopStepSensor() {
        sensorManager.unregisterListener(stepSensorListener)
    }

    private val stepSensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Noqulaylikni aniqlash uchun ishlatilishi mumkin
        }

        override fun onSensorChanged(event: SensorEvent) {
            // Har bir qadam uchun amalni bajarish
            stepCount++
            // MainActivityga ma'lumotni jo'natish
            sendStepCountBroadcast()
        }
    }

    private fun sendStepCountBroadcast() {
        val intent = Intent("step-count-event")
        intent.putExtra("step_count", stepCount)
        Log.d("POP", stepCount.toString())
        sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}