package dev.acardosi.libertas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {

    private String token;
    private CardArrayAdapter cardArrayAdapter;





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


        // Save the token to SharedPrefs
        final SharedPreferences mPrefs = getSharedPreferences("voat", 0);
        final SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("token", token);
        editor.commit();


        getPosts();

    }

    private void getPosts() {
        //
        final OkHttpClient client = new OkHttpClient();
        final String url = "https://api.voat.co/api/v1/v/_front";
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
                final String res = response.body().string();
                final ListView list = findViewById(R.id.posts);
                if (response.isSuccessful()) {
                    Log.i("alex", "BODY: " + res);
                    try {
                        final JSONObject jsonRes = new JSONObject(res);
                        final JSONArray data = jsonRes.getJSONArray("data");


                        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);


                        for (int i = 0; i < data.length(); i++) {

                            CardView card = new CardView(MainActivity.this);
                            TextView title = new TextView(MainActivity.this);
                            title.setText(data.getJSONObject(i).getString("title"));
                            card.addView(title);
                            cardArrayAdapter.add(new Card("Card " + (i+1) + " Line 1", "Card " + (i+1) + " Line 2", "https://images.unsplash.com/photo-1558979158-65a1eaa08691?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1350&q=80"));
                        }

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                list.setAdapter(cardArrayAdapter);

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                // We need to re get a new token and re do the request
                Log.e("alex", "error: " + response.body().string());


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



}
