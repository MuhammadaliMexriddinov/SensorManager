package uz.catsi.pedometrdroid
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.circularprogressbar.CircularProgressBar
import com.owl93.dpb.CircularProgressView
import java.time.LocalDate

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
    lateinit var  progressBar : CircularProgressView

    private  var ckall = "0"
    private  var d_istance = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startService(Intent(this, MainService::class.java))
        loadData()
        resetSteps()

        // Adding a context of SENSOR_SERVICE as Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

         progressBar = findViewById<CircularProgressView >(R.id.progress)
       // progressBar.progress = 70f
    }

    override fun onResume() {
        super.onResume()
        running = true

        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        // So don't forget to add the following permission in AndroidManifest.xml present in manifest folder of the app.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {

        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        var txt_calory = findViewById<TextView>(R.id.txtCalory)
        var txt_distance = findViewById<TextView>(R.id.txtDistance)
        var txt_speed = findViewById<TextView>(R.id.txtSpeed)

        if (running) {
            totalSteps = event!!.values[0]

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            // It will show the current steps to the user
            tv_stepsTaken.text = ("$currentSteps")

            txt_calory.text="Calory: "+getCalories(currentSteps)
            txt_distance.text="Distance: "+getDistanceCovered(currentSteps)

            ckall=getCalories(currentSteps)
            d_istance=getDistanceCovered(currentSteps)
            progressBar.progress=(100f*currentSteps)/30

            val current = LocalDate.now()
          //  Toast.makeText(this, current.toString(), Toast.LENGTH_SHORT).show()

        }
    }

    fun resetSteps() {
        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        var txt_calory = findViewById<TextView>(R.id.txtCalory)
        var txt_distance = findViewById<TextView>(R.id.txtDistance)
        var txt_speed = findViewById<TextView>(R.id.txtSpeed)
        tv_stepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tv_stepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            tv_stepsTaken.text = 0.toString()

            // This will save the data
            saveData()

            true
        }
    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.putString("key2", ckall)
        editor.putString("key3",d_istance)
        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)
        var calory = sharedPreferences.getString("key2","")
        var dictan = sharedPreferences.getString("key3","")

        // Log.d is used for debugging purposes
        Log.d("MainActivity", "$savedNumber")

        previousTotalSteps = savedNumber
        if (calory != null) {
            ckall=calory
        }
        if (dictan != null) {
            d_istance=dictan
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

    fun getCalories(steps: Int): String {
        val Cal = (steps * 0.045).toInt()
        return "$Cal calories"
    }

    fun getDistanceCovered(steps: Int): String {
        val feet = (steps * 2.5).toInt()
        val distance = feet/3.281
       // val finalDistance:Double = String.format("%.2f", distance).toDouble()
        return "$distance"
    }

    fun getSpeed(distance:Float , ){

    }


}
