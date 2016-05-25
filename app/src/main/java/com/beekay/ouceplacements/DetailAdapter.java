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


    private ArrayList<Details> detailsList;


    DetailAdapter(ArrayList<Details> detailsList){
        this.detailsList=detailsList;
        setHasStableIds(true);
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
        holder.present.setText(details.present+"\n"+details.presentCity+"\n"+details.presentCountry+"\n"+details.presentPin);
        holder.permanent.setText(details.permanent+"\n"+details.permanentCity+"\n"+details.permanentCountry+"\n"+details.permanentPin);
        holder.phones.setText(details.phone1+"\n"+details.phone2);
        holder.mails.setText(details.mail1+"\n"+details.mail2);
        holder.sboard.setText(details.sboard);
        holder.scol.setText(details.scollege);
        holder.sadd.setText(details.saddress);
        holder.syop.setText(details.syop);
        holder.sgpa.setText(details.scgpa);
        holder.iboard.setText(details.iboard);
        holder.icol.setText(details.icollege);
        holder.iadd.setText(details.iaddress);
        holder.iyop.setText(details.iyop);
        holder.igpa.setText(details.igpa);
        holder.dboard.setText(details.dboard);
        holder.dcol.setText(details.dcollege);
        holder.dadd.setText(details.daddress);
        holder.dyop.setText(details.dyop);
        holder.dgpa.setText(details.dgpa);
        holder.bboard.setText(details.bboard);
        holder.bcol.setText(details.bcollege);
        holder.badd.setText(details.baddress);
        holder.byop.setText(details.byop);
        holder.bgpa.setText(details.bgpa);
        holder.eamcet.setText(details.eamcet);
        holder.aggregate.setText(details.aggregate);
        holder.semester.setText(holder.semester.getText().toString()+details.sem+"semesters");
        holder.gate.setText(details.gatescore);
        holder.pgaggregate.setText(details.pgscore);
        holder.thesis.setText(details.thesis);
        holder.project.setText(details.project);
        holder.org.setText(details.itdname);
        holder.duration.setText(details.itdduration);
        holder.type.setText(details.itdtype);
        holder.merits.setText(details.extras);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return 1;
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
        protected TextView permanent;
        protected TextView phones;
        protected TextView mails;
        protected TextView sboard,scol,sadd,syop,sgpa;
        protected TextView iboard,icol,iadd,iyop,igpa;
        protected TextView dboard,dcol,dadd,dyop,dgpa;
        protected TextView bboard,bcol,badd,byop,bgpa;
        protected TextView eamcet,aggregate,semester,project;
        protected TextView gate,pgaggregate,thesis;
        protected TextView org,duration,type,merits;
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
            permanent= (TextView) itemView.findViewById(R.id.permanentcomplete);
            phones = (TextView) itemView.findViewById(R.id.phones);
            mails = (TextView) itemView.findViewById(R.id.mails);
            sboard = (TextView) itemView.findViewById(R.id.sboard);
            scol = (TextView) itemView.findViewById(R.id.scol);
            sadd = (TextView) itemView.findViewById(R.id.sadd);
            syop = (TextView) itemView.findViewById(R.id.syop);
            sgpa = (TextView) itemView.findViewById(R.id.sgpa);
            iboard = (TextView) itemView.findViewById(R.id.iboard);
            icol = (TextView) itemView.findViewById(R.id.icol);
            iadd = (TextView) itemView.findViewById(R.id.iadd);
            iyop = (TextView) itemView.findViewById(R.id.iyop);
            igpa = (TextView) itemView.findViewById(R.id.igpa);
            dboard = (TextView) itemView.findViewById(R.id.dboard);
            dcol = (TextView) itemView.findViewById(R.id.dcol);
            dadd = (TextView) itemView.findViewById(R.id.dadd);
            dyop = (TextView) itemView.findViewById(R.id.dyop);
            dgpa = (TextView) itemView.findViewById(R.id.dgpa);
            bboard = (TextView) itemView.findViewById(R.id.bboard);
            bcol = (TextView) itemView.findViewById(R.id.bcol);
            badd = (TextView) itemView.findViewById(R.id.badd);
            byop = (TextView) itemView.findViewById(R.id.byop);
            bgpa = (TextView) itemView.findViewById(R.id.bgpa);
            eamcet = (TextView) itemView.findViewById(R.id.eamrank);
            aggregate = (TextView) itemView.findViewById(R.id.aggregate);
            semester = (TextView) itemView.findViewById(R.id.sems);
            project = (TextView) itemView.findViewById(R.id.project);
            gate = (TextView) itemView.findViewById(R.id.gaterank);
            pgaggregate = (TextView) itemView.findViewById(R.id.pgaggregate);
            thesis = (TextView) itemView.findViewById(R.id.thesis);
            org = (TextView) itemView.findViewById(R.id.org);
            duration = (TextView) itemView.findViewById(R.id.duration);
            type = (TextView) itemView.findViewById(R.id.type);
            merits = (TextView) itemView.findViewById(R.id.merits);
        }
    }
} 