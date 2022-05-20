package uz.codial6.codial.fragments

import android.app.PendingIntent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import uz.codial6.codial.R
import uz.codial6.codial.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var context: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        transition()

    }

    private fun transition() {
        binding.splashImage.alpha = 0f
        binding.splashImage.animate().setDuration(1500).alpha(1f).withEndAction {
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            findNavController().navigate(R.id.action_splashFragment_to_signUpFragment)
        }
    }
}