package uz.codial6.codial.fragments.singup

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import uz.codial6.codial.R
import uz.codial6.codial.databinding.CheckNumberDialogBinding
import uz.codial6.codial.databinding.CheckNumberDialogForEnterCodeBinding
import uz.codial6.codial.databinding.FragmentEnterCodeBinding

class EnterCodeFragment : Fragment() {

    lateinit var binding: FragmentEnterCodeBinding
    lateinit var verificationId: String
    lateinit var phoneNumber: String
    lateinit var auth: FirebaseAuth
    lateinit var realtimeDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var context: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEnterCodeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        auth = Firebase.auth
        realtimeDatabase = FirebaseDatabase.getInstance()
        reference = realtimeDatabase.getReference("users")
        verificationId = arguments?.getString("verificationId")!!
        phoneNumber = arguments?.getString("phoneNumber")!!

        binding.done.setOnClickListener {
            if (binding.otpView.otp.toString()
                    .trim().length == 6 || verificationId == binding.otpView.otp
            ) {
                val credential = PhoneAuthProvider
                    .getCredential(verificationId, binding.otpView.otp!!)
                signInWithPhoneAuthCredential(credential)
            }
        }

        binding.back.setOnClickListener {
            callDialog()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.otpView.showSuccess()
                checkUserDatabase()
            } else {
                binding.otpView.showError()
            }
        }
    }

    private fun checkUserDatabase() {
        reference.child(auth.currentUser!!.phoneNumber!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) {
                        findNavController().navigate(R.id.action_enterCodeFragment_to_userDataFragment)
                    } else {
                        findNavController().navigate(R.id.action_enterCodeFragment_to_listOfCoursesFragment)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun callDialog() {
        val customDialog = AlertDialog.Builder(context).create()
        val bindingDialog = CheckNumberDialogForEnterCodeBinding.inflate(layoutInflater)
        customDialog.setView(bindingDialog.root)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bindingDialog.number.text = phoneNumber

        bindingDialog.edit.setOnClickListener {
            customDialog.dismiss()
            findNavController().popBackStack()
        }

        bindingDialog.dismiss.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
    }
}