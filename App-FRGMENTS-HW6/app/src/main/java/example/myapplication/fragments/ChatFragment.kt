package example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import example.myapplication.databinding.ChatViewBinding
import example.myapplication.navigate

class ChatFragment : Fragment() {

    private lateinit var binding: ChatViewBinding
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        count = ChatFragmentArgs.fromBundle(requireArguments()).count
        with(binding.includedLayout) {
            text.text = count.toString()
            button.setOnClickListener {
                navigate(
                    ChatFragmentDirections.actionChatFragmentSelf()
                        .setCount(this@ChatFragment.count + 1)
                )
            }
        }
    }

}