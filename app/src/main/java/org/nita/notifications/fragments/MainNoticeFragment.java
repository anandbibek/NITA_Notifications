package org.nita.notifications.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nita.notifications.CustomRecyclerAdapter;
import org.nita.notifications.LinkContainer;
import org.nita.notifications.MainActivity;
import org.nita.notifications.R;
import org.nita.notifications.fetchers.FetcherAcademic;
import org.nita.notifications.fetchers.FetcherEvents;
import org.nita.notifications.fetchers.FetcherMain;
import org.nita.notifications.fetchers.FetcherUpcoming;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Anand on 24-Aug-15.
 */
public class MainNoticeFragment extends Fragment {

    CustomRecyclerAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    String category, req_url;
    View v;

    public MainNoticeFragment(){}

    @Override
    public void setArguments(Bundle args) {
        category = args.getString(MainActivity.CATEGORY_TAG);
        req_url = args.getString(MainActivity.URL_TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.listfrag_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new WebFetcher().execute(req_url);
            }
        });

        try {
            //show from saved file initially
            new FileRetriever().execute(getActivity().openFileInput(category));
        }catch (Exception e){
            //fall back to web if file read fails
            e.printStackTrace();
            new WebFetcher().execute(req_url);
        }
        setRetainInstance(true);
        v = view;
        return view;
    }

    class FileRetriever extends AsyncTask<FileInputStream,Void,ArrayList<LinkContainer>>{
        @Override
        protected ArrayList<LinkContainer> doInBackground(FileInputStream... fis) {
            try {
                ObjectInputStream is = new ObjectInputStream(fis[0]);
                ArrayList<LinkContainer> links = (ArrayList<LinkContainer>) is.readObject();
                is.close();
                fis[0].close();
                return links;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<LinkContainer> linkContainers) {
            setAdapter(linkContainers, false);
            //update from web after saved copy is displayed
            new WebFetcher().execute(req_url);
        }
    }

    class WebFetcher extends AsyncTask<String,Void,ArrayList<LinkContainer>>{

        @Override
        protected void onPreExecute() {
            refreshLayout.measure(200,400);
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected ArrayList<LinkContainer> doInBackground(String... params) {
            try {
                if(category.equals(getString(R.string.category_latest)))
                    return new FetcherMain().get(req_url);
                else if (category.equals(getString(R.string.category_academic)))
                    return new FetcherAcademic().get(req_url);
                else if (category.equals(getString(R.string.category_events)))
                    return new FetcherEvents().get(req_url);
                else if (category.equals(getString(R.string.category_upcoming)))
                    return new FetcherUpcoming().get(req_url);

            } catch (Exception e) {
                //TODO show popup if no internet
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<LinkContainer> linkContainers) {
            if(linkContainers!=null)
                setAdapter(linkContainers, true);
            else
                Snackbar.make(v, "Could not load data. Check your internet connection.", Snackbar.LENGTH_LONG).show();
            refreshLayout.setRefreshing(false);
        }
    }

    private void setAdapter(ArrayList<LinkContainer> linkContainers, boolean write){
        if(linkContainers!=null) {
            adapter = new CustomRecyclerAdapter(getActivity().getBaseContext(), linkContainers);
            recyclerView.setAdapter(adapter);

            //store web copy to local file for next run
            if(write) {
                try {
                    FileOutputStream fos = getActivity().openFileOutput(category,Context.MODE_PRIVATE);
                    ObjectOutputStream os = new ObjectOutputStream(fos);
                    os.writeObject(linkContainers);
                    os.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}