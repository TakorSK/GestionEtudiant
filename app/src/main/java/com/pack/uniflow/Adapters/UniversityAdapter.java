package com.pack.uniflow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Adapters.StudentAdapter;
import com.pack.uniflow.DummyData.DummyUniversity;
import com.pack.uniflow.R;

import java.util.List;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {

    private List<DummyUniversity> universityList;

    public UniversityAdapter(List<DummyUniversity> universityList) {
        this.universityList = universityList;
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

        public void bind(DummyUniversity university, Context context) {
            tvUniversityName.setText(university.getName());
            expandableLayout.setVisibility(View.GONE);
            ivToggle.setImageResource(R.drawable.ic_arrow_drop_down);

            // Toggle expansion
            itemView.findViewById(R.id.universityHeader).setOnClickListener(v -> {
                isExpanded = !isExpanded;
                expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                ivToggle.setImageResource(isExpanded ? R.drawable.ic_arrow_drop_up : R.drawable.ic_arrow_drop_down);

                // Set up student list
                if (isExpanded) {
                    studentRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    studentRecyclerView.setAdapter(new StudentAdapter(university.getStudents()));
                }
            });
        }
    }

    @Override
    public UniversityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item_uni, parent, false);
        return new UniversityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UniversityViewHolder holder, int position) {
        holder.bind(universityList.get(position), holder.itemView.getContext());
    }

    @Override
    public int getItemCount() {
        return universityList.size();
    }
}
