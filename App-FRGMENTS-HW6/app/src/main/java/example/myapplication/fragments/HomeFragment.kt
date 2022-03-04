package example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import example.myapplication.databinding.HomeViewBinding
import example.myapplication.navigate

class HomeFragment : Fragment() {

    lateinit var binding: HomeViewBinding
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        count = HomeFragmentArgs.fromBundle(requireArguments()).count
        with(binding.includedLayout) {
            text.text = count.toString()
            button.setOnClickListener {
                navigate(
                    HomeFragmentDirections.actionHomeFragmentSelf()
                        .setCount(this@HomeFragment.count + 1)
                )
            }
        }

    }


}