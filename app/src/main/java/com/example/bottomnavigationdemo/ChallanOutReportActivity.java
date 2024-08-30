package com.example.bottomnavigationdemo;





import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChallanOutReportActivity extends AppCompatActivity {

    private EditText fromDateEditText, toDateEditText , totalAmt,noOfTransactions;
    private FirebaseFirestore firestore;
    private Calendar calendar;
    private LinearLayout productEntries1;
    private double totalAmount = 0.0;
    private int transactionCount = 0;
    private static final String FIELD_CUSTOMER_NAME = "Customer Name";
    private static final String FIELD_TOTAL_AMOUNT = "totalAmount";
    private static final String FIELD_DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challanout_report);


        fromDateEditText = findViewById(R.id.fromDateEditText);
        toDateEditText = findViewById(R.id.toDateEditText);
        calendar = Calendar.getInstance();
        productEntries1 = findViewById(R.id.productEntries1);
        firestore = FirebaseFirestore.getInstance();
        totalAmt = findViewById(R.id.editTextTotalPurchase);
        noOfTransactions = findViewById(R.id.editTextTransactionCount);
        Button sort=findViewById(R.id.sortButton);


        fromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(fromDateEditText);
            }
        });

        toDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(toDateEditText);
            }
        });
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortdata();
            }
        });
        FetchData();
    }
    private void sortdata() {
        productEntries1.removeAllViews();

        String fromDate = fromDateEditText.getText().toString().trim();
        String toDate = toDateEditText.getText().toString().trim();
        firestore.collection("challanOut")
                .whereGreaterThanOrEqualTo(FIELD_DATE, fromDate)
                .whereLessThanOrEqualTo(FIELD_DATE, toDate)
                .orderBy(FIELD_DATE) // Sort by date
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                double totalAmount = 0.0;
                                int transactionCount = 0;

                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    String name = document.getString(FIELD_CUSTOMER_NAME);
                                    String total = document.getString(FIELD_TOTAL_AMOUNT);
                                    String date = document.getString(FIELD_DATE);
                                    addDataCard(name, total, date);

                                    if (total != null && !total.trim().isEmpty()) {
                                        try {
                                            double transactionAmount = Double.parseDouble(total.trim());
                                            totalAmount += transactionAmount;
                                            transactionCount++;
                                        } catch (NumberFormatException e) {
                                            // Handle the case where the total cannot be parsed to a double
                                            e.printStackTrace();
                                            showToast("Error parsing total amount: " + total);
                                        }
                                    }
                                }
                                totalAmt.setText(String.valueOf(totalAmount));
                                noOfTransactions.setText(String.valueOf(transactionCount));
                            }
                        } else {
                            showToast("Error fetching data from Firestore: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void FetchData() {
        firestore.collection("challanOut")
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
                                    addDataCard(name, total, date);

                                    if (total != null && !total.trim().isEmpty()) {
                                        try {
                                            double totalAmount = Double.parseDouble(total.trim());
                                            calculateTotalAmount(totalAmount);
                                        } catch (NumberFormatException e) {
                                            // Handle the case where the total cannot be parsed to a double
                                            e.printStackTrace();
                                            showToast("Error parsing total amount: " + total);
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
        Toast.makeText(ChallanOutReportActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showDatePickerDialog(final EditText editText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ChallanOutReportActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void addDataCard(String name, String total, String date) {
        View productCardView = getLayoutInflater().inflate(R.layout.challan_out_card, null);

        TextView nameTextView = productCardView.findViewById(R.id.nameTextView);
        TextView dateTextView = productCardView.findViewById(R.id.date1);
        TextView totalTextView = productCardView.findViewById(R.id.TolTextView);

        nameTextView.setText(name);
        dateTextView.setText((date));
        totalTextView.setText(total);

        // Now, add the view to the productEntriesContainer
        productEntries1.addView(productCardView);
    }
    private void calculateTotalAmount(double transactionAmount) {
        totalAmount += transactionAmount;
        transactionCount++;
    }

    private void displayTotalAmount() {
        totalAmt.setText(String.valueOf(totalAmount));
        noOfTransactions.setText(String.valueOf(transactionCount));
    }

    private String formatDate(String originalDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(originalDate);
    }
}
