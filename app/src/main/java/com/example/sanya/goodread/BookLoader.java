package com.example.sanya.goodread;

import android.content.AsyncTaskLoader;
import android.content.Context;

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

    @Override
    public List<BookDatas> loadInBackground() {
        return null;
    }
}
