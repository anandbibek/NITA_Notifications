package anandbibek.com.nitanotifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AppViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<LinkContainer> mLinks;


    public RecyclerAdapter(Context context, ArrayList<LinkContainer> links) {
        mInflater = LayoutInflater.from(context);
        mLinks = links;
    }

    class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mText;

        public AppViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.mainText);
            itemView.setOnClickListener(this);
        }

        public void setup(LinkContainer link){
            mText.setText(link.txt);
        }

        @Override
        public void onClick(View v) {
            //TODO
        }
    }

    @Override
    public int getItemCount() {
        return mLinks.size();
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.list_item, parent, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        holder.setup(mLinks.get(position));
    }
}
