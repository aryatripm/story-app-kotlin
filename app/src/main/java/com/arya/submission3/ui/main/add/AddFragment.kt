package com.arya.submission3.ui.main.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.arya.submission3.R
import com.arya.submission3.databinding.FragmentAddBinding
import com.arya.submission3.ui.main.MainActivity
import com.arya.submission3.utils.Result
import com.arya.submission3.utils.ViewModelFactory
import com.arya.submission3.utils.reduceFileImage
import com.arya.submission3.utils.rotateBitmap
import com.arya.submission3.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.Locale

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private val addViewModel : AddViewModel by activityViewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var tempFile: File? = null
    private var token: String = ""
    private var tempLat: Double? = null
    private var tempLon: Double? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        addViewModel.setUploaded(false)
        addViewModel.isUploaded.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                addViewModel.setUploaded(false)
            }
        }

        addViewModel.getUserToken().observe(viewLifecycleOwner) {
            token = it
        }

        binding.btnCamera.setOnClickListener {
            val intent = Intent(activity, CameraActivity::class.java)
            launcherIntentCameraX.launch(intent)
        }

        binding.btnGallery.setOnClickListener {
            val intent = Intent()
            intent.action = ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            launcherIntentGallery.launch(chooser)
        }

        getMyLocation()

        binding.btnUpload.setOnClickListener {
            if (tempFile != null && binding.edAddDescription.editText?.text?.isNotBlank()!!) {
                upload(tempFile, binding.edAddDescription.editText?.text.toString())
            } else if(binding.edAddDescription.editText?.text?.isEmpty()!!) {
                Toast.makeText(requireContext(), "Please add description first", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please add image first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == MainActivity.CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            tempFile = myFile
            val resultImg = rotateBitmap(
                BitmapFactory.decodeFile(tempFile?.path),
                isBackCamera
            )
            binding.detailPhoto.setImageBitmap(resultImg)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = context?.let { uriToFile(selectedImg, it) }
            tempFile = myFile

            binding.detailPhoto.setImageURI(selectedImg)
        }
    }

    private val launcherRequestLocation = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it[Manifest.permission.ACCESS_FINE_LOCATION] == true -> getMyLocation()
                it[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> getMyLocation()
            }
        }

    private fun getMyLocation() {
        if ((ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(requireActivity().applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                binding.swAddLocation.isEnabled = true
                binding.swAddLocation.text = getString(R.string.locationFormat, getAddressName(it.latitude, it.longitude))
                tempLat = it.latitude
                tempLon = it.longitude
            }
        } else {
            launcherRequestLocation.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun getAddressName(lat: Double, lon: Double): String? {
        var addressName: String? = null
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressName = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressName
    }

    private fun upload(file: File?, desc: String) {
        val compressFile = reduceFileImage(file as File)

        val description = desc.toRequestBody("text/plain".toMediaType())
        var lat: RequestBody? = null
        var lon: RequestBody? = null
        if (binding.swAddLocation.isChecked) {
            lat = tempLat.toString().toRequestBody("text/plain".toMediaType())
            lon = tempLon.toString().toRequestBody("text/plain".toMediaType())
        }
        val requestImageFile = compressFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            compressFile.name,
            requestImageFile
        )

        addViewModel.upload(imageMultipart, description, lat, lon, token).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    addViewModel.setLoading(false)
                    addViewModel.setUploaded(true)
                }

                is Result.Loading -> {
                    addViewModel.setLoading(true)
                }

                is Result.Error -> {
                    addViewModel.setLoading(false)
                    addViewModel.setError(it.message ?: "An error occurred")
                }
            }
        }
    }
}