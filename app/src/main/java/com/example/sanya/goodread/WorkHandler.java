package com.example.sanya.goodread;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class WorkHandler  {

    private WorkHandler()   {}

    /**
     *
     * @param stringURL the String URL to be transfromed into a real URL
     * @return an URL
     * @throws MalformedURLException as the URL making could cause problem, has to throw this exception
     */
    static URL transformIntoURL(String stringURL) throws MalformedURLException {
        return new URL(stringURL);
    }

    /**
     *
     * @param urlConnection from which we want to receive datas
     * @return a connection we can use to fetch datas
     */
    static HttpURLConnection openConnection(URL urlConnection)   {
        HttpURLConnection bookConnection = null;

        // try to open a connection
        try {
            bookConnection = (HttpURLConnection) urlConnection.openConnection();
            bookConnection.setReadTimeout(10000);
            bookConnection.setConnectTimeout(15000);
            bookConnection.setRequestMethod("GET");
            bookConnection.connect();

            // is it okay? is the response what we expected?
            if (bookConnection.getResponseCode() == 200) {
                // yes, return with the connection object
                return bookConnection;
            }   else    {
                // no, return a null
                Log.i("loadInBackground", "Error response code: " + bookConnection.getResponseCode());
                return bookConnection;
            }
        } catch (IOException e) {
            // something went awry
            Log.i("loadInBackGround", "Problem opening connection: "+ e.getMessage());
        }
        return bookConnection;
    }

    /**
     *
     * @param connection the connection to be used to fetch data
     * @return a String, read from the connection, to be used in parsing in bookDataParsingFromThis method
     */
    static String getJSONDataFromHere(HttpURLConnection connection)    {
        StringBuilder bookList = new StringBuilder();

        // try to build a stream
        try {
            InputStream bookStream = connection.getInputStream();
            bookList = new StringBuilder();
            // if it's okay, read
            if (bookStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(bookStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                // until there is no more thing to read
                while (line != null) {
                    bookList.append(line);
                    line = reader.readLine();
                }
            }
        }   catch   (IOException e) {
            // something went awry
            Log.i("getJSONDataFromHere", e.getMessage());
        }
        return bookList.toString();
    }

    /**
     *
     * @param stringBookDatas the String to be parsed
     * @return a List<BookDatas> to be put into the adapter
     */
    static List<BookDatas> bookDataParsingFromThis(Context context, String stringBookDatas)   {
        List<BookDatas> tempStore = new ArrayList<>();


        try {
            JSONObject bookJSON = new JSONObject(stringBookDatas);
            // get the book datas
            JSONArray JSONItems = bookJSON.getJSONArray("items");

            // process them one by one
            for(int i = 0; i< JSONItems.length(); i++) {
                JSONObject actualBook = JSONItems.getJSONObject(i);
                // get datas, that every book has
                JSONObject volInfo = actualBook.getJSONObject("volumeInfo");
                String stringTitle = volInfo.getString("title");
                String bookUrl = volInfo.getString("infoLink");

                StringBuilder stringAuthors = new StringBuilder();
                if (volInfo.has("authors")) {
                    JSONArray authorsArray = volInfo.getJSONArray("authors");
                    // if there is more authors, append them
                    stringAuthors.append(context.getString(R.string.by)).append(" ");
                    for(int j=0; j< authorsArray.length(); j++) {
                        stringAuthors.append(authorsArray.getString(j));
                        if(authorsArray.length()>1 && j<authorsArray.length()-1) {
                            stringAuthors.append(", ");
                        }
                    }
                }   else    {
                    stringAuthors.append(context.getString(R.string.na));
                }

                String stringPublisher;
                // get the publisher, if no such thing, place 'NA'
                try {
                    stringPublisher = volInfo.getString("publisher");
                }   catch (JSONException e) {
                    Log.i("bookDataParsingFromThis", e.getMessage());
                    stringPublisher = context.getString(R.string.na) + "   ";
                }

                String stringPublishedDate;
                // get the publisheddate, if no such thing, place 'NA'
                try {
                    stringPublishedDate = volInfo.getString("publishedDate");
                }   catch (JSONException e){
                    Log.i("bookDataParsingFromThis", e.getMessage());
                    stringPublishedDate = context.getString(R.string.na) + "   ";
                }

                String stringAverageRating;
                String stringRatingsCount;
                // get the ratings, if no data, they are '0'
                try {
                    stringAverageRating = volInfo.getString("averageRating");
                    stringRatingsCount = volInfo.getString("ratingsCount");
                }   catch   (JSONException e)   {
                    Log.i("bookDataParsingFromThis", e.getMessage());
                    stringAverageRating = "0";
                    stringRatingsCount = "0";
                }

                JSONObject thumbnails = volInfo.getJSONObject("imageLinks");
                String stringThumbnailURL = thumbnails.getString("smallThumbnail");

                // read the thumbnail from the website and convert it into a drawable
                // if no thumbnail found, use a placeholder
                Drawable drawableBookThumbnail;
                try {
                    InputStream is = (InputStream) new URL(stringThumbnailURL).getContent();
                    drawableBookThumbnail = Drawable.createFromStream(is, stringThumbnailURL);
                } catch (Exception e) {
                    Log.i("bookDataParsingFromThis", e.getMessage());
                    drawableBookThumbnail = ContextCompat.getDrawable(context, R.drawable.nobook);
                }

                BookDatas newBook = new BookDatas(stringAuthors.toString(), stringTitle, stringPublishedDate, stringPublisher, Integer.valueOf(stringRatingsCount), Float.valueOf(stringAverageRating), drawableBookThumbnail, bookUrl);
                tempStore.add(newBook);
            }
        }   catch (JSONException e) {
            // something is very very very wrong
            Log.i("BigProblem", e.getMessage());
            return null;
        }
        return tempStore;
    }
}
