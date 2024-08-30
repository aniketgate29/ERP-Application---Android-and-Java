package com.example.bottomnavigationdemo;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parches_Entry extends AppCompatActivity {

    private EditText supplierNameEditText, selectDateEditText, mobileNoEditText, totalAmountEditText, stateOfSupplyEditText, narration;
    private Button addButton, cancelButton, btnAddProduct;
    private Spinner gstSpinner;
    private List<Map<String, Object>> productEntries;
    private LinearLayout productEntriesContainer;
    private double totalAmt = 0.0; // Variable to store the total amount

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parches_entry);

        initializeViews();
        String[] gstNumbers = {"0", "5", "12", "18", "28"};
        ArrayAdapter<String> gstSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gstNumbers);
        gstSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gstSpinner.setAdapter(gstSpinnerAdapter);

        productEntriesContainer = findViewById(R.id.productEntries1);

        firestore = FirebaseFirestore.getInstance();

        productEntries = new ArrayList<>();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the product entry activity when "Add Product" button is clicked
                Intent intent = new Intent(Parches_Entry.this, product_purchese.class);
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
                        Parches_Entry.this,
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
        });

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
        gstSpinner = findViewById(R.id.gstSpinner);
        totalAmountEditText = findViewById(R.id.tamount);
        stateOfSupplyEditText = findViewById(R.id.stateofsupp);
        narration = findViewById(R.id.narration);
        addButton = findViewById(R.id.btnAdd);
        cancelButton = findViewById(R.id.btndelete);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        selectDateEditText = findViewById(R.id.selectDate);
    }

    private void addPurchaseEntryToFirestore() {
        String supplierName = supplierNameEditText.getText().toString().trim();
        String mobileNo = mobileNoEditText.getText().toString().trim();
        String totalAmount = totalAmountEditText.getText().toString().trim();
        String stateOfSupply = stateOfSupplyEditText.getText().toString().trim();
        String Narration = narration.getText().toString().trim();
        String GstSpinner = gstSpinner.getSelectedItem().toString(); // Obtain selected GST value
        String date = selectDateEditText.getText().toString().trim();

        // Check if any field is empty
        if (supplierName.isEmpty() || mobileNo.isEmpty() || totalAmount.isEmpty() || stateOfSupply.isEmpty() || date.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        // Create a Map to store purchase entry details
        Map<String, Object> purchaseEntry = new HashMap<>();
        purchaseEntry.put("date", date);
        purchaseEntry.put("Customer Name", supplierName);
        purchaseEntry.put("mobileNo", mobileNo);
        purchaseEntry.put("totalAmount", totalAmount);
        purchaseEntry.put("Address", stateOfSupply);
        purchaseEntry.put("GstNo", GstSpinner);
        purchaseEntry.put("Amount", totalAmount); // Change gstSpinner to GstSpinner

        purchaseEntry.put("Narration", Narration);
        purchaseEntry.put("products", productEntries);

        // Add the purchase entry to Firestore
        firestore.collection("PurchaseEntries")
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
        narration.setText("");
        selectDateEditText.setText("");
        finish();
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
            final String[] quantity = {data.getStringExtra("quantity")};
            String unit = data.getStringExtra("unit");
            String discount = data.getStringExtra("discount");
            String total = data.getStringExtra("total");
            String gstSpinner = data.getStringExtra("gst"); // Retrieve the GST value
            String selectdate = data.getStringExtra("selectdate");
            String batch = data.getStringExtra("batch");

            // Fetch existing quantity from ProdRegproducts collection
            firestore.collection("ProdRegproducts")
                    .whereEqualTo("productName", productName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String existingQuantityStr = document.getString("openingQty");
                                    double existingQuantity = Double.parseDouble(existingQuantityStr);
                                    double currentQuantity = Double.parseDouble(quantity[0]);
                                    double totalQuantity = existingQuantity + currentQuantity;
                                    quantity[0] = String.valueOf(totalQuantity); // Update quantity with total quantity

                                }
                                // Update the totalAmountEditText with the calculated total
                                totalAmountEditText.setText(String.valueOf(totalAmt));
                                Map<String, Object> productEntry = new HashMap<>();
                                productEntry.put("productName", productName);
                                productEntry.put("rate", rate);
                                productEntry.put("quantity", quantity[0]); // Updated quantity
                                productEntry.put("unit", unit);
                                productEntry.put("discount", discount);
                                productEntry.put("total", total);
                                productEntry.put("batch",batch);
                                productEntry.put("selectdate", selectdate);

                                // Add product entry to the list
                                productEntries.add(productEntry);
                                addProductCard(productName, quantity[0], discount, total,batch);
                                totalAmt = calculateTotalAmount();
                                totalAmountEditText.setText(String.valueOf(totalAmt));
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }



    private double calculateTotalAmount() {
        return productEntries.stream()
                .mapToDouble(entry -> Double.parseDouble(entry.get("total").toString()))
                .sum();
    }

    private void addProductCard(String productName, String quantity, String discount, String total,String batch) {
        View productCardView = getLayoutInflater().inflate(R.layout.product_card, null);

        TextView productNameTextView = productCardView.findViewById(R.id.productNameTextView);
        TextView qtyTextView = productCardView.findViewById(R.id.qty);
        TextView tolTextView = productCardView.findViewById(R.id.TolTextView);
        TextView disTextView = productCardView.findViewById(R.id.DisTextView);
        Button btnDelete = productCardView.findViewById(R.id.delete);

        productNameTextView.setText(productName);
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
                updateBatch(batch);
                updateproduct(productName, quantity );
        }


        }        );}
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
    private void updateBatch(String batch) {
        // Remove the data card view from the layout

        // Find the document with the specified batch name
        firestore.collection("AddBatch")
                .whereEqualTo("batchName", batch)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Check if any document was found
                            if (!task.getResult().isEmpty()) {
                                // Iterate through the documents (there should be only one)
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Delete the document
                                    document.getReference().delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        showToast("Document deleted successfully");
                                                    } else {
                                                        showToast("Error deleting document: " + task.getException().getMessage());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                showToast("No document found with batch name: " + batch);
                            }
                        } else {
                            showToast("Error getting documents: " + task.getException().getMessage());
                        }
                    }
                });
    }
}
