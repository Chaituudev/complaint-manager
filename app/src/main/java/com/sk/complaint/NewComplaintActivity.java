package com.sk.complaint;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewComplaintActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnSubmit;
    private Spinner spinnerAuthorities;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<String> authorityIds = new ArrayList<>();
    private List<String> authorityNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        spinnerAuthorities = findViewById(R.id.spinnerAuthorities);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadAuthorities();

        btnSubmit.setOnClickListener(v -> submitComplaint());
    }

    private void loadAuthorities() {
        db.collection("users")
                .whereEqualTo("role", "authority")
                .whereEqualTo("approved", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    authorityIds.clear();
                    authorityNames.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String id = doc.getId();
                        String name = doc.getString("name");
                        authorityIds.add(id);
                        authorityNames.add(name != null ? name : "Unknown");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_spinner_item, authorityNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerAuthorities.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load authorities", Toast.LENGTH_SHORT).show()
                );
    }

    private void submitComplaint() {
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String uid = auth.getCurrentUser().getUid();
        String complaintId = db.collection("complaints").document().getId();
        String complaintRef = "CMP-" + System.currentTimeMillis();

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (authorityIds.isEmpty()) {
            Toast.makeText(this, "No authority available", Toast.LENGTH_SHORT).show();
            return;
        }

        int pos = spinnerAuthorities.getSelectedItemPosition();
        String selectedAuthorityId = authorityIds.get(pos);

        Map<String, Object> complaint = new HashMap<>();
        complaint.put("complaintId", complaintId);
        complaint.put("userId", uid);
        complaint.put("title", title);
        complaint.put("description", desc);
        complaint.put("status", "submitted");
        complaint.put("authorityId", selectedAuthorityId);
        complaint.put("departmentId", null);
        complaint.put("createdAt", FieldValue.serverTimestamp());
        complaint.put("updatedAt", FieldValue.serverTimestamp());
        complaint.put("complaintRef", complaintRef);

        db.collection("complaints").document(complaintId)
                .set(complaint)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Complaint Submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
