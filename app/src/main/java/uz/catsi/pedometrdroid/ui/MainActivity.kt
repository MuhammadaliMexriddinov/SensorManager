package uz.catsi.pedometrdroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.circularprogressbar.BuildConfig
import com.google.android.material.navigation.NavigationView
import com.owl93.dpb.CircularProgressView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.catsi.pedometrdroid.R
import uz.catsi.pedometrdroid.local.SharedPref
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {
    // Added SensorEventListener the MainActivity class
    // Implement all the members in the class MainActivity
    // after adding SensorEventListener

    // we have assigned sensorManger to nullable


    private var sensorManager: SensorManager? = null

    // Creating a variable which will give the running status
    // and initially given the boolean value as false
    private var running = false

    // Creating a variable which will counts total steps
    // and it has been given the value of 0 float
    private var totalSteps = 0f

    // Creating a variable which counts previous total
    // steps and it has also been given the value of 0 float
    private var previousTotalSteps = 0f
    lateinit var progressBar: CircularProgressView
    lateinit var prog_distance: CircularProgressView
    lateinit var prog_calory: CircularProgressView
    lateinit var nav: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var spd: TextView
    lateinit var txt_Step: TextView
    lateinit var btnMenu: ImageView
    lateinit var btnSettings: ImageView
    lateinit var progress: ContentLoadingProgressBar
    val pref = SharedPref.getInstance()
    private var ckall = "0"
    private var d_istance = "0"
    private var speed = "0"
    lateinit var themeActivity :ConstraintLayout


    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        when(pref.language){
            "uz"->setLocate("uz")
            "ru"->setLocate("ru")
        }


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
        themeActivity=findViewById(R.id.mainView)

        when(pref.theme){
            "blue"->{
                themeActivity.setBackgroundResource(R.color.blue)
                this.window.setStatusBarColor(ContextCompat.getColor(this, com.owl93.dpb.R.color.blue))

            }
            "black"->{
                themeActivity.setBackgroundResource(R.color.theme_black)
                this.window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_black))
                nav.setBackgroundResource(R.drawable.drawer_night_theme)

            }
            "purple"->{
                themeActivity.setBackgroundResource(R.color.theme_purple)
                this.window.setStatusBarColor(ContextCompat.getColor(this,R.color.theme_purple))
                nav.setBackgroundResource(R.drawable.shape_theme_purple)

            }
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }



        txt_Step.text = getString(R.string.stp5)+": " + pref.stepName

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

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
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


            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()


            tv_stepsTaken.text = ("$currentSteps")


            var kal: String = getCalories(currentSteps).toString()
            var diss: String = getDistanceCovered(currentSteps).toString()
            txt_calory.text = String.format("%.2s", kal)
            txt_distance.text = String.format("%.2s", diss)

            ckall = getCalories(currentSteps)
            d_istance = getDistanceCovered(currentSteps)

//            if (pref.stepCount!=null){
//                progressBar.progress = (100f * currentSteps) / 1000//(pref.stepCount?.toInt() ?: 10000)
//            }

            progressBar.progress = (100f * currentSteps) / (pref.stepCount?.toInt() ?: 10000)
            prog_distance.progress = (100f * currentSteps) / (pref.stepDistance?.toInt() ?: 10000)
            prog_calory.progress = (100f * currentSteps) / (pref.stepCalory?.toInt() ?: 2500)


            if (currentSteps > 0) {
                lifecycleScope.launch {
                    delay(15000)
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

            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
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

    fun getCalories(steps: Int): String {
        val Cal = (steps * 0.045).toInt()
        return "$Cal"
    }

    fun getDistanceCovered(steps: Int): String {
        val feet = (steps * 2.5).toInt()
        val distance = feet / 3.281
        // val finalDistance:Double = String.format("%.2f", distance).toDouble()
        return "$distance"
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
}