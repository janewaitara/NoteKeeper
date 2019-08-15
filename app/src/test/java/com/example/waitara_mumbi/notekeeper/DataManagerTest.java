package com.example.waitara_mumbi.notekeeper;

import android.provider.ContactsContract;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    static  DataManager sDmInstance;

    @BeforeClass
    public static void classSetUp() throws Exception {
        sDmInstance = DataManager.getInstance();
    }

    @Before //run before all tests
    public void setUp() throws Exception{
        sDmInstance.getNotes().clear();
        sDmInstance.initializeExampleNotes();
    }

    @Test
    public void createNewNote() throws Exception{


        final CourseInfo course = sDmInstance.getCourse("android_async");
        final String noteTitle = "Test note Title";
        final String noteText = "This is the body of my test note ";
        
        int noteIndex = sDmInstance.createNewNote();//getting index of a new note
        NoteInfo newNote = sDmInstance.getNotes().get(noteIndex );//getting the empty note with that index
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);
        //testing
        NoteInfo compareNote = sDmInstance.getNotes().get(noteIndex);
        assertEquals(course, compareNote.getCourse());//first param should be what we expect, second, what we are testing against
        assertEquals(noteTitle, compareNote.getTitle());
        assertEquals(noteText, compareNote.getText());

    }

    @Test
    public void findSimilarNotes() throws Exception{

        final CourseInfo course =sDmInstance.getCourse("android_async");
        final String noteTitle = "Test note Title";
        final String noteText1 = "This is the body of my test note ";
        final String noteText2 = "This is the body of my second test note ";

        int noteIndex1 = sDmInstance.createNewNote();
        NoteInfo newNote1 = sDmInstance.getNotes().get(noteIndex1 );
        newNote1.setCourse(course);
        newNote1.setTitle(noteTitle);
        newNote1.setText(noteText1);

        int noteIndex2 = sDmInstance.createNewNote();
        NoteInfo newNote2 = sDmInstance.getNotes().get(noteIndex2 );
        newNote2.setCourse(course);
        newNote2.setTitle(noteTitle);
        newNote2.setText(noteText2);

        int foundIndex1 = sDmInstance.findNote(newNote1);
        assertEquals(noteIndex1,foundIndex1);

        int foundIndex2 = sDmInstance.findNote(newNote2);
        assertEquals(noteIndex2,foundIndex2);

    }
 //Test Driven Development
    @Test
    public void createNewNoteOneStepCreation (){
        final CourseInfo course = sDmInstance.getCourse("android_async");
        final String noteTitle = "Test note Title";
        final String noteText = "This is the body of my test note ";

        int noteIndex = sDmInstance.createNewNote(course, noteTitle,noteText );
        NoteInfo compareNote = sDmInstance.getNotes().get(noteIndex);

        assertEquals(course, compareNote.getCourse());
        assertEquals(noteTitle, compareNote.getTitle());
        assertEquals(noteText, compareNote.getText());

    }
}