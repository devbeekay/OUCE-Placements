package com.beekay.ouceplacements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bvepuri on 2/5/2016.
 */
public class YbdAdapter extends RecyclerView.Adapter<YbdAdapter.YbdViewHolder>  {

    ArrayList<Ybd_Details> details;
    ArrayList<String> linkId;
    Context context;
    ArrayList<HashMap<String,String>> cookie;

    YbdAdapter(ArrayList<Ybd_Details> details,ArrayList<String> linkId,Context context,ArrayList<HashMap<String,String>> cookies){
        this.details = details;
        this.linkId = linkId;
        this.context = context;
        this.cookie =  cookies;
        System.out.println(cookie);
    }

    @Override
    public YbdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ybd,parent,false);
        return new YbdViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(YbdViewHolder holder, int position) {

        Ybd_Details detail = details.get(position);
        holder.compName.setText(detail.company);
        holder.designation.setText(detail.designation);
        holder.lastDate.setText(detail.lastdate);
        holder.pay.setText(detail.pay);
        if(detail.eligible.equalsIgnoreCase("Not Eligible")){
            holder.eligible.setVisibility(View.VISIBLE);
            holder.eligible.setText(detail.eligible);
            holder.apply.setVisibility(View.GONE);
        }
        else {
            holder.apply.setVisibility(View.VISIBLE);
            holder.apply.setText("Apply");
            holder.eligible.setVisibility(View.GONE);
        }

    }



    @Override
    public int getItemCount() {
        return details.size();
    }

    public class YbdViewHolder extends RecyclerView.ViewHolder implements OnClickListener{

        protected TextView compName;
        protected TextView designation;
        protected TextView pay;
        protected TextView lastDate;
        protected TextView eligible;
        protected Button apply;

        public YbdViewHolder(View itemView) {
            super(itemView);
            compName = (TextView) itemView.findViewById(R.id.compName);
            designation = (TextView) itemView.findViewById(R.id.designation);
            pay = (TextView) itemView.findViewById(R.id.pay);
            lastDate = (TextView) itemView.findViewById(R.id.lastdate);
            eligible = (TextView) itemView.findViewById(R.id.eligible);
            apply = (Button) itemView.findViewById(R.id.apply);
            apply.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == apply.getId()){
                System.out.println("Apply clicked " + getLayoutPosition());
                Intent intent = new Intent(context,ApplyJobActivity.class);
                intent.putExtra("position",linkId.get(getLayoutPosition()));
                System.out.println(cookie);
                intent.putExtra("cookie",cookie);
                context.startActivity(intent);
            }
            else{
//                Snackbar.make(null,"view Clicked",Snackbar.LENGTH_LONG).show();
            }
        }

    }
}
