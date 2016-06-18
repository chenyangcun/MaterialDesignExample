package com.aswifter.material.news;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aswifter.material.R;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

/**
 * Created by erfli on 6/15/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private Context context;
    private final int mBackground;
    private List<Story> mDataset;

    private final TypedValue mTypedValue = new TypedValue();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView newsTitleTV;
        public ImageView newsIV;

        public ViewHolder(View v) {
            super(v);
            newsTitleTV = (TextView) v.findViewById(R.id.news_title);
            newsIV = (ImageView) v.findViewById(R.id.news_image);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NewsAdapter(Context context, List<Story> myDataset) {
        this.mDataset = myDataset;
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
//        v.setBackgroundResource(mBackground);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Story story = mDataset.get(position);
        holder.newsTitleTV.setText(mDataset.get(position).getTitle());
        Glide.clear(holder.newsIV);
        Glide.with(holder.newsIV.getContext())
                .load(story.getImages().get(0))
                .fitCenter()
                .into(holder.newsIV);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Story getItem(int position){
        return mDataset.get(position);
    }

    public void updateData(List<Story> stories) {
        mDataset.clear();
        mDataset.addAll(stories);
        notifyDataSetChanged();
    }

    public void addData(List<Story> stories){
        mDataset.addAll(stories);
        notifyDataSetChanged();
    }
}
