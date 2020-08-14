package com.tadiche.greflashcard;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.tadiche.greflashcard.model.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import utility.UtilityBean;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout layout;
    private TextView resultTextView;
    private UtilityBean utility;
    final ArrayList<String> wordlist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utility = new UtilityBean();

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        layout = findViewById(R.id.constraintLayout);
        layout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Toast.makeText(MainActivity.this, "Swipe Left gesture detected", Toast.LENGTH_SHORT).show();
                //checkIfMongoDBServerIsUpAndRunning();
                if(utility.getRightSwipeStack().isEmpty() ) {
                    getApiCallForRandomWord("/random");
                }else {
                    //read from right stack
                    if(!utility.getRightSwipeStack().isEmpty()) {
                        Word word = utility.getRightSwipeStack().pop();
                        utility.getLeftSwipeStack().push(word);
                        StringBuilder sb = new StringBuilder();
                        sb.append("Word : " + word.getWordstr() + "\n");
                        sb.append("Meaning:" + word.getMeaning() + "\n");
                        resultTextView.setText(sb.toString());
                    }
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Toast.makeText(MainActivity.this, "Swipe Right gesture detected", Toast.LENGTH_SHORT).show();
                //pop word from leftstack and push in to right stack
                if(!utility.getLeftSwipeStack().isEmpty()) {
                    Word word = utility.getLeftSwipeStack().pop();
                    utility.getRightSwipeStack().push(word);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Word : " + word.getWordstr() + "\n");
                    sb.append("Meaning:" + word.getMeaning() + "\n");
                    resultTextView.setText(sb.toString());
                }else {
                    resultTextView.setText("Swipe Left No More in History");
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //call Get REST api call
        //Call getApiCall() method
       // getApiCallForWords("/words");
        getApiCallForRandomWord("/random");
        getApiCall("/all");
    }

    private void getApiCallForRandomWord(String url) {
        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            GETAPIRequest getapiRequest = new GETAPIRequest();
            getapiRequest.request(MainActivity.this, fetchGetResultListenerRandomWord, url);
         } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void getApiCallForWords(String url) {
        GETAPIRequest getapiRequest = new GETAPIRequest();
        try {
            getapiRequest.request(MainActivity.this, fetchGetResultListener, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    private void getApiCall(String url) {
        try {
            //Create Instance of GETAPIRequest and call it's
            //request() method
            GETAPIRequest getapiRequest = new GETAPIRequest();

            //Attaching only part of URL as base URL is given
            //in our GETAPIRequest(of course that need to be same for all case)
            //String url = "/random";
            //String url="webapi.php?userId=1";

            getapiRequest.request_jsonarray(MainActivity.this, fetchGetResultListener, url);
            //checkIfMongoDBServerIsUpAndRunning();
            Toast.makeText(MainActivity.this,"GET API called: "+url,Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIfMongoDBServerIsUpAndRunning() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://192.168.2.2:8080/quotes/random";
        final boolean[] returnVal = {true};
        final String[] response = {null};
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String resp) {
                        // Display the first 500 characters of the response string.
                        resultTextView.setText("Response is: " + resp);
                        response[0] = resp;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultTextView.setText("That didn't work!");
                returnVal[0] = false;
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }
    FetchDataListener fetchGetResultListenerRandomWord = new FetchDataListener() {

        @Override
        public void onFetchComplete(JSONObject data) {
            RequestQueueService.cancelProgressDialog();
            try {
                //Now check result sent by our GETAPIRequest class
                if (data != null) {
                    // Process the JSON
                   StringBuilder sb = new StringBuilder();
                   sb.append("Word : " + data.getString("word") + "\n");
                   sb.append("Meaning:" + data.getString("meaning") + "\n");
                   resultTextView.setText(sb.toString());
                   utility.getLeftSwipeStack().push(new Word(data.getString("_id"),data.getString("word"),data.getString("type"),data.getString("meaning")));
                } else {
                    RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
                }
            } catch (Exception e) {
                RequestQueueService.showAlert("Something went wrong", MainActivity.this);
                e.printStackTrace();
            }
        }

        @Override
        public void onFetchComplete_JsonArray(JSONArray data) {

        }

        @Override
        public void onFetchFailure(String msg) {

        }

        @Override
        public void onFetchStart() {

        }
    };

    //Implementing interfaces of FetchDataListener for GET api request
    FetchDataListener fetchGetResultListener = new FetchDataListener() {

        @Override
       public void onFetchComplete(JSONObject response) {
           //Fetch Complete. Now stop progress bar  or loader
           //you started in onFetchStart
           RequestQueueService.cancelProgressDialog();
           try {
               //Now check result sent by our GETAPIRequest class
               if (response != null) {


                   // Process the JSON
                  /* List<Word> returnList = new ArrayList<Word>();
                   Iterator<String> keys = data.keys();

                   List<String> keysList = new ArrayList<String>();
                   while (keys.hasNext()) {
                       keysList.add(keys.next());
                   }
                   Collections.sort(keysList);*/

                   // Loop through the array elements
                       /*for(int i=0;i<array.length();i++){
                           // Get current json object
                           JSONObject student = array.getJSONObject(i);

                           // Get the current student (json object) data
                           String firstName = student.getString("firstname");
                           String lastName = student.getString("lastname");
                           String age = student.getString("age");

                           // Display the formatted json data in text view
                           mTextView.append(firstName +" " + lastName +"\nage : " + age);
                           mTextView.append("\n\n");
                       }*/
                   //resultTextView.setText(Integer.toBinaryString(keysList.size()));
                   /*StringBuilder sb = new StringBuilder();
                   sb.append("Quote : " + data.getString("quote") + "\n");
                   sb.append("Author:" + data.getString("author") + "\n");
                   sb.append("Genre :" + data.getString("genre"));
                   resultTextView.setText(sb.toString());*/

               } else {
                   RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
               }
           } catch (Exception e) {
               RequestQueueService.showAlert("Something went wrong", MainActivity.this);
               e.printStackTrace();
           }
       }

        @Override
        public void onFetchComplete_JsonArray(JSONArray response) {
            RequestQueueService.cancelProgressDialog();
            try {
                //Now check result sent by our GETAPIRequest class
                if (response != null) {
                    for(int i = 0; i < response.length(); i++){
                        JSONObject jresponse = null;
                        try {
                            jresponse = response.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String word = null;
                        try {
                            word = jresponse.getString("word");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Sridhar: ", word);
                        wordlist.add(word);
                    }
                    Toast.makeText(MainActivity.this,"Total Words Downloaded: "+String.valueOf(wordlist.size()),Toast.LENGTH_SHORT).show();
                } else {
                    RequestQueueService.showAlert("Error! No data fetched", MainActivity.this);
                }
            } catch (Exception e) {
                RequestQueueService.showAlert("Something went wrong", MainActivity.this);
                e.printStackTrace();
            }
        }

        boolean checkIfMongoDBServerIsUpAndRunning() {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://192.168.2.2:8080/quotes/check";
            final boolean[] returnVal = {true};
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                            //textView.setText("Response is: "+ response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //textView.setText("That didn't work!");
                    Toast.makeText(getApplicationContext(), "Mongo didn't work!", Toast.LENGTH_SHORT).show();
                    returnVal[0] = false;
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            return returnVal[0];
        }

        @Override
        public void onFetchFailure(String msg) {
            RequestQueueService.cancelProgressDialog();
            //Show if any error message is there called from GETAPIRequest class
            RequestQueueService.showAlert(msg, MainActivity.this);
        }

        @Override
        public void onFetchStart() {
            //Start showing progressbar or any loader you have
            RequestQueueService.showProgressDialog(MainActivity.this);
        }
    };
}