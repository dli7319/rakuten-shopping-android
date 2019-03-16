package me.davidl.rakutenshopping

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SearchResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        HeaderSearch(this);
    }
}
