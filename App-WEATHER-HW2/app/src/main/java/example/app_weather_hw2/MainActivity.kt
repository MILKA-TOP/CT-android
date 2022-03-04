package example.app_weather_hw2

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode

class MainActivity : AppCompatActivity(), View.OnClickListener {


    var isDarkTheme = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        isDarkTheme = systemTheme()

        setContentView(R.layout.activity_main)
    }

    override fun onClick(p0: View?) {
        if (!isDarkTheme) setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        isDarkTheme = !isDarkTheme
        setContentView(R.layout.activity_main)
    }

    private fun systemTheme(): Boolean {
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_NO -> return false
            Configuration.UI_MODE_NIGHT_YES -> return true
        }
        return false
    }
}