package com.aswifter.material.book;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aswifter.material.R;

/**
 * Created by erfli on 6/15/16.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public ImageView ivBook;
    public TextView tvTitle;
    public TextView tvDesc;

    public int position;

    public ViewHolder(View v) {
        super(v);
        ivBook = (ImageView) v.findViewById(R.id.ivBook);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvDesc = (TextView) v.findViewById(R.id.tvDesc);
    }
}
