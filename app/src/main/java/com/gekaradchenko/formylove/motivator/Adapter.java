package com.gekaradchenko.formylove.motivator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {
    private ArrayList<Motivation> motivations = new ArrayList<>();



    public ArrayList<Motivation> getMotivations() {
        return motivations;
    }

    public void setMotivations(ArrayList<Motivation> motivations) {
        this.motivations = motivations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        AdapterViewHolder adapterViewHolder = new AdapterViewHolder(view);
        return adapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {

        Motivation motivation = motivations.get(position);
        holder.textViewID.setText(motivation.getId()+1+":");
        holder.textViewText.setText(motivation.getText());

    }

    @Override
    public int getItemCount() {

        return motivations.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewID;
        private TextView textViewText;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewID = itemView.findViewById(R.id.textViewID);
            textViewText = itemView.findViewById(R.id.textViewText);
        }
    }
}
