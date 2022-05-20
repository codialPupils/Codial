package uz.codial6.codial.fragments.singup

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import uz.codial6.codial.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    lateinit var context: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        setColor()

        binding.done.setOnClickListener {
            Toast.makeText(context, "+998${binding.METPhoneNumber.rawText}", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun setColor() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                context.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                context.window.statusBarColor =
                    ContextCompat.getColor(context, uz.codial6.codial.R.color.splash_color)
            }

            Configuration.UI_MODE_NIGHT_NO -> {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                context.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                context.window.statusBarColor =
                    ContextCompat.getColor(context, uz.codial6.codial.R.color.yellow)
            }
        }
    }
}