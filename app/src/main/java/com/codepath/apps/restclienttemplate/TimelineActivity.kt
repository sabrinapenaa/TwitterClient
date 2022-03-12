package com.codepath.apps.restclienttemplate

import android.os.Bundle
import android.util.Log
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

    }


}