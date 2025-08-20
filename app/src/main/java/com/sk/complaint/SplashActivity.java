package com.sk.complaint;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                // Already logged in → check role
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(auth.getUid()).get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                boolean approved = doc.getBoolean("approved");
                                String role = doc.getString("role");

                                if (!approved) {
                                    Toast.makeText(this, "Wait for admin approval!", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(this, LoginActivity.class));
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
                                }
                                finish();
                            }
                        });
            } else {
                // Not logged in
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 7000); // 2 sec delay
    }
}
