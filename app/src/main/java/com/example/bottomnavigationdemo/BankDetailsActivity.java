package com.example.bottomnavigationdemo;

// BankDetailsActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BankDetailsActivity extends AppCompatActivity {

    private EditText editTextBankName, editTextBranch, editTextAddress, editTextAccountNumber, editTextIFSC;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details_company);

        // Initialize EditText fields
        editTextBankName = findViewById(R.id.editTextBankName);
        editTextBranch = findViewById(R.id.editTextBranch);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextAccountNumber = findViewById(R.id.editTextAccountNumber);
        editTextIFSC = findViewById(R.id.editTextIFSC);

        // Initialize Save button
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBankDetails();
            }
        });
    }

    private void saveBankDetails() {
        // Retrieve entered bank details
        String bankName = editTextBankName.getText().toString().trim();
        String branchName = editTextBranch.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String accountNumber = editTextAccountNumber.getText().toString().trim();
        String ifscCode = editTextIFSC.getText().toString().trim();

        // Validate if all fields are filled
        if (bankName.isEmpty() || branchName.isEmpty() || address.isEmpty() || accountNumber.isEmpty() || ifscCode.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an Intent to hold the bank details
        Intent resultIntent = new Intent();
        resultIntent.putExtra("bankName", bankName);
        resultIntent.putExtra("branchName", branchName);
        resultIntent.putExtra("address", address);
        resultIntent.putExtra("accountNumber", accountNumber);
        resultIntent.putExtra("ifscCode", ifscCode);

        // Set the result and finish the activity
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void clearFields() {
        editTextBankName.setText("");
        editTextBranch.setText("");
        editTextAddress.setText("");
        editTextAccountNumber.setText("");
        editTextIFSC.setText("");
    }
}
