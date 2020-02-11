package com.example.android.sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.sunshine.Data.SunshinePreferences;
import com.example.android.sunshine.Utilities.NetworkUtils;
import com.example.android.sunshine.Utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private TextView mWeatherTextView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method
        getMenuInflater to get a handle on the menu inflater */
        MenuInflater menuInflater = getMenuInflater();
        /* Use the inflater's inflate method
        to inflate our menu layout to this menu */
        menuInflater.inflate(R.menu.forecat, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Override onOptionsItemSelected to handle clicks on the refresh button
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh) {
            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mWeatherTextView = findViewById(R.id.tv_wheather_data);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        loadWeatherData();

    }

    /**
     * This method will get the user's preferred location for weather,
     * and then tell some
     * background method to get the weather data in the background.
     */
    private void loadWeatherData() {
        /* Call showWeatherDataView before executing the AsyncTask*/
        showWeatherDataView();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    @Override
    public void onClick(String weatherForToday) {

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, weatherForToday);

        startActivity(intent);
    }

    /**
     * Create a class that extends AsyncTask to perform network requests
     */
    @SuppressLint("StaticFieldLeak")
    class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        /*Within your AsyncTask, override the method
        onPreExecute and show the loading indicator*/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * Override the doInBackground method to perform your network requests.
         */
        @Override
        protected String[] doInBackground(String... params) {
            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL weatherRequestsUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestsUrl);

                return OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this,
                                jsonWeatherResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Override the onPostExecute method to display
         * the results of the network request
         */
        @Override
        protected void onPostExecute(String[] weatherData) {
            /* As soon as the data is finished loading, hide the loading indicator*/
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                /*If the weather data was not null, make sure the data view is visible*/
                showWeatherDataView();
                /*
                 * Iterate through the array and append the Strings to the TextView.
                 * The reason why we add the "\n\n\n" after the String is
                 *  to give visual separation between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                for (String weatherString : weatherData) {
                    mWeatherTextView.append((weatherString) + "\n\n\n");
                }
            } else {
                showErrorMessage();
            }
        }
    }


    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mWeatherTextView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

}

