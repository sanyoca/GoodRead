package com.example.sanya.goodread;

import android.graphics.drawable.Drawable;

/**
 * Created by sanya on 2017.05.04..
 */

public class BookDatas {
    private Drawable drawableThumbnail;
    private String urlBook;
    private String stringTitle;
    private String stringPublished;
    private String stringPublisher;
    private String stringAuthors;
    private float floatAverageRating;
    private int intRatingsCount;

    public BookDatas(String authors, String title, String published, String publisher, int ratings, float averageRating, Drawable thumbnail, String bookURL)  {
        drawableThumbnail = thumbnail;
        stringTitle = title;
        stringPublished = published;
        stringPublisher = publisher;
        stringAuthors = authors;
        floatAverageRating = averageRating;
        intRatingsCount = ratings;
        urlBook = bookURL;
    }

    public String getAuthors()   {
        return stringAuthors;
    }

    public String getTitle()    {
        return stringTitle;
    }

    public String getPublishedDate()    {
        return stringPublished;
    }

    public String getPublisher()    {
        return stringPublisher;
    }

    public int getRatings() {
        return intRatingsCount;
    }

    public float getAverageRating() {
        return floatAverageRating;
    }

    public Drawable getThumbnail() {
        return drawableThumbnail;
    }

    public String getBookURL() {
        return urlBook;
    }
}
