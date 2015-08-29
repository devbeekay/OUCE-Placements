package com.beekay.ouceplacements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Notification.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Notification#} factory method to
 * create an instance of this fragment.
 */
public class Notification extends android.support.v4.app.Fragment {


    private OnFragmentInteractionListener mListener;
    ArrayList<Contents> contentList;
    RecyclerView recyclerView;
    Map<String, String> cookie;
    ArrayList<Contents> recycleList;
    RecyclerView.Adapter adapter;
    ArrayList<String> linkId;
    SwipeRefreshLayout swipe;
    NetCheck netCheck;

    public Map<String, String> getCookie() {
        return cookie;
    }

    public void setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public Notification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            super.onCreateView(inflater, container, savedInstanceState);
        netCheck=new NetCheck();
            ContentWrapper wrapper = (ContentWrapper) getArguments().getSerializable("list");
            setRecycleList(wrapper.getContents());
            ArrayList<Map<String, String>> cooks = (ArrayList<Map<String, String>>) getArguments().getSerializable("cookies");
            setCookie(cooks.get(0));
            setLinkId(getArguments().getStringArrayList("ids"));
            View view = inflater.inflate(R.layout.fragment_notification, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.cardList);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            adapter = new ContentsAdapter(getRecycleList());
            recyclerView.setAdapter(adapter);
            //recyclerView.setItemAnimator();
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {

                    if (netCheck.isNetAvailable(getActivity())) {
                        ArrayList<Map<String, String>> cookies = new ArrayList<Map<String, String>>();
                        cookies.add(getCookie());
                        System.out.print(position);
                        System.out.println("touched");
                        android.support.v4.app.Fragment notice_fragment = new Notice();
                        Bundle arguments = new Bundle();
                        arguments.putString("id", getLinkId().get(position));
                        arguments.putSerializable("cookies", cookies);
                        notice_fragment.setArguments(arguments);
                        FragmentManager fm = getActivity().getSupportFragmentManager();

                        fm.beginTransaction().replace(R.id.frameContainer, notice_fragment).addToBackStack(null).commit();
                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Network Connection",Toast.LENGTH_SHORT).show();
                    }
                }
            }));
            swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (netCheck.isNetAvailable(getActivity())) {
                        new RefreshList().execute("");
                    }
                    else{
                        Toast.makeText(getActivity(),"Check your Network Connection",Toast.LENGTH_SHORT).show();
                    }

                }
            });
            return view;


    }

    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public ArrayList<Contents> getRecycleList() {
        return recycleList;
    }

    public void setRecycleList(ArrayList<Contents> recycleList) {
        this.recycleList = recycleList;
    }

    public ArrayList<String> getLinkId() {
        return linkId;
    }

    public void setLinkId(ArrayList<String> linkId) {
        this.linkId = linkId;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(ArrayList<Contents> contents);
    }


    public class RefreshList extends AsyncTask<String, String, ArrayList<Contents>> {

        ProgressDialog progressDialog;
        Document document;

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }



        @Override
        protected ArrayList<Contents> doInBackground(String... params) {


            try {


                Document notDoc = Jsoup.connect("http://oucecareers.org/students/showNotice.php").followRedirects(false).cookies(getCookie()).get();
                setDocument(notDoc);
                publishProgress("");
                Elements table = notDoc.select("table");
                Contents contents;
                boolean firstSkipped=false;

                ArrayList<Contents> list = new ArrayList<Contents>();
                int i = 0;
                for (Element tr : table.select("tr")) {
                    if(firstSkipped) {
                        contents = new Contents();
                        for (Element td : tr.select("td")) {
                            if (i == 0)
                                contents.number = td.text();

                            if (i == 1) {

                                contents.notificationContent = td.text();


                            }
                            if (i == 2)
                                contents.attachments = td.text();
                            if (i == 3)
                                contents.datePosted = td.text();
                            i++;
                        }

                        i = 0;

                        list.add(contents);
                    }
                    firstSkipped=true;
                }

                return list;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ArrayList<String> links = new ArrayList<>();
            Document doc = getDocument();
            int len, i = 0;
            boolean firstSkipped = false;
            Element table = doc.select("table").first();
            for (Element row : table.select("tr")) {
                i = 0;
                for (Element link : row.select("td")) {

                    if (i == 1) {
                        if (!firstSkipped)
                            firstSkipped = true;

                        else {
                            //   System.out.print(link.select("a").attr("href").toString().length());
                            len = link.select("a").attr("href").length();
                            links.add(link.select("a").attr("href").toString().substring(len - 5, len - 1));
                        }

                    }

                    i++;
                }
            }
            setLinkId(links);
        }

        @Override
        protected void onPostExecute(ArrayList<Contents> s) {

            super.onPostExecute(s);
            setRecycleList(s);
            adapter=new ContentsAdapter(getRecycleList());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            swipe.setRefreshing(false);
        }
    }
}
