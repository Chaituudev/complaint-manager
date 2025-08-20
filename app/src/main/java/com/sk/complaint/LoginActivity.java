package com.sk.complaint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin,btnregister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnregister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());

        btnregister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

    }

    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        db.collection("users").document(uid).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        boolean approved = documentSnapshot.getBoolean("approved");
                                        String role = documentSnapshot.getString("role");

                                        if (!approved) {
                                            Toast.makeText(this, "Wait for admin approval!", Toast.LENGTH_LONG).show();
                                            mAuth.signOut();
                                        } else {
                                            if (role.equals("admin")) {
                                                startActivity(new Intent(this, AdminDashboardActivity.class));
                                            } else if (role.equals("user")) {
                                                startActivity(new Intent(this, UserDashboardActivity.class));
                                            } else if (role.equals("authority")) {
                                                startActivity(new Intent(this, AuthorityDashboardActivity.class));
                                            } else if (role.equals("department")) {
                                                startActivity(new Intent(this, DepartmentDashboardActivity.class));
                                            }
                                            finish();
                                        }
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    } else {
                        Toast.makeText(this, "Login Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
