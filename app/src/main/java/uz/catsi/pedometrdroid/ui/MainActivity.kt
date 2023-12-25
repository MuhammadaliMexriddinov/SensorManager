package uz.catsi.pedometrdroid.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.circularprogressbar.BuildConfig
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.owl93.dpb.CircularProgressView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.catsi.pedometrdroid.R
import uz.catsi.pedometrdroid.local.SharedPref
import uz.catsi.pedometrdroid.service.CountService
import uz.catsi.pedometrdroid.utils.StepCounterManager
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    lateinit var progressBar: CircularProgressView
    lateinit var prog_distance: CircularProgressView
    lateinit var prog_calory: CircularProgressView
    lateinit var nav: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var spd: TextView
    lateinit var txt_Step: TextView
    lateinit var txtTitle: TextView
    lateinit var btnMenu: ImageView
    lateinit var btnSettings: ImageView
    lateinit var progress: ContentLoadingProgressBar
    val pref = SharedPref.getInstance()
    private var ckall = "0"
    private var d_istance = "0"
    private var speed = "0"
    lateinit var themeActivity: ConstraintLayout
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    private var countService: CountService? = null
    private var bound = false

    private lateinit var sensorServiceIntent: Intent
    private var isServiceRunning = false




    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //askNotificationPermission()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.FOREGROUND_SERVICE),
                100
            )
        } else {
            // Permission is already granted, start the foreground service here
            val serviceIntent = Intent(this, CountService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }

        when (pref.language) {
            "uz" -> setLocate("uz")
            "ru" -> setLocate("ru")
        }

         txtTitle=findViewById(R.id.txtTitleMain)


        //     this.window.setStatusBarColor(ContextCompat.getColor(this, com.owl93.dpb.R.color.blue));

        loadData()
        resetSteps()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        progressBar = findViewById<CircularProgressView>(R.id.progress)
        prog_distance = findViewById<CircularProgressView>(R.id.prog_dis)
        prog_calory = findViewById<CircularProgressView>(R.id.prog_kkal)
        spd = findViewById<TextView>(R.id.txtSpeed)
        nav = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        btnMenu = findViewById(R.id.btnMenu)
        btnSettings = findViewById(R.id.btnSettingsMain)
        progress = findViewById(R.id.verfied_progress)
        txt_Step = findViewById(R.id.steps)
        themeActivity = findViewById(R.id.mainView)


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        val intent = Intent(this, StepCounterService::class.java)
//        this.startService(intent)

        sensorServiceIntent = Intent(this, CountService::class.java)
        startSensorService()

        when (pref.theme) {
            "blue" -> {
                themeActivity.setBackgroundResource(R.color.blue)
                this.window.setStatusBarColor(
                    ContextCompat.getColor(
                        this,
                        com.owl93.dpb.R.color.blue
                    )
                )

            }

            "black" -> {
                themeActivity.setBackgroundResource(R.color.theme_black)
                this.window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_black))
                nav.setBackgroundResource(R.drawable.drawer_night_theme)

            }

            "purple" -> {
                themeActivity.setBackgroundResource(R.color.theme_purple)
                this.window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_purple))
                nav.setBackgroundResource(R.drawable.shape_theme_purple)

            }
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        txt_Step.text = getString(R.string.stp5) + ": " + pref.stepName
        nav.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dictionary -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.myStep -> {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.faq -> {
                    startActivity(Intent(this@MainActivity, AdviceActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.settings -> {
                    startActivity(Intent(this@MainActivity, ThemeActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.about -> {
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.share -> {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                        var shareMessage = "\nLet me recommend you this application\n\n"
                        shareMessage =
                            """
                            ${shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.LIBRARY_PACKAGE_NAME}
                            
                            
                            """.trimIndent()
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        startActivity(Intent.createChooser(shareIntent, "choose one"))
                    } catch (e: Exception) {
                        //e.toString();
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.exit_app -> {
                    this.finish()
                }
            }
            return@setNavigationItemSelectedListener true
        }

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the foreground service
                val serviceIntent = Intent(this, CountService::class.java)
                ContextCompat.startForegroundService(this, serviceIntent)
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {

        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        var txt_calory = findViewById<TextView>(R.id.txtCalory)
        var txt_distance = findViewById<TextView>(R.id.txtDistance)
        var txt_speed = findViewById<TextView>(R.id.txtSpeed)
        var stepName = findViewById<TextView>(R.id.steps)

        if (running) {
            totalSteps = event!!.values[0]

        //    val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
          //  tv_stepsTaken.text = ("$currentSteps")
            tv_stepsTaken.text=pref.liveStep


            var kal: Float = getCalories(StepCounterManager.stepCount)
            var diss: String = getDistanceCovered(StepCounterManager.stepCount).toString()
            txt_calory.text = String.format("%.2f", kal)
            txt_distance.text = diss

            ckall = getCalories(StepCounterManager.stepCount).toString()
            d_istance = getDistanceCovered(StepCounterManager.stepCount)
            progressBar.progress = (100f * StepCounterManager.stepCount) / (pref.stepCount?.toInt()!!)
            prog_distance.progress = (diss.toFloat()*100f/pref.stepDistance.toString().toFloat())
            prog_calory.progress = ckall.toFloat()*100f/pref.stepCalory.toString().toFloat()

            if (StepCounterManager.stepCount >= pref.stepCount.toString().toInt()) {
                val intent = Intent(this, MainActivity::class.java)

//                val pendingIntent =
//                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationChannel = NotificationChannel(
                        channelId,
                        description,
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.GREEN
                    notificationChannel.enableVibration(false)
                    notificationManager.createNotificationChannel(notificationChannel)

                    builder = Notification.Builder(this, channelId)
                        //  .setContent(contentView)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Piyoda")
                        .setContentText("Siz maqsadga erishdingiz !")
                     //   .setContentIntent(pendingIntent)
                } else {

                    builder = Notification.Builder(this)
                        // .setContent(contentView)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Piyoda")
                        .setContentText("Siz maqsadga erishdingiz !")
                        //.setContentIntent(pendingIntent)
                }
                notificationManager.notify(1234, builder.build())
            }
            if (StepCounterManager.stepCount > 0) {
                lifecycleScope.launch {
                    delay(8000)
                    spd.text = "0.91"
                    delay(25000)
                    spd.text = "1.2"
                    delay(250000)
                    spd.text = "1.1"
                }
            }
        }
    }

    fun resetSteps() {
        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        var txt_calory = findViewById<TextView>(R.id.txtCalory)
        var txt_distance = findViewById<TextView>(R.id.txtDistance)
        var txt_speed = findViewById<TextView>(R.id.txtSpeed)
        tv_stepsTaken.setOnClickListener {

            Toast.makeText(this, "Qadamlarni tiklash uchun uzoq bosing", Toast.LENGTH_SHORT).show()
        }

        tv_stepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps
            tv_stepsTaken.text = 0.toString()
            txt_calory.text = 0.toString()
            txt_distance.text = 0.toString()
            progressBar.progress = 0f
            spd.text = "0.0"
            saveData()

            true
        }
    }

    private fun saveData() {

        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.putString("key2", ckall)
        editor.putString("key3", d_istance)
        editor.apply()
    }

    private fun loadData() {

        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        var calory = sharedPreferences.getString("key2", "")
        var dictan = sharedPreferences.getString("key3", "")

        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
        if (calory != null) {
            ckall = calory
        }
        if (dictan != null) {
            d_istance = dictan
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
    fun getCalories(steps: Int): Float  = (steps * 0.05).toFloat()

    fun getDistanceCovered(steps: Int): String {
//        val feet = (steps * 2.5).toInt()
//        val distance = feet / 3.281
//        // val finalDistance:Double = String.format("%.2f", distance).toDouble()
//        return distance.toInt().toString()
        return  (steps*0.78).toInt().toString()
    }

    private fun setLocate(language: String) {
        val resources: Resources = resources
        val metric: DisplayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = Locale(language)
        resources.updateConfiguration(configuration, metric)
        onConfigurationChanged(configuration)
        SharedPref.getInstance()?.language = language
    }

    private fun startSensorService() {
        if (!isServiceRunning) {
            startService(sensorServiceIntent)
            isServiceRunning = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        registerReceiver(stepCountReceiver, IntentFilter("step-count-event") , RECEIVER_EXPORTED)
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(this, "Bu qurilmada sensor aniqlanmadi", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private val stepCountReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val stepCount = intent?.getFloatExtra("step_count", 0f)
            StepCounterManager.stepCount=stepCount!!.toInt()
            pref.liveStep= stepCount.toInt().toString()
        }
    }
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
//                Log.e(TAG, "PERMISSION_GRANTED")
                // FCM SDK (and your app) can post notifications.
            } else {
//                Log.e(TAG, "NO_PERMISSION")
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
//            Toast.makeText(
//                this, "${getString(R.string.app_name)} can't post notifications without Notification permission",
//                Toast.LENGTH_LONG
//            ).show()

            Snackbar.make(
               this.findViewById(R.id.txtTitleMain),
                String.format(
                    String.format(
                        "123",
                        getString(R.string.app_name)
                    )
                ),
                Snackbar.LENGTH_INDEFINITE
            ).setAction("456") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    startActivity(settingsIntent)
                }
            }.show()
        }
    }
}