package com.project.foodflux;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bumptech.glide.Glide;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAuth = FirebaseAuth.getInstance();

        ImageView startImageView = findViewById(R.id.startImageView);

        // Load the GIF using Glide
        Glide.with(this)
                .asGif()  // Tell Glide to load a GIF
                .load(R.drawable.start)  // The GIF resource
                .into(startImageView);  // Set the ImageView
    }
    public void startConversion(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(StartActivity.this, DashboardActivity.class));
        } else {
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
        }
    }
}
