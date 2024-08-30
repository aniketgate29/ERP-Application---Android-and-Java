package com.example.bottomnavigationdemo;



import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class salesReturnActivity extends AppCompatActivity {

    private EditText mobileNoEditText, totalAmountEditText, stateOfSupplyEditText, selectDateEditText;
    private Button addButton, cancelButton, btnAddProduct;
    private AutoCompleteTextView supplierNameEditText;
    private List<Map<String, Object>> productEntries;
    private LinearLayout productEntriesContainer;
    private double totalAmt = 0.0; // Variable to store the total amount
    String documentId,tamt;


    private FirebaseFirestore firestore;
    private Calendar myCalendar;
    private List<String> invoiceNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return);

        initializeViews();
        productEntriesContainer = findViewById(R.id.productEntries1);

        firestore = FirebaseFirestore.getInstance();
        productEntries = new ArrayList<>();
        myCalendar = Calendar.getInstance();



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPurchaseEntryToFirestore();
                String totalAt = totalAmountEditText.getText().toString().trim();
                Double tAt = Double.parseDouble(totalAt);
                Double CTA =Double.parseDouble(tamt);
                Double total1 = CTA - tAt;
                updateinvoce(documentId,total1);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEditTextFields();
            }
        });

        selectDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(salesReturnActivity.this, dateSetListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fetchProductNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, invoiceNames);
        supplierNameEditText.setAdapter(adapter);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the supplier name from the EditText
                String supplierName = supplierNameEditText.getText().toString().trim();

                // Open the product entry activity when "Add Product" button is clicked
                Intent intent = new Intent(salesReturnActivity.this, sales_product_return.class);

                // Pass the supplier name as an extra to the intent
                intent.putExtra("documentId", documentId);
                startActivityForResult(intent, 1);
            }
        });

    }

    private void fetchProductNames() {
        invoiceNames = new ArrayList<>();
        firestore.collection("invoice")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String productName = document.getString("Customer Name");
                            invoiceNames.add(productName);
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to fetch product names", Toast.LENGTH_SHORT).show();
                    }
                });

        supplierNameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedInvoiceName = (String) parent.getItemAtPosition(position);
                firestore.collection("invoice")
                        .whereEqualTo("Customer Name", selectedInvoiceName)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    tamt = document.getString("totalAmount");
                                    String mno = document.getString("mobileNo");
                                    documentId = document.getId();
                                    String state = document.getString("State");

                                    mobileNoEditText.setText(mno);
                                    stateOfSupplyEditText.setText(state);
                                }
                            } else {
                                Log.e("Firestore", "Error getting documents: ", task.getException());
                                Toast.makeText(salesReturnActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void initializeViews() {
        supplierNameEditText = findViewById(R.id.cname);
        mobileNoEditText = findViewById(R.id.mobileno);
        totalAmountEditText = findViewById(R.id.tamount);
        stateOfSupplyEditText = findViewById(R.id.stateofsupp);
        selectDateEditText = findViewById(R.id.selectDate);
        addButton = findViewById(R.id.btnAdd);
        cancelButton = findViewById(R.id.btndelete);
        btnAddProduct = findViewById(R.id.btnAddProduct);
    }

    private void addPurchaseEntryToFirestore() {
        String supplierName = supplierNameEditText.getText().toString().trim();
        String mobileNo = mobileNoEditText.getText().toString().trim();
        String totalAmount = totalAmountEditText.getText().toString().trim();
        String stateOfSupply = stateOfSupplyEditText.getText().toString().trim();
        String selectedDate = selectDateEditText.getText().toString().trim();

        // Check if any field is empty
        if (supplierName.isEmpty() || mobileNo.isEmpty() || totalAmount.isEmpty() || stateOfSupply.isEmpty() || selectedDate.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        // Create a Map to store purchase entry details
        Map<String, Object> purchaseEntry = new HashMap<>();
        purchaseEntry.put("supplierName", supplierName);
        purchaseEntry.put("mobileNo", mobileNo);
        purchaseEntry.put("totalAmount", totalAmount);
        purchaseEntry.put("stateOfSupply", stateOfSupply);
        purchaseEntry.put("selectedDate", selectedDate);
        purchaseEntry.put("products", productEntries);

        // Add the purchase entry to Firestore
        firestore.collection("SalesReturn")
                .add(purchaseEntry)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            showToast("Purchase Return added successfully");
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
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            // Update the calendar instance
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update EditText with selected date
            updateDateLabel();
        }
    };

    // Helper method to update the EditText with selected date
    private void updateDateLabel() {
        String myFormat = "MM/dd/yyyy"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        selectDateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Retrieve product details from the product entry activity
            String productName = data.getStringExtra("productName");
            String rate = data.getStringExtra("rate");
            String quantity = data.getStringExtra("quantity");
            String batch = data.getStringExtra("batch");
            String returnqty = data.getStringExtra("return");
            String total = data.getStringExtra("total");

            // Create a Map to store product details
            Map<String, Object> productEntry = new HashMap<>();
            productEntry.put("productName", productName);
            productEntry.put("rate", rate);
            productEntry.put("batch", batch);
            productEntry.put("returnqty", returnqty);
            productEntry.put("total", total);

            // Add product entry to the list
            productEntries.add(productEntry);
            addProductCard(productName, returnqty, batch, total);
            totalAmt = calculateTotalAmount();
            totalAmountEditText.setText(String.valueOf(totalAmt));
        }
    }

    @SuppressLint("NewApi")
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
        TextView batchTextView = productCardView.findViewById(R.id.DisTextView1);
        Button btnDelete = productCardView.findViewById(R.id.delete);

        productNameTextView.setText(productName);
        batchTextView.setText("Batch");
        qtyTextView.setText(quantity);
        tolTextView.setText(total);
        disTextView.setText(discount);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer.addView(productCardView);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the product entry from the list
                productEntriesContainer.removeView(productCardView);
                totalAmt = totalAmt - Double.parseDouble(total);
                totalAmountEditText.setText(String.valueOf(totalAmt));
                updateBatch(discount,quantity);
                updateproduct(productName, quantity );
            }
        });
    }
    private void updateproduct(String productName, String quantity) {
        // Parse the quantity string to an integer
        int quantityToAdd = Integer.parseInt(quantity);

        // Query Firestore to get the document reference for the product
        firestore.collection("ProdRegproducts")
                .whereEqualTo("productName", productName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the document ID (product name) of the matching product
                            String productId = document.getId();

                            // Update the quantity of the product in Firestore
                            DocumentReference productRef = firestore.collection("ProdRegproducts").document(productId);
                            productRef.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                    if (documentSnapshot.exists()) {
                                        // Get the previous quantity stored as a string
                                        String previousQuantityStr = documentSnapshot.getString("openingQty");

                                        // Parse the previous quantity string to an integer
                                        int previousQuantity = 0;
                                        if (previousQuantityStr != null && !previousQuantityStr.isEmpty()) {
                                            previousQuantity = Integer.parseInt(previousQuantityStr);
                                        }

                                        // Add the new quantity to the previous quantity
                                        int newQuantity = previousQuantity - quantityToAdd;

                                        // Update the quantity of the product in Firestore
                                        productRef.update("openingQty", String.valueOf(newQuantity))
                                                .addOnSuccessListener(aVoid -> {
                                                    // Quantity updated successfully
                                                    showToast("Quantity updated successfully");
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Error updating quantity
                                                    showToast("Failed to update quantity");
                                                });
                                    } else {
                                        // Product document does not exist
                                        showToast("Product not found");
                                    }
                                } else {
                                    // Error getting document
                                    showToast("Error getting product details: " + task1.getException());
                                }
                            });
                        }
                    } else {
                        // Error getting documents
                        showToast("Error getting products: " + task.getException());
                    }
                });

    }
    private void updateBatch(String selectedBatch, String quantity) {
        // Parse the quantity string to an integer
        int quantityToAdd = Integer.parseInt(quantity);

        // Query Firestore to get the document reference for the product
        firestore.collection("AddBatch")
                .whereEqualTo("productBatch", selectedBatch)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the document ID (product name) of the matching product
                            String productId = document.getId();

                            // Update the quantity of the product in Firestore
                            DocumentReference productRef = firestore.collection("AddBatch").document(productId);
                            productRef.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                    if (documentSnapshot.exists()) {
                                        // Get the previous quantity stored as a string
                                        String previousQuantityStr = documentSnapshot.getString("quantity");

                                        // Parse the previous quantity string to an integer
                                        int previousQuantity = 0;
                                        if (previousQuantityStr != null && !previousQuantityStr.isEmpty()) {
                                            previousQuantity = Integer.parseInt(previousQuantityStr);
                                        }

                                        // Add the new quantity to the previous quantity
                                        int newQuantity = previousQuantity - quantityToAdd;

                                        // Update the quantity of the product in Firestore
                                        productRef.update("quantity", String.valueOf(newQuantity))
                                                .addOnSuccessListener(aVoid -> {
                                                    // Quantity updated successfully
                                                    showToast("Quantity updated successfully");
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Error updating quantity
                                                    showToast("Failed to update quantity");
                                                });
                                    } else {
                                        // Product document does not exist
                                        showToast("Product not found");
                                    }
                                } else {
                                    // Error getting document
                                    showToast("Error getting product details: " + task1.getException());
                                }
                            });
                        }
                    } else {
                        // Error getting documents
                        showToast("Error getting products: " + task.getException());
                    }
                });

    }
    private void updateinvoce(String invoiceId, double  updatedTotalAmount) {
        // Get reference to the invoice document
        DocumentReference invoiceRef = firestore.collection("invoice").document(invoiceId);

        // Create a map to update the document
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("totalAmount", String.valueOf(updatedTotalAmount));

        // Perform the update
        invoiceRef.update(updateData)
                .addOnSuccessListener(aVoid -> {
                    showToast("Invoice updated successfully");
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to update invoice: " + e.getMessage());
                });
    }

}
