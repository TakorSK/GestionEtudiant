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
import com.pack.uniflow.Uni;

import java.util.ArrayList;
import java.util.List;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {

    private List<Uni> universityList;
    private final Context context;
    private final List<Boolean> expandedStates;

    public UniversityAdapter(List<Uni> universityList, Context context) {
        this.universityList = universityList;
        this.context = context;
        this.expandedStates = new ArrayList<>();
        for (int i = 0; i < universityList.size(); i++) {
            expandedStates.add(false); // all collapsed initially
        }
    }

    public void updateList(List<Uni> newList) {
        this.universityList = newList;
        expandedStates.clear();
        for (int i = 0; i < newList.size(); i++) {
            expandedStates.add(false);
        }
        notifyDataSetChanged();
    }

    public static class UniversityViewHolder extends RecyclerView.ViewHolder {
        TextView tvUniversityName, tvLocation, tvYear, tvWebsite;
        ImageView ivToggle;
        LinearLayout expandableLayout, universityHeader;

        public UniversityViewHolder(View itemView) {
            super(itemView);
            tvUniversityName = itemView.findViewById(R.id.tvUniversityName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvWebsite = itemView.findViewById(R.id.tvWebsite);
            ivToggle = itemView.findViewById(R.id.ivToggle);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            universityHeader = itemView.findViewById(R.id.universityHeader);
        }

        public void bind(Uni uni, boolean isExpanded) {
            tvUniversityName.setText(uni.getName());
            tvLocation.setText("Location: " + (uni.getLocation() != null ? uni.getLocation() : "-"));
            tvYear.setText("Established: " + uni.getEstablishedYear());
            tvWebsite.setText("Website: " + (uni.getWebsite() != null ? uni.getWebsite() : "-"));

            expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            ivToggle.setRotation(isExpanded ? 180f : 0f); // arrow rotation for expand/collapse
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
        Uni uni = universityList.get(position);
        boolean isExpanded = expandedStates.get(position);
        holder.bind(uni, isExpanded);

        holder.universityHeader.setOnClickListener(v -> {
            // Toggle expanded state on click
            expandedStates.set(position, !expandedStates.get(position));
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return universityList != null ? universityList.size() : 0;
    }
}
