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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class purchaseReturnActivity extends AppCompatActivity {

    private EditText  mobileNoEditText, totalAmountEditText, selectDateEditText, addressEditText;
    private Button addButton, cancelButton, btnAddProduct;
    private AutoCompleteTextView supplierNameEditText;

    private List<Map<String, Object>> productEntries;
    private LinearLayout productEntriesContainer;
    private double totalAmt = 0.0; // Variable to store the total amount
    private String documentId ,tamt;
    private FirebaseFirestore firestore;
    private Calendar myCalendar;
    List<String> supplierNames ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_return);

        initializeViews();
        productEntriesContainer = findViewById(R.id.productEntries1);

        firestore = FirebaseFirestore.getInstance();
        productEntries = new ArrayList<>();
        myCalendar = Calendar.getInstance();
        supplierNameEditText = findViewById(R.id.supplierNameEditText);
        FetchSupplierNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(purchaseReturnActivity.this,
                android.R.layout.simple_dropdown_item_1line, supplierNames);
        supplierNameEditText.setAdapter(adapter);
        // Inside onCreate method after setting the adapter for AutoCompleteTextView
        supplierNameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSupplier = (String) parent.getItemAtPosition(position);

                // Fetch the corresponding data from Firebase based on the selected supplier name
                firestore.collection("PurchaseEntries")
                        .whereEqualTo("Customer Name", selectedSupplier)
                        .limit(1) // Limit to 1 document, assuming unique supplier names
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    // Retrieve data from the document and fill the form fields
                                    String mobileNo = document.getString("mobileNo");
                                    tamt = document.getString("totalAmount");
                                String address = document.getString("Address");
                                    String selectedDate = document.getString("date");
                                    documentId = document.getId();

                                    // Set the retrieved data to respective EditText fields
                                    mobileNoEditText.setText(mobileNo);
                                    selectDateEditText.setText(selectedDate);
                                    addressEditText.setText(address);

                                    // Fetch and add product entries if needed
                                    // You can add this logic based on your requirements
                                } else {
                                    Toast.makeText(purchaseReturnActivity.this, "Error fetching data for selected supplier", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the product entry activity when "Add Product" button is clicked
                Intent intent = new Intent(purchaseReturnActivity.this, Return_parches.class);
                intent.putExtra("documentId", documentId);
                startActivityForResult(intent, 1);

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPurchaseEntryToFirestore();
                String totalAt = totalAmountEditText.getText().toString().trim();
                Double tAt = Double.parseDouble(totalAt);
                Double CTA =Double.parseDouble(tamt);
                Double total1 = CTA - tAt;
                String Ammount = String.valueOf(total1);
                updateinvoce(Ammount);


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
                new DatePickerDialog(purchaseReturnActivity.this, dateSetListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void FetchSupplierNames() {
        supplierNames = new ArrayList<>();
        firestore.collection("PurchaseEntries")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String supplierName = document.getString("Customer Name");
                                if (supplierName != null && !supplierName.isEmpty()) {
                                    supplierNames.add(supplierName);
                                }
                            }
                        } else {
                            Toast.makeText(purchaseReturnActivity.this, "Error fetching supplier names", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeViews() {
        mobileNoEditText = findViewById(R.id.mobileno);
        totalAmountEditText = findViewById(R.id.tamount);
        selectDateEditText = findViewById(R.id.selectDate);
        addButton = findViewById(R.id.btnAdd);
        cancelButton = findViewById(R.id.btndelete);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        addressEditText = findViewById(R.id.address);
    }

    private void addPurchaseEntryToFirestore() {
        String supplierName = supplierNameEditText.getText().toString().trim();
        String mobileNo = mobileNoEditText.getText().toString().trim();
        String totalAmount = totalAmountEditText.getText().toString().trim();
        String selectedDate = selectDateEditText.getText().toString().trim();
String address = addressEditText.getText().toString().trim();
        // Check if any field is empty
        if (supplierName.isEmpty() || mobileNo.isEmpty() || totalAmount.isEmpty()  || selectedDate.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        // Create a Map to store purchase entry details
        Map<String, Object> purchaseEntry = new HashMap<>();
        purchaseEntry.put("supplierName", supplierName);
        purchaseEntry.put("mobileNo", mobileNo);
        purchaseEntry.put("totalAmount", totalAmount);
        purchaseEntry.put("address", address);
        purchaseEntry.put("selectedDate", selectedDate);
        purchaseEntry.put("products", productEntries);

        // Add the purchase entry to Firestore
        firestore.collection("PurchaseReturn")
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
        mobileNoEditText.setText("");
        totalAmountEditText.setText("");
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
            String batch = data.getStringExtra("batch");
            String qty = data.getStringExtra("qty");
            String total = data.getStringExtra("total");

            // Create a Map to store product details
            Map<String, Object> productEntry = new HashMap<>();
            productEntry.put("productName", productName);
            productEntry.put("rate", rate);
            productEntry.put("batch", batch);
            productEntry.put("qty", qty);
            productEntry.put("total", total);

            // Add product entry to the list
            productEntries.add(productEntry);
            addProductCard(productName, qty, total, batch);
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

    private void addProductCard(String productName, String quantity, String total, String batch) {
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
        disTextView.setText(batch);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer.addView(productCardView);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the product entry from the list
                productEntriesContainer.removeView(productCardView);
                totalAmt = totalAmt - Double.parseDouble(total);
                totalAmountEditText.setText(String.valueOf(totalAmt));
                updateBatch(batch,quantity);
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
    private void updateinvoce( String updatedTotalAmount) {

        // Get reference to the invoice document
        DocumentReference invoiceRef = firestore.collection("PurchaseEntries").document(documentId);
        //print documentid
        Log.d("TAG", "updateinvoce document id is : "+documentId);




        // Update the quantity of the product in Firestore
        invoiceRef.update("totalAmount",updatedTotalAmount)
                .addOnSuccessListener(aVoid -> {
                    // Quantity updated successfully
                    showToast("total amount updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Error updating quantity
                     showToast("Failed to update total amount: " + e.getMessage());
                       Log.e("TAG", "Failed to update total amount", e)   ;   
                });
    }

}
