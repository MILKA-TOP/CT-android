package example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import example.myapplication.databinding.FragmentViewBinding
import example.myapplication.navigate

class DictionaryFragment : Fragment() {

    lateinit var binding: FragmentViewBinding
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        count = DictionaryFragmentArgs.fromBundle(requireArguments()).count
        with(binding.includedLayout) {
            text.text = count.toString()
            button.setOnClickListener {
                navigate(
                    DictionaryFragmentDirections.actionDictFragmentSelf()
                        .setCount(this@DictionaryFragment.count + 1)
                )
            }
        }

    }


}