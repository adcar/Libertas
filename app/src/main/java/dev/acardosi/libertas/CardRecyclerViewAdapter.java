package dev.acardosi.libertas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardRecyclerViewAdapter.CustomViewHolder> {
    private List<Card> cardList;
    private Context mContext;
    private Activity activity;

    public CardRecyclerViewAdapter(Context context, List<Card> feedItemList, Activity activity) {
        cardList = feedItemList;
        mContext = context;
        this.activity = activity;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_card, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }
    public void adjustUI(final boolean isImage, final String url, final CustomViewHolder customViewHolder) {
        if (isImage) {
            Picasso.get().load(url).placeholder(R.drawable.placeholder).into(customViewHolder.thumbnail);
        } else {
            customViewHolder.thumbnail.setImageResource(R.drawable.link);
        }
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        Card card = cardList.get(i);

        customViewHolder.subverse.setText("v/" + card.getSubverse());
        customViewHolder.title.setText(card.getTitle());
        customViewHolder.content.setText(card.getContent());
        customViewHolder.score.setText(card.getScore());

        View parent = (View)customViewHolder.upvote.getParent();

        parent.setTag(new Vote(card.getId()));


        if (card.getContent() == null || card.getContent().length() < 1) {
            customViewHolder.content.setVisibility(View.GONE);
        } else {
            customViewHolder.content.setVisibility(View.VISIBLE);
        }

        final String url = card.getUrl();

        if (card.getType().equals("Link") && url != null) {
            // TODO: Do an okhttp request to figure out if the header is an image. Not all images will have the extension.

            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {


                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adjustUI(false, url, customViewHolder);
                        }
                    });

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    final String contentType = response.header("Content-Type");

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (contentType.contains("image")) {
                                adjustUI(true, url, customViewHolder);
                            } else {
                                adjustUI(false, url, customViewHolder);
                            }
                        }
                    });
                }
            });

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
        TextView score;
        ImageView thumbnail;
        LinearLayout card;
        Button upvote;
        Button downvote;

        public CustomViewHolder(View view) {
            super(view);
            subverse = view.findViewById(R.id.subverse);
            title = view.findViewById(R.id.title);
            content = view.findViewById(R.id.content);
            thumbnail = view.findViewById(R.id.thumbnail);
            card = view.findViewById(R.id.cardLinear);
            score = view.findViewById(R.id.score);
            upvote = view.findViewById(R.id.upvote);
            downvote = view.findViewById(R.id.downvote);
        }
    }
}