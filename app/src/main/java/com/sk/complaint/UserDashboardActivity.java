package com.sk.complaint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnNewComplaint, btnLogout;
    private Spinner spinnerFilter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<DocumentSnapshot> complaintList = new ArrayList<>();
    private ComplaintAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        recyclerView = findViewById(R.id.recyclerComplaints);
        btnNewComplaint = findViewById(R.id.btnNewComplaint);
        btnLogout = findViewById(R.id.btnLogout);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new ComplaintAdapter(complaintList);
        recyclerView.setAdapter(adapter);

        btnNewComplaint.setOnClickListener(v ->
                startActivity(new Intent(UserDashboardActivity.this, NewComplaintActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(UserDashboardActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        // Handle filter changes
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                loadComplaints(filter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadComplaints("All"); // default
    }

    private void loadComplaints(String filter) {
        String uid = auth.getCurrentUser().getUid();

        Query query = db.collection("complaints").whereEqualTo("userId", uid);

        if (!filter.equals("All")) {
            query = query.whereEqualTo("status", filter);
        }

        query.get().addOnSuccessListener(querySnapshot -> {
            complaintList.clear();
            complaintList.addAll(querySnapshot.getDocuments());
            adapter.notifyDataSetChanged();
        });
    }
}
