package dev.acardosi.libertas;

import org.json.JSONException;
import org.json.JSONObject;

public class Card {
    private String title;
    private String content;
    private String url;
    private String type;

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
}