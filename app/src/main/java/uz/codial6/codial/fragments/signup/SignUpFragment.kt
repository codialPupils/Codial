package uz.codial6.codial.fragments.singup

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.codial6.codial.R
import uz.codial6.codial.databinding.CheckNumberDialogBinding
import uz.codial6.codial.databinding.FragmentSignUpBinding
import java.util.concurrent.TimeUnit


class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    lateinit var context: FragmentActivity
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var auth: FirebaseAuth

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
        auth = Firebase.auth
        binding.done.isGone = true

        binding.METPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.done.isVisible = true
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.done.setOnClickListener {
            if (binding.METPhoneNumber.text!!.isNotEmpty()) {
                callDialog("+998${binding.METPhoneNumber.rawText}")
            } else if (binding.METPhoneNumber.rawText.toString().trim().length != 9) {
                Toast.makeText(context, "Raqam noto`g`ri kiritildi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        binding.progressBar.isVisible = true
        binding.done.isGone = true
        auth.setLanguageCode("uz")
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.isGone = true
                binding.done.isVisible = true
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                binding.progressBar.isGone = true
                binding.done.isVisible = true
                findNavController().navigate(R.id.action_signUpFragment_to_enterCodeFragment,
                    bundleOf(
                        "verificationId" to verificationId,
                        "phoneNumber" to "+998${binding.METPhoneNumber.rawText}")
                )
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun callDialog(phoneNumber: String) {
        val customDialog = AlertDialog.Builder(context).create()
        val bindingDialog = CheckNumberDialogBinding.inflate(layoutInflater)
        customDialog.setView(bindingDialog.root)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bindingDialog.number.text = phoneNumber

        bindingDialog.edit.setOnClickListener {
            customDialog.dismiss()
        }

        bindingDialog.yes.setOnClickListener {
            customDialog.dismiss()
            sendVerificationCode("+998${binding.METPhoneNumber.rawText}")
        }

        customDialog.show()
    }
}