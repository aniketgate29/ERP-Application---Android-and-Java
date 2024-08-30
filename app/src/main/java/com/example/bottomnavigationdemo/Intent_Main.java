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

public class Intent_Main extends AppCompatActivity {
    private LinearLayout productEntriesContainer1;
    private FirebaseFirestore firestore;

    private static final String FIELD_EMPLOYEE_NAME = "EmployeeName";
    private static final String FIELD_EMPLOYEE_CODE = "EmployeeCode";
    private static final String FILED_ADRESS ="Address";
    private static final String FIELD_PAN_NUMBER="PanNumber";
    private static final String FIELD_MOBILE_NUMBER="MobileNumber";
    private static final String FIELD_EMAIL="Email";
    private static final String FIELD_AADHAR_NUMBER="AadharNumber";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_main);


        // Initialize TextView
        TextView textView = findViewById(R.id.textview);
        firestore = FirebaseFirestore.getInstance();
        productEntriesContainer1 = findViewById(R.id.productEntriesContainer1);



        // Set text for the TextView
        textView.setText("EmployeeDetails");

        // Initialize Button
        Button AddEmployee = findViewById(R.id.AddEmployee);
        fetchData();
        // Set onClickListener for the Button
        AddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent_Main.this, AddEmployeeActivity.class);
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

        firestore.collection("AddEmployee")
                .whereEqualTo(FIELD_EMPLOYEE_NAME, query)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_EMPLOYEE_NAME);
                                    String code = document.getString(FIELD_EMPLOYEE_CODE);
                                    String address = document.getString(FILED_ADRESS);
                                    String pannumber = document.getString(FIELD_PAN_NUMBER);
                                    String mobilenumber = document.getString(FIELD_MOBILE_NUMBER);
                                    String email = document.getString(FIELD_EMAIL);
                                    String aadharnumber = document.getString(FIELD_AADHAR_NUMBER);


                                    String documentId = document.getId();

                                    addDataCard( name, code, address, pannumber, mobilenumber, email, aadharnumber, documentId);
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
        firestore.collection("AddEmployee")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_EMPLOYEE_NAME);
                                    String code = document.getString(FIELD_EMPLOYEE_CODE);
                                    String address = document.getString(FILED_ADRESS);
                                    String pannumber = document.getString(FIELD_PAN_NUMBER);
                                    String mobilenumber = document.getString(FIELD_MOBILE_NUMBER);
                                    String email = document.getString(FIELD_EMAIL);
                                    String aadharnumber = document.getString(FIELD_AADHAR_NUMBER);

                                    String documentId = document.getId();

                                    addDataCard(name, code, address, pannumber, mobilenumber, email, aadharnumber, documentId);




                                }

                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }    private void showToast(String message) {
        Toast.makeText(Intent_Main.this, message, Toast.LENGTH_SHORT).show();
    }
    private void addDataCard(String name, String code, String address, String pannumber, String mobilenumber, String email, String aadharnumber, String documentID) {
        @SuppressLint("InflateParams") View productCardView = getLayoutInflater().inflate(R.layout.parches_card_emp, null);


        TextView employeenameTextView = productCardView.findViewById(R.id.employeenameTextView);
        TextView employeecodeTextView = productCardView.findViewById(R.id.codeTextView);
        TextView addressTextView = productCardView.findViewById(R.id.addressTextView);
        TextView pannumberTextView = productCardView.findViewById(R.id.pannumberTextView);
        TextView mobilenumberTextView = productCardView.findViewById(R.id.mobilenumberTextView);
        TextView emailTextView = productCardView.findViewById(R.id.emailTextView);
        TextView aadharnumberTextView = productCardView.findViewById(R.id.aadharnumberTextView);

        Button Deletebtn = productCardView.findViewById(R.id.deletebton12);

        employeenameTextView.setText(name);
        employeecodeTextView.setText((code));
        addressTextView.setText(address);
        pannumberTextView.setText(pannumber);
        mobilenumberTextView.setText(mobilenumber);
        emailTextView.setText(email);
        aadharnumberTextView.setText(aadharnumber);



        // Now, add the view to the productEntriesContainer
        productEntriesContainer1.addView(productCardView);

        Deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the data card view from the layout
                productEntriesContainer1.removeView(productCardView);

                // Delete the document from Firestore
                firestore.collection("AddEmployee").document(documentID)
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