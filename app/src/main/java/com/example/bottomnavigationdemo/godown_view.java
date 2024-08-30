package com.example.bottomnavigationdemo;



import android.annotation.SuppressLint;
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

public class godown_view extends AppCompatActivity {
    private LinearLayout productEntriesContainer1;
    private FirebaseFirestore firestore;

    private static final String FIELD_GODOWN_NAME = "godownName";
    private static final String FIELD_ABOUT = "About";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_godown_view);


        // Initialize TextView
        TextView textView = findViewById(R.id.textview);
        firestore = FirebaseFirestore.getInstance();
        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);



        // Set text for the TextView
        textView.setText("Godown Details");

        // Initialize Button
        Button godownButton = findViewById(R.id.godownbtn);
        fetchData();
        // Set onClickListener for the Button
        godownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(godown_view.this, godownActivity.class);
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

        firestore.collection("addgodown")
                .whereEqualTo(FIELD_GODOWN_NAME, query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_GODOWN_NAME);
                                    String about = document.getString(FIELD_ABOUT);
                                    String documentId = document.getId();

                                    addDataCard(name, about, documentId);
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
        firestore.collection("addgodown")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_GODOWN_NAME);
                                    String about = document.getString(FIELD_ABOUT);
                                    String documentId = document.getId();

                                    addDataCard(name, about , documentId);




                                }

                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }    private void showToast(String message) {
        Toast.makeText(godown_view.this, message, Toast.LENGTH_SHORT).show();
    }
    private void addDataCard(String name, String about , String documentID) {
        @SuppressLint("InflateParams") View productCardView = getLayoutInflater().inflate(R.layout.godown_card, null);

        TextView gnameTextView = productCardView.findViewById(R.id.godowntext);
        TextView aboutTextView = productCardView.findViewById(R.id.about);
        Button Deletebtn = productCardView.findViewById(R.id.deletebton12);



        gnameTextView.setText(name);
        aboutTextView.setText((about));


        // Now, add the view to the productEntriesContainer
        productEntriesContainer1.addView(productCardView);

        Deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the data card view from the layout
                productEntriesContainer1.removeView(productCardView);

                // Delete the document from Firestore
                firestore.collection("addgodown").document(documentID)
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