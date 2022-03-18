package com.codepath.apps.restclienttemplate

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException


class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTweets: RecyclerView
    lateinit var adapter: TweetAdapter
    lateinit var swipeContainer: SwipeRefreshLayout

   // lateinit var scrollListener: EndlessRecyclerViewScrollListener

    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // val actionBar: ActionBar?
        //actionBar = this.supportActionBar

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
       // val colorDrawable = ColorDrawable(Color.parseColor("#1DA1F2"))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#1DA1F2")))

        // Set BackgroundDrawable

        // Set BackgroundDrawable
       // actionBar.setBackgroundDrawable(colorDrawable)
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setIcon(R.drawable.ic_iconmonstr_twitter_4);
        setContentView(R.layout.activity_timeline)
        val linearLayoutManager = LinearLayoutManager(this)
        client = TwitterApplication.getRestClient(this)
       // rvTweets.addOnScrollListener(scrollListener as EndlessRecyclerViewScrollListener)
//        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to the bottom of the list
//                populateHomeTimeline()
//            }
//        }
        // Adds the scroll listener to RecyclerView
        // Adds the scroll listener to RecyclerView


        swipeContainer = findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing timeline")
            populateHomeTimeline()

        }
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        );



        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetAdapter(tweets)

        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter
        populateHomeTimeline()
    }
    //TODO
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }
    //Handles clicks on menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.Compose){
        //naviagre to compose screen
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
        return super.onOptionsItemSelected(item)
    }
    //This method is called when we come back from ComposeActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if( resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            //Getting data from intent
            val tweet = data?.getParcelableExtra("tweet") as Tweet

            //Update timeline
            //Modify the data source of twets
            tweets.add(0,tweet)

            //Update adapter
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun populateHomeTimeline(){
        client.getHomeTimeline(object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "onSuccess $json")

                try {
                    //clear out currenttly fetched attributes
                    adapter.clear()
                    val jsonArray = json.jsonArray
                    val listOfNewTweets = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(listOfNewTweets)
                    adapter.notifyDataSetChanged()
                    swipeContainer.setRefreshing(false)
                } catch (e: JSONException) {
                    Log.e(TAG, "JSONException $e")
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure")
            }


        })

    }
    companion object{
        const val TAG = "TimelineActivity"
        val REQUEST_CODE = 10

    }


}