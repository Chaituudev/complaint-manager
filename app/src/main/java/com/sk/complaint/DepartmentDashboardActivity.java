package com.sk.complaint;

import android.content.Intent;
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

public class DepartmentDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnLogout;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<DocumentSnapshot> complaintList = new ArrayList<>();
    private DepartmentComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_dashboard);

        recyclerView = findViewById(R.id.recyclerComplaintsDepartment);
        btnLogout = findViewById(R.id.btnLogout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(DepartmentDashboardActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadComplaints();
    }

    private void loadComplaints() {
        String deptUid = auth.getCurrentUser().getUid();

        db.collection("complaints")
                .whereEqualTo("departmentId", deptUid)
                .get()
                .addOnSuccessListener(query -> {
                    complaintList.clear();
                    complaintList.addAll(query.getDocuments());
                    adapter = new DepartmentComplaintAdapter(this, complaintList, db);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
