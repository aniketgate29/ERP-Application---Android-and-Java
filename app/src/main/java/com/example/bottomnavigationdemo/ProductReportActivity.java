package com.example.bottomnavigationdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ProductReportActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private LinearLayout report;
    private double totalAmount = 0.0;
    private int transactionCount = 0;
    private static final String FIELD_PRODUCT_NAME = "productName";
    private static final String FIELD_QTY = "openingQty";
    private static final String FIELD_RATE = "openingRate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_main);

        report = findViewById(R.id.report);
        firestore = FirebaseFirestore.getInstance();

        FetchData();
    }

    private void FetchData() {
        firestore.collection("ProdRegproducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_PRODUCT_NAME);
                                    String qty = document.getString(FIELD_QTY);
                                    String rate = document.getString(FIELD_RATE);
                                    addDataCard(name, qty, rate);

                                    if (qty != null && !qty.trim().isEmpty()) {
                                        try {
                                            double qtyAmount = Double.parseDouble(qty.trim());
                                            calculateTotalAmount(qtyAmount);
                                        } catch (NumberFormatException e) {
                                            // Handle the case where the quantity cannot be parsed to a double
                                            e.printStackTrace();
                                            showToast("Error parsing quantity: " + qty);
                                        }
                                    }
                                }
                                displayTotalAmount();
                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(ProductReportActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void addDataCard(String name, String qty, String rate) {
        View productCardView = getLayoutInflater().inflate(R.layout.activity_product, null);

        TextView nameTextView = productCardView.findViewById(R.id.nameTextView);
        TextView qtyTextView = productCardView.findViewById(R.id.openingQty);
        TextView rateTextView = productCardView.findViewById(R.id.openingRate);

        nameTextView.setText(name);
        qtyTextView.setText(qty);
        rateTextView.setText(rate);

        // Now, add the view to the productEntriesContainer
        report.addView(productCardView);
    }

    private void calculateTotalAmount(double quantity) {
        totalAmount += quantity;
        transactionCount++;
    }

    private void displayTotalAmount() {
        // You can display total amount and transaction count if needed
    }
}
