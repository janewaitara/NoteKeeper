package com.example.waitara_mumbi.notekeeper;

import android.provider.BaseColumns;

//designing the database
public final class NoteKeeperDatabaseContract {

    private NoteKeeperDatabaseContract() {} //makes it non creatable //prevents the class from being instantiated outside of this class

    public final class CourseInfoEntry implements BaseColumns  {

        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        //the command CREATE TABLE course_info(course_id, course_title)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " +TABLE_NAME +" (" +
                        _ID +" INTEGER PRIMARY KEY, "+
                        COLUMN_COURSE_ID +" TEXT UNIQUE NOT NULL, "
                        + COLUMN_COURSE_TITLE +  " TEXT NOT NULL)";
    }

    public final class NoteInfoEntry implements BaseColumns {

        public static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        //SQL Statement to create the table
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " +TABLE_NAME +" (" +
                        _ID +" INTEGER PRIMARY KEY, "+
                        COLUMN_NOTE_TITLE+" TEXT NOT NULL, "+
                        COLUMN_NOTE_TEXT +" TEXT, "+
                        COLUMN_COURSE_ID +" TEXT NOT NULL)";


    }


}
