package populairmovies.synleaf.com.populairmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> mMovies;

    public ImageAdapter(Context c, ArrayList<Movie> movies) {
        mContext = c;
        mMovies = movies;
    }

    public int getCount() {
        return mMovies.size();
    }

    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    public long getItemId(int position) {
        return mMovies.get(position).getId();
    }

    public void updateResults(ArrayList<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        ImageView imageView = null;

        if (convertView == null) {
            gridView = inflater.inflate(R.layout.grid_movie, null);
        } else {
            gridView = convertView;
        }

        imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
        imageView.setImageBitmap(mMovies.get(position).getPoster());

        return gridView;
    }
}