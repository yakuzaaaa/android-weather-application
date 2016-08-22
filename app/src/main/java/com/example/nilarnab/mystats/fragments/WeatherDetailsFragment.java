package com.example.nilarnab.mystats.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.database.WeatherContract;
import com.example.nilarnab.mystats.models.WeatherSingleDay;
import com.example.nilarnab.mystats.utility.Utility;
import com.example.nilarnab.mystats.utility.WeatherUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nilarnab on 7/8/16.
 */
public class WeatherDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID_WEATHER = 102;

    private static final String[] DETAILS_COLUMNS = {
            WeatherContract.WeatherTable.TABLE_NAME + "." + WeatherContract.WeatherTable._ID,
            WeatherContract.WeatherTable.COLUMN_DATE,
            WeatherContract.WeatherTable.COLUMN_CONDITION,
            WeatherContract.WeatherTable.COLUMN_MAX_TEMP,
            WeatherContract.WeatherTable.COLUMN_MIN_TEMP,
            WeatherContract.WeatherTable.COLUMN_WEATHER_CONDITION_ID,
            WeatherContract.WeatherTable.COLUMN_HUMIDITY,
            WeatherContract.WeatherTable.COLUMN_PRESSURE,
            WeatherContract.WeatherTable.COLUMN_WIND_SPEED,
            WeatherContract.WeatherTable.COLUMN_DEGREES
    };

    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_CONDITION = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_CONDITION_ID = 5;
    private static final int COL_HUMIDITY = 6;
    private static final int COL_PRESSURE = 7;
    private static final int COL_WIND_SPEED = 8;
    private static final int COL_DEGREE = 9;
    private static final String STATE_URI = "state_uri";
    private static final String KEY_WEATHER_DETAILS_URI = "single_day_uri";
    @BindView(R.id.humidity_tv) TextView mHumidity;
    @BindView(R.id.wind_tv) TextView mWindSpeed;
    @BindView(R.id.pressure_tv) TextView mPressure;
    @BindView(R.id.date_tv) TextView mDateText;
    @BindView(R.id.max_temp_tv) TextView mMaxTemp;
    @BindView(R.id.min_temp_tv) TextView mMinTemp;
    @BindView(R.id.weather_icon_view) ImageView mIconView;
    @BindView(R.id.weather_desc_tv) TextView mWeatherCondition;
    @BindView(R.id.fab_share_weather_details) FloatingActionButton mShareButton;
    private Uri mDataUri;
    private WeatherSingleDay mSingleDayObject;

    public static WeatherDetailsFragment newInstance(@Nullable String dataUri) {
        Bundle args = new Bundle();
        args.putString(KEY_WEATHER_DETAILS_URI, dataUri);
        WeatherDetailsFragment fragment = new WeatherDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getString(KEY_WEATHER_DETAILS_URI) != null) {
            mDataUri = Uri.parse(getArguments().getString(KEY_WEATHER_DETAILS_URI));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_weather_details, container, false);
        ButterKnife.bind(this, root);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSingleDayObject != null) {
                    startActivity(Intent.createChooser(Utility.buildShareIntent(mSingleDayObject.getDescription()), "Share"));
                }
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_WEATHER, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mDataUri != null) {
            return new CursorLoader(getActivity(),
                    mDataUri,
                    DETAILS_COLUMNS,
                    null,
                    null,
                    null
            );
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mSingleDayObject = new WeatherSingleDay();

            mSingleDayObject.setDescription(data.getString(COL_WEATHER_CONDITION));
            mSingleDayObject.setMax(data.getDouble(COL_WEATHER_MAX_TEMP));
            mSingleDayObject.setMin(data.getDouble(COL_WEATHER_MIN_TEMP));
            mSingleDayObject.setDayOfWeek(WeatherUtils.getReadableDate(data.getLong(COL_WEATHER_DATE)));
            mSingleDayObject.setDate(data.getLong(COL_WEATHER_DATE));
            mSingleDayObject.setHumidity(data.getDouble(COL_HUMIDITY));
            mSingleDayObject.setPressure(data.getDouble(COL_PRESSURE));
            mSingleDayObject.setWindSpeed(data.getDouble(COL_WIND_SPEED));
            mSingleDayObject.setWeatherConditionId(data.getLong(COL_WEATHER_CONDITION_ID));
            mSingleDayObject.setDegree(data.getDouble(COL_DEGREE));

            bindViewsWithData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getString(STATE_URI) != null) {
            mDataUri = Uri.parse(savedInstanceState.getString(STATE_URI));
            getLoaderManager().initLoader(LOADER_ID_WEATHER, null, this);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mDataUri != null) {
            outState.putString(STATE_URI, mDataUri.toString());
        }
    }

    private void bindViewsWithData() {
        mMaxTemp.setText(WeatherUtils.formatTemperature(mSingleDayObject.getMax()));
        mMinTemp.setText(WeatherUtils.formatTemperature(mSingleDayObject.getMin()));
        mDateText.setText(WeatherUtils.getFormattedMonthDay(mSingleDayObject.getDate()));
        mHumidity.setText(new StringBuilder().append(String.valueOf(mSingleDayObject.getHumidity())).append("%").toString());
        mPressure.setText(String.valueOf(mSingleDayObject.getPressure()));
        mWindSpeed.setText(WeatherUtils.getFormattedWind(mSingleDayObject.getWindSpeed(), mSingleDayObject.getDegree()));
        mWeatherCondition.setText(mSingleDayObject.getDescription());
        mIconView.setImageResource(WeatherUtils.getArtResourceForWeatherCondition(mSingleDayObject.getWeatherConditionId()));
    }
}
