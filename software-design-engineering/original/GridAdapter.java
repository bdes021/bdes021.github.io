package com.example.bruno_desousa_trakker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private List<GridItem> items;

    public GridAdapter(List<GridItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_edit_event_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the views in each ViewHolder
        GridItem item = items.get(position);
        holder.textViewDate.setText(item.getDate());
        holder.textStartTime.setText(item.getTime());
        holder.textEndTime.setText(item.getTime());
        holder.editTextTitle.setText(item.getTitle());
        holder.editTextDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the data set
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Declare views in the ViewHolder
        TextView textViewDate;
        TextView textStartTime;

        TextView textEndTime;
        EditText editTextTitle;
        EditText editTextDescription;
        Button buttonSave;
        Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views in the ViewHolder
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textStartTime = itemView.findViewById(R.id.textStartTime);
            textEndTime = itemView.findViewById(R.id.textEndTime);
            editTextTitle = itemView.findViewById(R.id.editTextTitle);
            editTextDescription = itemView.findViewById(R.id.editTextDescription);
            buttonSave = itemView.findViewById(R.id.buttonSave);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
