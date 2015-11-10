package populairmovies.synleaf.com.populairmoviesapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import populairmovies.synleaf.com.populairmoviesapp.R;
import populairmovies.synleaf.com.populairmoviesapp.SettingsActivity;

public class DetailFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        TextView title = (TextView) rootView.findViewById(R.id.detail_origininal_title);
        title.setText(getActivity().getIntent().getStringExtra("original_title"));

        TextView releaseDate = (TextView) rootView.findViewById(R.id.detail_release_date);
        releaseDate.setText(getActivity().getIntent().getStringExtra("release_date"));

        ImageView poster = (ImageView) rootView.findViewById(R.id.detail_image);
        Bitmap b = BitmapFactory.decodeByteArray(
                getActivity().getIntent().getByteArrayExtra("poster"),
                0,
                getActivity().getIntent().getByteArrayExtra("poster").length);
        poster.setImageBitmap(b);

        SpannableString ss = new SpannableString(getActivity().getIntent().getStringExtra("plot"));
        int marinLeft = (int) (b.getScaledWidth(b.getDensity()) * 2.5);
        ss.setSpan(new MyLeadingMarginSpan2(11, marinLeft), 0, ss.length(), 0);
        TextView plot = (TextView) rootView.findViewById(R.id.detail_plot);
        plot.setText(ss);

        double rating = getActivity().getIntent().getDoubleExtra("rating", 0);

        Drawable halfStar;
        Drawable noStar;

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            halfStar = getActivity().getDrawable(R.drawable.ic_star_half);
            noStar = getActivity().getDrawable(R.drawable.ic_star_off);
        } else {
            halfStar = getActivity().getResources().getDrawable(R.drawable.ic_star_half);
            noStar = getActivity().getResources().getDrawable(R.drawable.ic_star_off);
        }

        if (rating > 0.2 && rating < 0.7) {
            ((ImageView) rootView.findViewById(R.id.detail_star_1)).setImageDrawable(halfStar);
        } else if (rating < 0.3) {
            ((ImageView) rootView.findViewById(R.id.detail_star_1)).setImageDrawable(noStar);
        }

        TextView ratingTV = (TextView) rootView.findViewById(R.id.detail_user_rating);
        ratingTV.setText(String.valueOf(rating));

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

    private void setRatingStars(View rootView, double rating) {

    }

    private class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
        private int margin;
        private int lines;

        MyLeadingMarginSpan2(int lines, int margin) {
            this.margin = margin;
            this.lines = lines;
        }

        @Override
        public int getLeadingMargin(boolean first) {
            if (first) {
                return margin;
            } else {
                return 0;
            }
        }

        @Override
        public void drawLeadingMargin(Canvas c, Paint p, int x, int dir,
                                      int top, int baseline, int bottom, CharSequence text,
                                      int start, int end, boolean first, Layout layout) {}


        @Override
        public int getLeadingMarginLineCount() {
            return lines;
        }
    }
}
