package com.pack.uniflow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.R;
import com.pack.uniflow.Models.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private final Context context;
    private final List<Boolean> expandedStates;

    public StudentAdapter(List<Student> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
        this.expandedStates = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++) {
            expandedStates.add(false); // All collapsed initially
        }
    }

    public void updateList(List<Student> newStudentList) {
        this.studentList = newStudentList;
        expandedStates.clear();
        for (int i = 0; i < newStudentList.size(); i++) {
            expandedStates.add(false);
        }
        notifyDataSetChanged();
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
        boolean isExpanded = expandedStates.get(position);
        holder.bind(student, isExpanded);

        holder.studentHeader.setOnClickListener(v -> {
            boolean newState = !expandedStates.get(position);
            expandedStates.set(position, newState);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView studentName, studentStatus;
        LinearLayout expandableLayout;
        LinearLayout studentHeader;
        ImageView arrowIcon;

        TextView tvStudentId, tvEmail, tvAge, tvTelephone, tvUniId, tvClubId, tvIsAdmin, tvRegDate, tvLastLogin, tvBio;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            studentName = itemView.findViewById(R.id.studentName);
            studentStatus = itemView.findViewById(R.id.studentStatus);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            studentHeader = itemView.findViewById(R.id.studentHeader);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);

            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvTelephone = itemView.findViewById(R.id.tvTelephone);
            tvUniId = itemView.findViewById(R.id.tvUniId);
            tvClubId = itemView.findViewById(R.id.tvClubId);
            tvIsAdmin = itemView.findViewById(R.id.tvIsAdmin);
            tvRegDate = itemView.findViewById(R.id.tvRegDate);
            tvLastLogin = itemView.findViewById(R.id.tvLastLogin);
            tvBio = itemView.findViewById(R.id.tvBio);
        }

        public void bind(Student student, boolean isExpanded) {
            studentName.setText(student.getFullName());
            studentStatus.setText(student.isOnline() ? "Online" : "Offline");

            // Set the ID here
            tvStudentId.setText("ID: " + (student.getId() != null ? student.getId() : "-"));

            tvEmail.setText("Email: " + student.getEmail());
            tvAge.setText("Age: " + student.getAge());
            tvTelephone.setText("Tel: " + (student.getTelephone() != null ? student.getTelephone() : "-"));
            tvUniId.setText("University ID: " + (student.getUniId() != null ? student.getUniId() : "-"));
            tvClubId.setText("Club ID: " + (student.getClubId() != null ? student.getClubId() : "-"));
            tvIsAdmin.setText("Admin: " + (student.isAdmin() ? "Yes" : "No"));
            tvRegDate.setText("Registered: " + (student.getRegistrationDate() != null ? student.getRegistrationDate() : "-"));
            tvLastLogin.setText("Last Login: " + (student.getLastLogin() != null ? student.getLastLogin() : "-"));
            tvBio.setText("Bio: " + (student.getBio() != null ? student.getBio() : "-"));

            expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            arrowIcon.setImageResource(isExpanded ? R.drawable.ic_arrow_drop_up : R.drawable.ic_arrow_drop_down);
        }
    }

}
