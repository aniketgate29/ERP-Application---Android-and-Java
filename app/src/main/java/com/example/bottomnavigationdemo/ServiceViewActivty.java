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

public class ServiceViewActivty extends AppCompatActivity {
    private LinearLayout productEntriesContainer1;
    private FirebaseFirestore firestore;

    private static final String FIELD_NAME = "serviceName";
    //private static final String FIELD_Date = "date";
    private static final String FIELD_Batch = "rate";
    private static final String FIELD_Qty = "gst";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_view);


        // Initialize TextView
        TextView textView = findViewById(R.id.textview);
        firestore = FirebaseFirestore.getInstance();
        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);



        // Set text for the TextView
        textView.setText("Services");

        // Initialize Button
        Button invoiceButton = findViewById(R.id.servicebtn);
        fetchData();
        // Set onClickListener for the Button
        invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceViewActivty.this, AddServiceActivity.class);
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

        firestore.collection("AddService")
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
                                    //String date = document.getString(FIELD_Date);
                                    String batch = document.getString(FIELD_Batch);
                                    String qty = document.getString(FIELD_Qty);

                                    addDataCard(name,batch,qty,documentId);



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
        firestore.collection("AddService")
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
                                   // String date = document.getString(FIELD_Date);
                                    String batch = document.getString(FIELD_Batch);
                                    String qty = document.getString(FIELD_Qty);

                                    addDataCard(name,batch,qty,documentId);




                                }

                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }    private void showToast(String message) {
        Toast.makeText(ServiceViewActivty.this, message, Toast.LENGTH_SHORT).show();
    }
    private void addDataCard(String name, String batch, String qty, String documentId) {
        View productCardView = getLayoutInflater().inflate(R.layout.service_card, null);

        TextView nameTextView = productCardView.findViewById(R.id.nameTextView);
        TextView Batch = productCardView.findViewById(R.id.batch);
        TextView Qty = productCardView.findViewById(R.id.QTYTextView);

        Button deleteButton = productCardView.findViewById(R.id.deleteButton);

        // Set document ID as tag to the product card view
        productCardView.setTag(documentId);

        nameTextView.setText(name);
        Batch.setText(batch);
        Qty.setText(qty);

        // Now, add the view to the productEntriesContainer
        productEntriesContainer1.addView(productCardView);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the data card view from the layout
                productEntriesContainer1.removeView(productCardView);

                // Get the document ID from the productCardView's tag
                String documentID = (String) productCardView.getTag();

                // Delete the document from Firestore using the documentID
                if (documentID != null) {
                    firestore.collection("AddService").document(documentID)
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
            }
        });
    }




}