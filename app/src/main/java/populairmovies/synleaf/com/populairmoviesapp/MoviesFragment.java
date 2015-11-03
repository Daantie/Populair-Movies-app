package populairmovies.synleaf.com.populairmoviesapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MoviesFragment extends Fragment {
    private ImageAdapter mImageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_movies, container, false);

        mImageAdapter = new ImageAdapter(getActivity(), new ArrayList<Movie>());
        GridView gridView = (GridView) container.findViewById(R.id.movies_grid);
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent sendIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(sendIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String[] params) {
            final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";
            final String PAGE_PARAM = "page";

            int pages = 1;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr;

            try {
                Uri builtUri = Uri.parse(TMDB_BASE_URL)
                        .buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, "b7baccdc0113b7c56ffd774a36018f8f")
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(PAGE_PARAM, Integer.toString(pages))
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    builder.append(line).append("\n");
                }

                if (builder.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = builder.toString();

                return getMovieDataFromJson(moviesJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (result != null) {
                mImageAdapter.updateResults(result);
            }
        }

        private ArrayList<Movie> getMovieDataFromJson(String moviesJsonStr) throws JSONException {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            final String TMDB_RESULTS = "results";
            final String TMDB_ADULT = "adult";
            final String TMDB_BACKDROP_PATH = "backdrop_path";
            final String TMDB_GENRE_IDS = "genre_ids";
            final String TMDB_ID = "id";
            final String TMDB_ORIGINAL_LANGUAGE = "original_language";
            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_POPULARITY = "popularity";
            final String TMDB_TITLE = "title";
            final String TMDB_VIDEO = "video";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_VOTE_COUNT = "vote_count";


            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(TMDB_RESULTS);

            ArrayList<Movie> moviesList = new ArrayList<>();

            for(int i = 0; i < moviesArray.length(); i++) {
                Movie movie = new Movie();
                JSONObject movieObj = moviesArray.getJSONObject(i);
                movie.setAdult(movieObj.getBoolean(TMDB_ADULT));
                movie.setBackdrop(getBitmapFromPath(movieObj.getString(TMDB_BACKDROP_PATH)));

                // TODO: movie.setGenreIds(movieObj.getJSONArray(TMDB_GENRE_IDS));

                movie.setId(movieObj.getInt(TMDB_ID));
                movie.setOriginalLanguage(movieObj.getString(TMDB_ORIGINAL_LANGUAGE));
                movie.setOriginalTitle(movieObj.getString(TMDB_ORIGINAL_TITLE));
                movie.setOverview(movieObj.getString(TMDB_OVERVIEW));

                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdf.parse(TMDB_RELEASE_DATE));
                    movie.setReleaseDate(c);
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Error parsing date", e);
                }

                movie.setPoster(getBitmapFromPath(movieObj.getString(TMDB_POSTER_PATH)));
                movie.setPopularity(movieObj.getDouble(TMDB_POPULARITY));
                movie.setTitle(movieObj.getString(TMDB_TITLE));
                movie.setVideo(movieObj.getBoolean(TMDB_VIDEO));
                movie.setVoteAverage(movieObj.getDouble(TMDB_VOTE_AVERAGE));
                movie.setVoteCount(movieObj.getInt(TMDB_VOTE_COUNT));
                moviesList.add(movie);
            }

            return moviesList;
        }

        private Bitmap getBitmapFromPath(String path) {
            final String TMDB_BASE_URL = "http://image.tmdb.org/t/p/w185";
            Uri builtUri = Uri.parse(TMDB_BASE_URL + path)
                    .buildUpon()
                    .build();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), builtUri);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error getting image", e);
            }
            return bitmap;
        }
    }
}
