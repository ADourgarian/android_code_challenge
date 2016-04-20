package com.nerdery.umbrella.recyclers;

/**
 * Created by AVALON on 3/13/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nerdery.umbrella.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
    private List<FeedItem> feedItemList;
    private Context mContext;

    public MyRecyclerAdapter(Context context, List<FeedItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_hour, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);

        //Download image using picasso library
        Picasso.with(mContext).load(feedItem.getThumbnail())
                .into(customViewHolder.imageView);

        //Setting text view Time
        customViewHolder.timeView.setText(Html.fromHtml(feedItem.getTime()));

        //Setting text view Temp
        customViewHolder.tempView.setText(Html.fromHtml(feedItem.getTemp()));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView timeView;
        protected TextView tempView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.timeView = (TextView) view.findViewById(R.id.time);
            this.tempView = (TextView) view.findViewById(R.id.temp);
        }
    }
}