package me.davidl.rakutenshopping

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import me.davidl.rakutenshopping.utilities.NetworkUtils
import org.json.JSONArray
import java.lang.ref.WeakReference

class SearchResultsActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TERM_EXTRA = "search_term"
    }

    private lateinit var itemListRV: RecyclerView
    private lateinit var itemListAdapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val startingIntent = intent
        var searchTerm: String? = null
        if (startingIntent != null && startingIntent.hasExtra(SEARCH_TERM_EXTRA)) {
            searchTerm = startingIntent.getStringExtra(SEARCH_TERM_EXTRA)
        }

        val headerBundle = Bundle()
        if (searchTerm != null) {
            headerBundle.putString(HeaderSearchFragment.SEARCH_TERM_EXTRA, searchTerm)
        }
        val headerFragment = HeaderSearchFragment()
        headerFragment.arguments = headerBundle
        val manager = supportFragmentManager
        manager.beginTransaction()
            .add(R.id.fl_search_fragment, headerFragment)
            .commit()

        if (searchTerm != null) {
            makeSearchQuery(searchTerm)
        }

        itemListRV = findViewById(R.id.rv_item_list)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itemListRV.layoutManager = layoutManager
        itemListRV.setHasFixedSize(true)
        itemListAdapter = SearchResultsAdapter(this)
        itemListRV.adapter = itemListAdapter
    }

    private fun makeSearchQuery(searchTerm: String) {
        RakutenSearchQueryTask(this).execute(searchTerm)
    }

    private class RakutenSearchQueryTask internal constructor(context: SearchResultsActivity)
        : AsyncTask<String, Void, JSONArray?>() {
        val activityRef = WeakReference(context)

        override fun doInBackground(vararg params: String?): JSONArray? {
            val response = NetworkUtils.submitSearchQuery(params[0] as String)
            val searchResults = response?.getJSONObject("data")?.getJSONObject("searchResults")
            return searchResults?.getJSONArray("docs")

        }

        override fun onPostExecute(result: JSONArray?) {
            super.onPostExecute(result)
            val activity = activityRef.get()
            if (result != null) {
                activity?.itemListAdapter?.setItemData(result)
            }
        }
    }
}
