package com.example.eightleaves.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by gkhera on 11/03/2016.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final  Context mContext;
    private List<Review> mValues;
    public static class ViewHolder extends RecyclerView.ViewHolder {


        public final View mView;
        public final TextView mContentView;
        public final TextView mAuthorView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.list_item_review_content);
            mAuthorView = (TextView) view.findViewById(R.id.list_item_review_author);

        }

    }

    public ReviewAdapter(Context context, List<Review> items) {
        mValues = items;
        mContext = context;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View reviewView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(reviewView);
        return viewHolder;
    }

    public Review getValueAt(int position) {
        return mValues.get(position);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, final int position) {
        holder.mContentView.setText(mValues.get(position).getContent());
        holder.mAuthorView.setText(mValues.get(position).getAuthor());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}