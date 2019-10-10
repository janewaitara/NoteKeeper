package com.example.waitara_mumbi.notekeeper;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private NoteRecyclerAdapter mNoteRecyclerAdapter;

    // private ArrayAdapter<NoteInfo> mAdapterNotes; //for the list view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // mAdapterNotes.notifyDataSetChanged();//notifies the adapter that the list has been changed when a new note is added
        mNoteRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
        /*//get a reference to the ListView
        final ListView listNotes = (ListView)findViewById(R.id.list_notes);
        //get a list of notes using the DataManager
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        //notes is the our list
        mAdapterNotes = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, notes);
        listNotes.setAdapter(mAdapterNotes);

        //handles an item click with a method that accepts a reference to an interface
        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //the body of this method onItemClick gets called when the user makes a selection from the list view
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //starts the noteActivity
                Intent intent = new Intent(NoteListActivity.this, NoteActivity.class);
                //to pass our Note info instances  with the intents to pass from our notelist activity to note activity
                //getting a reference to the note info the user selected,we need to have the position info and reference to the list view by making the list view final to reference it at our anonymous class
                //declare a local variable and assign it the result of getItemAtPosition from the list view
               //BEFORE NoteInfo note = (NoteInfo)listNotes.getItemAtPosition(position);//reference to the notes that corresponds to the user selection
                //putting it as an extra into the intent.intent Extras are name value pairs, and those names of course are strings and when we use those strings we generally want them to be Constants(set at the note activity(destination of the extra))
                //putting the extra in place, the note chosen is passed to the note activity
                intent.putExtra(NoteActivity.NOTE_ID,position);

                startActivity(intent);

            }
        });*/

        final RecyclerView recyclerNotes = (RecyclerView) findViewById(R.id.list_notes); //getting the reference to the Recycler View
        final GridLayoutManager notesLayoutManager = new GridLayoutManager(this, 2);//instance of layout Manager needed by recycler View
        recyclerNotes.setLayoutManager(notesLayoutManager); //associating both

        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        //instance of our noteRecyclerAdapter
        mNoteRecyclerAdapter = new NoteRecyclerAdapter(this, notes);
        recyclerNotes.setAdapter(mNoteRecyclerAdapter);
    }

}
