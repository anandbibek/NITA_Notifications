package org.nita.notifications;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomAppViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<LinkContainer> mLinks;


    public CustomRecyclerAdapter(Context context, ArrayList<LinkContainer> links) {
        mInflater = LayoutInflater.from(context);
        mLinks = links;
    }

    class CustomAppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mText;
        View mainView, divider;

        public CustomAppViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            mText = (TextView) itemView.findViewById(R.id.mainText);
            divider = itemView.findViewById(R.id.divider);
            itemView.setOnClickListener(this);
        }

        public void setup(LinkContainer link){
            mText.setText(link.txt);
            if(link.isHeader){
                mText.setTextSize(22);
                mText.setTextColor(Color.WHITE);
                mainView.setBackgroundResource(R.color.colorPrimary);
                divider.setVisibility(View.GONE);
            } else {
                mText.setTextSize(16);
                mText.setTextColor(Color.BLACK);
                mainView.setBackgroundResource(0);

                //don't show divider in last item
                divider.setVisibility((getLayoutPosition()<mLinks.size()-1)?View.VISIBLE : View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            //String link = Uri.encode(mLinks.get(getLayoutPosition()).url);
            String link = mLinks.get(getLayoutPosition()).url;
            Log.d("URL", link);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            v.getContext().startActivity(browserIntent);
        }
    }

    @Override
    public int getItemCount() {
        return mLinks.size();
    }

    @Override
    public CustomAppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.list_item, parent, false);
        return new CustomAppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomAppViewHolder holder, int position) {
        holder.setup(mLinks.get(position));
    }
}
