package com.android.lab9;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import org.json.JSONArray;
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

public class MainActivity extends AppCompatActivity {
    private OkHttpClient client;
    private ArrayList<Message> dialog;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private Button sendButton;
    private EditText messageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.message_box);
        sendButton = findViewById(R.id.send_button);
        messageInput = findViewById(R.id.input_message);

        client = new OkHttpClient();
        dialog = new ArrayList<>();
        messageAdapter = new MessageAdapter(dialog);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = messageInput.getText().toString().trim();
                addToChat(userInput, Message.SENT_BY_USER);
                messageInput.setText("");
                callAPI(userInput);
            }
        });
    }

    public void callAPI(String userInput) {
        JSONObject requestBody = createRequestJSON(userInput);
        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.get("application/json; charset=utf-8"));

        String apiKey = getString(R.string.API_KEY);
        String authorizationHeader = "Bearer " + apiKey;

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", authorizationHeader)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                addToChat("Failed to load response due to " + e.getMessage(), Message.SENT_BY_BOT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("choices");
                    String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                    addToChat(result, Message.SENT_BY_BOT);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    addToChat("Failed to load response due to: " + e.getMessage(), Message.SENT_BY_BOT);
                }
            }
        });
    }

    private JSONObject createRequestJSON(String userInput) {
        JSONObject requestBody = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject messageObject = new JSONObject();
        try {
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("temperature", 0);
            messageObject.put("role", "user");
            messageObject.put("content", userInput);
            messages.put(messageObject);
            requestBody.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestBody;
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
}
