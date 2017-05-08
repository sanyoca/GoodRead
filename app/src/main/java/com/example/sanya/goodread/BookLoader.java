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
        // set the search criteria
        stringCriteria = criteria;
    }

    /**
     * start the loader
     */
    protected void onStartLoading()	{
        forceLoad();
    }

    /**
     *
     * @return a list of bookdatas to display
     * the stuff we do in the background. Async. On another thread.
     */
    @Override
    public List<BookDatas> loadInBackground() {
        List<BookDatas> loadIntoThis;

        URL urlGetDataFromHere = null;

        // get the URL from the string
        try {
            urlGetDataFromHere = transformIntoURL(stringCriteria);
        }   catch   (MalformedURLException e)   {
            Log.i("loadInBackGround", e.getMessage().toString());
            return null;
        }

        // build up the connection
        HttpURLConnection bookConnection = openConnection(urlGetDataFromHere);
        String stringBookJSONData = null;

        // if the connection was successful
        if(bookConnection != null)  {
            // get the JSON adat
            stringBookJSONData = getJSONDataFromHere(bookConnection);
        }   else    {
            // else return a null
            return null;
        }

        // if receiving the JSON data was successful (it's not empty)
        if(!stringBookJSONData.isEmpty())   {
            // then parse it into a nice List<BookDatas>
            loadIntoThis = bookDataParsingFromThis(stringBookJSONData);
        }   else    {
            // else return null
            return null;
        }
        // return with the parsed data
        return loadIntoThis;
    }

    /**
     *
     * @param stringURL the String URL to be transfromed into a real URL
     * @return an URL
     * @throws MalformedURLException
     */
    private URL transformIntoURL(String stringURL) throws MalformedURLException {
        URL urlGetDataFromHere = new URL(stringCriteria);
        return urlGetDataFromHere;
    }

    /**
     *
     * @param urlConnection from which we want to receive datas
     * @return a connection we can use to fetch datas
     */
    private HttpURLConnection openConnection(URL urlConnection)   {
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
            Log.i("loadInBackGround", "Problem opening connection: "+ e.getMessage().toString());
        }
        return bookConnection;
    }

    /**
     *
     * @param connection the connection to be used to fetch data
     * @return a String, read from the connection, to be used in parsing in bookDataParsingFromThis method
     */
    private String getJSONDataFromHere(HttpURLConnection connection)    {
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
            Log.i("getJSONDataFromHere", e.getMessage().toString());
        }
        return bookList.toString();
    }

    /**
     *
     * @param stringBookDatas the String to be parsed
     * @return a List<BookDatas> to be put into the adapter
     */
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
