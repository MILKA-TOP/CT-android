package example.app_images_hw4

import android.animation.AnimatorInflater
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import example.app_images_hw4.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        runOnUiThread {
            val a = AnimatorInflater.loadAnimator(this@MainActivity, R.animator.rotate)
            a.setTarget(binding.image)
            a.start()
        }

    }
}