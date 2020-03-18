package dev.acardosi.libertas;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardRecyclerViewAdapter.CustomViewHolder> {
    private List<Card> cardList;
    private Context mContext;

    public CardRecyclerViewAdapter(Context context, List<Card> feedItemList) {
        cardList = feedItemList;
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_card, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        Card card = cardList.get(i);


        customViewHolder.subverse.setText(card.getSubverse());
        customViewHolder.title.setText(card.getTitle());
        customViewHolder.content.setText(card.getContent());
        if (card.getContent() == null || card.getContent().length() < 1) {
            customViewHolder.content.setVisibility(View.GONE);
        } else {
            customViewHolder.content.setVisibility(View.VISIBLE);
        }


        final String url = card.getUrl();

        if (card.getType().equals("Link") && url != null) {
            // TODO: Do an okhttp request to figure out if the header is an image. Not all images will have the extension.
            if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg")) {
                Picasso.get().load(url).placeholder(R.drawable.placeholder).into(customViewHolder.thumbnail);
                customViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            } else {
                customViewHolder.thumbnail.setImageResource(R.drawable.link);
            }
        }
        customViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("alex", v.toString());
                Uri uri = Uri.parse(url);

                // create an intent builder
                CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

                // Begin customizing
                // set toolbar colors
                intentBuilder.setToolbarColor(mContext.getColor(R.color.colorPrimary));
                intentBuilder.setSecondaryToolbarColor(mContext.getColor(R.color.colorPrimaryDark));

                // build custom tabs intent
                CustomTabsIntent customTabsIntent = intentBuilder.build();
                customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // launch the url
                customTabsIntent.launchUrl(mContext, uri);
            }
        });
    }



    @Override
    public int getItemCount() {
        return (null != cardList ? cardList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView subverse;
        TextView title;
        TextView content;
        ImageView thumbnail;
        LinearLayout card;

        public CustomViewHolder(View view) {
            super(view);
            subverse = view.findViewById(R.id.subverse);
            title = view.findViewById(R.id.title);
            content = view.findViewById(R.id.content);
            thumbnail = view.findViewById(R.id.thumbnail);
            card = view.findViewById(R.id.cardLinear);
        }
    }
}