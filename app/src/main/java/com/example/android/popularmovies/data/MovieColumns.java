package com.example.android.popularmovies.data;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface MovieColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID =
            "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String MOVIE_DB_ID = "movie_db_id";

    @DataType(DataType.Type.TEXT) @NotNull
    public String moviePosterURL = "poster";

    @DataType(DataType.Type.TEXT) @NotNull
    public String moviePlot = "plot";

    @DataType(DataType.Type.TEXT) @NotNull
    public String movieRating = "rating";

    @DataType(DataType.Type.TEXT) @NotNull
    public String movieReleaseDate = "release_date";
}

