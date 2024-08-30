package com.example.bottomnavigationdemo;
// Your improved AddBatchActivity

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddBatchActivity extends AppCompatActivity {

    private EditText quantityEditText, batchEditText;
    private AutoCompleteTextView productNameAutoComplete;

    private TextView selectedDateTextView;
    private Button selectDateButton, saveButton, cancelButton;

    private FirebaseFirestore database;
    private List<String> productNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_batch);

        database = FirebaseFirestore.getInstance();

        quantityEditText = findViewById(R.id.editTextqty);
        batchEditText = findViewById(R.id.editbatch);
        productNameAutoComplete = findViewById(R.id.productNameAutoComplete);

        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        selectDateButton = findViewById(R.id.buttonSelectDate);
        saveButton = findViewById(R.id.buttonSave);
        cancelButton = findViewById(R.id.buttoncancel);
        Date currentDate = new Date();

        // Format the date as per your requirement
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Set the formatted date to the TextView
        selectedDateTextView.setText(formattedDate);

        fetchProductNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, productNames);
        productNameAutoComplete.setAdapter(adapter);

        selectDateButton.setOnClickListener(view -> showDatePickerDialog());

        saveButton.setOnClickListener(view -> saveData());

        cancelButton.setOnClickListener(view -> finish());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectedDateTextView.setText(selectedDate);
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }

    private void saveData() {
        String batch = batchEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String productName = productNameAutoComplete.getText().toString().trim();
        String selectedDate = selectedDateTextView.getText().toString();
        AddQty( productName,quantity);

        if (batch.isEmpty() || quantity.isEmpty() || productName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("productBatch", batch);
        data.put("quantity", quantity);
        data.put("productName", productName);
        data.put("selectedDate", selectedDate);

        database.collection("AddBatch")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    clearFields();
                    Toast.makeText(this, "Batch added successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                    Toast.makeText(this, "Failed to add batch", Toast.LENGTH_SHORT).show();
                });

    }
    private void AddQty(String productName, String quantity) {
        // Parse the quantity string to an integer
        int quantityToAdd = Integer.parseInt(quantity);

        // Query Firestore to get the document reference for the product
        database.collection("ProdRegproducts")
                .whereEqualTo("productName", productName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the document ID (product name) of the matching product
                            String productId = document.getId();

                            // Update the quantity of the product in Firestore
                            DocumentReference productRef = database.collection("ProdRegproducts").document(productId);
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




    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    private void fetchProductNames() {
        productNames = new ArrayList<>();
        database.collection("ProdRegproducts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String productName = document.getString("productName");
                            productNames.add(productName);
                        }
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                        Toast.makeText(this, "Failed to fetch product names", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        batchEditText.setText("");
        quantityEditText.setText("");
        productNameAutoComplete.setText("");
        selectedDateTextView.setText("Selected Date:");
    }
}
