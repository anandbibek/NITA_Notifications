package org.nita.notifications.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import org.nita.notifications.CustomRecyclerAdapter;
import org.nita.notifications.LinkContainer;
import org.nita.notifications.MainActivity;
import org.nita.notifications.R;
import org.nita.notifications.fetchers.FetcherAllNotice;
import org.nita.notifications.fetchers.FetcherNewsEvents;
import org.nita.notifications.fetchers.FetcherHome;
import org.nita.notifications.fetchers.FetcherOrderCirculars;
import org.nita.notifications.fetchers.FetcherStudentNotifications;
import org.nita.notifications.fetchers.FetcherDownloadCorner;

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
        if(args != null) {
            category = args.getString(MainActivity.CATEGORY_TAG);
            req_url = args.getString(MainActivity.URL_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.listfrag_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getBaseContext()));
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
            new FileRetriever().execute(requireActivity().openFileInput(category));
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
                    return new FetcherHome().get(req_url);
                else if (category.equals(getString(R.string.category_notice)))
                    return new FetcherAllNotice().get(req_url);
                else if (category.equals(getString(R.string.category_events)))
                    return new FetcherNewsEvents().get(req_url);
                else if (category.equals(getString(R.string.category_students)))
                    return new FetcherStudentNotifications().get(req_url);
                else if (category.equals(getString(R.string.category_download)))
                    return new FetcherDownloadCorner().get(req_url);
                else if (category.equals(getString(R.string.category_order)))
                    return new FetcherOrderCirculars().get(req_url);

            } catch (Exception e) {
                Snackbar.make(v, "Could not load data. " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<LinkContainer> linkContainers) {
            if(linkContainers!=null)
                setAdapter(linkContainers, true);
            refreshLayout.setRefreshing(false);
        }
    }

    private void setAdapter(ArrayList<LinkContainer> linkContainers, boolean write){
        if(linkContainers!=null) {
            adapter = new CustomRecyclerAdapter(requireActivity().getBaseContext(), linkContainers);
            recyclerView.setAdapter(adapter);

            //store web copy to local file for next run
            if(write) {
                try {
                    FileOutputStream fos = requireActivity().openFileOutput(category,Context.MODE_PRIVATE);
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