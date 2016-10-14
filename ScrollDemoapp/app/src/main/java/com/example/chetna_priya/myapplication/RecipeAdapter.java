package com.example.chetna_priya.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by chetna_priya on 9/3/2016.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private static final String LOG_TAG = RecipeAdapter.class.getSimpleName();
    private List<Recipe> recipeList;

    public RecipeAdapter(@NonNull List<Recipe> recipeList){
        this.recipeList = recipeList;
    }
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recipe,
                parent, false);
        //  Log.d(LOG_TAG, "Inflate item");
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipe_name.setText(recipe.getName());
        //Log.d(LOG_TAG, "POsition: "+position);
        holder.recipe_description.setText(recipe.getDescription());
    }

    @Override
    public int getItemCount() {
        //  Log.d(LOG_TAG, "Total Size: "+recipeList.size());
        return recipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        public TextView recipe_name, recipe_description;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipe_name = (TextView) itemView.findViewById(R.id.tv_recipe);
            recipe_description = (TextView) itemView.findViewById(R.id.tv_recipe_desc);
        }
    }
}
