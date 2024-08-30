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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class   Return_parches extends AppCompatActivity {

    private Spinner spinnerProducts;
    private EditText rateEditText, quantityEditText, unitEditText, discountEditText, totalEditText,availableQuantityEditText;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private String selectedProduct;
    private Button save, cancel;
    private String selectedDocumentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_entry);

        initializeViews();
        firestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            selectedDocumentId = intent.getStringExtra("documentId");
            Log.d("Return_parches", "Document ID fetched successfully: " + selectedDocumentId);

        }

        if (selectedDocumentId != null) {
            fetchProducts(selectedDocumentId);
        } else {
            Toast.makeText(this, "No document ID found", Toast.LENGTH_SHORT).show();
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
        unitEditText = findViewById(R.id.unit);
        discountEditText = findViewById(R.id.discount);
        progressBar = findViewById(R.id.progressBar);
        totalEditText = findViewById(R.id.Total);
        save = findViewById(R.id.btnsave);
        cancel = findViewById(R.id.btncancel);
        availableQuantityEditText = findViewById(R.id.aqty);


        if (progressBar == null) {
            throw new IllegalStateException("Progress bar not found in the layout");
        }

        discountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                calculateTotal();
            }
        });
    }

    private void fetchProducts(String documentId) {
        showProgressBar();
        Log.d("Return_parches", "Fetching products for documentId: " + documentId);
        firestore.collection("PurchaseEntries").document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<String> productNames = new ArrayList<>();
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
                                Log.d("Return_parches", "Document does not exist");
                                // Handle case where document doesn't exist
                            }
                        } else {
                            handleFirestoreError("Error fetching document", task.getException());
                        }
                    }
                });
    }
    private void populateSpinner(List<String> productNames) {
        Log.d("SpinnerData", "Product names fetched: " + productNames.toString()); // Log the list of product names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, productNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducts.setAdapter(adapter);
    }

    private void sendDataToMainActivity() {
        String rate = rateEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String batch = unitEditText.getText().toString().trim();
        String returnqty = discountEditText.getText().toString().trim();
        String total = totalEditText.getText().toString().trim();

        Intent intent = new Intent();
        intent.putExtra("productName", selectedProduct);
        intent.putExtra("rate", rate);
        intent.putExtra("quantity", quantity);
        intent.putExtra("batch", batch);
        intent.putExtra("qty", returnqty);
        intent.putExtra("total", total);
        updateinvoice(selectedDocumentId,selectedProduct,returnqty,total);
        updateBatch(batch,returnqty);
        updateproduct(selectedProduct, returnqty );

        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateinvoice(String selectedDocumentId, String selectedProduct, String returnqty, String total) {
        // Get the reference to the invoice document in Firestore
        DocumentReference invoiceRef = firestore.collection("PurchaseEntries").document(selectedDocumentId);

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
                                // Before parsing values
                                Log.d("UpdateInvoice", "Current Quantity Str: " + currentQuantityStr);
                                Log.d("UpdateInvoice", "Current Total Str: " + currentTotalStr);


// After parsing values
                                double currentQuantity = Double.parseDouble(currentQuantityStr);
                                double currentTotal = Double.parseDouble(currentTotalStr);
                                Log.d("UpdateInvoice", "Parsed Current Quantity: " + currentQuantity);
                                Log.d("UpdateInvoice", "Parsed Current Total: " + currentTotal);


                                // Convert the current quantity and total to integers


                                // Subtract the return quantity from the current quantity
                                double returnQty = Double.parseDouble(returnqty);
                                double newQuantity = currentQuantity - returnQty;

                                // Calculate the new total after subtracting the returned quantity
                                double total1 = Double.parseDouble(total);
                                double newTotal = currentTotal - total1;
                                Log.d("UpdateInvoice", "Return Quantity: " + returnQty);
                                Log.d("UpdateInvoice", "Total: " + total1);

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


    private void calculateTotal() {
        try {
            String rateStr = rateEditText.getText().toString();

            String discountStr = discountEditText.getText().toString();

            if (rateStr.isEmpty()  || discountStr.isEmpty()) {
                totalEditText.setText("");
                return;
            }

            double rate = Double.parseDouble(rateStr);

            double discount = Double.parseDouble(discountStr);

            double total = rate *discount;
            String formattedTotal = String.format("%.2f", total);

            totalEditText.setText(String.valueOf(formattedTotal));
        } catch (NumberFormatException e) {
            totalEditText.setText("");
            Log.e("ProductEntryActivity", "Error parsing input values", e);
        }
    }

    private void fetchProductData(String selectedProduct, String documentId) {
        showProgressBar();

        firestore.collection("PurchaseEntries")
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
                                            String unit1 = (String) product.get("unit");
                                            String price = (String) product.get("rate");
                                            String batch = (String) product.get("batch");

                                            if (price != null) {
                                                updateUI(qt, price, batch);
                                                Batchdata(batch);
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
    private void Batchdata(String batch) {
        firestore.collection("AddBatch").whereEqualTo("productBatch", batch).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String qty = document.getString("quantity");
                            availableQuantityEditText.setText(qty);
                            // Process qty data as needed
                            // For example, you can add it to a list or display it in the UI
                        }
                    } else {
                        // Handle the case when there are no documents found for the batch
                        // This might mean the batch name is incorrect or there's no data for that batch
                        showToast("No data found for batch: " + batch);
                    }
                } else {
                    // Handle errors
                    showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                }
            }
        });
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
        Toast.makeText(Return_parches.this, message, Toast.LENGTH_SHORT).show();
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
    private void showToast(String message) {
        Toast.makeText(Return_parches.this, message, Toast.LENGTH_SHORT).show();
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


}
