package com.allattentionhere.songsearch.Helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allattentionhere.songsearch.Activities.DetailsActivity;
import com.allattentionhere.songsearch.Model.Results;
import com.allattentionhere.songsearch.R;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;


public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.MyViewHolder> {

    private Results[] results;
    private Activity _activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_track, txt_artist, txt_genre, txt_duration, txt_price;
        CardView card_view;
        ImageView img_contact;

        public MyViewHolder(View view) {
            super(view);
            txt_track = (TextView) view.findViewById(R.id.txt_track);
            txt_genre = (TextView) view.findViewById(R.id.txt_genre);
            txt_artist = (TextView) view.findViewById(R.id.txt_artist);
            txt_duration = (TextView) view.findViewById(R.id.txt_duration);
            txt_price = (TextView) view.findViewById(R.id.txt_price);
            card_view = (CardView) view.findViewById(R.id.card_view);
            img_contact = (ImageView) view.findViewById(R.id.img_contact);
        }
    }


    public TrackAdapter(Results[] results, Activity _activity) {
        this.results = results;
        this._activity = _activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_track, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Results r = results[position];
        holder.txt_track.setText(r.getTrackName());
        holder.txt_genre.setText(r.getPrimaryGenreName());
        holder.txt_artist.setText(r.getArtistName());

        String dur = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(r.getTrackTimeMillis()) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(r.getTrackTimeMillis())),
                TimeUnit.MILLISECONDS.toSeconds(r.getTrackTimeMillis()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(r.getTrackTimeMillis())));
        holder.txt_duration.setText(dur);
        float p = (r.getTrackPrice() < 0) ? 0.0f : r.getTrackPrice();
        if (r.getCurrency().equalsIgnoreCase("USD")) {
            holder.txt_price.setText(p + " $");
        } else {
            holder.txt_price.setText(p + " " + r.getCurrency());
        }

        String url = "";
        if (r.getArtworkUrl100() != null) {
            url = r.getArtworkUrl100();
        } else if (r.getArtworkUrl60() != null) {
            url = r.getArtworkUrl60();
        } else {
            url = r.getArtworkUrl30();
        }
        MyApplication.imageLoader.displayImage(url, holder.img_contact);
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(_activity, DetailsActivity.class);
                i.putExtra("result", new Gson().toJson(r, Results.class));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Pair<View, String> pair1 = Pair.create((View) holder.img_contact, holder.img_contact.getTransitionName());
                    Pair<View, String> pair2 = Pair.create((View) holder.txt_artist, holder.txt_artist.getTransitionName());
                    Pair<View, String> pair3 = Pair.create((View) holder.txt_duration, holder.txt_duration.getTransitionName());
                    Pair<View, String> pair4 = Pair.create((View) holder.txt_genre, holder.txt_genre.getTransitionName());
                    Pair<View, String> pair5 = Pair.create((View) holder.txt_price, holder.txt_price.getTransitionName());
                    Pair<View, String> pair6 = Pair.create((View) holder.txt_track, holder.txt_track.getTransitionName());
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(_activity, pair1, pair2, pair3, pair4, pair5, pair6);
                    _activity.startActivity(i, options.toBundle());
                } else {
                    _activity.startActivity(i);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return results.length;
    }
}