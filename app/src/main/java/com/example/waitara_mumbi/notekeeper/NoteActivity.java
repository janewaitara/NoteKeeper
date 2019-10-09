package com.example.waitara_mumbi.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.waitara_mumbi.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    //tag for logs
    public final String TAG = getClass().getSimpleName();
    //a constant for our intent extra with a unique name
    public static final String NOTE_POSITION = "com.example.waitara_mumbi.notekeeper.NOTE_POSITION";
    //constants names of each things to be stored into a bundle SaveInstanceState
    public  static final  String ORIGINAL_NOTE_COURSE_ID = "com.example.waitara_mumbi.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public  static final  String ORIGINAL_NOTE_TITLE = "com.example.waitara_mumbi.notekeeper.ORIGINAL_NOTE_TITLE";
    public  static final  String ORIGINAL_NOTE_TEXT = "com.example.waitara_mumbi.notekeeper.ORIGINAL_NOTE_TEXT";

    public static final int POSITION_NOT_SET = -1;
    //public static final int REQUEST_CODE_SHOW_CAMERA = 1;
    private NoteInfo mNote;
    private Boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private String mOriginalNoteCourseId;
    private String mOriginalNoteTitle;
    private String mOriginalNoteText;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        mSpinnerCourses = (Spinner)findViewById(R.id.spinner_courses);

      //getting list of courses
      List<CourseInfo> courses =DataManager.getInstance().getCourses();
      ArrayAdapter<CourseInfo> adapterCourses =
              new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,courses);
      adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mSpinnerCourses.setAdapter(adapterCourses);

      readDisplayStateValues();
      
      if (savedInstanceState == null){//if being created for the first time
          saveOriginalNoteValues();
      }else{ //if being recreated after being destroyed
            //passing instanceState to the onCreate
          restoreOriginalNoteValues(savedInstanceState);
      }
      

      //populating the notes on the activity
      //references to the two editTexts
        mTextNoteTitle = (EditText) findViewById(R.id.text_note_title);
        mTextNoteText = (EditText) findViewById(R.id.text_note_text);

        if(!mIsNewNote)
            loadNoteData();
            //displayNote();
        Log.d(TAG, "onCreate");

    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    private void loadNoteData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();//connecting to Db to create

        String courseId = "android_intents";
        String titleStart ="dynamic";
        //specifying selection criteria
        String selection = NoteInfoEntry.COLUMN_COURSE_ID + " = ? AND " +
                NoteInfoEntry.COLUMN_NOTE_TITLE + " LIKE ?";  //selection clause
        String [] selectionArg = {courseId, titleStart + "%"};  //selection value


        final String[] noteColumns = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT};
        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME,
               noteColumns, selection, selectionArg, null, null, null);

        //getting the position
        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        mNoteCursor.moveToNext();
        displayNote();

    }

    @Override //writing the instances into our Bundle
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState); //saving the state instances in case the activity is destroyed
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT,mOriginalNoteText);
    }

    private void saveOriginalNoteValues() {//saves the original values
        if(mIsNewNote)
            return;

        mOriginalNoteCourseId = mNote.getCourse().getCourseId();
        mOriginalNoteTitle = mNote.getTitle();
        mOriginalNoteText = mNote.getText();
    }
        //restoring the notes after the state had been destroyed and recreated
    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginalNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    //called when the back button is clicked,it saves the changes also called when cancelling
    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling){//the menu called cancel
          Log.i(TAG, "cancelling note at position: " + mNotePosition);
            if (mIsNewNote){
                DataManager.getInstance().removeNote(mNotePosition);//removing the backing store of the new note
            }else{
                storePreviousNoteValues();
            }
        } else {
        saveNote();//if back button is clicked
        }
        Log.d(TAG, "onPause");
    }
    //restores the previous notes when cancelling ,when the app uses implicit intents
    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginalNoteTitle);
        mNote.setText(mOriginalNoteText);
    }



    //called by onPause method and by moveNext
    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    private void displayNote() {
        //getting actual values from cursor in the loadData method
        String courseId = mNoteCursor.getString(mCourseIdPos);
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);


        List<CourseInfo> courses = DataManager.getInstance().getCourses();//getting list of courses from DataManager
        CourseInfo noteCourseId = DataManager.getInstance().getCourse(courseId);
        int courseIndex = courses.indexOf(noteCourseId);//getting index of the course from our notes / position of notes from spinner list
        mSpinnerCourses.setSelection(courseIndex);
        mTextNoteTitle.setText(noteTitle);
        mTextNoteText.setText(noteText);
    }

    //A method that gets value of the intents passed to them/gets notes from the extras
    private void readDisplayStateValues() {
            Intent intent = getIntent();//reference to the intent used to start this activity
        //getting the extra(constant info) containing the note from it by calling getParcelableExtra on the intent
       /*BEFORE mNote = intent.getParcelableExtra(NOTE_POSITION);
        //new Note
        mIsNewNote = mNote == null;*/

       mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        //for a new note intent
        mIsNewNote = mNotePosition == POSITION_NOT_SET;

        if (mIsNewNote){
            //if a new note is created,
            createNewNote();//to create a backing store for the new note
        }
        Log.i(TAG,"mNotePosition: " + mNotePosition);
        //else
            mNote = DataManager.getInstance().getNotes().get(mNotePosition);//gets note and loads it into the note reference
    }

    private void createNewNote() {
        //creating a backing store
        DataManager dm = DataManager.getInstance();//reference to DataManager
        //createNewNote returns the position of the newly created note which is loaded into mNotePosition field
        mNotePosition = dm.createNewNote();
       // mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }/*else if (id == R.id.action_take_photo){
            showCamera();
        }*/else if(id == R.id.action_cancel){
            mIsCancelling = true;
            finish();//exits an activity to the previous activity
        }else if(id == R.id.action_next){
            moveNext();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override //only called when the menu is first created
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);//reference to the menu item we want
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1; //finding the position of the last note
        item.setEnabled(mNotePosition < lastNoteIndex);//disabled if the current note is the last
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote(); //save the note first before moving to the next note

        ++mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        saveOriginalNoteValues();//save the original notes of the next note before making changes

        displayNote();

        invalidateOptionsMenu();//calls the onPrepareOptionsMenu again

    }

    /* private void showCamera(Uri photoFile) {
         Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
         startActivityForResult(intent, REQUEST_CODE_SHOW_CAMERA);
     }
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent result){
         //check that requestCode matches our resultCode and if the resultCode succeeded
         if (requestCode == REQUEST_CODE_SHOW_CAMERA && resultCode == RESULT_OK){
             Bitmap thumbnail = result.getParcelableExtra("data");//retrieving the copy(a thumbnail) of the photo in the result Intent, saved as a Bitmap in an extra field named "data".
             //
         }

     }
 */
    private void sendEmail() {
        CourseInfo courses = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Check Out what I learnt on PluralSight \"" + courses.getTitle() + "\"\n "+ mTextNoteText.getText().toString();

        Intent intent =new Intent(Intent.ACTION_SEND);//the action in email is send
        intent.setType("message/rfc2822");//mime type for email
        //intent.putExtra(Intent.EXTRA_EMAIL,"janewaitara99@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);

    }
}
