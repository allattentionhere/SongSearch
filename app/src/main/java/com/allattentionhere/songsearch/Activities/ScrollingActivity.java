package com.allattentionhere.songsearch.Activities;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.songsearch.Helper.Datacallback;
import com.allattentionhere.songsearch.Helper.HttpRequestHelper;
import com.allattentionhere.songsearch.Helper.TrackAdapter;
import com.allattentionhere.songsearch.Model.MyData;
import com.allattentionhere.songsearch.Model.Results;
import com.allattentionhere.songsearch.R;
import com.google.gson.Gson;

import org.json.JSONObject;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener, Datacallback {

    private TrackAdapter mAdapter;
    private RecyclerView recycler_view;
    private FloatingActionButton fab;
    private String search_name = "Michael Jackson";
    TextView txt_empty;
    Snackbar sb;
    Toolbar toolbar;
    ProgressBar pb;
    MyData mData;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        init();
        setListener();
        makeNetworkCall(search_name);
    }

    private void makeNetworkCall(String name) {
        collapsingToolbarLayout.setTitle(name);


        showProgress();
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //make network call
            ArrayMap<String, String> amap = new ArrayMap<>();
            amap.put("term", name.replace(" ", "+"));
            new HttpRequestHelper().MakeJsonGetRequest("/search", amap, this, this);
        } else {
            hideProgress();
            showSnackbar("Not connected to Internet", "RETRY");
        }
    }

    private void setListener() {
        fab.setOnClickListener(this);
    }

    private void init() {
        txt_empty = (TextView) findViewById(R.id.txt_empty);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pb = (ProgressBar) findViewById(R.id.pb);
        toolbar.setTitle(search_name);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
    }

    public void showSearchDialog() {
        final Dialog dialog = new Dialog(ScrollingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search);
        dialog.setCancelable(true);

        final EditText etxt_dialog = (EditText) dialog.findViewById(R.id.etxt_dialog);
        Button btn_search = (Button) dialog.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etxt_dialog.getText().toString().trim().length() > 0) {
                    search_name = etxt_dialog.getText().toString();
                    makeNetworkCall(search_name);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ScrollingActivity.this, "Please Enter Artist Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                showSearchDialog();
                break;
        }
    }

    private void showSnackbar(String s, String action) {
        if (sb != null && sb.isShownOrQueued()) {
            sb.dismiss();
        }
        sb = Snackbar.make(findViewById(R.id.cl), s, Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_INDEFINITE);
        if (action != null) {
            sb.setAction(action, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeNetworkCall(search_name);
                }
            });
        }
        sb.show();
    }

    @Override
    public void onSuccess(JSONObject success, String uri) {
        if (uri.equalsIgnoreCase("/search")) {
            mData = new Gson().fromJson(success.toString(), MyData.class);
            mAdapter = new TrackAdapter(mData.getResults(), this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recycler_view.setLayoutManager(mLayoutManager);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            recycler_view.setAdapter(mAdapter);
            hideProgress();
        }
    }

    @Override
    public void onFailure(JSONObject failure, String uri) {
        hideProgress();
        if (uri.equalsIgnoreCase("/search")) {
            showSnackbar("Network call failed", "RETRY");
        }
    }

    public void showProgress() {
        pb.setVisibility(View.VISIBLE);
        recycler_view.setVisibility(View.GONE);
        txt_empty.setVisibility(View.GONE);
    }

    public void hideProgress() {
        pb.setVisibility(View.GONE);
        if (mAdapter == null) {
            recycler_view.setVisibility(View.GONE);
            txt_empty.setVisibility(View.GONE);
        } else if (mAdapter.getItemCount() > 0) {
            recycler_view.setVisibility(View.VISIBLE);
            txt_empty.setVisibility(View.GONE);
        } else {
            recycler_view.setVisibility(View.GONE);
            txt_empty.setVisibility(View.VISIBLE);
        }
    }
}
