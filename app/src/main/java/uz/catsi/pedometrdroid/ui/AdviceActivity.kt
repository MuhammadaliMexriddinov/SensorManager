package uz.catsi.pedometrdroid.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import uz.catsi.pedometrdroid.R
import uz.catsi.pedometrdroid.local.SharedPref
import java.util.Locale

class AdviceActivity : AppCompatActivity() {

    lateinit var btnBack:ImageView
    val pref = SharedPref.getInstance()
    lateinit var themeActivity : ConstraintLayout
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_maslahat)
        when(pref.language){
            "uz"->setLocate("uz")
            "ru"->setLocate("ru")
        }

        btnBack=findViewById(R.id.btnBackk)
        themeActivity=findViewById(R.id.maslahat)

        when(pref.theme){
            "blue"->{
                themeActivity.setBackgroundResource(R.color.blue)
                this.window.setStatusBarColor(ContextCompat.getColor(this, com.owl93.dpb.R.color.blue))

            }
            "black"->{
                themeActivity.setBackgroundResource(R.color.theme_black)
                this.window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_black))

            }
            "purple"->{
                themeActivity.setBackgroundResource(R.color.theme_purple)
                this.window.setStatusBarColor(ContextCompat.getColor(this,R.color.theme_purple))
            }
        }


        btnBack.setOnClickListener {
            startActivity(Intent(this@AdviceActivity, MainActivity::class.java))
        }
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