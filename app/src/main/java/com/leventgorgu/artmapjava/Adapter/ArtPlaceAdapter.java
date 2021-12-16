package com.leventgorgu.artmapjava.Adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leventgorgu.artmapjava.Model.ArtPlace;
import com.leventgorgu.artmapjava.View.DetailsActivity;
import com.leventgorgu.artmapjava.databinding.RecyclerRowBinding;

import java.util.List;

public class ArtPlaceAdapter extends RecyclerView.Adapter<ArtPlaceAdapter.ArtPlaceHolder> {

    private List<ArtPlace> artPlaceList;
    public ArtPlaceAdapter(List<ArtPlace> artPlaceList){
        this.artPlaceList=artPlaceList;
    }

    @NonNull
    @Override
    public ArtPlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ArtPlaceHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtPlaceHolder holder, int position) {
        holder.binding.textViewRecycler.setText(artPlaceList.get(position).artName);
        Bitmap bitmap  = BitmapFactory.decodeByteArray(artPlaceList.get(position).selectedImage,0,artPlaceList.get(position).selectedImage.length);
        holder.binding.imageViewRecycler.setImageBitmap(bitmap);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),DetailsActivity.class);
                intent.putExtra("artPlace",artPlaceList.get(holder.getAdapterPosition()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artPlaceList.size();
    }

    public class ArtPlaceHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public ArtPlaceHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
