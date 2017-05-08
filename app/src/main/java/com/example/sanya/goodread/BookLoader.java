package com.example.sanya.goodread;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

/**
 * Created by sanya on 2017.05.05..
 */

public class BookLoader extends AsyncTaskLoader<List<BookDatas>> {

    String stringCriteria;

    public BookLoader(Context context, String criteria) {
        super(context);
        stringCriteria = criteria;
    }

    protected void onStartLoading()	{
        forceLoad();
    }

    @Override
    public List<BookDatas> loadInBackground() {
        List<BookDatas> loadIntoThis;

        URL urlGetDataFromHere = null;

        try {
            urlGetDataFromHere = transformIntoURL(stringCriteria);
        }   catch   (MalformedURLException e)   {
            Log.i("loadInBackGround", e.getMessage().toString());
            return null;
        }

        HttpURLConnection bookConnection = openConnection(urlGetDataFromHere);
        String stringBookJSONData = null;

        if(bookConnection != null)  {
            stringBookJSONData = getJSONDataFromHere(bookConnection);
        }   else    {
            return null;
        }

        if(!stringBookJSONData.isEmpty())   {
            loadIntoThis = bookDataParsingFromThis(stringBookJSONData);
        }   else    {
            return null;
        }
        return loadIntoThis;
    }

    private URL transformIntoURL(String stringURL) throws MalformedURLException {
        URL urlGetDataFromHere = new URL(stringCriteria);
        return urlGetDataFromHere;
    }

    private HttpURLConnection openConnection(URL urlConnection)   {
        HttpURLConnection bookConnection = null;

        try {
            bookConnection = (HttpURLConnection) urlConnection.openConnection();
            bookConnection.setReadTimeout(10000);
            bookConnection.setConnectTimeout(15000);
            bookConnection.setRequestMethod("GET");
            bookConnection.connect();

            if (bookConnection.getResponseCode() == 200) {
                return bookConnection;
            }   else    {
                Log.i("loadInBackground", "Error response code: " + bookConnection.getResponseCode());
                return bookConnection;
            }
        } catch (IOException e) {
            Log.i("loadInBackGround", "Problem opening connection: "+ e.getMessage().toString());
        }
        return bookConnection;
    }

    private String getJSONDataFromHere(HttpURLConnection connection)    {
        StringBuilder bookList = new StringBuilder();

        try {
            InputStream bookStream = connection.getInputStream();
            bookList = new StringBuilder();
            if (bookStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(bookStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    bookList.append(line);
                    line = reader.readLine();
                }
            }
        }   catch   (IOException e) {
            Log.i("getJSONDataFromHere", e.getMessage().toString());
        }
        return bookList.toString();
    }

    private List<BookDatas> bookDataParsingFromThis(String stringBookDatas)   {
        List<BookDatas> tempStore = new ArrayList<>();

        try {
            JSONObject bookJSON = new JSONObject(stringBookDatas);

            JSONArray JSONItems = bookJSON.getJSONArray("items");

            for(int i = 0; i< JSONItems.length(); i++) {
                JSONObject actualBook = JSONItems.getJSONObject(i);
                JSONObject volinfo = actualBook.getJSONObject("volumeInfo");
                String stringTitle = volinfo.getString("title");
                JSONArray authorsArray = volinfo.getJSONArray("authors");
                String bookUrl = volinfo.getString("infoLink");
                StringBuilder stringAuthors = new StringBuilder();
                stringAuthors.append("By ");
                for(int j=0; j< authorsArray.length(); j++) {
                    stringAuthors.append(authorsArray.getString(j));
                    if(authorsArray.length()>1 && j<authorsArray.length()-1) {
                        stringAuthors.append(", ");
                    }
                }

                String stringPublisher = volinfo.getString("publisher");
                String stringPublishedDate = volinfo.getString("publishedDate");

                String stringAverageRating;
                String stringRatingsCount;

                try {
                    stringAverageRating = volinfo.getString("averageRating");
                    stringRatingsCount = volinfo.getString("ratingsCount");
                }   catch   (JSONException e)   {
                    stringAverageRating = "0";
                    stringRatingsCount = "0";
                }

                JSONObject thumbnails = volinfo.getJSONObject("imageLinks");
                String stringThumbnailURL = thumbnails.getString("smallThumbnail");

                Drawable drawbleBookThumbnail = null;
                try {
                    InputStream is = (InputStream) new URL(stringThumbnailURL).getContent();
                    drawbleBookThumbnail = Drawable.createFromStream(is, stringThumbnailURL);
                } catch (Exception e) {
                    return null;
                }

                BookDatas newBook = new BookDatas(stringAuthors.toString(), stringTitle, stringPublishedDate, stringPublisher, Integer.valueOf(stringRatingsCount), Float.valueOf(stringAverageRating), drawbleBookThumbnail, bookUrl);
                tempStore.add(newBook);
            }
        }   catch (JSONException e) {
            Log.i("bookDataParsingFromThis", e.getMessage().toString());
            return null;
        }
        return tempStore;
    }
}
