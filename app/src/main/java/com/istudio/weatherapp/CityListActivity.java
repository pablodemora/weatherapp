package com.istudio.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.istudio.weatherapp.dummy.DummyContent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Cities. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CityDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CityListActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private List<City> mValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mValues = new ArrayList<>();

        // Iniciarlizar llamado servicio web
        String url="http://api.openweathermap.org/data/2.5/find?lat=9.97&lon=-84.07&cnt=10&units=metric&APPID=97885f6d20ebe9dfedbfaf5eda502389";
        new ReadWeatherJSONFeedTask().execute(url);


        if (findViewById(R.id.city_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        // Handle your other action bar items...
        switch (item.getItemId()) {

            case R.id.action_settings:

                Toast.makeText(getApplicationContext(), "Options", Toast.LENGTH_LONG).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void InitRecycler(){

        View recyclerView = findViewById(R.id.city_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mValues));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        public SimpleItemRecyclerViewAdapter(List<City> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.city_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
           // holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).getName());
            holder.mContentView.setText(mValues.get(position).getTemperature());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CityDetailFragment.ARG_ITEM_ID, mValues.get(position).getName());
                        CityDetailFragment fragment = new CityDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.city_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CityDetailActivity.class);
                        intent.putExtra(CityDetailFragment.ARG_ITEM_ID, mValues.get(position).getName());

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    // Web service


    private class ReadWeatherJSONFeedTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {

            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                // 1
                Log.w("LOG", result);

                JSONObject jasonObject = new JSONObject(result);

                Log.w("LOG", jasonObject.getString("list"));

                //2

                JSONArray jasonArray = new JSONArray( jasonObject.getString("list"));


                for(int i = 0; i < jasonArray.length(); ++i){

                    //1
                    Log.w("LOG", jasonArray.get(i).toString());

                    //2
                    JSONObject jsonObjectCity = new JSONObject(jasonArray.get(i).toString());

                    Log.w("LOG", jsonObjectCity.getString("name"));

                    //3
                    JSONObject jsonCityMain = new JSONObject(jsonObjectCity.getString("main"));
                    Log.w("LOG", jsonObjectCity.getString("main"));

                    //4
                    JSONArray jsonWeather = new JSONArray(jsonObjectCity.getString("weather"));
                    Log.w("LOG", jsonWeather.get(0).toString());

                    JSONObject jsonObjectWeather = new JSONObject(jsonWeather.get(0).toString());

                    JSONObject jsonWind = new JSONObject(jsonObjectCity.getString("wind"));

                    Double temp = (Double.valueOf(jsonCityMain.getString("temp_min").toString()) + Double.valueOf(jsonCityMain.getString("temp_max").toString())) / 2;

                    mValues.add(new City(jsonObjectCity.getString("name"), "" + temp, "" + temp.toString(), "", ""));

                    DummyContent.addItem(
                            new DummyContent.DummyItem(
                                    jsonObjectCity.getString("name"),
                                "Weather description: "+ jsonObjectWeather.getString("description")+
                                        ", temperature: "+temp+", "+
                                    "humidity: "+ jsonCityMain.getString("humidity")+
                                        ", wind speed: "+ jsonWind.getString("speed")+" knots/km",
                                            ""+temp));
                }


            } catch (Exception e) {
                Log.d("ReadWeatherJSONFeedTask", e.getLocalizedMessage());
            }

            InitRecycler();
        }
    }

    private String readJSONFeed(String URL) {

        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return stringBuilder.toString();
    }




}
