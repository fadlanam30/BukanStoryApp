package com.example.mystoryapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mystoryapp.databinding.ActivityAddStoryBinding
import com.example.mystoryapp.preferences.PrefViewModel
import com.example.mystoryapp.utils.reduceFileImage
import com.example.mystoryapp.utils.rotateBitmap
import com.example.mystoryapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private val prefViewModel: PrefViewModel by viewModels()

    private var _storyBinding: ActivityAddStoryBinding? = null
    private val binding get() = _storyBinding

    private val addStoryViewModel by viewModels<AddStoryViewModel>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var token: String

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, "Tidak mendapatkan permission.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        locationPermission()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _storyBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupViewModel()

        binding?.apply {
            btnBack.setOnClickListener { onBackPressed() }
            btnCamera.setOnClickListener { startCameraX() }
            btnGalery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener { uploadImageStory() }
        }

    }

    private fun setupViewModel() {
        prefViewModel.getInfo().observe(this) { story ->
            token = story.token
        }
    }

    private fun uploadImageStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding?.editTxtDescription?.text.toString()

            val currentImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                currentImageFile
            )

            addStoryViewModel.storyResponse.observe(this) { storyResponse ->
                if (!storyResponse.error) {
                    Toast.makeText(this, "Upload Success!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            AlertDialog.Builder(this).apply {
                setTitle("Hold Up!!")
                setMessage("Would u like to share your location with story ?")
                setPositiveButton("Yes") { _, _ ->
                    if (ActivityCompat.checkSelfPermission(
                            this@AddStoryActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                            addStoryViewModel.uploadStory(
                                token,
                                imageMultipart,
                                description,
                                location.latitude,
                                location.longitude
                            )
                        }
                    }

                    finish()
                }

                setNegativeButton("No") { _, _ ->
                    addStoryViewModel.uploadStory(token, imageMultipart, description, null, null)
                    finish()
                }
                create()
                show()
            }

            addStoryViewModel.isLoading.observe(this) {
                showLoading(it)
            }

        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                locationPermission()
            }
        }

    private fun locationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this@AddStoryActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private var getFile: File? = null

    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding?.previewImageView?.setImageBitmap(result)

            val bytes = ByteArrayOutputStream()
            result.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(this.contentResolver, result, "Title", null)
            val uri = Uri.parse(path.toString())
            getFile = uriToFile(uri, this)

        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile
            binding?.previewImageView?.setImageURI(selectedImg)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) binding?.progressBar?.visibility = View.VISIBLE
        else binding?.progressBar?.visibility = View.GONE
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onDestroy() {
        super.onDestroy()
        _storyBinding = null
    }

}