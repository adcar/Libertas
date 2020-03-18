package dev.acardosi.libertas;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardRecyclerViewAdapter.CustomViewHolder> {
    private List<Card> cardList;
    private Context mContext;

    public CardRecyclerViewAdapter(Context context, List<Card> feedItemList) {
        this.cardList = feedItemList;
        this.mContext = context;
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

//        //Render image using Picasso library
//        if (!TextUtils.isEmpty(feedItem.getThumbnail())) {
//            Picasso.get().load(feedItem.getThumbnail())
//                    .error(R.drawable.placeholder)
//                    .placeholder(R.drawable.placeholder)
//                    .into(customViewHolder.imageView);
//        }

        //Setting text view title
        customViewHolder.subverse.setText(card.getSubverse());
        customViewHolder.title.setText(card.getTitle());
        customViewHolder.content.setText(card.getContent());
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