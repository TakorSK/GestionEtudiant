package com.pack.uniflow.Adapters;
 // replace with your actual package

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Adapters.PostAdapter;
import com.pack.uniflow.DummyData.DummyStudent;
import com.pack.uniflow.R;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<DummyStudent> studentList;

    // Constructor
    public StudentAdapter(List<DummyStudent> studentList) {
        this.studentList = studentList;
    }

    // ViewHolder class
    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentStatus;
        ImageView studentAvatar;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentStatus = itemView.findViewById(R.id.studentStatus);
            studentAvatar = itemView.findViewById(R.id.studentAvatar);
        }

        public void bind(DummyStudent student) {
            studentName.setText(student.getName());
            studentStatus.setText(student.getStatus());

            // Color based on online/offline
            if (student.getStatus().equalsIgnoreCase("Online")) {
                studentStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                studentStatus.setTextColor(Color.parseColor("#F44336")); // Red
            }

            // Set a placeholder avatar for now
            studentAvatar.setImageResource(R.drawable.nav_profile_pic); // <-- Make sure this exists!
        }
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_student, parent, false); // <-- Make sure this layout exists!
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        holder.bind(studentList.get(position));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
