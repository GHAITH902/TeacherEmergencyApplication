package com.ghaithfattoum.teacheremergencyapplication.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ghaithfattoum.teacheremergencyapplication.databinding.ActivityMainBinding
import com.google.firebase.messaging.FirebaseMessaging
import java.io.InputStream

const val TOPIC = "emergency"

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                this,
                "You won't get any emergency notification unless you have this permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.event.observe(this, ::handleEvent)

        // Asking for notification permission if it is not granted
        askNotificationPermission()

        // subscribe to topic so that I can receive notification. This should only be in the student Apps
        // It is here now for testing purposes
        subscribeToTopic()

        binding.sendNotification.setOnClickListener {
            viewModel.sendNotification(
                accessTokenFileInputStream = getAccessTokenFileInputStream(),
                title = binding.editTextTitle.text.toString(),
                message = binding.editTextMessage.text.toString()
            )
        }
    }

    private fun handleEvent(event: MainViewModel.UiEvent) {
        when (event) {
            MainViewModel.UiEvent.NotificationSentSuccessfully -> showToast("Notification has been sent successfully")
            MainViewModel.UiEvent.FailedSendingNotification -> showToast("Notification has NOT been sent successfully, please try again")
        }
    }

    private fun showToast(toastMessage: String) =
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()

    private fun getAccessTokenFileInputStream(): InputStream {
        val assetManager = this.assets
        return assetManager.open("service-account.json")
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).addOnCompleteListener { task ->
            var msg = "Subscribed"
            if (!task.isSuccessful) {
                msg = "Subscribe failed"
            }
            Log.d(TAG, msg)
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

