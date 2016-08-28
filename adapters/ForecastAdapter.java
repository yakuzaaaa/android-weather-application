package com.example.nilarnab.mystats.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.database.WeatherContract;
import com.example.nilarnab.mystats.models.WeatherSingleDay;
import com.example.nilarnab.mystats.utility.WeatherUtils;


/**
 * Created by nilarnab on 6/8/16.
 */
public class ForecastAdapter extends CursorAdapter {
    private static final int VIEW_TODAY = 0;
    private static final int VIEW_FUTURE = 1;

    public ForecastAdapter(Context context, Cursor cursor) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public int getCount() {
        if (getCursor() != null) {
            return getCursor().getCount();
        }

        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TODAY : VIEW_FUTURE;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view;
        if (getItemViewType(cursor.getPosition()) == VIEW_TODAY) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_weather_today, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_weather, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    private WeatherSingleDay getWeatherItem(Cursor c) {
        WeatherSingleDay day = new WeatherSingleDay();
        day.setDescription(c.getString(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_CONDITION)));
        day.setMax(c.getDouble(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_MAX_TEMP)));
        day.setMin(c.getDouble(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_MIN_TEMP)));
        day.setWeatherConditionId(c.getLong(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_WEATHER_CONDITION_ID)));

        String dayOfWeek = WeatherUtils.getFriendlyDayString(c.getLong(c.getColumnIndex(WeatherContract.WeatherTable.COLUMN_DATE)));

        day.setDayOfWeek(dayOfWeek);

        return day;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            ViewHolder holder = (ViewHolder) view.getTag();

            WeatherSingleDay day = getWeatherItem(cursor);

            holder.desc.setText(day.getDescription());
            holder.dow.setText(day.getDayOfWeek());
            holder.max.setText(WeatherUtils.formatTemperature(day.getMax()));
            holder.min.setText(WeatherUtils.formatTemperature(day.getMin()));

            //todo remove me coz I dont need if's and buts
            if (getItemViewType(cursor.getPosition()) == VIEW_TODAY) {
                holder.iconView.setImageResource(WeatherUtils.getArtResourceForWeatherCondition(day.getWeatherConditionId()));
            } else {
                holder.iconView.setImageResource(WeatherUtils.getArtResourceForWeatherCondition(day.getWeatherConditionId()));
            }

        }
    }

    public static class ViewHolder {
        TextView max;
        TextView min;
        TextView desc;
        TextView dow;
        ImageView iconView;

        public ViewHolder(View view) {
            desc = (TextView) view.findViewById(R.id.list_weather_desc);
            max = (TextView) view.findViewById(R.id.list_weather_max);
            min = (TextView) view.findViewById(R.id.list_weather_min);
            dow = (TextView) view.findViewById(R.id.list_weather_day_of_week);
            iconView = (ImageView) view.findViewById(R.id.list_item_weather_icon);
        }
    }
}
