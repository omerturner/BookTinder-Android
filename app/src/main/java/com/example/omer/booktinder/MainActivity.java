package com.example.omer.booktinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    Book currentBook;

    @Bind(R.id.bookTitle) TextView bookTitle;
    @Bind(R.id.bookImage) ImageView bookImage;
    @Bind(R.id.bookDescription) TextView bookDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getBook();


    }

    private void getBook() {
        String url = "http://10.0.3.2:3000/api/books/get";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Exception onFailure caught: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        currentBook = getCurrentBook(response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setBookDisplay();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private void setBookDisplay() {
        bookTitle.setText(currentBook.getTitle());
        Picasso.with(this).load(currentBook.getImg()).into(bookImage);
        bookDescription.setText(currentBook.getDescription());
    }

    private Book getCurrentBook(Response response) throws IOException, JSONException {
        JSONArray responseArray = new JSONArray(response.body().string());
        JSONObject responseJson = responseArray.getJSONObject(0);
        Log.v(TAG, responseJson.toString());
        return new Book(responseJson.getString("_id"),
                        responseJson.getString("title"),
                        responseJson.getString("author"),
                        responseJson.getString("category"),
                        responseJson.getString("img"),
                        responseJson.getString("description"));
    }
}
