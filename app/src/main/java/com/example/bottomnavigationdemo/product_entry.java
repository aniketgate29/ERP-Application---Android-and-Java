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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class product_entry extends AppCompatActivity {

    private Spinner spinnerProducts;
    private EditText rateEditText, quantityEditText, unitEditText, discountEditText, totalEditText;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private String selectedProduct;
    private Button save, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_entry);

        initializeViews();
        firestore = FirebaseFirestore.getInstance();
        fetchProducts();

        spinnerProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedProduct = spinnerProducts.getSelectedItem().toString();
                fetchProductData(selectedProduct);
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

    private void fetchProducts() {
        showProgressBar();

        firestore.collection("ProdRegproducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            List<String> productNames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productName = document.getString("productName");
                                if (productName != null) {
                                    productNames.add(productName);
                                }
                            }
                            populateSpinner(productNames);
                        } else {
                            handleFirestoreError("Error fetching products", task.getException());
                        }
                    }
                });
    }

    private void populateSpinner(List<String> productNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, productNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducts.setAdapter(adapter);
    }

    private void sendDataToMainActivity() {
        String rate = rateEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String unit = unitEditText.getText().toString().trim();
        String discount = discountEditText.getText().toString().trim();
        String total = totalEditText.getText().toString().trim();

        Intent intent = new Intent();
        intent.putExtra("productName", selectedProduct);
        intent.putExtra("rate", rate);
        intent.putExtra("quantity", quantity);
        intent.putExtra("unit", unit);
        intent.putExtra("discount", discount);
        intent.putExtra("total", total);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void calculateTotal() {
        try {
            String rateStr = rateEditText.getText().toString();
            String quantityStr = quantityEditText.getText().toString();
            String discountStr = discountEditText.getText().toString();

            if (rateStr.isEmpty() || quantityStr.isEmpty() || discountStr.isEmpty()) {
                totalEditText.setText("");
                return;
            }

            double rate = Double.parseDouble(rateStr);
            double quantity = Double.parseDouble(quantityStr);
            double discount = Double.parseDouble(discountStr);

            double total = rate * quantity * (1 - discount / 100);
            String formattedTotal = String.format("%.2f", total);

            totalEditText.setText(String.valueOf(formattedTotal));
        } catch (NumberFormatException e) {
            totalEditText.setText("");
            Log.e("ProductEntryActivity", "Error parsing input values", e);
        }
    }

    private void fetchProductData(String selectedProduct) {
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

                                if (price != null) {
                                    updateUI(qt, String.valueOf(price), unit1);
                                } else {
                                    handleFirestoreError("No price found for the selected product", null);
                                    clearEditTextFields();
                                }
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
        Toast.makeText(product_entry.this, message, Toast.LENGTH_SHORT).show();
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
