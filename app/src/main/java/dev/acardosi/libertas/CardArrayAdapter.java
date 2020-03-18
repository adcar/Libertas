package dev.acardosi.libertas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardArrayAdapter  extends ArrayAdapter<Card>  {

    private static final String TAG = "CardArrayAdapter";
    private List<Card> cardList = new ArrayList<Card>();
    private
    PhotoView photoView;

    static class CardViewHolder {
        TextView subverse;
        TextView title;
        TextView content;
        ImageView thumbnail;
        LinearLayout card;
    }



    public CardArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Card object) {
        cardList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public Card getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View row = convertView;
        final CardViewHolder viewHolder;
        Card card = getItem(position);
        assert card != null;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_card, parent, false);
            viewHolder = new CardViewHolder();

            viewHolder.card = row.findViewById(R.id.cardLinear);
            viewHolder.title = row.findViewById(R.id.title);
            viewHolder.content = row.findViewById(R.id.content);
            viewHolder.thumbnail = row.findViewById(R.id.thumbnail);
            viewHolder.subverse = row.findViewById(R.id.subverse);
            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }

        viewHolder.title.setText(card.getTitle());
        if (card.getContent() == null || card.getContent().length() < 1) {
            viewHolder.content.setVisibility(View.GONE);
        } else {
            viewHolder.content.setVisibility(View.VISIBLE);
        }

        viewHolder.content.setText(card.getContent());
        viewHolder.subverse.setText("v/" + card.getSubverse());

        final String url = card.getUrl();

        if (card.getType().equals("Link") && url != null) {
            // TODO: Do an okhttp request to figure out if the header is an image. Not all images will have the extension.
            if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg")) {
                Picasso.get().load(url).placeholder(R.drawable.placeholder).into(viewHolder.thumbnail);
                viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            } else {
                viewHolder.thumbnail.setImageResource(R.drawable.link);
                viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("alex", v.toString());
                        Uri uri = Uri.parse(url);

                        // create an intent builder
                        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

                        // Begin customizing
                        // set toolbar colors
                        intentBuilder.setToolbarColor(getContext().getColor(R.color.colorPrimary));
                        intentBuilder.setSecondaryToolbarColor(getContext().getColor(R.color.colorPrimaryDark));

                        // build custom tabs intent
                        CustomTabsIntent customTabsIntent = intentBuilder.build();
                        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // launch the url
                        customTabsIntent.launchUrl(getContext(), uri);
                    }
                });

            }
        }

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


}