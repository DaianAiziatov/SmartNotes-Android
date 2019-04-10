package com.lambton.daianaiziatov.smartnotes.Activities;

import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lambton.daianaiziatov.smartnotes.CustomListAdapters.RecordingsListAdapter;
import com.lambton.daianaiziatov.smartnotes.R;
import com.lambton.daianaiziatov.smartnotes.RecyclerViewClickListener;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowRecordingsActivity extends AppCompatActivity implements RecyclerViewClickListener {

    @BindView(R.id.recordings_recycler_view)
    RecyclerView recyclerView;

    private ArrayList<String> recordings;
    private RecordingsListAdapter recordingsListAdapter;
    private String noteid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recordings);
        ButterKnife.bind(this);
        noteid = getIntent().getStringExtra("noteid");
        recordings = getRecordings();
        recordingsListAdapter = new RecordingsListAdapter(recordings, this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recordingsListAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private ArrayList<String> getRecordings() {
        ArrayList<String> recordings = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/" + noteid;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            String recordingPath = files[i].getPath();
            recordings.add(recordingPath);
        }
        return recordings;
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(recordings.get(position));
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // make something
        }
    }

    @Override
    public void recyclerViewListLongClicked(View v, int position) {
        // no actions
    }
}
