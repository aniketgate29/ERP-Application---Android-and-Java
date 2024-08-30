package com.example.bottomnavigationdemo;






import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class sales_product_return extends AppCompatActivity {

    private Spinner spinnerProducts;
    private EditText rateEditText, quantityEditText, unitEditText, discountEditText, totalEditText;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private String selectedProduct,rate,unit,total,quantity,discount,selectedDocumentId;
    private Button save, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_product_return);
        initializeViews();
        firestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            selectedDocumentId = intent.getStringExtra("documentId");
            Log.d("Return_parches", "Document ID fetched successfully: " + selectedDocumentId);
            if (selectedDocumentId != null) {
                fetchProducts(selectedDocumentId);
            } else {
                Toast.makeText(this, "No document ID found", Toast.LENGTH_SHORT).show();
            }

        }






        spinnerProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedProduct = spinnerProducts.getSelectedItem().toString();
                fetchProductData(selectedProduct,selectedDocumentId);
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

    private void initializeViews() {
        spinnerProducts = findViewById(R.id.spinnerProductName);
        rateEditText = findViewById(R.id.rate);
        quantityEditText = findViewById(R.id.quantity);
        unitEditText = findViewById(R.id.batch);
        discountEditText = findViewById(R.id.discount);
        progressBar = findViewById(R.id.progressBar);
        totalEditText = findViewById(R.id.Total);
        save = findViewById(R.id.btnsave);
        cancel = findViewById(R.id.btncancel);

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

    private void fetchProducts(String documentId) {
        showProgressBar();
        Log.d("Return_parches", "Fetching products for documentId: " + documentId);

        List<String> productNames = new ArrayList<>();

        firestore.collection("invoice").document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");
                                if (products != null) {
                                    for (Map<String, Object> product : products) {
                                        String productName = (String) product.get("productName");
                                        if (productName != null) {
                                            productNames.add(productName);
                                            Log.d("Return_parches", "Product names fetched successfully: " + productNames);
                                        } else {
                                            Log.d("Return_parches", "Product name is null");
                                        }
                                    }
                                    // Call populateSpinner on the main thread
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            populateSpinner(productNames);
                                        }
                                    });
                                } else {
                                    Log.d("Return_parches", "No products found in the document");
                                    // Handle case where no products are found
                                }
                            } else {
                                Log.d("Return_parches", "Document does not exist or is null");
                                // Handle case where document doesn't exist
                            }
                        } else {
                            handleFirestoreError("Error fetching document", task.getException());
                        }
                    }
                });
        if (firestore == null) {
            Log.e("Return_parches", "Firestore instance is null");
            return;
        }
    }
    private void populateSpinner(List<String> productNames) {
        Log.d("SpinnerData", "Product names fetched: " + productNames.toString()); // Log the list of product names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, productNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducts.setAdapter(adapter);
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
        String batch = unitEditText.getText().toString().trim();
        String returnqty = discountEditText.getText().toString().trim();
        String total = totalEditText.getText().toString().trim();

        // Create an Intent to send the data back to MainActivity
        Intent intent = new Intent();
        intent.putExtra("productName", selectedProduct);
        intent.putExtra("rate", rate);
        intent.putExtra("quantity", quantity);
        intent.putExtra("batch", batch);
        intent.putExtra("return", returnqty);
        intent.putExtra("total", total);
        updateBatch(batch,returnqty);
        updateproduct(selectedProduct, returnqty );
        updateinvoice(selectedDocumentId,selectedProduct,returnqty,total);

        // Set the result and finish the activity
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateinvoice(String selectedDocumentId, String selectedProduct, String returnqty, String total) {
        // Get the reference to the invoice document in Firestore
        DocumentReference invoiceRef = firestore.collection("invoice").document(selectedDocumentId);

        // Fetch the current data of the invoice
        invoiceRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");
                    if (products != null) {
                        // Loop through the products array to find the matching product
                        for (Map<String, Object> product : products) {
                            String productName = (String) product.get("productName");
                            if (selectedProduct.equals(productName)) {
                                // Get the current quantity and total from the product
                                String currentQuantityStr = (String) product.get("quantity");
                                String currentTotalStr = (String) product.get("total");

                                // Convert the current quantity and total to integers
                                int currentQuantity = Integer.parseInt(currentQuantityStr);
                                double currentTotal = Double.parseDouble(currentTotalStr);

                                // Subtract the return quantity from the current quantity
                                int newQuantity = currentQuantity - Integer.parseInt(returnqty);

                                // Calculate the new total after subtracting the returned quantity
                                double newTotal = currentTotal - Double.parseDouble(total);

                                // Update the quantity and total fields of the matching product
                                product.put("quantity", String.valueOf(newQuantity));
                                product.put("total", String.valueOf(newTotal));
                                break; // Break the loop after updating the product
                            }
                        }
                        // Update the invoice document with the modified products array
                        invoiceRef.update("products", products)
                                .addOnSuccessListener(aVoid -> {
                                    // Products array updated successfully
                                    showToast("Products array updated successfully");
                                })
                                .addOnFailureListener(e -> {
                                    // Error updating products array
                                    showToast("Failed to update products array: " + e.getMessage());
                                });
                    } else {
                        showToast("No products found in the invoice");
                    }
                } else {
                    showToast("Invoice document does not exist");
                }
            } else {
                showToast("Error fetching invoice document: " + task.getException());
            }
        });
    }



    private void fetchProductData(String selectedProduct, String documentId) {
        showProgressBar();

        firestore.collection("invoice")
                .document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");
                                if (products != null) {
                                    for (Map<String, Object> product : products) {
                                        String productName = (String) product.get("productName");
                                        if (selectedProduct.equals(productName)) {
                                            String qt = (String) product.get("quantity");
                                            String price = (String) product.get("rate");
                                            String batch = (String) product.get("batch");

                                            if (price != null) {
                                                updateUI(qt, price, batch);
                                                return; // Stop iterating once product data is found
                                            } else {
                                                handleFirestoreError("No price found for the selected product", null);
                                                clearEditTextFields();
                                                return; // Stop iterating if price is not found
                                            }
                                        }
                                    }
                                    // If loop completes without finding the product
                                    handleFirestoreError("No data found for the selected product", null);
                                    clearEditTextFields();
                                } else {
                                    handleFirestoreError("No products found in the document", null);
                                    clearEditTextFields();
                                }
                            } else {
                                handleFirestoreError("Document does not exist", null);
                                clearEditTextFields();
                            }
                        } else {
                            handleFirestoreError("Error fetching document", task.getException());
                            clearEditTextFields();
                        }
                    }


                });
    }

    private void updateUI(String qt, String price, String batch) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                quantityEditText.setText(qt);
                rateEditText.setText(price);
                unitEditText.setText(batch);
            }
        });
    }

    private void clearEditTextFields() {
        rateEditText.setText("");
        quantityEditText.setText("");
        unitEditText.setText("");
        discountEditText.setText("");
        totalEditText.setText("");
        finish();
    }

    private void handleFirestoreError(String message, Exception exception) {
        Log.e("ProductEntryActivity", message, exception);
        Toast.makeText(sales_product_return.this, message, Toast.LENGTH_SHORT).show();
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
                                        int newQuantity = previousQuantity + quantityToAdd;

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
                                        int newQuantity = previousQuantity +  quantityToAdd;

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

}

