package uz.catsi.pedometrdroid.local

import android.content.Context
import android.content.SharedPreferences
import uz.catsi.pedometrdroid.app.App
import java.util.*


/**
Mobile Developer
Creator:Mekhriddinov Muhammadali
 */
class SharedPref {


    private val preferences: SharedPreferences =
        App.context!!.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)



    val editor = preferences.edit()


    companion object {
        private lateinit var myPref: SharedPref

        fun getInstance(): SharedPref {
            if (!this::myPref.isInitialized) {
                myPref = SharedPref()
            }
            return myPref
        }
    }

    val ui=UUID.randomUUID().toString()

    var intExamplePref: String ?
        get() = preferences.getString("APP_PREF_INT_EXAMPLE", ui)
        set(value) = preferences.edit().putString("APP_PREF_INT_EXAMPLE", value).apply()


    var stepName: String ?
        get() = preferences.getString("stepname", "10000")
        set(value) = preferences.edit().putString("stepname", value).apply()


    var stepCount: String ?
        get() = preferences.getString("stepCount", "10000")
        set(value) = preferences.edit().putString("stepCount", value).apply()

    var stepCalory: String ?
        get() = preferences.getString("stepCalory", "8000")
        set(value) = preferences.edit().putString("stepCalory", value).apply()


    var stepDistance: String ?
        get() = preferences.getString("stepDistance", "2500")
        set(value) = preferences.edit().putString("stepDistance", value).apply()

    var language: String?
        get() = preferences.getString("language", "uz")
        set(language) {
            preferences.edit().putString("language", language).apply()
        }

    var theme: String?
        get() = preferences.getString("theme", "blue")
        set(theme) {
            preferences.edit().putString("theme", theme).apply()
        }





}