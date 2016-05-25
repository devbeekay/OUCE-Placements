package com.beekay.ouceplacements;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Notification.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Notification#} factory method to
 * create an instance of this fragment.
 */
public class Notification extends android.support.v4.app.Fragment implements SearchView.OnQueryTextListener {


    RecyclerView recyclerView;
    Map<String, String> cookie;
    ArrayList<Contents> recycleList;
    ContentsAdapter adapter;
    ArrayList<String> linkId,links;
    SwipeRefreshLayout swipe;
    NetCheck netCheck;
    private OnFragmentInteractionListener mListener;


    public Notification() {
        // Required empty public constructor
    }

    public Map<String, String> getCookie() {
        return cookie;
    }

    public void setCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            super.onCreateView(inflater, container, savedInstanceState);
            setHasOptionsMenu(true);
            netCheck=new NetCheck();
            ArrayList<HashMap<String, String>> cooks = Cooks.getCookies();
            try {
                setCookie(cooks.get(0));
                ContentWrapper wrapper = (ContentWrapper) getArguments().getSerializable("list");
                setRecycleList(wrapper.getContents());
                setLinkId(getArguments().getStringArrayList("ids"));
                links=getLinkId();
            }catch (NullPointerException ex){

                getActivity().finish();
            }
            View view = inflater.inflate(R.layout.fragment_notification, container, false);
            recyclerView = (RecyclerView) view.findViewById(R.id.cardList);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(llm);
            adapter = new ContentsAdapter(getRecycleList());
            recyclerView.setAdapter(adapter);
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {

                    if (netCheck.isNetAvailable(getActivity())) {
                        ArrayList<Map<String, String>> cookies = new ArrayList<Map<String, String>>();
                        cookies.add(getCookie());
                        android.support.v4.app.Fragment notice_fragment = new Notice();
                        Bundle arguments = new Bundle();
                        arguments.putString("id", links.get(position));
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
//            swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
//            swipe.setColorSchemeResources(R.color.green,R.color.yellow,R.color.blue);
//            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    if (netCheck.isNetAvailable(getActivity())) {
//                        new RefreshList().execute("");
//                    }
//                    else{
//                        Toast.makeText(getActivity(),"Check your Network Connection",Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//            });
            return view;


    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public ArrayList<Contents> getRecycleList() {
        return recycleList;
    }

    public final void setRecycleList(ArrayList<Contents> recycleList) {
        this.recycleList = recycleList;
    }

    public ArrayList<String> getLinkId() {
        return linkId;
    }

    public void setLinkId(ArrayList<String> linkId) {
        this.linkId = linkId;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.searchmenu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(Notification.this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        ArrayList<Contents> filteredList = filter(getRecycleList(),newText);
        adapter.setFilter(filteredList);
        recyclerView.scrollToPosition(0);
        return false;
    }

    private ArrayList<Contents> filter(ArrayList<Contents> oldList, String newText) {
        final ArrayList<Contents> filteredList = new ArrayList<>();
        links=new ArrayList<>();
        for(int i=0;i<oldList.size();i++){
            Contents contents = oldList.get(i);
            final String text = contents.notificationContent.toLowerCase();
            if(text.contains(newText)){
                filteredList.add(contents);
                links.add(getLinkId().get(i));
            }
        }
        return filteredList;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(ArrayList<Contents> contents);
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
            ArrayList<String> links = new ArrayList<>();
            int len;
            try {
                Document notDoc = Jsoup.connect("http://oucecareers.org/students/showNotice.php").followRedirects(false).timeout(50000).cookies(getCookie()).get();
                setDocument(notDoc);
                Elements table = notDoc.select("table");
                Contents contents;
                boolean firstSkipped=false;
                ArrayList<Contents> list = new ArrayList<>();
                int i = 0;
                for (Element tr : table.select("tr")) {
                    if(firstSkipped) {
                        contents = new Contents();
                        for (Element td : tr.select("td")) {
                            if (i == 0)
                                contents.number = td.text();
                            if (i == 1) {
                                contents.notificationContent = td.text();
                                len = td.select("a").attr("href").length();
                                links.add(td.select("a").attr("href").toString().substring(len - 5, len - 1));
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
                setLinkId(links);
                return list;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ArrayList<Contents> s) {
        super.onPostExecute(s);
            if(s!=null) {
                recycleList.clear();
                recycleList.addAll(s);
                recyclerView.removeAllViews();
                recyclerView.destroyDrawingCache();
                adapter.notifyDataSetChanged();
                adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            }
            else
                Toast.makeText(getActivity(),"Timed out while connecting",Toast.LENGTH_LONG).show();
            swipe.setRefreshing(false);

        }
    }





}
