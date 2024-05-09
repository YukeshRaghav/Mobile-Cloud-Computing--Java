package com.example.lab_9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    Button sendButton;
    Spinner spinner;
    List<Message> messageList;
    String selectedPrompt;
    MessageAdapter messageAdapter;
//    public static final MediaType JSON
//            = MediaType.get("application/json; charset=utf-8");
//    OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        spinner = findViewById(R.id.prompt_spinner);
        // System.out.println("Spinner value:"+spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.prompt_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Set an OnItemSelectedListener to the Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item from the Spinner
                selectedPrompt = parentView.getItemAtPosition(position).toString();
                // Do something with the selected prompt
                Toast.makeText(MainActivity.this, "Selected Prompt: " + selectedPrompt, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            addToChat(question,Message.SENT_BY_ME);
            messageEditText.setText("");
            String prompt="";
            if(selectedPrompt.equals("Check Grammer"))
                    prompt="You will be provided with statements, and your task is to convert them to standard English";
            else if(selectedPrompt.equals("Explain Code"))
                    prompt="You will be provided with a piece of code, and your task is to explain it in a concise way.";
                else if(selectedPrompt.equals("Translate to Spanish"))
                    prompt="You will be provided with a sentence in English, and your task is to translate it into Spanish.";
                    else if(selectedPrompt.equals("Emoji Translation"))
                        prompt="You will be provided with text, and your task is to translate it into emojis. Do not use any regular text. Do your best with emojis only.";
                        else if(selectedPrompt.equals("Tweet classifier"))
                            prompt="\n" +
                                    "You will be provided with a tweet, and your task is to classify its sentiment as positive, neutral, or negative.";


            String promp=prompt+"User:"+question;



            callAPI(promp);
            welcomeTextView.setVisibility(View.GONE);
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    void callAPI(String question){
        //okhttp
        messageList.add(new Message("Typing... ",Message.SENT_BY_BOT));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","gpt-3.5-turbo-instruct");
            jsonBody.put("prompt",question);
            jsonBody.put("max_tokens",4000);
            jsonBody.put("temperature",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        String access_token = "" // Set Access Token
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer " + access_token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    addResponse("Failed to load response due to "+response.body().toString());
                }
            }

        });

    }


}