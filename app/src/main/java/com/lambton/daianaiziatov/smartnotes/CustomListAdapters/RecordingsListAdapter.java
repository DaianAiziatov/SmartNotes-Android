package com.lambton.daianaiziatov.smartnotes.CustomListAdapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lambton.daianaiziatov.smartnotes.R;
import com.lambton.daianaiziatov.smartnotes.RecyclerViewClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordingsListAdapter extends RecyclerView.Adapter<RecordingsListAdapter.RecordingViewHolder> {

    private ArrayList<String> recordingsArrayList;
    private Context context;
    private static RecyclerViewClickListener itemListener;

    public RecordingsListAdapter(ArrayList<String> recordingsArrayList, Context context, RecyclerViewClickListener itemListener) {
        this.recordingsArrayList = recordingsArrayList;
        this.context = context;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recordings_item, viewGroup, false);
        RecordingViewHolder recordingViewHolder = new RecordingViewHolder(itemView);

        return recordingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingViewHolder recordingViewHolder, int i) {
        Uri recordingUri = new Uri.Builder().encodedPath(recordingsArrayList.get(i)).build();
        recordingViewHolder.titleTextView.setText(recordingUri.getLastPathSegment());
    }

    @Override
    public int getItemCount() {
        return recordingsArrayList.size();
    }

    public class RecordingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recording_title_text_view)
        TextView titleTextView;
        @BindView(R.id.play_stop_image_view)
        ImageView playStopImageView;

        public RecordingViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
            notifyDataSetChanged();
        }
    }
}
