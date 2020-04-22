package dev.acardosi.libertas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static dev.acardosi.libertas.LoginActivity.FORM;


public class MainActivity extends AppCompatActivity {

    private String token;
    private String refreshToken;
    private CardRecyclerViewAdapter cardAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final Intent intent = getIntent();
        token = intent.getStringExtra(LoginActivity.API_TOKEN);
        refreshToken = intent.getStringExtra(LoginActivity.REFRESH_TOKEN);

        // Save the token to SharedPrefs
        final SharedPreferences mPrefs = getSharedPreferences("voat", 0);
        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("token", token);
        editor.apply();

        getPosts();

    }

    private void getPosts() {
        final OkHttpClient client = new OkHttpClient();
        // I only have one subvoat hardcoded. Potentially this could be anything as an argument from `getPosts()`
        // can be v/_front for the front page... (Mix of subreddits)
        final String url = "https://api.voat.co/api/v1//v/TelevisionQuotes";

        final Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .addHeader("Api-Key", Client.CLIENT_ID)
                .addHeader("Authorization:", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String res = Objects.requireNonNull(response.body()).string();
                final RecyclerView rv = findViewById(R.id.posts);

                if (response.isSuccessful()) {
                    List<Card> cards = new ArrayList<>();
                    try {
                        final JSONObject jsonRes = new JSONObject(res);
                        final JSONArray data = jsonRes.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject post = data.getJSONObject(i);
                            Log.i("res", post.toString());

                            try {
                                cards.add(new Card(post));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        final ProgressBar progressBar = findViewById(R.id.progress_bar);

                        cardAdapter = new CardRecyclerViewAdapter(getApplicationContext(), cards, MainActivity.this);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                progressBar.setVisibility(View.GONE);
                                rv.setAdapter(cardAdapter);

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return;
                }



                // We need to re get a new token and re do the request
                Log.e("alex", "error: " + Objects.requireNonNull(response.body()).string());


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCardClick(View v) {
        Log.i("alex", "clicked");
    }

    public void onUpvote(View v) {

        vote(v, true);
    }
    public void onDownvote(View v) {

        vote(v, false);
    }

    private void vote(View v, boolean upvoting) {
        Log.i("alex", "voting");

        MaterialButton btn = (MaterialButton) v;

        LinearLayout parent = (LinearLayout) v.getParent();
        Vote vote = (Vote)parent.getTag();

        String id = vote.getId();



        if (upvoting) {
            Log.i("alex", "upvote");
            if (vote.isUpvoted()) {
                // # Undo upvote if they already upvoted
                vote.setUpvoted(false);
                vote.setDownvoted(false);

                // Make the button grey
                makeGrey(btn);

                // Run the API request
                voteRequest("https://api.voat.co/api/v1/vote/submission/" + vote.getId() + "/0", parent, btn);

            } else {
                // # Upvote normally
                vote.setUpvoted(true);
                vote.setDownvoted(false);

                // Make the downvote button grey
                makeGrey((MaterialButton)parent.findViewById(R.id.downvote));

                // Set the color of the icon to the primary color
                btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary, MainActivity.this.getTheme())));

                // Run the API request
                voteRequest("https://api.voat.co/api/v1/vote/submission/" + vote.getId() + "/1", parent, btn);

            }
        } else {
            Log.i("alex", "downvote");
            if (vote.isDownvoted()) {
                // # Undo downvote if they already upvoted
                vote.setDownvoted(false);
                vote.setUpvoted(false);

                // Make the button grey
                makeGrey(btn);

                // Run the API request
                voteRequest("https://api.voat.co/api/v1/vote/submission/" + vote.getId() + "/0", parent, btn);

            } else {
                // # Downvote normally
                vote.setDownvoted(true);
                vote.setUpvoted(false);

                // Make the upvote button grey
                makeGrey((MaterialButton)parent.findViewById(R.id.upvote));

                // Set the color of the icon to the accent color
                btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent, MainActivity.this.getTheme())));

                // Run the API request
                voteRequest("https://api.voat.co/api/v1/vote/submission/" + vote.getId() + "/-1", parent, btn);


            }
        }

        parent.setTag(vote);
    }

    private void makeGrey(final MaterialButton btn) {
        btn.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.colorDarkGrey, MainActivity.this.getTheme())));
    }

    private void voteRequest(final String url, final View parent, final MaterialButton btn) {

        Log.i("alex", "Running API Request with URL: " + url);
        final OkHttpClient client = new OkHttpClient();
        final TextView score = (TextView) parent.findViewById(R.id.score);
        final Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .addHeader("Api-Key", Client.CLIENT_ID)
                .addHeader("Authorization:", "Bearer " + token)
                .addHeader("Content-Length", "0")
                .method("POST", RequestBody.create("{\"revokeOnRevote\": \"false\"}", JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Unable to vote. Internet Connection error.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (response.isSuccessful()) {

                    // TODO: Get the recorded value and set the text color based on that.
                    // TODO: Set the new value of text view to whatever the sum is.

                    score.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                    Log.i("alex", String.valueOf(response.body()));
                } else {
                    try {
                        final String urlResponse = Objects.requireNonNull(response.body()).string();
                        final JSONObject res = new JSONObject(urlResponse);
                        final JSONObject err = new JSONObject(res.getString("error"));


                        if (err.getString("message").contains("Auth")) {
                            Log.i("alex", "Contains Auth");
                            // Get a new token from refresh_token
                            final String url = "https://api.voat.co/oauth/token";
                            final String bodyString = "grant_type=refresh_token&refresh_token=" + refreshToken + "&client_id=" + Client.CLIENT_ID + "&client_secret=" + Client.CLIENT_SECRET + "&redirect_uri=" + Client.REDIRECT_URI;
                            Log.i("alex", "bodyString: " + bodyString);
                            final RequestBody body = RequestBody.create(bodyString, FORM);
                            final Request request = new Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .header("Content-Type", "application/x-www-form-urlencoded")
                                    .addHeader("Api-Key", Client.CLIENT_ID)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    Log.i("alex", response.body().string());
                                }
                            });
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(MainActivity.this, "Unable to vote: " + err.getString("message"), Toast.LENGTH_LONG).show();
                                    makeGrey(btn);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    // https://stackoverflow.com/a/28777489/6501208 (modified slightly)
    public static int getThemeAccentColor(final Context context, boolean primaryColor) {
        final TypedValue value = new TypedValue();

        if (primaryColor) {
            context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        } else {
            context.getTheme().resolveAttribute(R.attr.colorAccent, value, true);
        }

        return value.data;
    }



}
