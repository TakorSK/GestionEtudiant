package com.pack.uniflow.Adapters;

import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pack.uniflow.Student;
import com.pack.uniflow.R;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private final List<Student> studentList;

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentStatus;
        ImageView studentAvatar;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentStatus = itemView.findViewById(R.id.studentStatus);
            studentAvatar = itemView.findViewById(R.id.studentAvatar);
        }

        public void bind(Student student) {
            studentName.setText(student.getFullName());

            boolean isOnline = student.isOnline();
            studentStatus.setText(isOnline ? "Online" : "Offline");
            studentStatus.setTextColor(isOnline ?
                    Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

            String profileUri = student.getProfilePictureUri();
            if (profileUri != null && !profileUri.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(Uri.parse(profileUri))
                        .placeholder(R.drawable.nav_profile_pic)
                        .error(R.drawable.nav_profile_pic)
                        .circleCrop()
                        .into(studentAvatar);
            } else {
                studentAvatar.setImageResource(R.drawable.nav_profile_pic);
            }
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(studentList.get(position));
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }
}
