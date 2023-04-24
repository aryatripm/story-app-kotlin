package com.arya.submission3.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.arya.submission3.R
import com.arya.submission3.databinding.ActivityMainBinding
import com.arya.submission3.ui.auth.AuthActivity
import com.arya.submission3.ui.auth.AuthViewModel
import com.arya.submission3.ui.main.add.AddViewModel
import com.arya.submission3.ui.main.detail.DetailViewModel
import com.arya.submission3.ui.main.list.ListViewModel
import com.arya.submission3.ui.maps.MapsActivity
import com.arya.submission3.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val authViewModel : AuthViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val listViewModel : ListViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val detailViewModel : DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val addViewModel : AddViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.container_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        listViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        addViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        listViewModel.errorMessage.observe(this) {
            showSnackbar(it)
        }
        detailViewModel.errorMessage.observe(this) {
            showSnackbar(it)
        }
        addViewModel.errorMessage.observe(this) {
            showSnackbar(it)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                authViewModel.logout()
                startActivity(
                    Intent(this, AuthActivity::class.java)
                )
                finish()
                return true
            }
            R.id.action_maps -> {
                startActivity(
                    Intent(
                        this, MapsActivity::class.java
                    )
                )
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.container_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }
}