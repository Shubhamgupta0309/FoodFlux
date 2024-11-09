package com.project.foodflux;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddFoodActivity extends AppCompatActivity {

    private EditText nameEditText;
    private DatePicker expiryDatePicker; // Use DatePicker for expiry date
    private Spinner quantitySpinner; // Use Spinner for quantity
    private Button addButton;
    private DatabaseReference database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // Initialize the UI elements
        nameEditText = findViewById(R.id.foodNameEditText);
        expiryDatePicker = findViewById(R.id.expiryDatePicker);
        quantitySpinner = findViewById(R.id.quantitySpinner);
        addButton = findViewById(R.id.addButton);

        // Load the quantity options into the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.quantity_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner.setAdapter(adapter);

        // Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("foodItems");

        // Create notification channel (for Android O and above)
        createNotificationChannel();

        // Set button click listener to add food item to database
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFoodToDatabase();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Food Expiry Notifications";
            String description = "Notifications for food items approaching expiry";
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    "food_flux_channel", name, importance);
            channel.setDescription(description);
            android.app.NotificationManager notificationManager =
                    getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addFoodToDatabase() {
        // Get values from EditText fields
        String name = nameEditText.getText().toString().trim();

        // Check if name is empty
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a food name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected quantity from the Spinner
        Object selectedItem = quantitySpinner.getSelectedItem();
        if (selectedItem == null) {
            Toast.makeText(this, "Please select a quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantityText = selectedItem.toString().trim();

        // Validate quantity input
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the selected expiry date from the DatePicker
        int year = expiryDatePicker.getYear();
        int month = expiryDatePicker.getMonth() + 1; // Month is 0-based, so we add 1
        int day = expiryDatePicker.getDayOfMonth();
        String expiryDate = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);

        // Validate expiry date
        if (expiryDate.isEmpty()) {
            Toast.makeText(this, "Please select an expiry date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create food item and push to database
        FoodItem food = new FoodItem(name, quantity, expiryDate);
        database.push().setValue(food)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(AddFoodActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
                    scheduleExpiryNotifications(expiryDate); // Schedule notifications
                })
                .addOnFailureListener(e -> Toast.makeText(AddFoodActivity.this, "Failed to add food: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void scheduleExpiryNotifications(String expiryDate) {
        // Parse the expiry date into a Calendar object
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Assuming yyyy-MM-dd format
        try {
            Date date = sdf.parse(expiryDate);
            if (date == null) {
                Log.e("AddFoodActivity", "Invalid expiry date format");
                Toast.makeText(this, "Invalid expiry date format", Toast.LENGTH_SHORT).show();
                return;
            }

            calendar.setTime(date);
            long expiryTimeMillis = calendar.getTimeInMillis();
            long currentTimeMillis = System.currentTimeMillis();

            // Calculate the time for the first notification (1 minute after current time)
            long firstNotificationTime = currentTimeMillis + 60000; // 1 minute later

            // If the first notification is in the past, set it immediately
            if (firstNotificationTime < currentTimeMillis) {
                firstNotificationTime = currentTimeMillis;
            }

            // Set the alarm to trigger a notification every minute until expiry
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(this, ExpiryNotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            // Set repeating alarm (every minute)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstNotificationTime, 60000, pendingIntent);
            Log.d("AddFoodActivity", "Alarm set for expiry notification");

        } catch (ParseException e) {
            Log.e("AddFoodActivity", "Error parsing expiry date", e);
            Toast.makeText(this, "Error parsing expiry date", Toast.LENGTH_SHORT).show();
        }
    }
}
