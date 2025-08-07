package com.pack.uniflow.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pack.uniflow.Club;
import com.pack.uniflow.R;

import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    private List<Club> clubList;
    private final Context context;

    public ClubAdapter(List<Club> clubList, Context context) {
        this.clubList = clubList;
        this.context = context;
    }

    public void updateClubs(List<Club> newClubs) {
        this.clubList = newClubs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_club, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = clubList.get(position);

        holder.name.setText(club.getName());
        holder.description.setText(club.getDescription());
        holder.universityName.setText("University: " + club.getUniName());

        // Set click listener for expand/collapse
        holder.headerLayout.setOnClickListener(v -> {
            boolean isVisible = holder.expandableLayout.getVisibility() == View.VISIBLE;

            holder.expandableLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            holder.ivToggle.setImageResource(isVisible ? R.drawable.ic_arrow_drop_down : R.drawable.ic_arrow_drop_up);
        });
    }

    @Override
    public int getItemCount() {
        return clubList != null ? clubList.size() : 0;
    }

    public static class ClubViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, universityName;
        ImageView ivToggle;
        LinearLayout expandableLayout, headerLayout;

        public ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.clubName);
            description = itemView.findViewById(R.id.clubDescription);
            universityName = itemView.findViewById(R.id.clubUniversity);

            ivToggle = itemView.findViewById(R.id.ivToggle);
            headerLayout = itemView.findViewById(R.id.clubHeader);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
        }
    }
}
