package com.lambton.daianaiziatov.smartnotes;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lambton.daianaiziatov.smartnotes.Database.DatabaseNote;
import com.lambton.daianaiziatov.smartnotes.Database.Note;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, RecyclerViewClickListener {

    @BindView(R.id.notes_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.notes_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab_add_note)
    FloatingActionButton addNoteFAB;

    private DatabaseNote databaseNote;
    private ArrayList<Note> notes;
    private NotesListAdapter notesListAdapter;
    private SearchView searchView;
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        addNoteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ShowNoteActivity.class);
                getBaseContext().startActivity(intent);
            }
        });

        databaseNote = new DatabaseNote(this);
        notes = databaseNote.getAllNotes(DatabaseNote.KEY_NOTE_DATE, "ASC");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        notesListAdapter = new NotesListAdapter(notes, this, this);
        recyclerView.setAdapter(notesListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notes = databaseNote.getAllNotes(DatabaseNote.KEY_NOTE_DATE, "ASC");
        notesListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        notesListAdapter.getFilter().filter(newText);
        return true;
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        Note selectedNote = notesListAdapter.getNotesArrayList().get(position);
        Intent intent = new Intent(this, ShowNoteActivity.class);
        intent.putExtra("note", selectedNote);
        this.startActivity(intent);
    }
}
