package com.lambton.daianaiziatov.smartnotes.CustomListAdapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.lambton.daianaiziatov.smartnotes.Database.Note;
import com.lambton.daianaiziatov.smartnotes.R;
import com.lambton.daianaiziatov.smartnotes.RecyclerViewClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteViewHolder> implements Filterable {

    private ArrayList<Note> notesArrayList;
    private ArrayList<Note> filteredNotesArrayList;
    private Context context;
    private NoteFilter noteFilter;
    private static RecyclerViewClickListener itemListener;

    public NotesListAdapter(ArrayList<Note> notesArrayList, Context context, RecyclerViewClickListener itemListener) {
        this.filteredNotesArrayList = notesArrayList;
        this.notesArrayList = notesArrayList;
        this.context = context;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_item, parent, false);
        NoteViewHolder noteViewHolder = new NoteViewHolder(itemView);

        return noteViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesArrayList.get(position);
        String text = note.getDetails();
        String[] lines = text.split("\n");
        String title = lines.length > 0 ?
                lines[0] : "New note";
        String details = lines.length > 1 ?
                String.join(" ", Arrays.copyOfRange(lines, 1, lines.length)) : "No additional text";
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        String date = dateFormat.format(note.getDate());
        String subtitle = date + " - " + details;
        holder.titleTextView.setText(Html.fromHtml(title).toString());
        holder.subtitleTextView.setText(Html.fromHtml(subtitle).toString());
    }

    public void updateNotesList(ArrayList<Note> newNotes) {
        this.notesArrayList.clear();
        this.notesArrayList.addAll(newNotes);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (noteFilter == null) {
            noteFilter = new NoteFilter();
        }
        return noteFilter;
    }

    public ArrayList<Note> getNotesArrayList() {
        return notesArrayList;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.titleTextView)
        TextView titleTextView;
        @BindView(R.id.subtitleTextView)
        TextView subtitleTextView;


        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
            notifyDataSetChanged();
        }

        @Override
        public boolean onLongClick(View v) {
            itemListener.recyclerViewListLongClicked(v, this.getLayoutPosition());
            notifyDataSetChanged();
            return true;
        }
    }

    private class NoteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Note> tempList = new ArrayList<>();

                for (Note note: filteredNotesArrayList) {
                    if (note.getDetails().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(note);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else  {
                filterResults.count = filteredNotesArrayList.size();
                filterResults.values = filterResults;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notesArrayList = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }
    }
}
