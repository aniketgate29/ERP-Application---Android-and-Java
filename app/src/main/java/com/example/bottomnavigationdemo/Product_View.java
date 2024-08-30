package com.example.bottomnavigationdemo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Product_View extends AppCompatActivity {
    private LinearLayout productEntriesContainer1;
    private FirebaseFirestore firestore;

    private static final String FIELD_Product_name = "productName";
    private static final String FIELD_TOTAL_AMOUNT = "withTaxMRP";
    private static final String FIELD_Qty = "openingQty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);


        // Initialize TextView
        TextView textView = findViewById(R.id.textview);
        firestore = FirebaseFirestore.getInstance();
        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);



        // Set text for the TextView
        textView.setText("Products");

        // Initialize Button
        Button ProdRegproductsButton = findViewById(R.id.ProdRegproductsbtn);
        fetchData();
        // Set onClickListener for the Button
        ProdRegproductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Product_View.this, ProdcRegActivity.class);
                startActivity(intent);
                // Add your button click functionality here
            }
        });
        SearchView Search = findViewById(R.id.searchView);
        Search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newtext) {
                // Handle the search query submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // Handle the search query text change (you can implement real-time filtering here)
                performSearch(query);
                return true;
            }
        });
    }
    private void performSearch(String query) {
        // Clear the existing data cards before displaying search results
        productEntriesContainer1.removeAllViews();

        firestore.collection("ProdRegproducts")
                .whereEqualTo(FIELD_Product_name, query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_Product_name);
                                    String total = document.getString(FIELD_TOTAL_AMOUNT);
                                    String date = document.getString(FIELD_Qty);
                                    String documentId = document.getId();

                                    addDataCard(name, total, date, documentId);
                                }
                            } else {
                                showToast("No matching records found");
                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void fetchData() {
        firestore.collection("ProdRegproducts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String productName = document.getString(FIELD_Product_name);
                                    String total = document.getString(FIELD_TOTAL_AMOUNT);
                                    String quantity = document.getString(FIELD_Qty);
                                    String documentId = document.getId();

                                    addDataCard(productName, quantity, total , documentId);




                                }

                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }    private void showToast(String message) {
        Toast.makeText(Product_View.this, message, Toast.LENGTH_SHORT).show();
    }
    private void addDataCard(String productName, String quantity, String total, String documentId) {
        View productCardView = getLayoutInflater().inflate(R.layout.product_card_view, null);

        TextView productNameTextView = productCardView.findViewById(R.id.productNameTextView);
        TextView qtyTextView = productCardView.findViewById(R.id.qty);
        TextView tolTextView = productCardView.findViewById(R.id.TolTextView);

        productNameTextView.setText(productName);
        qtyTextView.setText(quantity);
        tolTextView.setText(total);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer1.addView(productCardView);
    }

}