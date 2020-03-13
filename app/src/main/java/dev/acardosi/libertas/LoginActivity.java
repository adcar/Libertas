package dev.acardosi.libertas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public static final MediaType FORM
            = MediaType.get("application/x-www-form-urlencoded");
    public static final String API_TOKEN = "dev.acardosi.libertas.API_TOKEN";

    private String clientSecret = "D69D0D855CB549BFB93F3F94B33200A359EB05B0FBA04643A0007BCFD3C024DC";
    private String clientId = "VO5319BFF1218B4247840349C3FB798101AT";
    private String redirectUri = "libertas://callback";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final SharedPreferences mPrefs = getSharedPreferences("voat",0);
        final String token = mPrefs.getString("token", "");


        if (token.length() > 0) {
            // We have the token
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(API_TOKEN, token);
            startActivity(intent);
            finish();
        }
    }


    public void onVoatLogin(View v) {

        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://api.voat.co/oauth/authorize?response_type=code&state=1&client_id="
                        + clientId + "&scope=profile&redirect_uri=" + redirectUri));
        startActivity(intent);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();
        // Check if not null and check to make sure the URL is correct so it only does it with this intent
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            final String code = uri.getQueryParameter("code");

            final OkHttpClient client = new OkHttpClient();
            final String url = "https://api.voat.co/oauth/token";

            final String bodyString = "grant_type=authorization_code&code=" + code + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + redirectUri;
            RequestBody body = RequestBody.create(bodyString, FORM);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Api-Key", clientId)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                    if (response.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        try {
                            JSONObject data = new JSONObject(response.body().string());
                            // there is also refresh_token, user_name, and scope
                            String token = data.getString("access_token");
                            intent.putExtra(API_TOKEN, token);
                            startActivity(intent);
                            finish(); // finishes this activity so the users can't press the back button to go back to the sign in page

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }
            });
        }
    }
}
