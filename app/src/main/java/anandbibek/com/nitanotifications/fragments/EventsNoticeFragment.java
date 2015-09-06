package anandbibek.com.nitanotifications.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;

import anandbibek.com.nitanotifications.CustomRecyclerAdapter;
import anandbibek.com.nitanotifications.LinkContainer;
import anandbibek.com.nitanotifications.MainActivity;
import anandbibek.com.nitanotifications.R;
import anandbibek.com.nitanotifications.fetchers.FetcherEvents;

/**
 * Created by Anand on 24-Aug-15.
 */
public class EventsNoticeFragment extends Fragment {

    CustomRecyclerAdapter adapter;
    RecyclerView recyclerView;

    public EventsNoticeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.listfrag_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.setHasFixedSize(true);
        new AsyncFetcher().execute(MainActivity.EVENTS_URL);
        return view;
    }

    class AsyncFetcher extends AsyncTask<String,Void,ArrayList<LinkContainer>>{


        @Override
        protected ArrayList<LinkContainer> doInBackground(String... params) {
            try {
                return new FetcherEvents().get(params[0]);
            } catch (IOException e) {
                //TODO handle exceptions
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<LinkContainer> linkContainers) {
            if(linkContainers!=null) {
                adapter = new CustomRecyclerAdapter(getActivity().getBaseContext(), linkContainers);
                recyclerView.setAdapter(adapter);
            }
        }
    }
}