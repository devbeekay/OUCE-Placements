package com.beekay.ouceplacements;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class JobStatusFrag extends Fragment {

    public TableLayout table;


    public JobStatusFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_status, container, false);
        table = (TableLayout) view.findViewById(R.id.table);
        NetCheck netCheck = new NetCheck();
        if(netCheck.isNetAvailable(getActivity()))
            new GetJobStatus().execute("");
        else
            Toast.makeText(getActivity(), "Check your network connection", Toast.LENGTH_LONG).show();
        return view;
    }

    public class GetJobStatus extends AsyncTask<String, String, ArrayList<Company>>{
        ProgressDialog progressDialog;
        @Override
        protected ArrayList<Company> doInBackground(String... strings) {
            publishProgress("");
            ArrayList<Company> rows = new ArrayList<>();
            try {
                Document document = Jsoup.connect("http://oucecareers.org/students/jobstatus.php").followRedirects(false).cookies(Cooks.getCookies().get(0)).timeout(50000).get();
                Elements table = document.select("table");
                int i = 0;
                for(Element tr : table.select("tr")){
                    Company company = new Company();
                    for(Element td : tr.select("td")){

                        if(i==0) {
                            i++;
                        }
                        else if(i==1){
                            company.cName=td.text();
                            i++;
                        }
                        else if(i==2){
                            company.designation=td.text();
                            i++;
                        }
                        else if(i==3){
                            company.status=td.text();
                            i=0;
                        }
                    }
                    rows.add(company);
                }
            } catch (IOException e) {
                rows.clear();
                rows.add(null);
            }
            return rows;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Company> companies) {
            super.onPostExecute(companies);
            if(companies.get(0)==null) {
                Toast.makeText(getActivity(), "Timed out while connecting", Toast.LENGTH_LONG).show();
                companies.clear();
            }
            TableRow.LayoutParams fieldParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            int j=0;
            for(int i=0; i<companies.size();i++){
                TableRow row = new TableRow(getActivity());
                row.setLayoutParams(rowParams);
                TextView cView = new TextView(getActivity());
                cView.setLayoutParams(fieldParams);
                cView.setText(companies.get(i).cName);
                cView.setPadding(10, 10, 10, 10);
                row.addView(cView);
                TextView dView = new TextView(getActivity());
                dView.setLayoutParams(fieldParams);
                dView.setText(companies.get(i).designation);
                dView.setPadding(10, 10, 10, 10);
                row.addView(dView);
                TextView sView = new TextView(getActivity());
                sView.setLayoutParams(fieldParams);
                sView.setText(companies.get(i).status);
                sView.setPadding(10,10,10,10);
                row.addView(sView);
                if(j==0){
                    row.setBackgroundColor(Color.parseColor("#3399ff"));
                    cView.setTypeface(null, Typeface.BOLD);
                    dView.setTypeface(null, Typeface.BOLD);
                    sView.setTypeface(null, Typeface.BOLD);
                    j++;
                }
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(1);
                table.addView(row);
            }
            progressDialog.hide();
        }

    }



    protected class Company{
        String cName ;
        String designation;
        String status;
    }

}
