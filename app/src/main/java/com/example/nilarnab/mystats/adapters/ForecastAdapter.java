package com.example.nilarnab.mystats.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.database.WeatherContract;
import com.example.nilarnab.mystats.models.Weather;
import com.example.nilarnab.mystats.utility.WeatherUtils;


/**
 * Created by nilarnab on 6/8/16.
 */
public class ForecastAdapter extends GenericRecyclerViewCursorAdapter<ForecastAdapter.WeatherListViewHolder> {
    private static final int VIEW_TODAY = 0;
    private static final int VIEW_FUTURE = 1;
    public static WeatherListViewHolder.ItemClickListener mListener;
    private Context mContext;

    public ForecastAdapter(Context context, Cursor cursor, WeatherListViewHolder.ItemClickListener listener) {
        super(cursor);
        mContext = context;
        mListener = listener;
    }

    private static Weather getWeatherItem(Cursor c) {
        Weather day = new Weather();
        day.setDate(c.getLong(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_DATE)));
        day.setDescription(c.getString(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_CONDITION)));
        day.setMax(c.getDouble(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_MAX_TEMP)));
        day.setMin(c.getDouble(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_MIN_TEMP)));
        day.setWeatherConditionId(c.getLong(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_WEATHER_CONDITION_ID)));

        String dayOfWeek = WeatherUtils.getFriendlyDayString(c.getLong(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_DATE)));

        day.setDayOfWeek(dayOfWeek);

        return day;
    }

    @Override
    public WeatherListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_FUTURE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_weather, parent,false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_weather_today, parent,false);
        }

        return new WeatherListViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TODAY : VIEW_FUTURE;
    }

    @Override
    public void onBindViewHolder(final WeatherListViewHolder holder, Cursor cursor) {
        if (cursor != null) {
            final Weather day = getWeatherItem(cursor);

            holder.desc.setText(day.getDescription());
            holder.dow.setText(day.getDayOfWeek());
            holder.max.setText(WeatherUtils.formatTemperature(day.getMax()));
            holder.min.setText(WeatherUtils.formatTemperature(day.getMin()));
            holder.iconView.setImageResource(WeatherUtils.getArtResourceForWeatherCondition(day.getWeatherConditionId()));
            holder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.bindClickListener(holder.iconView,holder.desc,holder.max,holder.min,day);
                }
            });
        }
    }

    public static class WeatherListViewHolder extends RecyclerView.ViewHolder {
        TextView max;
        TextView min;
        TextView desc;
        TextView dow;
        ImageView iconView;
        View parent;

        public WeatherListViewHolder(View view) {
            super(view);
            desc = (TextView) view.findViewById(R.id.list_weather_desc);
            max = (TextView) view.findViewById(R.id.list_weather_max);
            min = (TextView) view.findViewById(R.id.list_weather_min);
            dow = (TextView) view.findViewById(R.id.list_weather_day_of_week);
            iconView = (ImageView) view.findViewById(R.id.list_item_weather_icon);
            parent = view.findViewById(R.id.list_item_parent);
        }

        public void bindClickListener(ImageView iconView, TextView desc,TextView max,TextView min, Weather day) {
            mListener.onItemClicked(iconView,desc,max,min,day,getAdapterPosition());
        }

        public interface ItemClickListener {
            void onItemClicked(ImageView icon, TextView desc, TextView max, TextView min, Weather day, int position);
        }
    }
}
