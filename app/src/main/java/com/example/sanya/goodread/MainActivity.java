package com.example.sanya.goodread;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//import android.support.v4.content.Loader;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<BookDatas>>, View.OnClickListener{

    String stringCriteria;
    ListView listview_listbooks;
    BookAdapter bookAdapter;
    TextView emptyView = (TextView) findViewById(R.id.text_emptyview);
    ProgressBar pb = (ProgressBar) findViewById(R.id.progress_loadbooks);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button listButton = (Button) findViewById(R.id.button_list);
        listButton.setOnClickListener(this);

        listview_listbooks = (ListView) findViewById(R.id.list_books);
        bookAdapter = new BookAdapter(this, new ArrayList<BookDatas>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        listview_listbooks.setAdapter(bookAdapter);
    }

    public void startSearch()    {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<List<BookDatas>> onCreateLoader(int id, Bundle args) {
        pb.setVisibility(View.VISIBLE);
        BookLoader loadBooks = new BookLoader(this, "https://www.googleapis.com/books/v1/volumes?q="+stringCriteria);
        emptyView = (TextView) findViewById(R.id.text_emptyview);
        emptyView.setVisibility(View.INVISIBLE);
        return loadBooks;
    }

    @Override
    public void onLoadFinished(Loader<List<BookDatas>> loader, List<BookDatas> books) {
        bookAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            bookAdapter.addAll(books);
            emptyView.setVisibility(View.INVISIBLE);

        }   else    {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No books found matching your criteria");
        }
        pb.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<BookDatas>> loader) {
        bookAdapter.clear();
    }

    @Override
    public void onClick(View v) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if(isConnected) {
            EditText searchEdit = (EditText) findViewById(R.id.edit_searchcriteria);
            stringCriteria = searchEdit.getText().toString();
            startSearch();
        }   else    {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(getString(R.string.nointernet));
        }
    }
}
