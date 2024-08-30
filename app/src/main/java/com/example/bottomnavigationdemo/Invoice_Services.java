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

public class Invoice_Services extends AppCompatActivity {

    private Spinner spinnerProducts;
    private EditText rateEditText, transportEditText, vnoEditText, placeEditText, NarrationEditText;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    private String selectedProduct,rate,transport,vno,place,Narration;
    List<String> serviceNames;
    private Button save, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_services);

        initializeViews();
        firestore = FirebaseFirestore.getInstance();
        fetchProducts();
        fetchProductData();

     /*   spinnerProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
*/
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
        transportEditText = findViewById(R.id.transport);
        vnoEditText = findViewById(R.id.vno);
        placeEditText = findViewById(R.id.place);
        NarrationEditText = findViewById(R.id.Narration);
        progressBar = findViewById(R.id.progressBar);
        save = findViewById(R.id.btnsave);
        cancel = findViewById(R.id.btncancel);

        if (progressBar == null) {
            throw new IllegalStateException("Progress bar not found in the layout");
        }

    }
    private void fetchProducts() {
        showProgressBar();

        firestore.collection("AddService")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            List<String> productNames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productName = document.getString("serviceName");
                                productNames.add(productName);
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
        // Check if any of the EditText fields is null
        if (rateEditText == null || transportEditText == null ||
                vnoEditText == null || placeEditText == null || spinnerProducts == null || NarrationEditText.animate()== null) {
            // Handle the null references here, for example, show a Toast and return
            Toast.makeText(this, "One or more fields are null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the values from the EditText fields
        String price = rateEditText.getText().toString().trim();
        String quantity = transportEditText.getText().toString().trim();
        String unit = vnoEditText.getText().toString().trim();
        String discount = placeEditText.getText().toString().trim();
        String GST = NarrationEditText.getText().toString().trim();
        String serviceName = spinnerProducts.getSelectedItem().toString();


        // Create an Intent to send the data back to MainActivity
        Intent intent = new Intent();
        intent.putExtra("serviceName", serviceName);
        intent.putExtra("price", price);
        intent.putExtra("transport", quantity);
        intent.putExtra("vno", unit);
        intent.putExtra("place", discount);
        intent.putExtra("Narration", GST);


        // Set the result and finish the activity
        setResult(RESULT_OK, intent);
        finish();

    }

    private void fetchProductData() {
        showProgressBar();
        serviceNames = new ArrayList<>();

        firestore.collection("AddService")
                .get()
                .addOnCompleteListener(task -> {
                    hideProgressBar();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String productName = document.getString("serviceName");
                            if (productName != null) {
                                serviceNames.add(productName);
                            }
                        }
                        Log.d("Invoice_AddProd", "Fetched " + serviceNames.size() + " product names");
                        // Set up ArrayAdapter for AutoCompleteTextView after fetching product names
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_dropdown_item_1line, serviceNames);
                        spinnerProducts.setAdapter(adapter);
                    } else {
                        handleFirestoreError("Error fetching product names", task.getException());
                    }
                });

        spinnerProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProduct = parent.getItemAtPosition(position).toString();

                if (selectedProduct != null && !selectedProduct.isEmpty()) {
                    showProgressBar();

                    firestore.collection("AddService")
                            .whereEqualTo("serviceName", selectedProduct)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    hideProgressBar();
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                            String price = document.getString("rate");
                                            rateEditText.setText(price);
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
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected (if needed)
            }
        });

    }


   /* private void fetchProductData(String selectedProduct) {
        showProgressBar();

        firestore.collection("AddService")
                .whereEqualTo("serviceName", selectedProduct)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String price = document.getString("rate");


                                rateEditText.setText(price);
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
    }*/

    private void clearEditTextFields() {
        rateEditText.setText("");
        transportEditText.setText("");
        vnoEditText.setText("");
        placeEditText.setText("");
        NarrationEditText.setText("");

        finish();
    }

    private void handleFirestoreError(String message, Exception exception) {
        Log.e("InvoiveActivity", message, exception);
        Toast.makeText(Invoice_Services.this, message, Toast.LENGTH_SHORT).show();
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

