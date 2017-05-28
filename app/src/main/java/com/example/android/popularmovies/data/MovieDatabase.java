package com.example.android.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = MovieDatabase.VERSION)
public class MovieDatabase {
    private MovieDatabase(){}
    public static final int VERSION = 2;
    @Table(MovieColumns.class) public static final String MOVIES = "movies";
}