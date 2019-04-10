package com.lambton.daianaiziatov.smartnotes.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.lambton.daianaiziatov.smartnotes.Database.DatabaseNote;
import com.lambton.daianaiziatov.smartnotes.Database.Note;
import com.lambton.daianaiziatov.smartnotes.GlideApp;
import com.lambton.daianaiziatov.smartnotes.R;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowNoteActivity extends AppCompatActivity {

    private static final String TAG = ShowNoteActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_LOCATION = 200;

    @BindView(R.id.noteEditText)
    EditText noteEditText;

    Note note;
    private DatabaseNote databaseNote;
    private String savedStateOfNote;
    private double lattitude;
    private double longitude;
    private MediaRecorder myAudioRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note);
        ButterKnife.bind(this);
        databaseNote = new DatabaseNote(this);
        note = getIntent().getParcelableExtra("note");
        if (note != null) {
            lattitude = note.getLocationLatitude();
            longitude = note.getLocationLongitude();
            savedStateOfNote = note.getDetails();
            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String source) {
                    Drawable image = new BitmapDrawable(getResources(), getBitmapFromURL(source));
                    image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                    return image;
                }
            };
            noteEditText.setText(Html.fromHtml(note.getDetails(), imageGetter, null));
        }
    }

    @Override
    protected void onPause() {
        save();
        super.onPause();
        Log.d("TEST", "Note paused");
    }

    @Override
    public void onBackPressed() {
        save();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    appendToText(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                lattitude = data.getDoubleExtra("lattitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);
                save();
                Log.d("TEST", "Getting location: " + lattitude + ":" + longitude);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                save();
                finish();
                break;
            case R.id.action_map:
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("lattitude", lattitude);
                intent.putExtra("longitude", longitude);
                startActivityForResult(intent, REQUEST_LOCATION);
                break;
            case R.id.action_recordings:
                Intent recordingsIntent = new Intent(this, ShowRecordingsActivity.class);
                recordingsIntent.putExtra("noteid", note.getNoteId());
                startActivityForResult(recordingsIntent, 0);
                break;
        }
        return true;
    }

    @OnClick(R.id.fab_record)
    void onRecordStartClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            myAudioRecorder = new MediaRecorder();
                            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yy_hh-mm-ss");
                            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + note.getNoteId());
                            if (!directory.exists()) {
                                directory.mkdir();
                            }
                            String outputFilePath = directory.getPath() + "/" + dateFormat.format(new Date(new java.util.Date().getTime())) + ".3gp";

                            myAudioRecorder.setOutputFile(outputFilePath);
                            Log.d("TEST", "filepath: " + outputFilePath);

                            try {
                                myAudioRecorder.prepare();
                                myAudioRecorder.start();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ShowNoteActivity.this);
                                builder.setTitle("Recording...");
                                builder.setMessage("Press stop to save recording or cancel to dismiss");
                                builder.setPositiveButton("Stop", (dialog, which) -> {
                                    myAudioRecorder.stop();
                                    myAudioRecorder.release();
                                    myAudioRecorder = null;
                                    dialog.cancel();
                                });
                                builder.setNegativeButton("Cancel", (dialog, which) -> {
                                    myAudioRecorder.stop();
                                    myAudioRecorder.release();
                                    myAudioRecorder = null;
                                    // TODO: delete file
                                    File fdelete = new File(outputFilePath);
                                    if (fdelete.exists()) {
                                        if (fdelete.delete()) {
                                            Log.d("TEST","file Deleted :" + outputFilePath);
                                        } else {
                                            Log.d("TEST","file not Deleted :" + outputFilePath);
                                        }
                                    }
                                    dialog.cancel();
                                });
                                builder.show();
                            } catch (IllegalStateException ise) {
                                Log.d("TEST","IllegalStateException: " + ise.getLocalizedMessage());
                            } catch (IOException ioe) {
                                Log.d("TEST","IOException: " + ioe.getLocalizedMessage());
                            }
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @OnClick(R.id.fab_add_image)
    void onAddImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void appendToText(String url) {
        Log.d(TAG, "Image cache path: " + url);
        int position = Selection.getSelectionStart(noteEditText
                .getText());
        SpannableString spannableString = new SpannableString(" ");
        GlideApp.with(this)
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        Drawable image = new BitmapDrawable(getResources(), resource);
                        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                        ImageSpan imageSpan = new ImageSpan(image, url, ImageSpan.ALIGN_BASELINE);
                        spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        noteEditText.getText().insert(position, spannableString);
                    }
                });
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void save() {
        if (!noteEditText.getText().equals(savedStateOfNote) ||
                lattitude != 0.0 && longitude != 0.0) {
            Log.d("TEST", "latitude: " + lattitude + " longitude " + longitude );
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
            Spannable spannable = noteEditText.getText();
            String htmlDetails = Html.toHtml(spannable);
            Log.d(TAG, htmlDetails);
            newNote.setDetails(htmlDetails);
            newNote.setDate(new Date(new java.util.Date().getTime()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
            Log.d("TEST", "Date: " + dateFormat.format(newNote.getDate()));
            newNote.setLocationLatitude(lattitude);
            newNote.setLocationLongitude(longitude);
            if (isNew) {
                databaseNote.insert(newNote);
                Log.d("TEST", "Successfully saved");
            } else {
                databaseNote.update(newNote);
                Log.d("TEST", "Successfully updated");
            }
        }
    }



    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return image;
        } catch(IOException e) {
            return null;
        }
    }
}
