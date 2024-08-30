package com.example.bottomnavigationdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCropActivity extends AppCompatActivity {

    private EditText ed1, ed2;
    private Button btnAdd, btnCancel;

    private FirebaseFirestore database;
    private int serialNumber = 0; // Counter for serial numbers

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crop);

        ed1 = findViewById(R.id.ed1);
        ed2 = findViewById(R.id.ed2);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel4);
        database = FirebaseFirestore.getInstance();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear input fields when the "Cancel" button is clicked
                ed1.setText("");
                ed2.setText("");
                finish();
            }
        });
    }


    private void add() {
        String name = ed1.getText().toString();
        String description = ed2.getText().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("Name", name);
        data.put("description", description);

        database.collection("AddCrop")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    // Show toast message on successful addition
                    Toast.makeText(AddCropActivity.this, "Crop added successfully", Toast.LENGTH_SHORT).show();
                    // Clear EditText fields after successful addition
                   // clearEditTexts();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                    // Show toast message on failure
                    Toast.makeText(AddCropActivity.this, "Failed to add crop", Toast.LENGTH_SHORT).show();
                });
    }
}