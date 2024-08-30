package com.example.bottomnavigationdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddServiceActivity extends AppCompatActivity {

    private EditText editServiceName, editDescription, editHSN, editRate, editTotal;
    private Spinner gstBox;
    private Button btnAdd, btnCancel;

    private FirebaseFirestore database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        database = FirebaseFirestore.getInstance();

        // Initialize UI components
        editServiceName = findViewById(R.id.editServiceName);
        editDescription = findViewById(R.id.editDescription);
        editHSN = findViewById(R.id.editHSN);
        editRate = findViewById(R.id.editRate);
        editTotal = findViewById(R.id.editTotal);
        gstBox = findViewById(R.id.gstBox);

        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel6);

        String[] gstNumbers = {"0", "5", "12", "18", "28"};
        ArrayAdapter<String> gstAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gstNumbers);
        gstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gstBox.setAdapter(gstAdapter);

        // Set onClickListeners for buttons
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecord();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement search functionality
                Cancel();
            }
        });

        // Calculate total whenever gst or rate changes
        gstBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                calculateTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        editRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });
    }

    private void calculateTotal() {
        String gstValue = gstBox.getSelectedItem().toString();
        String rateValue = editRate.getText().toString();

        if (!TextUtils.isEmpty(gstValue) && !TextUtils.isEmpty(rateValue)) {
            double gst = Double.parseDouble(gstValue);
            double rate = Double.parseDouble(rateValue);

            // Calculate total based on gst and rate
            double total = rate + gst;
            editTotal.setText(String.valueOf(total));
        }
    }

    private void addRecord() {
        String serviceName = editServiceName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String hsn = editHSN.getText().toString().trim();
        String gst = gstBox.getSelectedItem().toString(); // Get selected GST value from spinner
        String rate = editRate.getText().toString().trim();
        String total = editTotal.getText().toString();

        // Check if service name is empty
        if (serviceName.isEmpty()) {
            editServiceName.setError("Service name is required");
            editServiceName.requestFocus();
            return;
        }

        // Query Firestore to check if service name already exists
        database.collection("AddService")
                .whereEqualTo("serviceName", serviceName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Service name already exists, show error message
                            editServiceName.setError("Service name already exists");
                            editServiceName.requestFocus();
                        } else {
                            // Service name does not exist, proceed to add the record
                            Map<String, Object> data = new HashMap<>();
                            data.put("serviceName", serviceName);
                            data.put("description", description);
                            data.put("hsn", hsn);
                            data.put("gst", gst);
                            data.put("rate", rate);
                            data.put("total", total);

                            // Add data to Firestore
                            database.collection("AddService")
                                    .add(data)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        // Clear EditText fields after successful addition
                                        // clearEditTextFields();
                                    })
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }


    public void Cancel() {
        editServiceName.setText("");
        editDescription.setText("");
        editHSN.setText("");
        editRate.setText("");
        editTotal.setText("");
        finish();
    }
}
