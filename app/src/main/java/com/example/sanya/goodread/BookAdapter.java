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

public class BookAdapter extends ArrayAdapter<BookDatas>{

    public BookAdapter(Activity context, ArrayList<BookDatas> books)  {
        super(context, 0, books);
    }

    static class ViewHolderItem {
        TextView text_title;
        TextView text_published;
        TextView text_publisher;
        TextView text_authors;
        ImageView image_bookthumbnail;
        RatingBar ratingbar;
        TextView text_ratingcount;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolderItem viewHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.listview_books, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.text_title = (TextView) convertView.findViewById(R.id.text_title);
            viewHolder.text_published = (TextView) convertView.findViewById(R.id.text_published);
            viewHolder.text_publisher = (TextView) convertView.findViewById(R.id.text_publisher);
            viewHolder.text_authors = (TextView) convertView.findViewById(R.id.text_authors);
            viewHolder.image_bookthumbnail = (ImageView) convertView.findViewById(R.id.image_bookthumbnail);
            viewHolder.ratingbar = (RatingBar) convertView.findViewById(R.id.ratingbar);
            viewHolder.text_ratingcount = (TextView) convertView.findViewById(R.id.text_ratingcount);
            convertView.setTag(viewHolder);
        }   else    {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        BookDatas actualBook = getItem(position);

        if(actualBook != null)  {
            viewHolder.text_title.setText(actualBook.getTitle());
            viewHolder.text_published.setText(actualBook.getPublishedDate());
            viewHolder.text_publisher.setText(actualBook.getPublisher());
            viewHolder.text_authors.setText(actualBook.getAuthors());
            viewHolder.image_bookthumbnail.setImageDrawable(actualBook.getThumbnail());
            viewHolder.ratingbar.setRating(actualBook.getAverageRating());
            viewHolder.text_ratingcount.setText("(" + String.valueOf(actualBook.getRatings()) + ")");
        }
        return convertView;
    }
}
