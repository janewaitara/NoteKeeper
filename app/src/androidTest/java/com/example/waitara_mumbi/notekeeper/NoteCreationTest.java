package com.example.waitara_mumbi.notekeeper;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.*;//
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.*;


@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {

    static  DataManager sDmInstance;

    @BeforeClass
    public static void classSetUp() throws Exception {
        sDmInstance = DataManager.getInstance();
    }


    @Rule
    public ActivityTestRule<NoteListActivity> mNoteListActivityRule =
            new ActivityTestRule<> (NoteListActivity.class);

    @Test
    public void createNewNote(){
        final CourseInfo course = sDmInstance.getCourse("java_lang");
        final String noteTitle = "Test note Title";
        final String noteText = "This is the body of my test note ";

        /*ViewInteraction fabNewNote = onView(withId(R.id.fab));
        fabNewNote.perform(click());*/
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class),equalTo(course))).perform(click());
        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(containsString(course.getTitle()))));// assertion test for UI behaviour
        onView(withId(R.id.text_note_title)).perform(typeText(noteTitle)).check(matches(withText(containsString(noteTitle))));//with assertion test for UI behaviour
        onView(withId(R.id.text_note_text)).perform(typeText(noteText),closeSoftKeyboard());
        //Assertion test for UI behaviour
        onView(withId(R.id.text_note_text)).check(matches(withText(containsString(noteText))));
        pressBack();

        //verifying logic behaviour
        int index = sDmInstance.getNotes().size() -1;
        NoteInfo note = sDmInstance.getNotes().get(index);
        assertEquals(course, note.getCourse());
        assertEquals(noteTitle, note.getTitle());
        assertEquals(noteText, note.getText());

    }

}