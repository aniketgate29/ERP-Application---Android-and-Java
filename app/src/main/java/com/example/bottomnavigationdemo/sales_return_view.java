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

public class sales_return_view extends AppCompatActivity {
    private LinearLayout productEntriesContainer1;
    private FirebaseFirestore firestore;

    private static final String FIELD_CUSTOMER_NAME = "supplierName";
    private static final String FIELD_TOTAL_AMOUNT = "totalAmount";
    private static final String FIELD_DATE = "selectedDate";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_return_view);


        // Initialize TextView
        TextView textView = findViewById(R.id.textview);
        firestore = FirebaseFirestore.getInstance();
        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);



        // Set text for the TextView
        textView.setText("Seles Return");

        // Initialize Button
        Button invoiceButton = findViewById(R.id.invoicebtn);
        fetchData();
        // Set onClickListener for the Button
        invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sales_return_view.this, salesReturnActivity.class);
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

        firestore.collection("SalesReturn")
                .whereEqualTo(FIELD_CUSTOMER_NAME, query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_CUSTOMER_NAME);
                                    String total = document.getString(FIELD_TOTAL_AMOUNT);
                                    String date = document.getString(FIELD_DATE);
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
        firestore.collection("SalesReturn")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_CUSTOMER_NAME);
                                    String total = document.getString(FIELD_TOTAL_AMOUNT);
                                    String date = document.getString(FIELD_DATE);
                                    String documentId = document.getId();
                                    if (name != null && total != null && date != null && documentId != null) {
                                        addDataCard(name, total, date, documentId);
                                    } else {
                                        showToast("Error: Some fields are null for document " + documentId);
                                    }
                                }
                            } else {
                                showToast("Error: No documents found in collection");
                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }
    private void showToast(String message) {
        Toast.makeText(sales_return_view.this, message, Toast.LENGTH_SHORT).show();
    }
    private void addDataCard(String name, String total, String date, String documentID) {
        View productCardView = getLayoutInflater().inflate(R.layout.purchase_card, null);

        TextView nameTextView = productCardView.findViewById(R.id.nameTextView);
        TextView dateTextView = productCardView.findViewById(R.id.date1);
        TextView totalTextView = productCardView.findViewById(R.id.TolTextView);
        Button updateButton = productCardView.findViewById(R.id.updateButton);
        Button deleteButton = productCardView.findViewById(R.id.deleteButton);


        nameTextView.setText(name);
        dateTextView.setText((date));
        totalTextView.setText(total);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer1.addView(productCardView);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle update button click event
                Toast.makeText(sales_return_view.this, "Update clicked!", Toast.LENGTH_SHORT).show();
                performUpdate(); // Call a method to perform update
            }

            private void performUpdate() {
                // Implement update logic here
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the data card view from the layout
                productEntriesContainer1.removeView(productCardView);

                // Delete the document from Firestore
                firestore.collection("SalesReturn").document(documentID)
                        .delete()
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
        });


    }
}