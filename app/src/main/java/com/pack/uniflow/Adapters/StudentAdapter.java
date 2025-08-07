package com.pack.uniflow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.R;
import com.pack.uniflow.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private Context context;

    public StudentAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.studentName.setText(student.getFullName());
        holder.studentStatus.setText(student.isOnline() ? "Online" : "Offline");
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    // Method to update the student list
    public void updateList(List<Student> newStudentList) {
        this.studentList = newStudentList;
        notifyDataSetChanged();
    }

    // ViewHolder class for binding student data to views
    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView studentName;
        TextView studentStatus;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentStatus = itemView.findViewById(R.id.studentStatus);
        }
    }
}
