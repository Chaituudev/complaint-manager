package com.sk.complaint;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AuthorityComplaintAdapter extends RecyclerView.Adapter<AuthorityComplaintAdapter.ViewHolder> {

    private final Activity activity;
    private final List<DocumentSnapshot> complaints;
    private final FirebaseFirestore db;

    public AuthorityComplaintAdapter(Activity activity, List<DocumentSnapshot> complaints, FirebaseFirestore db) {
        this.activity = activity;
        this.complaints = complaints;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_authority_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = complaints.get(position);

        holder.tvTitle.setText(doc.getString("title"));
        holder.tvDesc.setText(doc.getString("description"));
        holder.tvStatus.setText("Status: " + doc.getString("status"));

        holder.btnApprove.setOnClickListener(v -> showDepartmentSelectionDialog(doc));
        holder.btnDisapprove.setOnClickListener(v -> updateStatus(doc.getId(), "disapproved"));
    }

    private void updateStatus(String complaintId, String status) {
        db.collection("complaints").document(complaintId)
                .update("status", status)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(activity, "Complaint " + status, Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void showDepartmentSelectionDialog(DocumentSnapshot doc) {
        View dialogView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_select_department, null);

        Spinner spinnerDepartments = dialogView.findViewById(R.id.spinnerDepartments);

        db.collection("users")
                .whereEqualTo("role", "department")
                .get()
                .addOnSuccessListener(query -> {
                    List<String> deptList = new ArrayList<>();
                    List<String> deptIds = new ArrayList<>();
                    for (DocumentSnapshot d : query.getDocuments()) {
                        deptList.add(d.getString("name"));
                        deptIds.add(d.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            activity,
                            android.R.layout.simple_spinner_item,
                            deptList
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDepartments.setAdapter(adapter);

                    new AlertDialog.Builder(activity)
                            .setTitle("Assign Department")
                            .setView(dialogView)
                            .setPositiveButton("Assign", (dialog, which) -> {
                                int pos = spinnerDepartments.getSelectedItemPosition();
                                if (pos >= 0) {
                                    String deptId = deptIds.get(pos);
                                    db.collection("complaints").document(doc.getId())
                                            .update("departmentId", deptId, "status", "in_process")
                                            .addOnSuccessListener(aVoid ->
                                                    Toast.makeText(activity,
                                                            "Assigned to department!", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(activity,
                                                            "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                });
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvStatus;
        Button btnApprove, btnDisapprove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvComplaintTitle);
            tvDesc = itemView.findViewById(R.id.tvComplaintDesc);
            tvStatus = itemView.findViewById(R.id.tvComplaintStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnDisapprove = itemView.findViewById(R.id.btnDisapprove);
        }
    }
}
