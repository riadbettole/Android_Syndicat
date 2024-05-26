package com.example.android_syndicat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    public ItemAdapter(List<Item> itemList) {this.itemList = itemList;}

    public void setFilteredList(List<Item> filteredList){
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position){
        Item item = itemList.get(position);
        holder.itemNameTv.setText(item.getItemName());
//        holder.itemImageView.setImageResource(item.getItemImage());

//        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.a));
    }

    @Override
    public int getItemCount(){
        if(itemList == null){
            return 0;
        }
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView itemNameTv;
        private ImageView itemImageView;
        private CardView cardView;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.eachCardView);
            itemNameTv = itemView.findViewById(R.id.eachItemTextView);
            itemImageView = itemView.findViewById(R.id.eachItemImageView);
        }
    }
}

