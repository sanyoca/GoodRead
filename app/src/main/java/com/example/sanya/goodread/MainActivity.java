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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<BookDatas>>{

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
        listview_listbooks = (ListView) findViewById(R.id.list_books);          // the listview

        // make the list button to list the books
        bookAdapter = new BookAdapter(this, new ArrayList<BookDatas>());

        // set the adapter and the emptyview
        listview_listbooks.setAdapter(bookAdapter);
        listview_listbooks.setEmptyView(emptyView);

        // set an onitemclicklistener on the adapter, so the items within can be clicked
        listview_listbooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // visit the clicked book website
                BookDatas bookToVisit = bookAdapter.getItem(position);
                // if we had an internet connection
                if (isConnected()) {
                    // then try to visit the book's website
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bookToVisit.getBookURL())));
                    } catch (NullPointerException e) {
                        Log.i("Main/onitemclick", e.getMessage());
                        Toast.makeText(getApplicationContext(), getString(R.string.nowebsite), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    bookAdapter.clear();
                    emptyView.setText(getString(R.string.nointernet));
                }
            }
        });

        // set the searchview
        ((SearchView) findViewById(R.id.searchbook)).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // start the search on the criteria, the user wrote
                if(isConnected()) {
                    if(!query.isEmpty())    {
                        stringCriteria = query;
                        startSearch();
                    }
                }   else    {
                    // or popup a toast, if no internet connection was found
                    bookAdapter.clear();
                    emptyView.setText(getString(R.string.nointernet));
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        // do we have an internet connection
        if(isConnected()) {
            // yes, get a loader and the possible datas
            getLoaderManager().initLoader(0, null, this);
        }   else    {
            // if we don't have internet connection, we might still have something in the adapter
            if(!bookAdapter.isEmpty())   {
                emptyView.setText(getString(R.string.nointernet));
            }   else    {
                getLoaderManager().initLoader(0, null, this);
            }
        }
    }

    /**
     * This one starts the search when the searchview is done
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
        // if returned from a book's website, we have to delete the adapter's content, cause it re-reads and appends the datas to the existing adapter datas
        bookAdapter.clear();
        // make the progressbar appear
        loadInProgress.setVisibility(View.VISIBLE);
        // and the emptyview disappear
         emptyView.setVisibility(View.INVISIBLE);
        // let's start it. Async. In the background. On a different thread.
        // return the fetched data to onLoadFinished
        return new BookLoader(this, "https://www.googleapis.com/books/v1/volumes?q="+stringCriteria+"&maxResults=20");
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
            // if there are no datas, set the emptyview text
            emptyView.setText(getString(R.string.nobookfound));
            // but it can be the lack of internet connection
            if(!isConnected()) {
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
     * Checks if there is an available internet connection
     * @return true, if yes - false, if no
     */
    private boolean isConnected()   {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }
}
