package com.example.bottomnavigationdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ledger_View extends AppCompatActivity {
    private LinearLayout productEntriesContainer1;
    private FirebaseFirestore firestore;
    private Button invoiceButton;

    private static final String FIELD_CUSTOMER_NAME = "ledgerName";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_view);

        Button invoiceButton = findViewById(R.id.invoicebtn);
        // Initialize TextView
        TextView textView = findViewById(R.id.textview);
        firestore = FirebaseFirestore.getInstance();



        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);



        // Set text for the TextView
        textView.setText("Ledger");

        // Initialize Button

        fetchData();
        // Set onClickListener for the Button
        invoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ledger_View.this, CreateLedgerActivity.class);
                startActivity(intent);// Add your button click functionality here
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

        // Inside onCreate() method
        Button filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to fetch and show the "Under" values in a popup
                showFilterPopup();
            }
        });



    }
    // Method to show the filter popup
    public void showFilterPopup() {
        // Fetch the distinct "Under" values from Firestore
        firestore.collection("under")
                .orderBy("underName") // Assuming FIELD_UNDER is the field name for "Under"
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> underValues = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String under = document.getString("underName");
                                if (under != null && !under.isEmpty() && !underValues.contains(under)) {
                                    underValues.add(under);
                                }
                            }
                            // Show the "Under" values in a popup
                            showUnderPopup(underValues);
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }

    // Method to show the popup with "Under" values
    private void showUnderPopup(List<String> underValues) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Under");
        // Convert list to array for use in AlertDialog
        CharSequence[] underArray = underValues.toArray(new CharSequence[underValues.size()]);
        builder.setItems(underArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Filter ledger records based on the selected "Under" value
                String selectedUnder = underValues.get(which);
                filterByUnder(selectedUnder);
            }
        });
        builder.show();
    }

    // Method to filter ledger records based on "Under" value
    private void filterByUnder(String selectedUnder) {
        firestore.collection("ledger")
                .whereEqualTo("selectedUnder", selectedUnder)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Clear the existing data cards before displaying filtered results
                            productEntriesContainer1.removeAllViews();
                            for (DocumentSnapshot document : task.getResult()) {
                                String name = document.getString(FIELD_CUSTOMER_NAME);
                                String openingBalance = document.getString("openingBalance");
                                String payTerm = document.getString("payTerm");
                                String documentId = document.getId();

                                addDataCard(name, openingBalance, payTerm, documentId);
                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void performSearch(String query) {
        // Clear the existing data cards before displaying search results
        productEntriesContainer1.removeAllViews();

        firestore.collection("ledger")
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
                                    String openingBalance = document.getString("openingBalance");
                                    String payTerm = document.getString("payTerm");
                                    String documentId = document.getId();

                                    addDataCard(name, openingBalance, payTerm, documentId);
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
        firestore.collection("ledger")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String ledgerName = document.getString(FIELD_CUSTOMER_NAME);
                                    String openingBalance = document.getString("openingBalance");
                                    String payTerm = document.getString("payTerm");
                                    String documentId = document.getId();

                                    addDataCard(ledgerName , openingBalance, payTerm, documentId);




                                }

                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }    private void showToast(String message) {
        Toast.makeText(com.example.bottomnavigationdemo.ledger_View.this, message, Toast.LENGTH_SHORT).show();
    }
    private void addDataCard(String name, String openingBalance , String payTerm, String documentID) {
        View productCardView = getLayoutInflater().inflate(R.layout.ledger_card, null);

        TextView nameTextView = productCardView.findViewById(R.id.productNameTextView);
        TextView openingBalTextView = productCardView.findViewById(R.id.openingBalTextView);
        TextView payTermTextView = productCardView.findViewById(R.id.payTermTextView);
        Button deleteButton = productCardView.findViewById(R.id.Delete);


        nameTextView.setText(name);
        openingBalTextView.setText(String.valueOf(openingBalance));
        payTermTextView.setText(String.valueOf(payTerm));

        // Now, add the view to the productEntriesContainer
        productEntriesContainer1.addView(productCardView);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the data card view from the layout
                productEntriesContainer1.removeView(productCardView);

                // Delete the document from Firestore
                firestore.collection("ledger").document(documentID)
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