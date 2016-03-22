package com.example.eightleaves.popularmovie.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.eightleaves.popularmovie.MovieFragment;
import com.example.eightleaves.popularmovie.R;
import com.example.eightleaves.popularmovie.Utility;
import com.squareup.picasso.Picasso;


/**
 * Created by gkhera on 17/02/2016.
 */
public class MovieAdapter extends CursorAdapter {
    private String[] results;
    private Context context;

    public MovieAdapter(Context context,Cursor cursor) {
        super(context,cursor);
    }
    private int mItemSelected = -1 ;
    public void setItemSelected(int position){
        mItemSelected=position;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(mItemSelected==cursor.getPosition()){
            view.setActivated(true);
        }else{
            view.setActivated(false);
        }
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        String imageUrl = Utility.getImageUrl(cursor.getString(MovieFragment.COL_MOVIE_POSTER_PATH));
        Picasso.with(context).load(imageUrl)
                    .into(viewHolder.imageView);
    }

    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.list_item_movie_image);
        }
    }


}
