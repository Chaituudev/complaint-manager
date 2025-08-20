package com.sk.complaint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    private List<DocumentSnapshot> complaints;

    public ComplaintAdapter(List<DocumentSnapshot> complaints) {
        this.complaints = complaints;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_complaint, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = complaints.get(position);

        holder.tvRef.setText(doc.getString("complaintRef"));
        holder.tvTitle.setText(doc.getString("title"));
        holder.tvDesc.setText(doc.getString("description"));
        holder.tvStatus.setText("Status: " + doc.getString("status"));

        String authority = doc.getString("authorityId");
        String department = doc.getString("departmentId");

        holder.tvAuthority.setText("Authority: " + (authority != null ? authority : "Not Assigned"));
        holder.tvDepartment.setText("Department: " + (department != null ? department : "Not Assigned"));

        Timestamp created = doc.getTimestamp("createdAt");
        Timestamp updated = doc.getTimestamp("updatedAt");

        if (created != null) {
            holder.tvCreated.setText("Submitted: " + created.toDate().toString());
        }
        if (updated != null) {
            holder.tvUpdated.setText("Last Updated: " + updated.toDate().toString());
        }
    }


    @Override
    public int getItemCount() {
        return complaints.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRef, tvTitle, tvDesc, tvStatus, tvAuthority, tvDepartment, tvCreated, tvUpdated;
        ViewHolder(View itemView) {
            super(itemView);
            tvRef = itemView.findViewById(R.id.tvRef);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAuthority = itemView.findViewById(R.id.tvAuthority);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvCreated = itemView.findViewById(R.id.tvCreated);
            tvUpdated = itemView.findViewById(R.id.tvUpdated);
        }
    }
}
