package com.example.bottomnavigationdemo;



import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class godownActivity extends AppCompatActivity {

    private EditText editTextGodownName, editTextAbout;
    private Button btnAdd, btnDelete;

    // Firestore instance
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_godown);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editTextGodownName= findViewById(R.id.editTextGodownName);
        editTextAbout = findViewById(R.id.editTextAbout);
        btnAdd = findViewById(R.id.btnAdd);
        btnDelete = findViewById(R.id.btnDelete);

        // Set onClickListener for Add button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String godownName = editTextGodownName.getText().toString().trim();
                String about = editTextAbout.getText().toString().trim();

                // Create a new godown object
                Map<String, Object> godown = new HashMap<>();
                godown.put("godownName", godownName);
                godown.put("about", about);

                // Add data to Firestore
                db.collection("addgodown")
                        .add(godown)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(godownActivity.this, "Godown added successfully with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(godownActivity.this, "Error adding godown: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Set onClickListener for Delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add your logic here for deleting a godown
                // For example, you can show a toast message
                Toast.makeText(godownActivity.this, "Delete button clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
