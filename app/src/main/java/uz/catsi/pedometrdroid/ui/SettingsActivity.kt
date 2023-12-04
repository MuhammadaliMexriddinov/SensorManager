package uz.catsi.pedometrdroid.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import uz.catsi.pedometrdroid.R
import uz.catsi.pedometrdroid.local.SharedPref
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

//    lateinit var inputStep: NumberPicker
//    lateinit var inputCalory: NumberPicker
//    lateinit var inputDistance: NumberPicker


    lateinit var inputStep: EditText
    lateinit var inputCalory: EditText
    lateinit var inputDistance: EditText
    lateinit var back: ImageView
    lateinit var btnUpdate :ImageView
    lateinit var historyStep: TextView
    lateinit var historyCalory: TextView
    lateinit var historyDistance: TextView
    val pref = SharedPref.getInstance()
    lateinit var themeActivity: ConstraintLayout
    lateinit var f1: LinearLayout
    lateinit var f2: LinearLayout
    lateinit var f3: LinearLayout
    lateinit var f4: ConstraintLayout

    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_step)

        when (pref.language) {
            "uz" -> setLocate("uz")
            "ru" -> setLocate("ru")
        }



        inputStep = findViewById(R.id.inputStep)
        inputCalory = findViewById(R.id.inputCalory)
        inputDistance = findViewById(R.id.inputDistance)
        back = findViewById(R.id.btnBackMaqsad)

        historyStep = findViewById(R.id.txtf1_1)
        historyCalory = findViewById(R.id.txtf1_2)
        historyDistance = findViewById(R.id.txtf1_3)
        themeActivity = findViewById(R.id.inputView)
        btnUpdate=findViewById(R.id.btnUpdate)
        f1 = findViewById(R.id.f1)
        f2 = findViewById(R.id.f2)
        f3 = findViewById(R.id.f3)
        f4 = findViewById(R.id.f4)


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
                f1.setBackgroundResource(R.drawable.shape_theme_black)
                f2.setBackgroundResource(R.drawable.shape_theme_black)
                f3.setBackgroundResource(R.drawable.shape_theme_black)
                f4.setBackgroundResource(R.drawable.shape_theme_black)


            }

            "purple" -> {
                themeActivity.setBackgroundResource(R.color.theme_purple)
                this.window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_purple))
                f1.setBackgroundResource(R.drawable.shape_theme_purple)
                f2.setBackgroundResource(R.drawable.shape_theme_purple)
                f3.setBackgroundResource(R.drawable.shape_theme_purple)
                f4.setBackgroundResource(R.drawable.shape_theme_purple)
            }
        }

        back.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
        }



        btnUpdate.setOnClickListener {
            if (inputStep.text.isNotEmpty()) {
                if (inputCalory.text.isNotEmpty()) {
                    if (inputDistance.text.isNotEmpty()) {
                        pref.stepName = inputStep.text.toString()
                        pref.stepCount = inputStep.text.toString()
                        pref.stepCalory = inputCalory.text.toString()
                        pref.stepDistance = inputDistance.text.toString()
                        startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
                    } else {
                        val snackbar = Snackbar.make(inputDistance, getString(R.string.inp1), Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                } else {
                    val snackbar = Snackbar.make(inputCalory, getString(R.string.inp2), Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            } else {
                val snackbar = Snackbar.make(inputStep, getString(R.string.inp3), Snackbar.LENGTH_LONG)
                snackbar.show()
            }
        }


        historyStep.text = getString(R.string.stp1) + " : " + pref.stepCount
        historyCalory.text = getString(R.string.stp2) + " : " + pref.stepCalory + " kcal"
        historyDistance.text = getString(R.string.stp3) + " : " + pref.stepDistance + " m"


//        lifecycleScope.launch {
//            withContext(Dispatchers.Default) {
//                val myArrayList2_1 = ArrayList<String>()
//                var j: Int = 1000
//                while (j <= 100_000) {
//                    j += 1000
//                    myArrayList2_1.add(j.toString())
//                }
//                val listForField8_1 = myArrayList2_1.toTypedArray()
//
//
//                val listCalory = ArrayList<String>()
//                var l: Int = 0
//                while (l <= 1000) {
//                    l += 10
//                    listCalory.add(l.toString())
//                }
//                val listForCalory = listCalory.toTypedArray()
//
//
//                val listDistance = ArrayList<String>()
//                var m: Int = 0
//                while (m <= 100_000) {
//                    m += 1
//                    listDistance.add(m.toString())
//                }
//                val listForDistance = listDistance.toTypedArray()
//
//
//                inputStep.minValue = 0
//                inputStep.maxValue = listForField8_1.size - 1
//                inputStep.wrapSelectorWheel = false
//                inputStep.displayedValues = listForField8_1
//
//
//                inputCalory.minValue = 0
//                inputCalory.maxValue = listForCalory.size - 1
//                inputCalory.wrapSelectorWheel = false
//                inputCalory.displayedValues = listForCalory
//
//
//                inputDistance.minValue = 0
//                inputDistance.maxValue = listForDistance.size - 1
//                inputDistance.wrapSelectorWheel = false
//                inputDistance.displayedValues = listForDistance
//
//
//                val pref = SharedPref.getInstance()
//                pref.stepName = inputStep.value.toString()
//                pref.stepCount = inputStep.value.toString()
//                pref.stepCalory = inputCalory.value.toString()
//                pref.stepDistance = inputDistance.value.toString()
//
//
//                StepData.step=listForField8_1[inputStep.value]
//
//
//            }
//
//
//        }

//        back.setOnClickListener {
//            Toast.makeText(
//                this,
//                SharedPref().stepCount + " " + SharedPref.getInstance().stepCalory + " " + SharedPref.getInstance().stepDistance,
//                Toast.LENGTH_SHORT
//            ).show()
//            startActivity(Intent(this@SettingsActivity, MainActivity::class.java).apply {
//
////                putExtra(
////                    "MY_DATA",
////                    MyData(
////                        listForField8_1[inputStep.value],
////                        listForCalory[inputCalory.value],
////                        listForDistance[inputDistance.value]
////                    )
////                )
//            })
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
