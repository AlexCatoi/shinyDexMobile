package com.example.proiect;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isInternetAvailable()) {
            showNoInternetDialog();
        }
        showInternetUsageNotification();
        if (savedInstanceState == null) {
            // Load the default fragment
            loadFragment(new PokemonListFragment());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.full_pokemon_list) {
                selectedFragment = new PokemonListFragment();
            } else if (item.getItemId() == R.id.nav_add_pokemon) {
                selectedFragment = new AddPokemonFragment();
            }
            else if(item.getItemId()==R.id.nav_games){
                selectedFragment = new GamesFragment();
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void applyFilters(String searchText, String seeAll, String type, String game, int generation) {
        PokemonListFragment mainFragment = (PokemonListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.d("MainActivity", "applyFilters called with type: " + type);
        if (mainFragment == null) {
            Log.e("MainActivity", "PokemonListFragment is null. Ensure it is properly added to the container.");
        } else {
            mainFragment.updateFilters(searchText, type, seeAll);
        }
    }

    private void showInternetUsageNotification() {
        String channelId = "internet_usage_channel";
        String channelName = "Internet Usage";
        int notificationId = 1;

        // Create the Notification Channel (Required for Android 8.0 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifies when the app is using the internet connection.");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Check permission for notifications (required for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission if it's not already granted
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                return; // Exit the method for now, notification will be handled in onRequestPermissionsResult
            }
        }

        // Build the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle("Internet Connection in Use")
                .setContentText("This application is currently using your internet connection.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the Notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());
    }
    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        }
        return false;
    }
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection")
                .setMessage("Your internet connection is currently turned off. Please enable it to continue.")
                .setPositiveButton("Turn On", (dialog, which) -> {
                    // Open network settings
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 1);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Close the application
                })
                .setCancelable(false)
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // Check if the request code matches
            if (isInternetAvailable()) {
                // Restart the activity to reinitialize components
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                // Show the dialog again if there's still no connection
                showNoInternetDialog();
            }
        }
    }
}

