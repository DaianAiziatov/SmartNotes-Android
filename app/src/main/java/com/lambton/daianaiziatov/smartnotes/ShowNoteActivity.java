package com.lambton.daianaiziatov.smartnotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.lambton.daianaiziatov.smartnotes.Database.DatabaseNote;
import com.lambton.daianaiziatov.smartnotes.Database.Note;

import java.sql.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowNoteActivity extends AppCompatActivity {

    @BindView(R.id.noteEditText)
    EditText noteEdittext;

    Note note;
    private DatabaseNote databaseNote;
    private String savedStateOfNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);
        ButterKnife.bind(this);
        databaseNote = new DatabaseNote(this);
        note = getIntent().getParcelableExtra("note");
        if (note != null) {
            savedStateOfNote = note.getDetails();
            noteEdittext.setText(savedStateOfNote);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();
    }

    private void save() {
        if (!noteEdittext.getText().equals(savedStateOfNote)) {
            Note newNote;
            boolean isNew = true;
            if (note != null) {
                newNote = note;
                isNew = false;
            } else {
                newNote = new Note();
                newNote.setNoteId(UUID.randomUUID().toString());
                note = newNote;
            }
            newNote.setDetails(noteEdittext.getText().toString());
            newNote.setDate(new Date(new java.util.Date().getTime()));
            if (isNew) {
                databaseNote.insert(newNote);
            } else {
                databaseNote.update(newNote);
            }
            Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show();
        }
    }
}
