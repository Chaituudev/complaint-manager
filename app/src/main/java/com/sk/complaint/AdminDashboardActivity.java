package com.sk.complaint;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<DocumentSnapshot> pendingUsers = new ArrayList<>();
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        loadPendingUsers();
    }

    private void loadPendingUsers() {
        db.collection("users")
                .whereEqualTo("approved", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pendingUsers.clear();
                    pendingUsers.addAll(queryDocumentSnapshots.getDocuments());
                    adapter = new UserAdapter(pendingUsers, this::approveUser);
                    recyclerView.setAdapter(adapter);
                });
    }

    private void approveUser(String uid) {
        db.collection("users").document(uid)
                .update("approved", true)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "User approved!", Toast.LENGTH_SHORT).show()
                );
    }
}
