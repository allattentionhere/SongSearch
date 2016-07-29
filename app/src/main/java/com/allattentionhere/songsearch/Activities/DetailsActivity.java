package com.allattentionhere.songsearch.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.songsearch.Helper.Datacallback;
import com.allattentionhere.songsearch.Helper.HttpRequestHelper;
import com.allattentionhere.songsearch.Helper.MyApplication;
import com.allattentionhere.songsearch.Helper.TrackAdapter;
import com.allattentionhere.songsearch.Model.MyData;
import com.allattentionhere.songsearch.Model.Results;
import com.allattentionhere.songsearch.R;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView img_contact, img_back;
    Results r;
    TextView txt_collectionexplicitness, txt_viewartist, txt_viewcollection, txt_viewtrack, txt_track, txt_artist, txt_genre, txt_duration, txt_price, txt_type, txt_kind, txt_collectionname, txt_collectionprice, txt_releasedate, txt_disccount, txt_trackcount, txt_trackexplicitness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        r = new Gson().fromJson(getIntent().getStringExtra("result"), Results.class);
        init();
        setListener();
    }

    private void setListener() {

        txt_viewtrack.setOnClickListener(this);
        txt_viewartist.setOnClickListener(this);
        txt_viewcollection.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    private void init() {
        txt_track = (TextView) findViewById(R.id.txt_track);
        txt_genre = (TextView) findViewById(R.id.txt_genre);
        txt_artist = (TextView) findViewById(R.id.txt_artist);
        txt_duration = (TextView) findViewById(R.id.txt_duration);
        txt_price = (TextView) findViewById(R.id.txt_price);
        img_contact = (ImageView) findViewById(R.id.img_contact);
        img_back = (ImageView) findViewById(R.id.img_back);
        txt_type = (TextView) findViewById(R.id.txt_type);
        txt_kind = (TextView) findViewById(R.id.txt_kind);
        txt_collectionname = (TextView) findViewById(R.id.txt_collectionname);
        txt_collectionprice = (TextView) findViewById(R.id.txt_collectionprice);
        txt_releasedate = (TextView) findViewById(R.id.txt_releasedate);
        txt_disccount = (TextView) findViewById(R.id.txt_disccount);
        txt_trackcount = (TextView) findViewById(R.id.txt_trackcount);
        txt_trackexplicitness = (TextView) findViewById(R.id.txt_trackexplicitness);
        txt_collectionexplicitness = (TextView) findViewById(R.id.txt_collectionexplicitness);
        txt_viewtrack = (TextView) findViewById(R.id.txt_viewtrack);
        txt_viewartist = (TextView) findViewById(R.id.txt_viewartist);
        txt_viewcollection = (TextView) findViewById(R.id.txt_viewcollection);


        txt_type.setText("Type: " + r.getWrapperType());
        txt_kind.setText("Kind: " + r.getKind());
        txt_collectionname.setText("Collection: " + r.getCollectionName());
        txt_collectionprice.setText("Collection Price: " + r.getCollectionPrice() + " " + r.getCurrency());
        txt_disccount.setText("Disc: " + r.getDiscNumber() + "/" + r.getDiscCount());
        txt_trackcount.setText("Track: " + r.getTrackNumber() + "/" + r.getTrackCount());
        txt_trackexplicitness.setText("Track Explicitness: " + r.getTrackExplicitness());
        txt_collectionexplicitness.setText("Collection Explicitness: " + r.getCollectionExplicitness());
        txt_releasedate.setText("Release Date: " + r.getReleaseDate().substring(0, 10));

        txt_track.setText(r.getTrackName());
        txt_genre.setText(r.getPrimaryGenreName());
        txt_artist.setText(r.getArtistName());

        String dur = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(r.getTrackTimeMillis()) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(r.getTrackTimeMillis())),
                TimeUnit.MILLISECONDS.toSeconds(r.getTrackTimeMillis()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(r.getTrackTimeMillis())));
        txt_duration.setText(dur);
        float p = (r.getTrackPrice() < 0) ? 0.0f : r.getTrackPrice();
        if (r.getCurrency().equalsIgnoreCase("USD")) {
            txt_price.setText(p + " $");
        } else {
            txt_price.setText(p + " " + r.getCurrency());
        }

        String url = "";
        if (r.getArtworkUrl100() != null) {
            url = r.getArtworkUrl100();
        } else if (r.getArtworkUrl60() != null) {
            url = r.getArtworkUrl60();
        } else {
            url = r.getArtworkUrl30();
        }
        MyApplication.imageLoader.displayImage(url, img_contact);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_viewtrack:
                startBrowser(r.getTrackViewUrl());
                break;
            case R.id.txt_viewartist:
                startBrowser(r.getArtistViewUrl());
                break;
            case R.id.txt_viewcollection:
                startBrowser(r.getCollectionViewUrl());
                break;
            case R.id.img_back:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    supportFinishAfterTransition();
                } else {
                    finish();
                }
                break;
        }
    }

    private void startBrowser(String s) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
        if (browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(browserIntent);
        } else {
            Toast.makeText(this, "No browser installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        } else {
            finish();
        }
    }
}
