package uz.codial6.codial.fragments.singup

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import uz.codial6.codial.BuildConfig
import uz.codial6.codial.R
import uz.codial6.codial.databinding.BottomSheetDialogItemBinding
import uz.codial6.codial.databinding.FragmentUserDataBinding
import uz.codial6.codial.utils.UserData
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)    // LocalDateTime.now()  uchun
class UserDataFragment : Fragment() {

    lateinit var binding: FragmentUserDataBinding
    lateinit var context: FragmentActivity
    lateinit var realtimeDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference        //path lar bilan ishlash uchun
    lateinit var storageRef: StorageReference
    lateinit var uploadUrl: String
    lateinit var photoUri: Uri
    var selectedImagePath = ""

    // for permission
    private val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                photoUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID,
                    createImageFile()
                )
                getTakeImageContent.launch(photoUri)
            } else {
                Snackbar.make(binding.root, "Camera required", Snackbar.LENGTH_INDEFINITE).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUserDataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = requireActivity()
        realtimeDatabase = FirebaseDatabase.getInstance()
        reference = realtimeDatabase.getReference("users")
        storageRef = FirebaseStorage.getInstance().getReference("UsersImage")

        binding.done.setOnClickListener {
            if (binding.TIEName.text!!.isNotEmpty() || binding.TIESurname.text!!.isNotEmpty()) {
                writeData()
                findNavController().navigate(R.id.action_userDataFragment_to_listOfCoursesFragment)
            } else {
                Toast.makeText(context, "Berilgan maydonlarni to`ldiring!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.image.setOnClickListener {
            openBottomSheetDialog()
        }
    }

    private fun openBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bindingDialog = BottomSheetDialogItemBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bindingDialog.root)

        bindingDialog.camera.setOnClickListener {
            addFromCamera()
            bottomSheetDialog.dismiss()
        }

        bindingDialog.gallery.setOnClickListener {
            addFromGallery()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun addFromCamera() {
        requestPermission()
    }

    private fun addFromGallery() {
        result.launch("image/*")
    }

    // for Camera
    private val getTakeImageContent =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                binding.image.setImageURI(photoUri)
                val inputStream = context.contentResolver?.openInputStream(photoUri)
                val file = File(context.filesDir, "${LocalDateTime.now()}.jpg")
                val fileOutputStream = FileOutputStream(file)
                inputStream?.copyTo(fileOutputStream)
                inputStream?.close()
                fileOutputStream.close()
                selectedImagePath = file.absolutePath
            }
        }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_$format", ".jpg", externalFilesDir).apply {}
    }
// / - / - / - / - /

    // for Gallery
    private val result = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it ?: return@registerForActivityResult
        binding.image.setImageURI(it)
        val inputStream = context.contentResolver?.openInputStream(it)
        val file = File(context.filesDir, "${LocalDateTime.now()}.jpg")
        val fileOutputStream = FileOutputStream(file)
        inputStream?.copyTo(fileOutputStream)
        inputStream?.close()
        fileOutputStream.close()
        selectedImagePath = file.absolutePath
    }
// / - / - / - / - /

    private fun writeData() {
        if (selectedImagePath != "") {
            val bitmap = binding.image.drawable.toBitmap()
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val storageRefChild =
                storageRef.child("${Firebase.auth.currentUser!!.phoneNumber}_user_image")
            val uploadTask = storageRefChild.putBytes(byteArray)

            val task = uploadTask.continueWithTask { task ->
                storageRefChild.downloadUrl
            }.addOnCompleteListener { task ->
                uploadUrl = task.result.toString()

                // RealtimeDatabase ga malumot yozish
                val user = UserData(
                    reference.push().key!!,
                    binding.TIEName.text.toString().trim(),
                    binding.TIESurname.text.toString().trim(),
                    uploadUrl
                )
                reference.child("${Firebase.auth.currentUser!!.phoneNumber}").setValue(user)
                // - / - / - / - /
            }
        } else {
            val user = UserData(
                reference.push().key!!,
                binding.TIEName.text.toString().trim(),
                binding.TIESurname.text.toString().trim(),
                "null"
            )
            reference.child("${Firebase.auth.currentUser!!.phoneNumber}").setValue(user)
        }
    }

    // Permission
    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                photoUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.APPLICATION_ID,
                    createImageFile()
                )
                getTakeImageContent.launch(photoUri)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

}