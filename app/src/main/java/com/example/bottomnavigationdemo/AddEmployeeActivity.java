package com.example.bottomnavigationdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEmployeeActivity extends AppCompatActivity {

    private EditText editTextEmployeeName, editTextAddress, editTextPanNumber,
            editTextMobileNumber, editTextEmail, editTextEmployeeCode, editTextAadharNumber;

    private Button btnAdd, btnCancel;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Initialize Firebase Firestore
        database = FirebaseFirestore.getInstance();

        // Initialize UI elements
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);

        editTextEmployeeName = findViewById(R.id.editTextEmployeeName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPanNumber = findViewById(R.id.editTextPanNumber);
        editTextMobileNumber = findViewById(R.id.editTextMobile);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmployeeCode = findViewById(R.id.editTextEmployeeCode);
        editTextAadharNumber = findViewById(R.id.editTextAadharNumber);

        // Set click listeners for buttons
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEmployee();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addEmployee() {
        // Get employee details from EditText fields
        String employeeName = editTextEmployeeName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String panNumber = editTextPanNumber.getText().toString().trim();
        String mobileNumber = editTextMobileNumber.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String employeeCode = editTextEmployeeCode.getText().toString().trim();
        String aadharNumber = editTextAadharNumber.getText().toString().trim();

        // Check if any field is empty
        if (employeeName.isEmpty() || address.isEmpty() || panNumber.isEmpty() || mobileNumber.isEmpty() || email.isEmpty() || employeeCode.isEmpty() || aadharNumber.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        // Check if PAN number has exactly 10 digits
        if (panNumber.length() != 10) {
            showToast("PAN number must contain exactly 10 digits");
            return;
        }

        // Check if mobile number has exactly 10 digits
        if (mobileNumber.length() != 10) {
            showToast("Mobile number must contain exactly 10 digits");
            return;
        }

        // Check if the employee name already exists
        database.collection("AddEmployee")
                .whereEqualTo("EmployeeName", employeeName) // Corrected to "EmployeeName"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        showNameExistsAlertDialog(employeeName);
                    } else {
                        // Create a map to store employee data
                        Map<String, Object> data = new HashMap<>();
                        data.put("EmployeeName", employeeName); // Corrected to "EmployeeName"
                        data.put("Address", address);
                        data.put("PanNumber", panNumber);
                        data.put("MobileNumber", mobileNumber);
                        data.put("Email", email);
                        data.put("EmployeeCode", employeeCode);
                        data.put("AadharNumber", aadharNumber);

                        // Add employee data to Firestore
                        database.collection("AddEmployee")
                                .add(data)
                                .addOnSuccessListener(documentReference -> {
                                    showToast("Employee added successfully");
                                    navigateToDetails(employeeName, address, employeeCode, panNumber, mobileNumber, email, aadharNumber);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error adding document", e);
                                    showToast("Failed to add employee");
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error checking existing employee", e);
                    showToast("Failed to add employee");
                });
    }

    private void showNameExistsAlertDialog(String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Employee with name '" + name + "' already exists!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing or handle any specific action
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToDetails(String employeeName, String address, String employeeCode, String pannumber, String mobilenumber, String email, String aadharnumber) {
        Intent intent = new Intent(); // Create an instance of Intent
        intent.putExtra("EmployeeName", employeeName);
        intent.putExtra("Address", address);
        intent.putExtra("Code", employeeCode);
        intent.putExtra("Pannumber", pannumber);
        intent.putExtra("MobileNumber", mobilenumber);
        intent.putExtra("Email", email);
        intent.putExtra("AadharNumber", aadharnumber);
        startActivity(intent);
    }

}