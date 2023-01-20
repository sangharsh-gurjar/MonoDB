package com.example.mongodbtut;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private Task[] data;
    public StudentAdapter(Task[] data){
        this.data=data;


    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_layout,parent,false);
        return new StudentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {

        Task t=data[position];

        holder.SName.setText(t.getName());
        holder.SEmail.setText(t.getEmail());
        holder.SAddress.setText(t.getAddress());

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder{
        TextView SName;
        TextView SAddress;
        TextView SEmail;


        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            SName = itemView.findViewById(R.id.SName);
            SAddress = itemView.findViewById(R.id.SAddress);
            SEmail=itemView.findViewById(R.id.SEmail);
        }
    }

}