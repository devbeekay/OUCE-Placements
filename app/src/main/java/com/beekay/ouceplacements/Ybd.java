package com.beekay.ouceplacements;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Ybd extends Fragment {

    private Notification.OnFragmentInteractionListener mListener;
    ArrayList<Ybd_Details> details;
    RecyclerView recyclerView;
    static Map<String, String> cookie;
    ArrayList<HashMap<String,String>> cookies;
    ArrayList<Ybd_Details> recycleList;
    RecyclerView.Adapter adapter;
    static ArrayList<Ybd_Details> list = new ArrayList<Ybd_Details>();
    static ArrayList<String> linkId = new ArrayList<String>();
    NetCheck netCheck;


    public Map<String, String> getCookie() {
        return cookie;
    }

    public void setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public Ybd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        super.onCreateView(inflater,container,savedInstanceState);
        netCheck=new NetCheck();
        if(netCheck.isNetAvailable(getActivity())){
            cookies = (ArrayList<HashMap<String, String>>) Cooks.getCookies();
            setCookie(cookies.get(0));
            GetYBD getYBD = new GetYBD();
            getYBD.execute("");
        }

        View view= inflater.inflate(R.layout.fragment_ybd, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.ybdlist);
        return view;

    }

    public class GetYBD extends AsyncTask<String,String,ArrayList<Ybd_Details>>{
        ProgressDialog progressDialog;

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Ybd_Details> doInBackground(String... strings) {
            try {
                publishProgress("");
                Document notDoc = Jsoup.connect("http://oucecareers.org/students/ybdnoticeboard.php").followRedirects(false).cookies(getCookie()).get();
//                System.out.println(notDoc.body());
                Elements table = notDoc.select("table");
                Ybd_Details details;
                boolean firstSkipped=false;


                int i = 0;
                for (Element tr : table.select("tr")) {
                    if(firstSkipped) {
                        details = new Ybd_Details();
                        for (Element td : tr.select("td")) {

                            if (i == 1) {

                                details.company = td.text();
                            }
                            if (i == 2) {
                                details.designation = td.text();

                            }
                            if (i == 3) {
                                details.pay = td.text();

                            }
                            if (i == 4) {
                                details.lastdate = td.text();

                            }
                            if(i==6){
                                if(td.text().toString().equalsIgnoreCase("Not Eligible") || td.text().equalsIgnoreCase("Action")){
                                    details.eligible=td.text();
                                    linkId.add("Not Eligible");
                                }
                                else{
                                    details.eligible="Apply";
                                    String link = td.select("a").attr("href").toString();
                                    int startIndex = link.indexOf('(');
                                    int endIndex = link.indexOf(')');
                                    linkId.add(link.substring(startIndex+1,endIndex));
                                }
                            }
                            i++;
                        }

                        i = 0;
                        list.add(details);
                    }
                    firstSkipped=true;
                }
                System.out.println(linkId);
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Ybd_Details> s) {
            super.onPostExecute(s);
            progressDialog.hide();
            adapter = new YbdAdapter(s,linkId,getActivity(),cookies);
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            recyclerView.smoothScrollBy(20, 20);
        }
    }


}
