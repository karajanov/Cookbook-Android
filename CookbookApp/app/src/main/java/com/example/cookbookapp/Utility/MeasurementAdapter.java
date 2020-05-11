package com.example.cookbookapp.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbookapp.Models.Measurement;
import com.example.cookbookapp.R;

import java.util.List;

public class MeasurementAdapter extends  RecyclerView.Adapter<MeasurementAdapter.MeasurementViewHolder> {

    private Context context;
    private List<Measurement> measurementList;

    public MeasurementAdapter(Context context, List<Measurement> measurementList) {
        this.context = context;
        this.measurementList = measurementList;
    }

    @NonNull
    @Override
    public MeasurementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.card_view_measurement, parent, false);
        return new MeasurementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MeasurementViewHolder holder, int position) {
           Measurement currentItem = measurementList.get(position);
           String ingredient = currentItem.getIngredientTitle();
           String quantity = currentItem.getQuantity();
           String consistency = currentItem.getConsistency();

           holder.textViewIngredient.setText("Ingredient: " + ingredient);
           holder.textViewQuantity.setText("Quantity: " + quantity);
           holder.textViewConsistency.setText("Consistency: " + consistency);
    }

    @Override
    public int getItemCount() {
        return measurementList.size();
    }

    public class MeasurementViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewIngredient, textViewQuantity, textViewConsistency;

        public MeasurementViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewIngredient = (TextView) itemView.findViewById(R.id.text_view_card_ingredient);
            textViewQuantity = (TextView) itemView.findViewById(R.id.text_view_card_quantity);
            textViewConsistency = (TextView) itemView.findViewById(R.id.text_view_card_consistency);
        }
    }
}
