package uz.catsi.pedometrdroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.catsi.pedometrdroid.R
import uz.catsi.pedometrdroid.databinding.ScreenMainBinding
import java.time.LocalDate

class MainScreen : Fragment(R.layout.screen_main) , SensorEventListener {

    private  val binding by  viewBinding(ScreenMainBinding::bind)

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private  var ckall = "0"
    private  var d_istance = "0"

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        loadData()
//        resetSteps()
//
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        resetSteps()

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }



    override fun onResume() {
        super.onResume()
        running = true

        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            Toast.makeText(requireContext(), "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {

//        var tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
//        var txt_calory = findViewById<TextView>(R.id.txtCalory)
//        var txt_distance = findViewById<TextView>(R.id.txtDistance)
//        var txt_speed = findViewById<TextView>(R.id.txtSpeed)

        if (running) {
            totalSteps = event!!.values[0]

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            // It will show the current steps to the user

            binding.apply {
                tvStepsTaken.text = ("$currentSteps")

                txtCalory.text="Calory: "+getCalories(currentSteps)
                txtDistance.text="Distance: "+getDistanceCovered(currentSteps)
            }


            ckall=getCalories(currentSteps)
            d_istance=getDistanceCovered(currentSteps)

            val current = LocalDate.now()
            Toast.makeText(requireContext(), current.toString(), Toast.LENGTH_SHORT).show()

        }
    }

    fun resetSteps() {

        binding.tvStepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(requireContext(), "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        binding.tvStepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            binding.tvStepsTaken.text = 0.toString()

            // This will save the data
            saveData()

            true
        }
    }

    private fun saveData() {

        // Shared Preferences will allow us to save
        // and retrieve data in the form of key,value pair.
        // In this function we will save data
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putFloat("key1", previousTotalSteps)
        editor.putString("key2", ckall)
        editor.putString("key3",d_istance)
        editor.apply()
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
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



}