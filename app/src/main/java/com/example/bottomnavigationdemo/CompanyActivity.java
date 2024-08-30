package com.example.bottomnavigationdemo;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CompanyActivity extends AppCompatActivity {

    EditText companyNameEditText, ownerEditText, addressEditText, cityEditText, zipCodeEditText,
            countryEditText, emailEditText, phoneNumberEditText, gstEditText, phoneNumber2EditText, stateEditText;
    private LinearLayout productEntriesContainer1;

    Spinner stateSpinner;
    Button registerButton,bankButton;
    Button financialYearButton, currentDateButton;
    Calendar calendar;

    FirebaseFirestore db;
    String selectedDate, currentDate,selectedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        financialYearButton = findViewById(R.id.financialYearButton);

        currentDateButton = findViewById(R.id.currentDateButton);
        calendar = Calendar.getInstance();
        stateSpinner = findViewById(R.id.spinnerState);
        gstEditText = findViewById(R.id.gstEditText);


        financialYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFinancialYearDatePicker();
            }
        });

        currentDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentDatePicker();
            }
        });

        db = FirebaseFirestore.getInstance();
        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);

        companyNameEditText = findViewById(R.id.editTextCompanyName);
        ownerEditText = findViewById(R.id.editTextOwner);
        addressEditText = findViewById(R.id.editTextAddress);
        cityEditText = findViewById(R.id.editTextCity);
        zipCodeEditText = findViewById(R.id.editTextZipCode);
        countryEditText = findViewById(R.id.editTextCountry);
        emailEditText = findViewById(R.id.editTextEmail);
        phoneNumberEditText = findViewById(R.id.PhoneNumber);
        phoneNumber2EditText = findViewById(R.id.PhoneNumber2);
        registerButton = findViewById(R.id.buttonAdd);
        bankButton = findViewById(R.id.addbank);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.state_names,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(adapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedState = adapterView.getItemAtPosition(position).toString();
                // Do something with the selected state
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do something when nothing is selected
            }
        });

        bankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(CompanyActivity.this,BankDetailsActivity.class);
                startActivityForResult(intent, 1);

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCompany();
                finish();
            }
        });
    }

    private void showFinancialYearDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        Toast.makeText(CompanyActivity.this, "Financial Year: " + selectedDate, Toast.LENGTH_SHORT).show();
                        financialYearButton.setText(selectedDate);
                    }
                }, year, month, 1);
        datePickerDialog.show();
    }

    private void showCurrentDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        currentDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        Toast.makeText(CompanyActivity.this, "Current Date: " + currentDate, Toast.LENGTH_SHORT).show();
                        currentDateButton.setText(currentDate);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void registerCompany() {
        String companyName = companyNameEditText.getText().toString();
        String owner = ownerEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String state = selectedState; // Use stateEditText here
        String zipCode = zipCodeEditText.getText().toString();
        String country = countryEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String phoneNumber2 = phoneNumber2EditText.getText().toString();
        String gst = gstEditText.getText().toString(); // Change variable name here

        // Check for null values
        if (selectedDate == null || currentDate == null) {
            // Handle the situation where dates are null
            return;
        }

        Map<String, Object> company = new HashMap<>();
        company.put("companyName", companyName);
        company.put("owner", owner);
        company.put("address", address);
        company.put("city", city);
        company.put("state", state);
        company.put("zipCode", zipCode);
        company.put("country", country);
        company.put("email", email);
        company.put("phoneNumber", phoneNumber);
        company.put("phoneNumber2", phoneNumber2);
        company.put("gst", gst);
        company.put("financialYear", selectedDate);
        company.put("currentDate", currentDate);
        company.put("Banks", BankEntries);


        db.collection("CompanyMaster")
                .add(company)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CompanyActivity.this, "Company Registered Successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CompanyActivity.this, "Failed to Register Company!", Toast.LENGTH_SHORT).show();
                    Log.e("CompanyActivity", "Error adding document", e);
                });
    }
    // Declaration of BankEntries list
    private List<Map<String, Object>> BankEntries = new ArrayList<>();

    // Updated onActivityResult() method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Retrieve bank details from the BankDetailsActivity
            String bankName = data.getStringExtra("bankName");
            String branchName = data.getStringExtra("branchName");
            String address = data.getStringExtra("address");
            String accountNumber = data.getStringExtra("accountNumber");
            String ifscCode = data.getStringExtra("ifscCode");

            // Create a Map to store bank details
            Map<String, Object> bankEntry = new HashMap<>();
            bankEntry.put("bankName", bankName);
            bankEntry.put("branchName", branchName);
            bankEntry.put("address", address);
            bankEntry.put("accountNumber", accountNumber);
            bankEntry.put("ifscCode", ifscCode);

            // Add bank entry to the list
            BankEntries.add(bankEntry);

            // Add bank card to the layout
            addBankCard(bankName, branchName, address, accountNumber, ifscCode);
        }
    }

    private void addBankCard(String bankName, String branchName, String address, String accountNumber, String ifscCode) {
        // Inflate the bank card layout
        View bankCardView = getLayoutInflater().inflate(R.layout.bank_card, null);

        // Find views in the inflated layout
        TextView bankNameTextView = bankCardView.findViewById(R.id.bankNameTextView);
        TextView branchNameTextView = bankCardView.findViewById(R.id.branch);
        TextView accountNumberTextView = bankCardView.findViewById(R.id.editAccountNo);
        TextView ifscCodeTextView = bankCardView.findViewById(R.id.IFSC);
        Button closeButton = bankCardView.findViewById(R.id.close);

        // Set bank details to the views
        bankNameTextView.setText(bankName);
        branchNameTextView.setText(branchName);
        accountNumberTextView.setText(accountNumber);
        ifscCodeTextView.setText(ifscCode);

        // Add the bank card view to the layout
        productEntriesContainer1.addView(bankCardView);

        // Set OnClickListener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the corresponding card view from the layout
                productEntriesContainer1.removeView(bankCardView);
                // Remove the bank entry from the list
                removeBankEntry(bankName, branchName, address, accountNumber, ifscCode);
            }
        });
    }

    // Method to remove the bank entry from the list
    private void removeBankEntry(String bankName, String branchName, String address, String accountNumber, String ifscCode) {
        for (int i = 0; i < BankEntries.size(); i++) {
            Map<String, Object> entry = BankEntries.get(i);
            if (entry.get("bankName").equals(bankName) && entry.get("branchName").equals(branchName)
                    && entry.get("address").equals(address) && entry.get("accountNumber").equals(accountNumber)
                    && entry.get("ifscCode").equals(ifscCode)) {
                BankEntries.remove(i);
                break;
            }
        }
    }}

