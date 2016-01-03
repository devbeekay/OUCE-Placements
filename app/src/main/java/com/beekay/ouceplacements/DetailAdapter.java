package com.beekay.ouceplacements;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;


/**
 * Created by Krishna on 8/30/2015. 
 */
public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {


    ArrayList<Details> detailsList;


    DetailAdapter(ArrayList<Details> detailsList){
        this.detailsList=detailsList;
    }


    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.details_card,parent,false);
        return new DetailViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {


        Details details=detailsList.get(0);
        holder.course.setText(details.course);
        holder.branch.setText(details.branch);
        holder.year.setText(details.year);
        holder.dream.setText(details.dream);
        holder.roll.setText(details.roll);
        holder.dob.setText(details.dob);
        holder.place.setText(details.placeOfBirth);
        holder.age.setText(details.age);
        holder.gender.setText(details.gender);
        holder.national.setText(details.nationality);
        holder.height.setText(details.height);
        holder.weight.setText(details.weight);
        holder.eye.setText(details.eyesight);
        holder.father.setText(details.father);
        holder.occupation.setText(details.occupation);
        holder.income.setText(details.income);
        holder.present.setText(details.present);
    }


    @Override
    public int getItemCount() {
        return detailsList.size();
    }


    public class DetailViewHolder extends RecyclerView.ViewHolder{


        protected TextView course;
        protected TextView branch;
        protected TextView year;
        protected TextView dream;
        protected TextView roll;
        protected TextView dob;
        protected TextView place;
        protected TextView age;
        protected TextView gender;
        protected TextView national;
        protected TextView height;
        protected TextView weight;
        protected TextView eye;
        protected TextView father;
        protected TextView occupation;
        protected TextView income;
        protected TextView present;

        public DetailViewHolder(View itemView) {
            super(itemView);
            course=(TextView)itemView.findViewById(R.id.coursevalue);
            branch=(TextView)itemView.findViewById(R.id.branchvalue);
            dream=(TextView)itemView.findViewById(R.id.dreamvalue);
            roll=(TextView)itemView.findViewById(R.id.rollvalue);
            dob=(TextView)itemView.findViewById(R.id.dobvalue);
            year=(TextView)itemView.findViewById(R.id.yearvalue);
            place=(TextView)itemView.findViewById(R.id.placevalue);


            age=(TextView)itemView.findViewById(R.id.agevalue);


            gender=(TextView)itemView.findViewById(R.id.gendervalue);
            national=(TextView)itemView.findViewById(R.id.nationalvalue);
            height=(TextView)itemView.findViewById(R.id.heightvalue);
            weight=(TextView)itemView.findViewById(R.id.weightvalue);
            eye=(TextView)itemView.findViewById(R.id.eyevalue);
            father=(TextView)itemView.findViewById(R.id.fathervalue);
            occupation=(TextView)itemView.findViewById(R.id.occupationvalue);
            income=(TextView)itemView.findViewById(R.id.incomevalue);
            present=(TextView)itemView.findViewById(R.id.presentcomplete);
        }
    }
} 