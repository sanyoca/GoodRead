package com.example.sanya.goodread;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<BookDatas>>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button listButton = (Button) findViewById(R.id.button_list);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
                if(isConnected) {
                    EditText searchEdit = (EditText) findViewById(R.id.edit_searchcriteria);
                    String stringCriteria = searchEdit.getText().toString();
                    startSearch(stringCriteria);
                }   else    {
                    TextView emptyView = (TextView) findViewById(R.id.text_emptyview);
                    emptyView.setText(getString(R.string.nointernet));
                }
            }
        });
    }

    public void startSearch(String criteria)    {
        BookLoader loadBooks = new BookLoader(this, criteria);
    }

    @Override
    public Loader<List<BookDatas>> onCreateLoader(int id, Bundle args) {
    }

    @Override
    public void onLoadFinished(Loader<List<BookDatas>> loader, List<BookDatas> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<BookDatas>> loader) {

    }
}
