package com.example.bottomnavigationdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddTypeActivity extends AppCompatActivity {

    private EditText editTextId;
    private EditText editTextProductType;
    private EditText editTextAbout;
    private FirebaseFirestore database;

    private int serialNumberCounter = 1; // Counter for generating serial numbers


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_type);

        database = FirebaseFirestore.getInstance();
        // Initialize UI elements
        editTextId = findViewById(R.id.editTextId);
        editTextProductType = findViewById(R.id.editTextProductType);
        editTextAbout = findViewById(R.id.editTextAbout);

        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform some action when the "Add" button is clicked
                String id = editTextId.getText().toString();
                String productType = editTextProductType.getText().toString();
                String about = editTextAbout.getText().toString();

                Map<String, Object> data = new HashMap<>();
                data.put("id", id);
                data.put("productType", productType);
                data.put("about", about);

                database.collection("addType")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                            // Clear EditText fields after successful addition
                            clearEditTextFields();
                        })
                        .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));

            }
        });


        Button btnCancel = findViewById(R.id.btnCancel5);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear input fields when the "Cancel" button is clicked
                editTextId.setText("");
                editTextProductType.setText("");
                editTextAbout.setText("");
                finish();
            }
        });


    }
    private void clearEditTextFields() {
        editTextId.setText("");
        editTextProductType.setText("");
        editTextAbout.setText("");
    }


}