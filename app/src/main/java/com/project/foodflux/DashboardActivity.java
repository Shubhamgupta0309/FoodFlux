package com.project.foodflux;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private List<FoodItem> foodList;
    private DatabaseReference database;
    private Button addFoodButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addFoodButton = findViewById(R.id.addFoodButton);
        foodList = new ArrayList<>();
        adapter = new FoodAdapter(foodList);
        recyclerView.setAdapter(adapter);

        // Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("foodItems");

        // Fetch food items
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FoodItem food = dataSnapshot.getValue(FoodItem.class);
                    foodList.add(food);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database error
            }
        });

        // Set listener for add food button
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example: Open AddFoodActivity to add a new food item
                Intent intent = new Intent(DashboardActivity.this, AddFoodActivity.class);
                startActivity(intent);
            }
        });
    }
}
