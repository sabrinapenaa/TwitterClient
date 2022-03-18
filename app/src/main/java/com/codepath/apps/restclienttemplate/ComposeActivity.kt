package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
   lateinit var etCompose : EditText
   lateinit var btnTweet : Button

   lateinit var client : TwitterClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        btnTweet.setOnClickListener {
            //Grab the content of edit text for the (etCompose)
            val tweetContent = etCompose.text.toString()
            //1. male sure tweet isnt empty
            if(tweetContent.isEmpty()){
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()

            }else
            //2. make sure tweet is in character count
            if(tweetContent.length > 280){
                Toast.makeText(this, "Tweet is too long!", Toast.LENGTH_SHORT).show()

            } else {
                //Make an api call to Twitter to publish the tweet
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler(){
                    override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                        Log.e(TAG, "Failed to publish tweet", throwable )
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        //send the tweet back to timeline activity
                        Log.i(TAG, "Published tweet")
                        //Send back to timeline activity, close current composeActivity
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet )
                        setResult(RESULT_OK,intent)
                        finish()
                    }


                })
            }
        }

    }
    companion object{
        val TAG = "ComposeActivity"
    }
}