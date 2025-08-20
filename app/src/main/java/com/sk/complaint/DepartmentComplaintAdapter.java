package com.sk.complaint;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DepartmentComplaintAdapter extends RecyclerView.Adapter<DepartmentComplaintAdapter.ViewHolder> {

    private final Activity activity;
    private final List<DocumentSnapshot> complaints;
    private final FirebaseFirestore db;

    public DepartmentComplaintAdapter(Activity activity, List<DocumentSnapshot> complaints, FirebaseFirestore db) {
        this.activity = activity;
        this.complaints = complaints;
        this.db = db;
    }

    @NonNull
    @Override
    public DepartmentComplaintAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department_complaint, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartmentComplaintAdapter.ViewHolder holder, int position) {
        DocumentSnapshot doc = complaints.get(position);

        String title = doc.getString("title");
        String desc = doc.getString("description");
        String status = doc.getString("status");
        String ref = doc.getString("complaintRef");

        holder.tvRef.setText(ref != null ? ref : "-");
        holder.tvTitle.setText(title != null ? title : "");
        holder.tvDesc.setText(desc != null ? desc : "");
        holder.tvStatus.setText(activity.getString(R.string.status_prefix, status != null ? status : "-"));

        holder.btnResolve.setOnClickListener(v -> confirmAndUpdate(doc.getId(), "resolved"));
        holder.btnSendBack.setOnClickListener(v -> confirmAndUpdate(doc.getId(), "rejected_by_department"));
    }

    private void confirmAndUpdate(String complaintId, String newStatus) {
        String msg;
        if ("resolved".equals(newStatus)) {
            msg = activity.getString(R.string.confirm_mark_resolved);
        } else {
            msg = activity.getString(R.string.confirm_send_back);
        }

        new AlertDialog.Builder(activity)
                .setTitle(R.string.confirm_action_title)
                .setMessage(msg)
                .setPositiveButton(R.string.confirm, (d, w) -> updateStatus(complaintId, newStatus))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void updateStatus(String complaintId, String status) {
        db.collection("complaints").document(complaintId)
                .update(
                        "status", status,
                        "updatedAt", FieldValue.serverTimestamp()
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activity, activity.getString(R.string.status_updated), Toast.LENGTH_SHORT).show();
                    // Remove from current list if you want instant UI feedback
                    int indexToRemove = -1;
                    for (int i = 0; i < complaints.size(); i++) {
                        if (complaints.get(i).getId().equals(complaintId)) {
                            indexToRemove = i;
                            break;
                        }
                    }
                    if (indexToRemove >= 0) {
                        complaints.remove(indexToRemove);
                        notifyItemRemoved(indexToRemove);
                    } else {
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRef, tvTitle, tvDesc, tvStatus;
        Button btnResolve, btnSendBack;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRef = itemView.findViewById(R.id.tvRef);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnResolve = itemView.findViewById(R.id.btnResolve);
            btnSendBack = itemView.findViewById(R.id.btnSendBack);
        }
    }
}
