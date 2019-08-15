package com.example.waitara_mumbi.notekeeper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.*;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class NextThroughNotesTest {

    @Rule //1
    public  ActivityTestRule <MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void NextThroughNotes(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());//gets the view with the id and opens it
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));//to select our note option

        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));//this goes to the recyclerView and clicks on the first note

        List<NoteInfo> notes = DataManager.getInstance().getNotes();//reference to our Note List
        for (int index = 0; index < notes.size(); index++ ){
            NoteInfo note = notes.get(index); //reference of the first note
            //checking that it has the right texts
            onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(note.getCourse().getTitle())));
            onView(withId(R.id.text_note_title)).check(matches(withText(note.getTitle())));
            onView(withId(R.id.text_note_text)).check(matches(withText(note.getText())));

            if (index <notes.size() - 1) //ensures that if thats not the last note, the next menu is enabled and clicked
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click());

        }
        onView(withId(R.id.action_next)).check(matches(not(isEnabled()))); //checks if its disabled on the last note
        pressBack();


    }

}