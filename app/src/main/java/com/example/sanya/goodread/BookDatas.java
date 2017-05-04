package com.example.sanya.goodread;

/**
 * Created by sanya on 2017.05.04..
 */

public class BookDatas {
    private String stringThumbnailUrl;
    private String stringTitle;
    private String stringPublished;
    private String[] stringAuthors;
    private float floatAverageRating;
    private int intRatingsCount;

    public BookDatas(String[] authors, String title, String published, int ratings, float averageRating, String thumbnail)  {
        stringThumbnailUrl = thumbnail;
        stringTitle = title;
        stringPublished = published;
        stringAuthors = authors;
        floatAverageRating = averageRating;
        intRatingsCount = ratings;
    }

    public String[] getAuthor()   {
        return stringAuthors;
    }

    public String getTitle()    {
        return stringTitle;
    }

    public String getPublishedDate()    {
        return stringPublished;
    }

    public int getRatings() {
        return intRatingsCount;
    }

    public float getAverageRating() {
        return floatAverageRating;
    }
}
