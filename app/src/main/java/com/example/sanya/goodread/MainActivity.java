package com.example.sanya.goodread;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//import android.support.v4.content.Loader;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<BookDatas>>, View.OnClickListener{

    TextView emptyView;
    ProgressBar loadInProgress;
    ListView listview_listbooks;
    BookAdapter bookAdapter;

    String stringCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the views we'll use in the activity's methods
        emptyView = (TextView) findViewById(R.id.text_emptyview);               // the emptyview *doh*
        loadInProgress = (ProgressBar) findViewById(R.id.progress_loadbooks);   // the progressbar
        Button listButton = (Button) findViewById(R.id.button_list);            // the listing button
        listview_listbooks = (ListView) findViewById(R.id.list_books);          // the listview

        // make the list button to list the books
        listButton.setOnClickListener(this);
        bookAdapter = new BookAdapter(this, new ArrayList<BookDatas>());
        listview_listbooks.setAdapter(bookAdapter);
        listview_listbooks.setEmptyView(emptyView);

        // set an onitemclicklistener on the adapter, so the items can be clicked
        listview_listbooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // visit the clicked book website
                BookDatas bookToVisit = bookAdapter.getItem(position);
                Uri bookURL = Uri.parse(bookToVisit.getBookURL());
                Intent visitBook = new Intent(Intent.ACTION_VIEW, bookURL);
                startActivity(visitBook);
            }
        });

        // get a loader
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * This one starts the search when the list button is clicked
     */
    public void startSearch()    {
        // make the soft keyboard disappear
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // restart the loadermanager to refetch data from the internet
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     *
     * @param id the id of the loader to be created
     * @param args  bundle
     * @return a loader value
     */
    @Override
    public Loader<List<BookDatas>> onCreateLoader(int id, Bundle args) {
        // make the progressbar appear
        loadInProgress.setVisibility(View.VISIBLE);
        // and the emptyview disappear
        emptyView.setVisibility(View.INVISIBLE);
        // let's start it. Async. In the background. On a different thread.
        BookLoader loadBooks = new BookLoader(this, "https://www.googleapis.com/books/v1/volumes?q="+stringCriteria+"&maxResults=20");
        // return the fetched data to onLoadFinished
        return loadBooks;
    }

    /**
     *
     * @param loader the loader we use
     * @param books the argument we receive after the loader's done
     */
    @Override
    public void onLoadFinished(Loader<List<BookDatas>> loader, List<BookDatas> books) {
        // clear up the adapter
        bookAdapter.clear();

        // if the datas we' got contains anything, display it
        if (books != null && !books.isEmpty()) {
            bookAdapter.addAll(books);
        }   else    {
            // if there are no datas set the emptyview text
            emptyView.setText(getString(R.string.nobookfound));
            // but it can be the lack of internet connection
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
            if(!isConnected) {
                // then change the emptyview text again
                emptyView.setText(getString(R.string.nointernet));
            }
        }
        // and make the progressbar woooosh!
        loadInProgress.setVisibility(View.INVISIBLE);
    }

    /**
     *
     * @param loader
     * when it's not more needed, release the loader and the resources it used
     */
    @Override
    public void onLoaderReset(Loader<List<BookDatas>> loader) {
        bookAdapter.clear();
    }

    /**
     *
     * @param v the view we clicked on - the list button
     */
    @Override
    public void onClick(View v) {
        // is there internet connection
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if(isConnected) {
            // yes, start the search
            EditText searchEdit = (EditText) findViewById(R.id.edit_searchcriteria);
            stringCriteria = searchEdit.getText().toString();
            startSearch();
        }   else    {
            // no, clear the adapter and make the message appear
            bookAdapter.clear();
            emptyView.setText(getString(R.string.nointernet));
        }
    }
}
