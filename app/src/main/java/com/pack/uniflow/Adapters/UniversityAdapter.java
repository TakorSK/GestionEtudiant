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

import com.pack.uniflow.R;
import com.pack.uniflow.Uni;  // Import the Uni class

import java.util.List;

public class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {

    private List<Uni> universityList;  // Use Uni directly
    private final Context context;

    public UniversityAdapter(List<Uni> universityList, Context context) {
        this.universityList = universityList;
        this.context = context;
    }

    public void updateList(List<Uni> newList) {
        this.universityList = newList;
        notifyDataSetChanged();  // Notify the adapter that the list has been updated
    }

    public static class UniversityViewHolder extends RecyclerView.ViewHolder {
        TextView tvUniversityName;

        public UniversityViewHolder(View itemView) {
            super(itemView);
            tvUniversityName = itemView.findViewById(R.id.tvUniversityName);
        }

        public void bind(Uni university) {
            if (university != null) {
                tvUniversityName.setText(university.getName());
            }
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
