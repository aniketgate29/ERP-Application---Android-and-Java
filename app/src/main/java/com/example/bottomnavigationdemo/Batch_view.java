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

public class Batch_view extends AppCompatActivity {
    private LinearLayout productEntriesContainer1;
    private FirebaseFirestore firestore;

    private static final String FIELD_NAME = "productName";
    private static final String FIELD_Date = "selectedDate";
    private static final String FIELD_Batch = "productBatch";
    private static final String FIELD_Qty = "quantity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_view);


        // Initialize TextView
        TextView textView = findViewById(R.id.textview);
        firestore = FirebaseFirestore.getInstance();
        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);



        // Set text for the TextView
        textView.setText("Batch");

        // Initialize Button
        Button invoiceButton = findViewById(R.id.invoicebtn);
        fetchData();
        // Set onClickListener for the Button
        invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Batch_view.this, AddBatchActivity.class);
                startActivity(intent);

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

        firestore.collection("AddBatch")
                .whereEqualTo(FIELD_NAME, query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_NAME);
                                    String documentId = document.getId();
                                    String date = document.getString(FIELD_Date);
                                    String batch = document.getString(FIELD_Batch);
                                    String qty = document.getString(FIELD_Qty);

                                    addDataCard(name,batch,qty,date,documentId);



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
        firestore.collection("AddBatch")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_NAME);
                                    String documentId = document.getId();
                                    String date = document.getString(FIELD_Date);
                                    String batch = document.getString(FIELD_Batch);
                                    String qty = document.getString(FIELD_Qty);

                                    addDataCard(name,batch,qty,date,documentId);




                                }

                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }    private void showToast(String message) {
        Toast.makeText(Batch_view.this, message, Toast.LENGTH_SHORT).show();
    }
    private void addDataCard(String name,String batch,String qty, String date, String documentID) {
        View productCardView = getLayoutInflater().inflate(R.layout.batch_card, null);

        TextView nameTextView = productCardView.findViewById(R.id.nameTextView);
        TextView Batch = productCardView.findViewById(R.id.batch);
        TextView Qty = productCardView.findViewById(R.id.QTYTextView);
        TextView Date = productCardView.findViewById(R.id.date1);


        Button deleteButton = productCardView.findViewById(R.id.deleteButton);


        nameTextView.setText(name);
        Batch.setText(batch);
        Qty.setText(qty);
        Date.setText(date);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer1.addView(productCardView);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the data card view from the layout
                productEntriesContainer1.removeView(productCardView);

                // Delete the document from Firestore
                firestore.collection("AddBatch").document(documentID)
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