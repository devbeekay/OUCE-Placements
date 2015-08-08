package com.beekay.ouceplacements;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Krishna on 8/6/2015.
 */
public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ContentViewHolder>{

        private List<Contents> contentsList;

    public void setContext(Context context) {
        this.context = context;
    }

    Context context;

    ContentsAdapter(List<Contents> contentsList){
        this.contentsList=contentsList;
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        setContext(parent.getContext());
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);

        return new ContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
            Contents contents=contentsList.get(position);
        holder.notificationView.setText(contents.notificationContent);
        if(contents.attachments.equals("NO ATTACHMENT"))
        holder.attachmentView.setText(contents.attachments);
        else{
            holder.attachmentView.setText(contents.attachments);
            holder.attachmentView.setTextColor(context.getResources().getColor(R.color.red));
            holder.attachmentView.setClickable(true);
        }
        holder.dateView.setText(contents.datePosted);
    }

    @Override
    public int getItemCount() {
        return contentsList.size();
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder{

        protected TextView notificationView;
        protected TextView attachmentView;
        protected TextView dateView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            notificationView=(TextView)itemView.findViewById(R.id.notLink);
            attachmentView=(TextView)itemView.findViewById(R.id.attachment);
            dateView=(TextView)itemView.findViewById(R.id.published);
        }
    }
}
