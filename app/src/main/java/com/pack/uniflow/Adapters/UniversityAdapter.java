package com.pack.uniflow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Fragments.AdminFragment.UniversityWithStudents;
import com.pack.uniflow.R;
import com.pack.uniflow.Student;

import java.util.List;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {

    private final List<UniversityWithStudents> universityList;
    private final Context context;

    public UniversityAdapter(List<UniversityWithStudents> universityList, Context context) {
        this.universityList = universityList;
        this.context = context;
    }

    public static class UniversityViewHolder extends RecyclerView.ViewHolder {
        TextView tvUniversityName;
        ImageView ivToggle;
        LinearLayout expandableLayout;
        RecyclerView studentRecyclerView;
        boolean isExpanded = false;

        public UniversityViewHolder(View itemView) {
            super(itemView);
            tvUniversityName = itemView.findViewById(R.id.tvUniversityName);
            ivToggle = itemView.findViewById(R.id.ivToggle);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            studentRecyclerView = itemView.findViewById(R.id.studentRecyclerView);
        }

        public void bind(UniversityWithStudents universityWithStudents) {
            if (universityWithStudents == null || universityWithStudents.university == null) return;

            tvUniversityName.setText(universityWithStudents.university.getName());
            expandableLayout.setVisibility(View.GONE);
            ivToggle.setImageResource(R.drawable.ic_arrow_drop_down);

            itemView.findViewById(R.id.universityHeader).setOnClickListener(v -> {
                isExpanded = !isExpanded;
                expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                ivToggle.setImageResource(isExpanded ? R.drawable.ic_arrow_drop_up : R.drawable.ic_arrow_drop_down);

                if (isExpanded && universityWithStudents.students != null) {
                    studentRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                    studentRecyclerView.setAdapter(new StudentAdapter(universityWithStudents.students));
                }
            });
        }
    }

    @NonNull
    @Override
    public UniversityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_uni, parent, false);
        return new UniversityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UniversityViewHolder holder, int position) {
        holder.bind(universityList.get(position));
    }

    @Override
    public int getItemCount() {
        return universityList != null ? universityList.size() : 0;
    }
}