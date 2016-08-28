package com.example.nilarnab.mystats.adapters;

/**
 * Created by nilarnab on 7/8/16.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.models.WeatherSingleDay;


public class WeatherForecastAdapter extends GenericRecyclerViewCursorAdapter<WeatherForecastAdapter.ViewHolder> {

    private static OnItemClickListener mListener;
    private Context mContext;

    public WeatherForecastAdapter(Context context, Cursor cursor, OnItemClickListener listener) {
        super(cursor);
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_weather, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        // viewHolder.bindListener(member);
    }

    public interface OnItemClickListener {
        //void onItemSelected(UserContact mListItems);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public TextView mNumber;
        public TextView mWarning;
        public ImageView mPhoto;

        public ViewHolder(View view) {
            super(view);
//            mName = (TextView) view.findViewById(R.id.contact_list_name);
//            mWarning = (TextView) view.findViewById(R.id.list_item_warning);
//            mNumber = (TextView) view.findViewById(R.id.conatct_list_number);
//            mPhoto = (ImageView) view.findViewById(R.id.contact_list_image);
        }

        public void bindListener(final WeatherSingleDay member) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mListener.onItemSelected(member);
                }
            });
        }
    }
}