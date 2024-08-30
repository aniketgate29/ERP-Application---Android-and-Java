package com.example.bottomnavigationdemo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallanOutActivity extends AppCompatActivity {

    private EditText supplierNameEditText,selectDateEditText, mobileNoEditText, pinNoEditText,totalAmountEditText, stateOfSupplyEditText;
    private Button addButton, cancelButton, btnAddProduct;
    private List<Map<String, Object>> productEntries;
    private LinearLayout productEntriesContainer;
    private Spinner spinnerState, gstBox;
    String selectedState;

    private double totalAmt = 0.0; // Variable to store the total amount


    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challanout);

        initializeViews();
        productEntriesContainer = findViewById(R.id.productEntries1);

        firestore = FirebaseFirestore.getInstance();

        productEntries = new ArrayList<>();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the product entry activity when "Add Product" button is clicked
                Intent intent = new Intent(ChallanOutActivity.this, product_entry.class);
                startActivityForResult(intent, 1);

            }
        });
        selectDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }

            private void showDatePickerDialog() {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ChallanOutActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Set the selected date in the EditText
                                selectDateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        },
                        year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });     // Set up the spinner with state names
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.state_names,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter);

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        //for gst

        String[] gstNumbers = {"0", "5", "12", "18", "28"};
        ArrayAdapter<String> gst = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gstNumbers);
        gst.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gstBox.setAdapter(gst);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPurchaseEntryToFirestore();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEditTextFields();
            }
        });
    }

    private void initializeViews() {
        supplierNameEditText = findViewById(R.id.cname);
        mobileNoEditText = findViewById(R.id.mobileno);
        pinNoEditText = findViewById(R.id.editPincode);
        totalAmountEditText = findViewById(R.id.tamount);
        stateOfSupplyEditText = findViewById(R.id.address);
        addButton = findViewById(R.id.btnAdd);
        cancelButton = findViewById(R.id.btndelete);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        selectDateEditText = findViewById(R.id.selectDate);
        spinnerState = findViewById(R.id.spinnerState);
        gstBox = findViewById(R.id.gstBox);

    }

    private void addPurchaseEntryToFirestore() {
        String supplierName = supplierNameEditText.getText().toString().trim();
        String mobileNo = mobileNoEditText.getText().toString().trim();
        String totalAmount = totalAmountEditText.getText().toString().trim();
        String stateOfSupply = stateOfSupplyEditText.getText().toString().trim();
        String date = selectDateEditText.getText().toString().trim();
        String PinCode = pinNoEditText.getText().toString().trim();
        String gst = String.valueOf(gstBox.getSelectedItem()); // Retrieve GST value





        // Check if any field is empty
        if (supplierName.isEmpty() || mobileNo.isEmpty()||gst.isEmpty()  || totalAmount.isEmpty() || stateOfSupply.isEmpty()|| date.isEmpty()|| PinCode.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }
        // Calculate the total amount by summing up the total of each product


        // Create a Map to store purchase entry details
        Map<String, Object> purchaseEntry = new HashMap<>();
        purchaseEntry.put("date", date);
        purchaseEntry.put("pincode",PinCode);
        purchaseEntry.put("Customer Name", supplierName);
        purchaseEntry.put("mobileNo", mobileNo);
        purchaseEntry.put("totalAmount", totalAmount);
        purchaseEntry.put("Address", stateOfSupply);
        purchaseEntry.put("State",selectedState);
        purchaseEntry.put("products", productEntries);
        purchaseEntry.put("GST",gst);

        // Add the purchase entry to Firestore
        firestore.collection("challanOut")
                .add(purchaseEntry)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            showToast("Purchase entry added successfully");
                            clearEditTextFields();
                            productEntries.clear(); // Clear product entries after adding to Firestore
                        } else {
                            showToast("Error adding purchase entry to Firestore");
                        }
                    }
                });
    }

    private void clearEditTextFields() {
        supplierNameEditText.setText("");
        mobileNoEditText.setText("");
        totalAmountEditText.setText("");
        stateOfSupplyEditText.setText("");
        selectDateEditText.setText("");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Retrieve product details from the product entry activity
            String productName = data.getStringExtra("productName");
            String rate = data.getStringExtra("rate");
            String quantity = data.getStringExtra("quantity");
            String unit = data.getStringExtra("unit");
            String discount = data.getStringExtra("discount");
            String total = data.getStringExtra("total");

            // Create a Map to store product details


            // Update the totalAmountEditText with the calculated total
            totalAmountEditText.setText(String.valueOf(totalAmt));
            Map<String, Object> productEntry = new HashMap<>();
            productEntry.put("productName", productName);
            productEntry.put("rate", rate);
            productEntry.put("quantity", quantity);
            productEntry.put("unit", unit);
            productEntry.put("discount", discount);
            productEntry.put("total", total);

            // Add product entry to the list
            productEntries.add(productEntry);
            addProductCard(productName, quantity, discount, total);
            totalAmt = calculateTotalAmount();
            totalAmountEditText.setText(String.valueOf(totalAmt));


        }
    }
    private double calculateTotalAmount() {
        return productEntries.stream()
                .mapToDouble(entry -> Double.parseDouble(entry.get("total").toString()))
                .sum();
    }

    private void addProductCard(String productName, String quantity, String discount, String total) {
        View productCardView = getLayoutInflater().inflate(R.layout.product_card, null);

        TextView productNameTextView = productCardView.findViewById(R.id.productNameTextView);
        TextView qtyTextView = productCardView.findViewById(R.id.qty);
        TextView tolTextView = productCardView.findViewById(R.id.TolTextView);
        TextView disTextView = productCardView.findViewById(R.id.DisTextView);

        productNameTextView.setText(productName);
        qtyTextView.setText(quantity);
        tolTextView.setText(total);
        disTextView.setText(discount);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer.addView(productCardView);
    }




}
