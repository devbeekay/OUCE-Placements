package com.beekay.ouceplacements;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class Notice extends android.support.v4.app.Fragment {

    TextView resultView;
    String id;
    ArrayList<HashMap<String,String>> cooks;
    NetCheck netCheck;
    public Notice() {
        // Required empty public constructor
    }

    public String getNoticeId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        setId(getArguments().getString("id"));
        cooks= Cooks.getCookies();
        View v=inflater.inflate(R.layout.fragment_notice, container, false);
        resultView=(TextView)v.findViewById(R.id.resultView);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View rootView = view.getRootView();
                rootView.setDrawingCacheEnabled(true);
                Bitmap screen = rootView.getDrawingCache();
                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), screen, "ScreenShot", "Screen Shot");
                Uri uri = Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, "Placement"));


            }
        });
        netCheck=new NetCheck();
        if(cooks!=null) {
            if (netCheck.isNetAvailable(getActivity()))
                new OpenNotice().execute(getNoticeId());
            else
                Toast.makeText(getActivity(), "Check your network Connection!!!", Toast.LENGTH_SHORT).show();
        }
        else
            getActivity().finish();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);

        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public class OpenNotice extends AsyncTask<String,String,ArrayList<Element>>{

        ProgressDialog progressDialog;
        @Override
        protected ArrayList<Element> doInBackground(String... params) {
            publishProgress("");
            try {
                Document doc = Jsoup.connect("http://oucecareers.org/students/opennotice.php?noticeid=" + params[0]).cookies(cooks.get(0)).timeout(50000).followRedirects(false).get();
                boolean firstSkipped=false;
                ArrayList<Element> elementsList=new ArrayList<>();
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
                            else if(tag.tagName().equalsIgnoreCase("li") ){
                                elementsList.add(tag);
                            }

                        }
                    }
                }
                return elementsList;
            } catch (IOException e) {
                return null;
            }
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
            if(s!=null) {
                FrameLayout frame = (FrameLayout) getActivity().findViewById(R.id.notice_frame);
                ScrollView scrollView = new ScrollView(getActivity());
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                for (int i = 0; i < s.size(); i++) {
                    if (s.get(i).tagName().equalsIgnoreCase("table")) {
                        HorizontalScrollView hsv = new HorizontalScrollView(getActivity());
                        hsv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        TableLayout table = new TableLayout(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 10, 10, 10);
                        table.setLayoutParams(params);
                        table.setGravity(Gravity.CENTER_HORIZONTAL);
                        Iterator<Element> elementIterator = s.get(i).select("tr").iterator();
                        while (elementIterator.hasNext()) {
                            TableRow row = new TableRow(getActivity());
                            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
                            row.setOrientation(LinearLayout.HORIZONTAL);
                            row.setGravity(Gravity.CENTER_HORIZONTAL);
                            Iterator<Element> rowIterator = elementIterator.next().select("td").iterator();
                            while (rowIterator.hasNext()) {
                                TextView view = new TextView(getActivity());
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(10, 10, 10, 10);
                                view.setTextIsSelectable(true);
                                view.setGravity(Gravity.CENTER);
                                view.setText(rowIterator.next().text());
                                row.addView(view);
                            }
                            table.addView(row);
                        }
                        hsv.addView(table);
                        ll.addView(hsv);


                    } else if (s.get(i).tagName().equalsIgnoreCase("p")) {
                        TextView textView = new TextView(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 10, 10, 10);
                        textView.setLayoutParams(params);
                        textView.setText(s.get(i).text());
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        textView.setTextIsSelectable(true);
                        ll.addView(textView);
                    } else if (s.get(i).tagName().equalsIgnoreCase("li")) {
                        TextView textView = new TextView(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                progressDialog.dismiss();
            }
            else
                Toast.makeText(getActivity(),"Timed out while connecting",Toast.LENGTH_LONG).show();
            }

        }
    }


