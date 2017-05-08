package com.example.sanya.goodread;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sanya on 2017.05.08..
 */

public class BookAdapter extends ArrayAdapter<BookDatas>{

    public BookAdapter(Activity context, ArrayList<BookDatas> books)  {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.listview_books, parent, false);
        }

        BookDatas actualBook = getItem(position);
        TextView textTitle = (TextView) listItemView.findViewById(R.id.text_title);
        TextView textPublished = (TextView) listItemView.findViewById(R.id.text_published);
        TextView textPublisher = (TextView) listItemView.findViewById(R.id.text_publisher);
        TextView textAuthors = (TextView) listItemView.findViewById(R.id.text_authors);
        ImageView imageThumbnail = (ImageView) listItemView.findViewById(R.id.image_bookthumbnail);
        RatingBar rateBook = (RatingBar) listItemView.findViewById(R.id.ratingbar);
        TextView textRating = (TextView) listItemView.findViewById(R.id.text_ratingcount);

        textTitle.setText(actualBook.getTitle());
        textPublished.setText(actualBook.getPublishedDate().substring(0,4));
        textPublisher.setText("("+actualBook.getPublisher()+")");
        textAuthors.setText(actualBook.getAuthors());
        imageThumbnail.setImageDrawable(actualBook.getThumbnail());
        textRating.setText("(" + String.valueOf(actualBook.getRatings()) + ")");
        rateBook.setRating(actualBook.getAverageRating());

        return listItemView;
    }
}
