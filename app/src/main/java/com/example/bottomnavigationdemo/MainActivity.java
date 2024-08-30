package com.example.bottomnavigationdemo;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bottomnavigationdemo.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    private FirebaseFirestore database;

    DrawerLayout drawerLayout;
    TextView nameTextView,emailTextView;
    NavigationView navigationView;
    ImageView Image;
    Toolbar toolbar;
    String userId;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);


        // Set toolbar as action bar
        setSupportActionBar(toolbar);
        // Inflate the nav_header layout
        View headerView = navigationView.getHeaderView(0);

        // Find TextViews inside the nav_header
        nameTextView = headerView.findViewById(R.id.name);
        emailTextView = headerView.findViewById(R.id.email);
        Image = headerView.findViewById(R.id.image);


        // Update the text of TextViews



        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            fetch_user(userId);
        }

        else {
            // Handle the case where userId is not available
        }




        // Set up navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Set initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Set up bottom navigation
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.shorts:
                    replaceFragment(new ShortsFragment());
                    break;
                case R.id.subscriptions:
                    replaceFragment(new SubscriptionFragment());
                    break;
                case R.id.library:
                    replaceFragment(new LibraryFragment());
                    break;
            }
            return true;
        });

        // Set up FAB click listener
        binding.fab.setOnClickListener(view -> showBottomDialog());

        Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "ImageView clicked");
                // Start the next activity here
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userId", userId);

                startActivity(intent);
            }
        });


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        Button addbutton = dialog.findViewById(R.id.addproduct3);
        addbutton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                replaceFragment(new HomeFragment());
                break;
            case R.id.nav_settings:
                replaceFragment(new SettingsFragment());
                break;
            case R.id.nav_share:
                replaceFragment(new ShareFragment());
                break;
            case R.id.nav_about:
                replaceFragment(new AboutFragment());
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void fetch_user(String userId) {
        if (userId != null) {
            DocumentReference userRef = database.collection("users").document(userId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        // Update TextViews with user data
                        nameTextView.setText(name);
                        emailTextView.setText(email);


                        fetchImage();
                        // Load user image using Picasso


                    } else {
                        // Document does not exist
                        Log.d("MainActivity", "User document does not exist");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failure
                    Log.e("MainActivity", "Error fetching user data: " + e.getMessage());
                }
            });
        } else {
            // userId is null, handle this scenario
            Log.e("MainActivity", "userId is null");
        }
    }
    private void fetchImage () {
        // Get reference to the image in Firebase Storage
        StorageReference imageRef = storageReference.child("profile_Image/"+userId+".JPG");

        // Download the image into a local file
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image into the ImageView using a library like Picasso or Glide
            Picasso.get().load(uri).into(Image);
        }).addOnFailureListener(exception -> {
            if (exception instanceof StorageException) {
                // Handle StorageException
                StorageException storageException = (StorageException) exception;
                int errorCode = storageException.getErrorCode();
                String errorMessage = storageException.getMessage();
                Log.e("FirebaseStorage", "StorageException: " + errorMessage);
            } else {
                // Handle other exceptions
                Log.e("FirebaseStorage", "Failed to fetch image", exception);
            }
        });
    }
}

