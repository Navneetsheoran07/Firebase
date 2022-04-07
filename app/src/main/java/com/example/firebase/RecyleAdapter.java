package com.example.firebase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyleAdapter extends RecyclerView.Adapter<RecyleAdapter.MyViewHolder> {
ArrayList<ImageModelClass> imagelist;
ArrayList<ModelClass> userlists;
Context context;

    public RecyleAdapter(ArrayList<ImageModelClass> imagelist, ArrayList<ModelClass> userlists, Context context) {

        this.imagelist = imagelist;
        this.userlists = userlists;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recylerformet,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return  myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyleAdapter.MyViewHolder holder, int position) {

        ModelClass modelClass= userlists.get(position);
        ImageModelClass imageModelClass = imagelist.get(position);
        Glide.with(context).load(imageModelClass.getImageUrl()).into(holder.imageViewss);
        holder.nametext.setText(modelClass.getNametext());
        holder.numbertext.setText(modelClass.getNumbertext());


        
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent= new Intent(context,DetailsActivity.class);
              intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
              intent.putExtra("userid",imageModelClass.getUserid());


              context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userlists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewss ;
        Button button;
        TextView nametext,numbertext;
        RelativeLayout Layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Layout = itemView.findViewById(R.id.RelativeLayout);
            imageViewss = itemView.findViewById(R.id.imageviewadp);
            button = itemView.findViewById(R.id.moreimage);
            nametext = itemView.findViewById(R.id.nametextview);
            numbertext = itemView.findViewById(R.id.numbertext);
        }
    }
}
