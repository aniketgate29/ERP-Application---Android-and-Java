package com.example.bottomnavigationdemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Invoice_AddProd extends AppCompatActivity {

    private Spinner spinnerBatch;
    private AutoCompleteTextView spinnerProducts;
    private EditText rateEditText, quantityEditText,date, unitEditText, discountEditText, totalEditText;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private ArrayAdapter<String> batchAdapter;
    private String selectedBatch;
    private String batchQuantity;
    List<String> productNames;
    private String batchExpiry;

    private String selectedProduct,rate,unit,total,quantity,discount;
    private Button save, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_addprod);

        initializeViews();
        firestore = FirebaseFirestore.getInstance();

        fetchProductNames();
               batchAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBatch.setAdapter(batchAdapter);

        spinnerBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedBatch = spinnerBatch.getSelectedItem().toString();
                updateBatchDetails(selectedBatch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // No action needed when nothing is selected
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToMainActivity();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEditTextFields();
            }
        });
    }

    private void fetchBatchData(String selectedProduct) {
        showProgressBar();
        Log.d("Invoice_AddProd", "Fetching batch data for product: " + selectedProduct);

        firestore.collection("AddBatch")
                .whereEqualTo("productName", selectedProduct)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            List<String> batchNames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String batchName = document.getString("productBatch");
                                if (batchName != null) {
                                    batchNames.add(batchName);
                                }
                            }
                            Log.d("Invoice_AddProd", "Number of batch documents fetched: " + batchNames.size());
                            updateBatchSpinner(batchNames);
                        } else {
                            handleFirestoreError("Error fetching batches", task.getException());
                        }
                    }
                });
    }

    // Add the following method to update the batch spinner
    private void updateBatchSpinner(List<String> batchNames) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batchAdapter.clear();
                if (batchNames != null) {
                    for (String batchName : batchNames) {
                        if (batchName != null) {
                            batchAdapter.add(batchName);
                        }
                    }
                }
                batchAdapter.notifyDataSetChanged();
                Log.d("Invoice_AddProd", "Updated batch spinner with batch names");
            }
        });
    }

    // Add the following method to update the quantity and expiry date based on the selected batch
    private void updateBatchDetails(String selectedBatch) {
        if (selectedBatch != null && !selectedBatch.isEmpty()) {
            firestore.collection("AddBatch")
                    .whereEqualTo("productName", selectedProduct)
                    .whereEqualTo("productBatch", selectedBatch)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                    batchQuantity = document.getString("quantity");
                                    batchExpiry = document.getString("selectedDate");

                                    // Update the UI with the batch details
                                    updateBatchDetailsUI(batchQuantity, batchExpiry);
                                }
                            }
                        }
                    });
        }
    }

    // Add the following method to update the UI with batch details
    private void updateBatchDetailsUI(String quantity, String expiry) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update the UI with quantity and expiry details
                // You can set these values to the appropriate EditText fields
                // For example:
                quantityEditText.setText(quantity);
                date.setText(expiry);
            }
        });
    }

    private void initializeViews() {
        spinnerProducts = findViewById(R.id.Products);
        spinnerBatch = findViewById(R.id.spinnerBatch);
        rateEditText = findViewById(R.id.rate);
        quantityEditText = findViewById(R.id.quantity);
        unitEditText = findViewById(R.id.unit);
        discountEditText = findViewById(R.id.discount);
        progressBar = findViewById(R.id.progressBar);
        totalEditText = findViewById(R.id.Total);
        save = findViewById(R.id.btnsave);
        cancel = findViewById(R.id.btncancel);
        date= findViewById(R.id.date);

        if (progressBar == null) {
            throw new IllegalStateException("Progress bar not found in the layout");
        }

        discountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateTotal();
            }

            private void calculateTotal() {
                try {
                    String rateStr = rateEditText.getText().toString();
                    String quantityStr = quantityEditText.getText().toString();
                    String discountStr = discountEditText.getText().toString();

                    // Check if any of the input values are empty
                    if (rateStr.isEmpty() || quantityStr.isEmpty() || discountStr.isEmpty()) {
                        totalEditText.setText("");  // Set total to empty if any input is empty
                        return;
                    }

                    double rate = Double.parseDouble(rateStr);
                    double quantity = Double.parseDouble(quantityStr);
                    double discount = Double.parseDouble(discountStr);

                    double total = rate * quantity * (1 - discount / 100);
                    String formattedTotal = String.format("%.2f", total);

                    totalEditText.setText(String.valueOf(formattedTotal));
                } catch (NumberFormatException e) {
                    // Handle the exception (e.g., display an error message)
                    totalEditText.setText("");  // Set total to empty in case of an exception
                    Log.e("ProductEntryActivity", "Error parsing input values", e);
                }
            }

        });
    }
    private void fetchProductNames() {
        showProgressBar();
        productNames = new ArrayList<>();

        firestore.collection("ProdRegproducts")
                .get()
                .addOnCompleteListener(task -> {
                    hideProgressBar();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String productName = document.getString("productName");
                            if (productName != null) {
                                productNames.add(productName);
                            }
                        }
                        Log.d("Invoice_AddProd", "Fetched " + productNames.size() + " product names");
                        // Set up ArrayAdapter for AutoCompleteTextView after fetching product names
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_dropdown_item_1line, productNames);
                        spinnerProducts.setAdapter(adapter);
                    } else {
                        handleFirestoreError("Error fetching product names", task.getException());
                    }
                });

        spinnerProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Product = (String) parent.getItemAtPosition(position);
                selectedProduct = Product;

                if (selectedProduct != null && !selectedProduct.isEmpty()) {
                    showProgressBar();

                    firestore.collection("ProdRegproducts")
                            .whereEqualTo("productName", selectedProduct)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    hideProgressBar();
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                            String qt = document.getString("openingQty");
                                            String unit1 = document.getString("unit");
                                            String price = document.getString("withTaxMRP");
                                            updateUI(qt, price, unit1);
                                            fetchBatchData(selectedProduct);
                                        } else {
                                            handleFirestoreError("No data found for the selected product", null);
                                            clearEditTextFields();
                                        }
                                    } else {
                                        handleFirestoreError("Error fetching product data", task.getException());
                                        clearEditTextFields();
                                    }
                                }
                            });
                }

            }
        });

    }


    private void sendDataToMainActivity() {
        // Check if any of the EditText fields is null
        if (rateEditText == null || quantityEditText == null ||
                unitEditText == null || discountEditText == null || totalEditText == null) {
            // Handle the null references here, for example, show a Toast and return
            Toast.makeText(this, "One or more fields are null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the values from the EditText fields
        String rate = rateEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String unit = unitEditText.getText().toString().trim();
        String discount = discountEditText.getText().toString().trim();
        String total = totalEditText.getText().toString().trim();
        String expiry = date.getText().toString().trim();

        // Create an Intent to send the data back to MainActivity
        Intent intent = new Intent();
        intent.putExtra("productName", selectedProduct);
        intent.putExtra("rate", rate);
        intent.putExtra("quantity", quantity);
        intent.putExtra("unit", unit);
        intent.putExtra("discount", discount);
        intent.putExtra("total", total);
        intent.putExtra("batch", selectedBatch);
        intent.putExtra("expiry", expiry );

        updateBatch(selectedBatch, quantity);
        updateproduct(selectedProduct, quantity );
        // Set the result and finish the activity
        setResult(RESULT_OK, intent);
        finish();

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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void updateUI(String qt, String price, String unit1) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                quantityEditText.setText(qt);
                rateEditText.setText(price);
                unitEditText.setText(unit1);
            }
        });
    }

    private void clearEditTextFields() {
        rateEditText.setText("");
        quantityEditText.setText("");
        unitEditText.setText("");
        discountEditText.setText("");
        totalEditText.setText("");
    }

    private void handleFirestoreError(String message, Exception exception) {
        Log.e("ProductEntryActivity", message, exception);
        Toast.makeText(Invoice_AddProd.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
