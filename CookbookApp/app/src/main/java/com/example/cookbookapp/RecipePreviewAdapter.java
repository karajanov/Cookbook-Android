package com.example.cookbookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class RecipePreviewAdapter extends RecyclerView.Adapter<RecipePreviewAdapter.RecipeViewHolder> {

    private Context context;
    private List<RecipePreview> recipePreviewItemsList;
    private OnItemClickListener clickListener;

    public RecipePreviewAdapter(Context context, List<RecipePreview> recipePreviewItemsList) {
        this.context = context;
        this.recipePreviewItemsList = recipePreviewItemsList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.cardview_recipe_preview, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipePreview currentItem = recipePreviewItemsList.get(position);

        int id = currentItem.getId();
        String title = currentItem.getTitle();
        String author = currentItem.getAuthor();
        String imagePath = currentItem.getImagePath();

        holder.textViewRecipeId.setText(String.valueOf(id));
        holder.textViewRecipeTitle.setText(title);
        holder.textViewRecipeAuthor.setText("by " + author);

        if(imagePath != null) {
            Glide.with(context)
                    .load(Helper.IMAGE_API_URL + imagePath)
                    .error(R.drawable.notfound)
                    .fitCenter()
                    .centerInside()
                    .into(holder.imageViewRecipe);
        }
    }

    @Override
    public int getItemCount() {
        return recipePreviewItemsList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewRecipe;
        public TextView textViewRecipeTitle, textViewRecipeAuthor, textViewRecipeId;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewRecipe = (ImageView) itemView.findViewById(R.id.image_view_recipe);
            textViewRecipeId = (TextView) itemView.findViewById(R.id.text_view_recipe_id);
            textViewRecipeTitle = (TextView) itemView.findViewById(R.id.text_view_recipe_title);
            textViewRecipeAuthor = (TextView) itemView.findViewById(R.id.text_view_recipe_author);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(position, recipePreviewItemsList);
                        }
                    }
                }
            });
        }
    }

    public interface  OnItemClickListener {
        void onItemClick(int position, List<RecipePreview> recipePreviewList);
    }
}
