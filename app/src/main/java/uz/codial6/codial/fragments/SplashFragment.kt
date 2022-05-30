package uz.codial6.codial.fragments

import android.app.PendingIntent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.codial6.codial.R
import uz.codial6.codial.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var context: FragmentActivity
    lateinit var auth: FirebaseAuth
    lateinit var realtimeDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference

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
        auth = Firebase.auth
        realtimeDatabase = FirebaseDatabase.getInstance()
        reference = realtimeDatabase.getReference("users")
        transition()


    }

    private fun transition() {
        binding.splashImage.alpha = 0f
        binding.splashImage.animate().setDuration(1500).alpha(1f).withEndAction {
            if (auth.currentUser == null) {
                context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                findNavController().navigate(R.id.action_splashFragment_to_signUpFragment)
            } else {
                checkUserDatabase()
            }
        }
    }

    private fun checkUserDatabase() {
        reference.child(auth.currentUser!!.phoneNumber!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        context.overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out)
                        findNavController().navigate(R.id.action_splashFragment_to_userDataFragment)
                    } else {
                        context.overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out)
                        findNavController().navigate(R.id.action_splashFragment_to_listOfCoursesFragment)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }
}