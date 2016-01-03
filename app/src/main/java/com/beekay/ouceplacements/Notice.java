package com.beekay.ouceplacements;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notice extends android.support.v4.app.Fragment {

    TextView resultView;


    public String getNoticeId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    ArrayList<Map<String,String>> cooks;
    NetCheck netCheck;
    public Notice() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        setId(getArguments().getString("id"));
        cooks= (ArrayList<Map<String, String>>) getArguments().getSerializable("cookies");
            System.out.println(cooks.get(0).containsKey("PHPSESSID"));
        System.out.println(getNoticeId());
        View v=inflater.inflate(R.layout.fragment_notice, container, false);
        resultView=(TextView)v.findViewById(R.id.resultView);
        netCheck=new NetCheck();
        if(netCheck.isNetAvailable(getActivity()))
        new OpenNotice().execute(getNoticeId());
        else
            Toast.makeText(getActivity(),"Check your network Connection!!!",Toast.LENGTH_SHORT).show();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home,menu);

        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.share){
            Intent sharingIntent=new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String sharedText="http://oucecareers.org/students/opennotice.php?noticeid="+getNoticeId();
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Check out the placement notification @\n");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,sharedText);
            startActivity(Intent.createChooser(sharingIntent,"Share via"));
        }
        return super.onOptionsItemSelected(item);
    }

    public class OpenNotice extends AsyncTask<String,String,ArrayList<Element>>{

        ProgressDialog progressDialog;
        @Override
        protected ArrayList<Element> doInBackground(String... params) {
            publishProgress("");
            try {
                System.out.println("try start time"+(new java.sql.Timestamp(new java.util.Date().getTime())));
                Document doc = Jsoup.connect("http://oucecareers.org/students/opennotice.php?noticeid=" + params[0]).cookies(cooks.get(0)).followRedirects(false).get();
                boolean firstSkipped=false;

                String text=new String();
                ArrayList<Element> elementsList=new ArrayList<Element>();
                for(Element e: doc.getElementsByTag("center")){
                    if(!firstSkipped)
                        firstSkipped=true;
                    else
                    {
                        Elements element=e.getAllElements();
                        for(Element tag:element){

                            if(tag.tagName().toString().equals("p") && !tag.parent().tagName().equalsIgnoreCase("td")){
                                elementsList.add(tag);
                            }
                            else if(tag.tagName().toString().equals("table")){
                                elementsList.add(tag);
                            }

                        }
                    }
                }
                System.out.println("try end time"+(new java.sql.Timestamp(new java.util.Date().getTime())));
                return elementsList;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Element> s) {
            super.onPostExecute(s);
            System.out.println("layout starting time" + (new java.sql.Timestamp(new java.util.Date().getTime())));
            FrameLayout frame=(FrameLayout)getActivity().findViewById(R.id.notice_frame);
            ScrollView scrollView=new ScrollView(getActivity());
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            LinearLayout ll=new LinearLayout(getActivity());
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            for(int i=0;i<s.size();i++){
                if(s.get(i).tagName().equalsIgnoreCase("table")){
                    HorizontalScrollView hsv=new HorizontalScrollView(getActivity());
                    hsv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    TableLayout table=new TableLayout(getActivity());
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10, 10, 10, 10);
                    table.setLayoutParams(params);
                    table.setGravity(Gravity.CENTER_HORIZONTAL);
                    Iterator<Element> elementIterator=s.get(i).select("tr").iterator();
                    while(elementIterator.hasNext()){
                        TableRow row=new TableRow(getActivity());
                        row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.CENTER));
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setGravity(Gravity.CENTER_HORIZONTAL);
                        Iterator<Element> rowIterator=elementIterator.next().select("td").iterator();
                        while(rowIterator.hasNext()){
                            TextView view=new TextView(getActivity());
                            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(10,10,10,10);
                            view.setTextIsSelectable(true);
                            view.setGravity(Gravity.CENTER);
                            view.setText(rowIterator.next().text());
                            row.addView(view);
                        }
                        table.addView(row);
                    }
                    hsv.addView(table);
                    ll.addView(hsv);


                }

                else if(s.get(i).tagName().equalsIgnoreCase("p")){
                    TextView textView=new TextView(getActivity());
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10, 10, 10, 10);
                    textView.setLayoutParams(params);
                    textView.setText(s.get(i).text());
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                    textView.setTextIsSelectable(true);
                    ll.addView(textView);
                }
                }
                scrollView.addView(ll);
                frame.addView(scrollView);
            System.out.println("layout starting time" + (new java.sql.Timestamp(new java.util.Date().getTime())));
            progressDialog.dismiss();
            }

        }
    }

