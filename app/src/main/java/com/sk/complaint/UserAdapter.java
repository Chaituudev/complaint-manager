package com.sk.complaint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<DocumentSnapshot> userList;
    private OnApproveClickListener listener;

    public interface OnApproveClickListener {
        void onApproveClick(String uid);
    }

    public UserAdapter(List<DocumentSnapshot> userList, OnApproveClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        DocumentSnapshot doc = userList.get(position);
        String name = doc.getString("name");
        String email = doc.getString("email");
        String role = doc.getString("role");
        String uid = doc.getString("uid");

        holder.tvUserInfo.setText(name + " (" + role + ")\n" + email);

        holder.btnApprove.setOnClickListener(v -> {
            if (listener != null) listener.onApproveClick(uid);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserInfo;
        Button btnApprove;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserInfo = itemView.findViewById(R.id.tvUserInfo);
            btnApprove = itemView.findViewById(R.id.btnApprove);
        }
    }
}
