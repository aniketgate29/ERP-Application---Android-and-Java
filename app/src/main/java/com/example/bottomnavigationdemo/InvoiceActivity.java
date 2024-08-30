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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceActivity extends AppCompatActivity {

    private EditText supplierNameEditText, selectDateEditText, mobileNoEditText, pinNoEditText, totalAmountEditText, stateOfSupplyEditText;
    private Button addButton, cancelButton, btnAddProduct,btnServices;
    private List<Map<String, Object>> productEntries;
    private List<Map<String, Object>> Services;
    private LinearLayout productEntriesContainer;
    private Spinner spinnerState;
    private String selectedState;

    private double totalAmt = 0.0; // Variable to store the total amount

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        initializeViews();
        productEntriesContainer = findViewById(R.id.productEntries1);

        firestore = FirebaseFirestore.getInstance();

        productEntries = new ArrayList<>();
        Services = new ArrayList<>();

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the product entry activity when "Add Product" button is clicked
                Intent intent = new Intent(InvoiceActivity.this, Invoice_AddProd.class);
                startActivityForResult(intent, 1);

            }
        });

        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the product entry activity when "Add Product" button is clicked
                Intent intent = new Intent(InvoiceActivity.this, Invoice_Services.class);
                startActivityForResult(intent, 2);

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
                        InvoiceActivity.this,
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
        btnServices=findViewById(R.id.btnServices);
        selectDateEditText = findViewById(R.id.selectDate);
        spinnerState = findViewById(R.id.spinnerState);
    }

    private void addPurchaseEntryToFirestore() {
        String supplierName = supplierNameEditText.getText().toString().trim();
        String mobileNo = mobileNoEditText.getText().toString().trim();
        String totalAmount = totalAmountEditText.getText().toString().trim();
        String stateOfSupply = stateOfSupplyEditText.getText().toString().trim();
        String date = selectDateEditText.getText().toString().trim();
        String PinCode = pinNoEditText.getText().toString().trim();

        // Check if any field is empty
        if (supplierName.isEmpty() || mobileNo.isEmpty() || totalAmount.isEmpty() || stateOfSupply.isEmpty() || date.isEmpty() || PinCode.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }

        // Check if the customer name already exists
        checkCustomerNameExists(supplierName);
    }

    private void checkCustomerNameExists(final String customerName) {
        firestore.collection("invoice")
                .whereEqualTo("Customer Name", customerName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                showToast("Customer name already exists. Please use a different name.");
                            } else {
                                addPurchaseEntryToFirestore(customerName);
                            }
                        } else {
                            showToast("Error checking customer name existence.");
                        }
                    }
                });
    }

    private void addPurchaseEntryToFirestore(String customerName) {
        String mobileNo = mobileNoEditText.getText().toString().trim();
        String totalAmount = totalAmountEditText.getText().toString().trim();
        String stateOfSupply = stateOfSupplyEditText.getText().toString().trim();
        String date = selectDateEditText.getText().toString().trim();
        String PinCode = pinNoEditText.getText().toString().trim();

        // Create a Map to store purchase entry details
        Map<String, Object> purchaseEntry = new HashMap<>();
        purchaseEntry.put("date", date);
        purchaseEntry.put("pincode", PinCode);
        purchaseEntry.put("Customer Name", customerName);
        purchaseEntry.put("mobileNo", mobileNo);
        purchaseEntry.put("totalAmount", totalAmount);
        purchaseEntry.put("Address", stateOfSupply);
        purchaseEntry.put("Amount", totalAmount);
        purchaseEntry.put("products", productEntries);
        purchaseEntry.put("Services",Services);

        // Add the purchase entry to Firestore
        firestore.collection("invoice")
                .add(purchaseEntry)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            showToast("Purchase entry added successfully");
                            clearEditTextFields();
                            productEntries.clear(); // Clear product entries after adding to Firestore
                            Intent intent = new Intent(InvoiceActivity.this, Invoice_View.class);
                            startActivity(intent);
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
            String batch = data.getStringExtra("batch");
            String expiry = data.getStringExtra("expiry");


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
            productEntry.put("batch", batch);
            productEntry.put("expiry", expiry);



            // Add product entry to the list
            productEntries.add(productEntry);
            addProductCard(productName, quantity, discount, total,batch);
            totalAmt = calculateTotalAmount();
            totalAmountEditText.setText(String.valueOf(totalAmt));


        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // Retrieve product details from the product entry activity
            String serviceName = data.getStringExtra("serviceName");
            String rate = data.getStringExtra("price");
            String transport = data.getStringExtra("transport");
            String vno = data.getStringExtra("vno");
            String place = data.getStringExtra("place");
            String Narration = data.getStringExtra("Narration");

            // Create a Map to store product details


            // Update the totalAmountEditText with the calculated total
            Map<String, Object> services = new HashMap<>();
            services.put("serviceName", serviceName);
            services.put("rate", rate);
            services.put("transport", transport);
            services.put("vno", vno);
            services.put("place", place);
            services.put("Narration", Narration);

            // Add product entry to the list
            Services.add(services);
            addServicesCard(serviceName, vno,place,rate);
            totalAmt = calculateTotalAmount();
            totalAmountEditText.setText(String.valueOf(totalAmt));


        }

    }

    private void addServicesCard(String serviceName, String vno, String place, String rate) {
        View serviceCardView  = getLayoutInflater().inflate(R.layout.invoice_service_card, null);

        TextView serviceNameTextView  = serviceCardView .findViewById(R.id.serviceNameTextView);
        TextView vnoTextView  = serviceCardView .findViewById(R.id.vnoTextView);
        TextView placeTextView  = serviceCardView .findViewById(R.id.placeTextView);
        TextView rateTextView  = serviceCardView .findViewById(R.id.rateTextView);
        TextView btnDelete = serviceCardView .findViewById(R.id.close);


        serviceNameTextView.setText(serviceName);
        vnoTextView.setText(vno);
        placeTextView.setText(place);
        rateTextView.setText(rate);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer.addView(serviceCardView );
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the product entry from the list
                productEntriesContainer.removeView(serviceCardView);
                //  totalAmt = totalAmt - Double.parseDouble(total);
                //   totalAmountEditText.setText(String.valueOf(totalAmt));
                //  updateBatch(productName, quantity);
                //  updateproduct(productName, quantity );
            }
        });
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
        // Add a click listener to the delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the product entry from the list
                productEntriesContainer.removeView(productCardView);
                totalAmt = totalAmt - Double.parseDouble(total);
                totalAmountEditText.setText(String.valueOf(totalAmt));
                updateBatch(productName, quantity);
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
                                        int newQuantity = previousQuantity + quantityToAdd;

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
