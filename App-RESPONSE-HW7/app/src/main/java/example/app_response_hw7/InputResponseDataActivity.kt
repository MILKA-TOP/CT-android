package example.app_response_hw7

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import example.app_response_hw7.databinding.InfoResponseBinding

class InputResponseDataActivity : AppCompatActivity() {

    private lateinit var binding: InfoResponseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = InfoResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.send.setOnClickListener {

            if (hasEmptyLines()) return@setOnClickListener

            intent = Intent()
            intent.putExtra(TITLE, binding.title.text.toString())
            intent.putExtra(BODY, binding.body.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun hasEmptyLines(): Boolean {
        if (binding.title.text.toString().isEmpty() || binding.body.text.toString().isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.input_text), Toast.LENGTH_SHORT)
                .show()
            return true
        }
        return false
    }

}