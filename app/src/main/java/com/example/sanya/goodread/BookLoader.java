package com.example.sanya.goodread;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.example.sanya.goodread.WorkHandler.bookDataParsingFromThis;
import static com.example.sanya.goodread.WorkHandler.getJSONDataFromHere;
import static com.example.sanya.goodread.WorkHandler.openConnection;
import static com.example.sanya.goodread.WorkHandler.transformIntoURL;

public class BookLoader extends AsyncTaskLoader<List<BookDatas>> {

    String stringCriteria;
    Context mContext;

    public BookLoader(Context context, String criteria) {
        super(context);
        // set the search criteria
        stringCriteria = criteria;
        mContext = context;
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

        URL urlGetDataFromHere;

        // get the URL from the string
        try {
            urlGetDataFromHere = transformIntoURL(stringCriteria);
        }   catch   (MalformedURLException e)   {
            Log.i("loadInBackGround", e.getMessage());
            return null;
        }

        // build up the connection
        HttpURLConnection bookConnection = openConnection(urlGetDataFromHere);
        String stringBookJSONData;

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
            loadIntoThis = bookDataParsingFromThis(getContext(), stringBookJSONData);
        }   else    {
            // else return null
            return null;
        }
        // return with the parsed data
        return loadIntoThis;
    }
}
