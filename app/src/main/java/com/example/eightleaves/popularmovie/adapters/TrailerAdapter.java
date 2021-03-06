package com.example.eightleaves.popularmovie.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eightleaves.popularmovie.R;
import com.example.eightleaves.popularmovie.models.Trailer;
import com.example.eightleaves.popularmovie.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gkhera on 11/03/2016.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private final  Context mContext;
    private List<Trailer> mValues;
    public static class ViewHolder extends RecyclerView.ViewHolder {


        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.list_item_trailer_image_icon);
            mTextView = (TextView) view.findViewById(R.id.list_item_trailer_number);
        }

    }

    public TrailerAdapter(Context context, List<Trailer> items) {
        mValues = items;
        mContext = context;
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View trailerView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trailer, parent, false);
        return new ViewHolder(trailerView);
    }

    public Trailer getValueAt(int position) {
        return mValues.get(position);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, final int position) {
        String imageUrl = "http://img.youtube.com/vi/"+mValues.get(position).getKey()+"/mqdefault.jpg";
        Picasso.with(mContext).load(imageUrl)
                .into(holder.mImageView);
        holder.mTextView.setText(Utility.getTrailerNumber(mContext, position + 1));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(Utility.formatLink(context,mValues.get(position).getKey()))));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}