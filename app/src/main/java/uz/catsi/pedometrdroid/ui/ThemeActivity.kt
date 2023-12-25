package uz.catsi.pedometrdroid.ui

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import uz.catsi.pedometrdroid.R
import uz.catsi.pedometrdroid.local.SharedPref
import java.util.Locale

class ThemeActivity : AppCompatActivity() {

    lateinit var btnBack: ImageView
    lateinit var blueTheme: ImageView
    lateinit var blackTheme: ImageView
    lateinit var purpleTheme: ImageView
    lateinit var uzb: LinearLayout
    lateinit var rus: LinearLayout
    lateinit var btnActivate: LinearLayout
    lateinit var btnOthers: LinearLayout
    lateinit var txtUz: TextView
    lateinit var txtRu: TextView
    lateinit var themeActivity: ConstraintLayout
    val pref = SharedPref.getInstance()

    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)

        btnBack = findViewById(R.id.btnBackkSet)
        blueTheme = findViewById(R.id.btnBlue)
        blackTheme = findViewById(R.id.btnBlack)
        purpleTheme = findViewById(R.id.btnPurple)
        uzb = findViewById(R.id.l1)
        rus = findViewById(R.id.l2)
        btnActivate = findViewById(R.id.activateSettings)
        btnOthers = findViewById(R.id.othersSettings)
        txtUz = findViewById(R.id.txtUz)
        txtRu = findViewById(R.id.txtRu)
        themeActivity = findViewById(R.id.themeView)

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
                uzb.setBackgroundResource(R.drawable.shape_theme_black)
                rus.setBackgroundResource(R.drawable.shape_theme_black)
                btnActivate.setBackgroundResource(R.drawable.shape_theme_black)
                btnOthers.setBackgroundResource(R.drawable.shape_theme_black)

            }

            "purple" -> {
                themeActivity.setBackgroundResource(R.color.theme_purple)
                this.window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_purple))
                uzb.setBackgroundResource(R.drawable.shape_theme_purple)
                rus.setBackgroundResource(R.drawable.shape_theme_purple)
                btnActivate.setBackgroundResource(R.drawable.shape_theme_purple)
                btnOthers.setBackgroundResource(R.drawable.shape_theme_purple)
            }
        }

        boldLangText()
        clickEvents()

        btnBack.setOnClickListener {
            startActivity(Intent(this@ThemeActivity, MainActivity::class.java))

        }

        blueTheme.setOnClickListener {
            themeActivity.setBackgroundResource(R.color.blue)
            this.window.setStatusBarColor(ContextCompat.getColor(this, com.owl93.dpb.R.color.blue))
            uzb.setBackgroundResource(R.drawable.blue_shape)
            rus.setBackgroundResource(R.drawable.blue_shape)
            btnActivate.setBackgroundResource(R.drawable.blue_shape)
            btnOthers.setBackgroundResource(R.drawable.blue_shape)
            pref.theme = "blue"
        }

        blackTheme.setOnClickListener {
            themeActivity.setBackgroundResource(R.color.theme_black)
            this.window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_black))
            uzb.setBackgroundResource(R.drawable.shape_theme_black)
            rus.setBackgroundResource(R.drawable.shape_theme_black)
            btnActivate.setBackgroundResource(R.drawable.shape_theme_black)
            btnOthers.setBackgroundResource(R.drawable.shape_theme_black)
            pref.theme = "black"
        }

        purpleTheme.setOnClickListener {
            themeActivity.setBackgroundResource(R.color.theme_purple)
            this.window.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_purple))
            uzb.setBackgroundResource(R.drawable.shape_theme_purple)
            rus.setBackgroundResource(R.drawable.shape_theme_purple)
            btnActivate.setBackgroundResource(R.drawable.shape_theme_purple)
            btnOthers.setBackgroundResource(R.drawable.shape_theme_purple)
            pref.theme = "purple"
        }

    }

    private fun boldLangText(locale: Locale = resources.configuration.locale) {
        txtUz.setTypeface(null, NORMAL)
        txtRu.setTypeface(null, NORMAL)
        when (locale.toLanguageTag()) {
            "uz" -> {
                txtUz.setTypeface(null, BOLD)
            }

            "ru" -> {
                txtRu.setTypeface(null, BOLD)
            }
        }
    }

    private fun clickEvents() {


        uzb.setOnClickListener {
            clearAllCheck()
            setLocate("uz")
            txtUz.setTypeface(null, BOLD)
            txtUz.textSize = 22f
        }

        rus.setOnClickListener {
            clearAllCheck()
            setLocate("ru")
            txtRu.setTypeface(null, BOLD)
            txtRu.textSize = 22f

        }

        btnActivate.setOnClickListener{
            val  intent =  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); startActivity(intent);
        }

        btnOthers.setOnClickListener {
            if (areNotificationsEnabled(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
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

    fun clearAllCheck() {
        txtUz.textSize = 18f
        txtRu.textSize = 18f
        txtUz.setTypeface(null, Typeface.NORMAL)
        txtRu.setTypeface(null, Typeface.NORMAL)
    }


    fun areNotificationsEnabled(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }

}