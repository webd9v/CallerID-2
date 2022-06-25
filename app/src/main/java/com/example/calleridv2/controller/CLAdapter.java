package com.example.calleridv2.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calleridv2.R;
import com.example.calleridv2.model.CallLogModel;
import com.example.calleridv2.view.AddContactScreen;

import java.util.ArrayList;

public class CLAdapter extends RecyclerView.Adapter<CLAdapter.MyViewHolder>{
        Context context;
        ArrayList<CallLogModel> callLogModels;
    public CLAdapter(Context context, ArrayList<CallLogModel> callLogModels){
            this.context=context;
            this.callLogModels = callLogModels;
            }
    @NonNull
    @Override
    public CLAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(context);
            View view=layoutInflater.inflate(R.layout.recycler_view_row,parent,false);

            return new CLAdapter.MyViewHolder(view);
            }

    @Override
    public void onBindViewHolder(@NonNull CLAdapter.MyViewHolder holder, int position) {
            holder.name.setText(callLogModels.get(position).getName());
            holder.number.setText(callLogModels.get(position).getNumber());
            holder.duration.setText(callLogModels.get(position).getDuration());
            holder.type.setText(callLogModels.get(position).getType());
            holder.btn.setImageResource(callLogModels.get(position).getBtn());
            holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, AddContactScreen.class);
                    intent.putExtra("phoneNumber",holder.number.getText().toString());
                    context.startActivity(intent);
                }
            });
            Boolean isknown= callLogModels.get(position).getIsKnown();
            if (!isknown){
                holder.btn.setVisibility(View.GONE);
            }
            }

    @Override
    public int getItemCount() {
            return callLogModels.size();
            }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,number,duration,type;
        ImageButton btn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.list_name);
            number=itemView.findViewById(R.id.list_number);
            duration=itemView.findViewById(R.id.list_callDuration);
            type=itemView.findViewById(R.id.list_type);
            btn=itemView.findViewById(R.id.list_addBtn);
        }
    }
}
