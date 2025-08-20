package com.sk.complaint;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AuthorityDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnLogout;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<DocumentSnapshot> complaintList = new ArrayList<>();
    private AuthorityComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_dashboard);

        recyclerView = findViewById(R.id.recyclerComplaintsAuthority);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadComplaints();
    }

    private void loadComplaints() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("complaints")
                .whereEqualTo("authorityId", uid) // show only assigned to this authority
                .get()
                .addOnSuccessListener(query -> {
                    complaintList.clear();
                    complaintList.addAll(query.getDocuments());
                    adapter = new AuthorityComplaintAdapter(this, complaintList, db);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
