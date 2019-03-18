package me.davidl.rakutenshopping

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.davidl.rakutenshopping.utilities.NetworkUtils
import org.json.JSONArray

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

        setupViewModel()
        executeSearch(searchTerm)

        itemListRV = findViewById(R.id.rv_item_list)
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        itemListRV.layoutManager = layoutManager
        itemListRV.setHasFixedSize(true)
        itemListAdapter = SearchResultsAdapter(this)
        itemListRV.adapter = itemListAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        val viewModel = ViewModelProviders.of(this as FragmentActivity)
            .get<SearchResultsViewModel>(SearchResultsViewModel::class.java)
        val layoutModel = itemListRV.layoutManager as LinearLayoutManager
        viewModel.scrollPosition = layoutModel.findFirstVisibleItemPosition()
    }

    private fun setupViewModel() {
        val viewModel = ViewModelProviders.of(this as FragmentActivity)
            .get<SearchResultsViewModel>(SearchResultsViewModel::class.java)
        viewModel.getArrayOfItems().observe(this, Observer<JSONArray> {
            if (it != null) {
                val imagesArray = Array<Bitmap?>(it.length()) { null }
                viewModel.getArrayOfImages().value = imagesArray
                itemListAdapter.setItemData(it, imagesArray)
                itemListRV.scrollToPosition(viewModel.scrollPosition)
            }

        })

    }

    fun executeSearch(searchTerm: String?) {
        val viewModel = ViewModelProviders.of(this as FragmentActivity)
            .get<SearchResultsViewModel>(SearchResultsViewModel::class.java)
        if (searchTerm != null) {
            NetworkUtils.executeSearchQuery(searchTerm, viewModel)
        }
    }
}
