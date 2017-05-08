package com.example.sanya.goodread;

/**
 * Created by sanya on 2017.05.04..
 */

public class BookDatas {
    private String stringThumbnailUrl;
    private String stringTitle;
    private String stringPublished;
    private String stringPublisher;
    private String stringAuthors;
    private float floatAverageRating;
    private int intRatingsCount;

    public BookDatas(String authors, String title, String published, String publisher, int ratings, float averageRating, String thumbnail)  {
        stringThumbnailUrl = thumbnail;
        stringTitle = title;
        stringPublished = published;
        stringPublisher = publisher;
        stringAuthors = authors;
        floatAverageRating = averageRating;
        intRatingsCount = ratings;
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
}
