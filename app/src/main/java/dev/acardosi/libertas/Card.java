package dev.acardosi.libertas;

import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

public class Card {
    private String title;
    private String content;
    private String url;
    private String type;
    private String subverse;
    private String score; // Yes this is odd, but we need to set the textView to this so we might as well keep it a string
    private String id;

    // API returns `"null"` (A string, not the null primitive), but here I fix it to `null` (the primitive)
    private String nullable(String s) {
        if (s.equals("null")) {
            return null;
        }
        return s;
    }

    public Card(JSONObject post) throws JSONException {
        title = nullable(post.getString("title"));
        content = nullable(post.getString("content"));
        url = nullable(post.getString("url"));
        type = nullable(post.getString("type"));
        subverse = post.getString("subverse");
        score = post.getString("sum");
        id = post.getString("id");

        // Vote is always null. Is the API broken?
//        Log.i("alex", post.getString("vote"));
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getSubverse() {
        return subverse;
    }

    public String getScore() {
        return score;
    }

    public String getId() {return id; }


}