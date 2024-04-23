package com.example.stickynotes.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.stickynotes.Fragment.HomeFragment;
import com.example.stickynotes.Fragment.ProfileFragment;
import com.example.stickynotes.R;
import com.example.stickynotes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getHomePage();
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                getHomePage();
            }else if (item.getItemId() == R.id.profile) {
                getProfilePage();
            } else {
                getHomePage();
            }
            return true;
        });
    }


    void getHomePage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
    }
    void getProfilePage() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
    }
}