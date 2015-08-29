package com.beekay.ouceplacements;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpdatePassFragment extends android.support.v4.app.Fragment {

    HashMap<String, String> cookie;

    public HashMap<String, String> getCookie() {
        return cookie;
    }

    public void setCookie(HashMap<String, String> cookie) {
        this.cookie = cookie;
    }

    public UpdatePassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_update_pass, container, false);
        ArrayList<HashMap<String,String>> clist=(ArrayList<HashMap<String,String>>)getArguments().getSerializable("cookies");
        setCookie(clist.get(0));
        final EditText oldpass=(EditText)view.findViewById(R.id.oldpass);
        final EditText newpass1=(EditText)view.findViewById(R.id.newpass1);
        final EditText newpass2=(EditText)view.findViewById(R.id.newpass2);
        Button button=(Button)view.findViewById(R.id.updatepass);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldpass.getText().toString().equals(NetCheck.getPass()) && newpass1.getText().toString().equals(newpass2.getText().toString()) && newpass1.getText().length()>=6){
                    new UpdatePass().execute(newpass1.getText().toString());
                }
                else if(!oldpass.getText().toString().equals(NetCheck.getPass())){
                    Toast.makeText(getActivity().getApplicationContext(),"Old password doesn't match",Toast.LENGTH_SHORT).show();

                }
                else if(newpass1.getText().length()<6){
                    Toast.makeText(getActivity().getApplicationContext(),"Password length must be atleast 6",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"New Passwords doesn't match",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public class UpdatePass extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            try{
                Document doc= Jsoup.connect("http://oucecareers.org/students/changepasswordaction.php?rollno="+NetCheck.getUser()+"&pass="+params[0]).cookies(getCookie()).get();
                if(doc.text().equals("Successfully changed password")){
                    return "success";
                }
                else{
                    return "fail";
                }
            }catch (IOException ex){

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("success")){
                Toast.makeText(getActivity().getApplicationContext(),"Successfully changed password",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(),"Couldn't update try again later",Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }
    }


}
