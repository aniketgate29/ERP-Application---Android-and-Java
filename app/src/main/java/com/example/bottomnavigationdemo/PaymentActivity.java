package com.example.bottomnavigationdemo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private Spinner spinnerPaymentMode, spinnerAccountt;
    private EditText editTextUpiId, editTextCardNumber, editTextExpiryDate, editTextCreditCardNumber, editTextCreditExpiryDate, editTextCustomerName, editTextAmount;
    private Button buttonVerify, buttonPay;
    private LinearLayout upiLayout, debitCardLayout, creditCardLayout;
    private EditText selecttransacDate;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        spinnerPaymentMode = findViewById(R.id.spinnerPaymentMode);
        editTextUpiId = findViewById(R.id.editTextUpiId);
        editTextCardNumber = findViewById(R.id.editTextCardNumber);
        editTextCustomerName = findViewById(R.id.editTextCustomerName);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextExpiryDate = findViewById(R.id.editTextExpiryDate);
        editTextCreditCardNumber = findViewById(R.id.editTextCreditCardNumber);
        editTextCreditExpiryDate = findViewById(R.id.editTextCreditExpiryDate);
        buttonVerify = findViewById(R.id.buttonVerify);
        buttonPay = findViewById(R.id.buttonPay);
        upiLayout = findViewById(R.id.upiLayout);
        debitCardLayout = findViewById(R.id.debitCardLayout);
        creditCardLayout = findViewById(R.id.creditCardLayout);
        selecttransacDate = findViewById(R.id.selecttransacDate);
        spinnerAccountt = findViewById (R.id.spinnerAccountt);




        // Set up payment mode spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.account_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountt.setAdapter(adapter);



        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.payment_mode_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMode.setAdapter(adapter1);

        spinnerPaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Show relevant layout based on selected payment mode
                String selectedMode = parent.getItemAtPosition(position).toString();
                switch (selectedMode) {
                    case "UPI":
                        upiLayout.setVisibility(View.VISIBLE);
                        debitCardLayout.setVisibility(View.GONE);
                        creditCardLayout.setVisibility(View.GONE);
                        break;
                    case "Debit Card":
                        upiLayout.setVisibility(View.GONE);
                        debitCardLayout.setVisibility(View.VISIBLE);
                        creditCardLayout.setVisibility(View.GONE);
                        break;
                    case "Credit Card":
                        upiLayout.setVisibility(View.GONE);
                        debitCardLayout.setVisibility(View.GONE);
                        creditCardLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        upiLayout.setVisibility(View.GONE);
                        debitCardLayout.setVisibility(View.GONE);
                        creditCardLayout.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up calendar for transaction date
        selecttransacDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Verify UPI ID
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String upiId = editTextUpiId.getText().toString().trim();
                // Perform validation or verification logic for the UPI ID here
                if (!TextUtils.isEmpty(upiId)) {
                    // Implement your verification logic here
                    // For example, you can check if the UPI ID follows a certain pattern
                    // and show a message accordingly
                    if (isValidUpiId(upiId)) {
                        // UPI ID is valid
                        Toast.makeText(PaymentActivity.this, "UPI ID Verified", Toast.LENGTH_SHORT).show();
                    } else {
                        // UPI ID is invalid
                        Toast.makeText(PaymentActivity.this, "Invalid UPI ID", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // UPI ID is empty
                    Toast.makeText(PaymentActivity.this, "Please enter UPI ID", Toast.LENGTH_SHORT).show();
                }
            }

            private boolean isValidUpiId(String upiId) {

                return upiId.matches("[a-zA-Z0-9]+@[a-zA-Z]+");
            }
        });


        // Pay Button
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // Redirect to next page
                    redirectToNextPage();
                }
            }
        });
    }

    // Method to display calendar for selecting transaction date
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        selecttransacDate.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // Method to verify UPI payment


    // Method to validate input fields
    private boolean validateFields() {
        String selectedMode = spinnerPaymentMode.getSelectedItem().toString();

        switch (selectedMode) {
            case "UPI":
                String upiId = editTextUpiId.getText().toString().trim();
                if (TextUtils.isEmpty(upiId)) {
                    Toast.makeText(this, "Please enter UPI ID", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case "Debit Card":
                String debitCardNumber = editTextCardNumber.getText().toString().trim();
                if (TextUtils.isEmpty(debitCardNumber) || debitCardNumber.length() != 16) {
                    Toast.makeText(this, "Please enter a valid debit card number", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String debitExpiryDate = editTextExpiryDate.getText().toString().trim();
                if (!isValidExpiryDate(debitExpiryDate)) {
                    Toast.makeText(this, "Please enter a valid expiry date (MM/YY) for debit card", Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Perform validation for debit card number and expiry date if needed
                break;
            case "Credit Card":
                String creditCardNumber = editTextCreditCardNumber.getText().toString().trim();
                if (TextUtils.isEmpty(creditCardNumber) || creditCardNumber.length() != 16) {
                    Toast.makeText(this, "Please enter a valid credit card number", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String creditExpiryDate = editTextCreditExpiryDate.getText().toString().trim();
                if (!isValidExpiryDate(creditExpiryDate)) {
                    Toast.makeText(this, "Please enter a valid expiry date (MM/YY) for credit card", Toast.LENGTH_SHORT).show();
                    return false;
                }
                // Perform validation for credit card number and expiry date if needed
                break;
        }
        String customerName = editTextCustomerName.getText().toString().trim();
        if (TextUtils.isEmpty(customerName)) {
            editTextCustomerName.setError("Customer name is required");
            editTextCustomerName.requestFocus();
            return false;
        }

        


        String transactionDate = selecttransacDate.getText().toString().trim();
        if (TextUtils.isEmpty(transactionDate)) {
            Toast.makeText(this, "Please select transaction date", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate Amount
        String amountString = editTextAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountString)) {
            editTextAmount.setError("Amount is required");
            editTextAmount.requestFocus();
            return false;
        }

        try {
            double amount = Double.parseDouble(amountString);
            if (amount <= 0) {
                editTextAmount.setError("Amount must be greater than zero");
                editTextAmount.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editTextAmount.setError("Invalid amount format");
            editTextAmount.requestFocus();
            return false;
        }


        // Validate other fields if needed
        // For example, validate customer name, amount, transaction date, etc.

        return true;
    }

    private boolean isValidExpiryDate(String creditExpiryDate) {
        // Check if the expiry date is in the format MM/YY and within valid range
        String expiryDate = null;
        if (expiryDate.matches("\\d{2}/\\d{2}")) {
            // Extract month and year from the expiry date
            String[] parts = expiryDate.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]);
            // Check if the month is in the range 1-12 and the year is in the future
            return (month >= 1 && month <= 12) && (year >= 21); // Assuming the current year is 2021
        }
        return false;

    }

    // Method to redirect to the next page
    private void redirectToNextPage() {
        String customerName = editTextCustomerName.getText().toString().trim();
        String transactionDate = selecttransacDate.getText().toString().trim();
        String paymentMode = spinnerPaymentMode.getSelectedItem().toString();
        double amount = Double.parseDouble(editTextAmount.getText().toString().trim());

        // Create a new document with a generated ID
        Map<String, Object> payment = new HashMap<>();
        payment.put("customerName", customerName);
        payment.put("transactionDate", transactionDate);
        payment.put("paymentMode", paymentMode);
        payment.put("amount", amount);

        // Add a new document with a generated ID
        db.collection("paymentactivity")
                .add(payment)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
                    // Redirect to the next page
                    Intent intent = new Intent(PaymentActivity.this, Paymentsuccessful.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Error occurred while adding document
                    Toast.makeText(this, "Failed to save payment details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });



        // Pass payment details to ReceiptActivity
        Intent intent = new Intent(PaymentActivity.this, ReceiptActivity.class);
        intent.putExtra("customerName", customerName);
        intent.putExtra("transactionDate", transactionDate);
        intent.putExtra("paymentMode", paymentMode);
        intent.putExtra("amount", amount);
        startActivity(intent);
    }
}
