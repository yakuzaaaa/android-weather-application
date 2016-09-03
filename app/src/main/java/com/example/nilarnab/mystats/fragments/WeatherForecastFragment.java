package com.example.nilarnab.mystats.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nilarnab.mystats.R;
import com.example.nilarnab.mystats.adapters.ForecastAdapter;
import com.example.nilarnab.mystats.database.WeatherContract;
import com.example.nilarnab.mystats.events.DataUpdatedEvent;
import com.example.nilarnab.mystats.events.LocationFetchedEvent;
import com.example.nilarnab.mystats.models.WeatherSingleDay;
import com.example.nilarnab.mystats.services.DataFetchUtil;
import com.example.nilarnab.mystats.services.LocationListenerService;
import com.example.nilarnab.mystats.utility.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nilarnab on 1/8/16.
 */
public class WeatherForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    static final int COL_WEATHER_DATE = 1;
    private static final String STATE_KEY_LAST_POSITION = "last_psotion_in_list";
    private static final int LOADER_ID_WEATHER = 101;
    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherTable.TABLE_NAME + "." + WeatherContract.WeatherTable._ID,
            WeatherContract.WeatherTable.COLUMN_DATE,
            WeatherContract.WeatherTable.COLUMN_CONDITION,
            WeatherContract.WeatherTable.COLUMN_MAX_TEMP,
            WeatherContract.WeatherTable.COLUMN_MIN_TEMP,
            WeatherContract.LocationTable.COLUMN_LOCATION_SETTINGS,
            WeatherContract.WeatherTable.COLUMN_WEATHER_CONDITION_ID,
            WeatherContract.LocationTable.COLUMN_COORD_LAT,
            WeatherContract.LocationTable.COLUMN_COORD_LNG
    };
    @BindView(R.id.weather_list) RecyclerView mRecyclerView;
    @BindView(R.id.empty_view) View mEmptyView;
    @BindView(R.id.weather_refresh) SwipeRefreshLayout mSwipeRefreshWeather;

    private ForecastAdapter mWeatherAdapter;
    private listener mListener;
    private int mLastSelectedPosition = 0;

    public static WeatherForecastFragment newInstance() {
        return new WeatherForecastFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (listener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather_list_forecast, container, false);
        ButterKnife.bind(this, rootView);

        mWeatherAdapter = new ForecastAdapter(getActivity(),
                null,
                new ForecastAdapter.WeatherListViewHolder.ItemClickListener() {
                    @Override
                    public void onItemClicked(ImageView icon, TextView desc, TextView max, TextView min, WeatherSingleDay day, int position) {
                        mLastSelectedPosition = position;
                        String locationSetting = Utility.getUserLocation();
                        mListener.onListItemClicked(WeatherContract.WeatherTable.buildWeatherWithLocationAndStartDateUri(
                                locationSetting, day.getDate()), icon, desc, position, max, min);
                    }
                });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mWeatherAdapter);

        mSwipeRefreshWeather.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //todo: move the whole location fetch and data fetch thing in a
                //todo: single work flow and make them timely
                Intent intent = new Intent(getActivity(), LocationListenerService.class);
                getActivity().startService(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mLastSelectedPosition = savedInstanceState.getInt(STATE_KEY_LAST_POSITION);
            mRecyclerView.smoothScrollToPosition(mLastSelectedPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_KEY_LAST_POSITION, mLastSelectedPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_WEATHER, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dataUpdatedEvent(DataUpdatedEvent event) {
        hideSwipeRefresh();
    }

    private void hideSwipeRefresh() {
        if(mSwipeRefreshWeather.isRefreshing()) {
            //GOOGLE CERTIFIED HACK TO ACTUALLY HIDE THE REFRESH LAYOUT
            mSwipeRefreshWeather.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshWeather.setRefreshing(false);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    public void locationFetchedEvent(LocationFetchedEvent event) {
        if (event.isChangedLocation()) {
            DataFetchUtil.fetchDataNow();
        } else {
            hideSwipeRefresh();
        }

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                WeatherContract.WeatherTable.buildWeatherWithLocationUri(Utility.getUserLocation()),
                FORECAST_COLUMNS,
                null,
                null,
                null// WeatherContract.WeatherTable.COLUMN_DATE+" ASC "
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            mWeatherAdapter.swapCursor(data);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWeatherAdapter.swapCursor(null);
    }

    public interface listener {
        void onListItemClicked(Uri uri, ImageView view, TextView desc, int transitionName, View max, View min);
    }
}