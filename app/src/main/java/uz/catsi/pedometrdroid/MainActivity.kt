package uz.catsi.pedometrdroid

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import uz.catsi.pedometrdroid.callback.stepsCallback
import uz.catsi.pedometrdroid.databinding.ActivityMainBinding
import uz.catsi.pedometrdroid.helper.GeneralHelper
import uz.catsi.pedometrdroid.service.StepDetectorService

class MainActivity : AppCompatActivity() , stepsCallback {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, StepDetectorService::class.java)
        startService(intent)

        StepDetectorService.subscribe.register(this)
    }


    override fun subscribeSteps(steps: Int) {
        binding.TVSTEPS.setText(steps.toString())
        binding.TVCALORIES.setText(GeneralHelper.getCalories(steps))
        binding.TVDISTANCE.setText(GeneralHelper.getDistanceCovered(steps))
    }
}