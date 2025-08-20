package com.sk.complaint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private List<DocumentSnapshot> complaintList;

    public ComplaintAdapter(List<DocumentSnapshot> complaintList) {
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        DocumentSnapshot doc = complaintList.get(position);

        holder.tvTitle.setText(doc.getString("title"));
        holder.tvDesc.setText(doc.getString("description"));
        holder.tvStatus.setText("Status: " + doc.getString("status"));


        Timestamp createdAt = doc.getTimestamp("createdAt");
        Timestamp updatedAt = doc.getTimestamp("updatedAt");

        if (createdAt != null) {
            holder.tvComplaintCreated.setText("Submitted: " +
                    new SimpleDateFormat("dd MMM yyyy, HH:mm").format(createdAt.toDate()));
        }

        if (updatedAt != null) {
            holder.tvComplaintUpdated.setText("Last Updated: " +
                    new SimpleDateFormat("dd MMM yyyy, HH:mm").format(updatedAt.toDate()));
        }

        // ✅ Show Authority Name instead of UID
        String authorityId = doc.getString("authorityId");
        if (authorityId != null && !authorityId.isEmpty()) {
            FirebaseFirestore.getInstance().collection("users").document(authorityId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            holder.tvAuthority.setText("Authority: " + userDoc.getString("name"));
                        } else {
                            holder.tvAuthority.setText("Authority: -");
                        }
                    });
        } else {
            holder.tvAuthority.setText("Authority: -");
        }

        // ✅ Show Department Name instead of UID
        String departmentId = doc.getString("departmentId");
        if (departmentId != null && !departmentId.isEmpty()) {
            FirebaseFirestore.getInstance().collection("users").document(departmentId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            holder.tvDepartment.setText("Department: " + userDoc.getString("name"));
                        } else {
                            holder.tvDepartment.setText("Department: -");
                        }
                    });
        } else {
            holder.tvDepartment.setText("Department: -");
        }
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvStatus, tvAuthority, tvDepartment, tvComplaintCreated, tvComplaintUpdated;

        public ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvComplaintTitle);
            tvDesc = itemView.findViewById(R.id.tvComplaintDesc);
            tvStatus = itemView.findViewById(R.id.tvComplaintStatus);
            tvAuthority = itemView.findViewById(R.id.tvAuthority);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvComplaintCreated = itemView.findViewById(R.id.tvComplaintCreated);
            tvComplaintUpdated = itemView.findViewById(R.id.tvComplaintUpdated);
        }
    }
}
