package me.davidl.rakutenshopping

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import me.davidl.rakutenshopping.utilities.RakutenHomepage
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TERM_EXTRA = "search_term"
    }

    private lateinit var carouselRV: RecyclerView
    private lateinit var carouselAdapter: MainCarouselAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        carouselRV = findViewById(R.id.rv_carousel)

        val startingIntent = intent
        val headerFragment = HeaderSearchFragment()
        if (startingIntent != null && startingIntent.hasExtra(SEARCH_TERM_EXTRA)) {
            val searchTerm = startingIntent.getStringExtra(SEARCH_TERM_EXTRA)
            val bundle = Bundle()
            bundle.putString(HeaderSearchFragment.SEARCH_TERM_EXTRA, searchTerm)
            headerFragment.arguments = bundle
        }
        val manager = supportFragmentManager
        manager.beginTransaction()
            .add(R.id.fl_search_fragment, headerFragment)
            .commit()

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        carouselRV.layoutManager = layoutManager
        carouselRV.setHasFixedSize(true)
        carouselAdapter = MainCarouselAdapter(this)
        carouselRV.adapter = carouselAdapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(carouselRV)

        HomepageInitializer(this).execute()
    }

    private class HomepageInitializer constructor(context: MainActivity) : AsyncTask<Void, Void, RakutenHomepage>() {
        val contextRef = WeakReference(context)

        override fun doInBackground(vararg params: Void?): RakutenHomepage {
            return RakutenHomepage()
        }

        override fun onPostExecute(result: RakutenHomepage?) {
            super.onPostExecute(result)
            val context = contextRef.get()
            if (result != null && context != null) {
                context.carouselAdapter.setData(result)
                context.carouselRV.visibility = View.VISIBLE
            }
        }
    }
}
