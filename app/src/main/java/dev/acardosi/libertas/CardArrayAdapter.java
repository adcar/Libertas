package dev.acardosi.libertas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardArrayAdapter  extends ArrayAdapter<Card>  {

    private static final String TAG = "CardArrayAdapter";
    private List<Card> cardList = new ArrayList<Card>();

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


        if (card.getContent() != null) {

            if (viewHolder.content.getParent() == null) {
                viewHolder.card.addView(viewHolder.content);
            }


            viewHolder.content.setText(card.getContent());
        } else if (card.getContent() == null) {
            viewHolder.card.removeView(viewHolder.content);
        }

        viewHolder.subverse.setText("v/" + card.getSubverse());



//        RichPreview richPreview = new RichPreview(new ResponseListener() {
//            @Override
//            public void onData(MetaData metaData) {
//                String imageUrl = metaData.getImageurl();
//                Log.i("meta",String.valueOf(metaData));
//
//                if (!imageUrl.equals(""))
//                    Picasso.get().load( metaData.getImageurl()).placeholder(R.drawable.ic_launcher_background).into(viewHolder.thumbnail);
//
//
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                //handle error
//            }
//        });

        String url = card.getUrl();



        if (card.getType().equals("Link") && url != null) {
            // TODO: Do an okhttp request to figure out if the header is an image. Not all images will have the extension.
            if (url.endsWith(".gif") || url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg")) {
//                recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
                Picasso.get().load(url).placeholder(R.drawable.placeholder).into(viewHolder.thumbnail);
//                Glide.with(getContext()).load(url).placeholder(R.drawable.placeholder).apply(new RequestOptions()
//                        .fitCenter()
//                        .format(DecodeFormat.PREFER_ARGB_8888)
//                        .override(Target.SIZE_ORIGINAL)).into(viewHolder.thumbnail);
            } else {
                viewHolder.thumbnail.setImageResource(R.drawable.link);
            }
        } else {
            viewHolder.card.removeView(viewHolder.thumbnail);
        }

        if (viewHolder.thumbnail.getParent() == null) {
            viewHolder.card.addView(viewHolder.thumbnail);
        }


        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


}