package me.davidl.rakutenshopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*

class HeaderSearchFragment() : Fragment() {

    var mSearchImageButton: ImageButton? = null
    var mSearchEditText: EditText? = null
    var mLogo: ImageView? = null

    companion object {
        val SEARCH_TERM_EXTRA = "search_term"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.header_search, container, false)
        mSearchEditText = rootView.findViewById(R.id.et_header_search)
        mSearchImageButton = rootView.findViewById(R.id.ib_header_search)
        mLogo = rootView.findViewById(R.id.iv_header_logo)
        mSearchEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_NEXT ||
                actionId == EditorInfo.IME_ACTION_GO
            ) {
                executeSearch()
            }
            true
        }
        mSearchImageButton!!.setOnClickListener { executeSearch() }
        mLogo!!.setOnClickListener { gotoMainActivity() }

        val searchTerm = arguments?.getString(SEARCH_TERM_EXTRA)
        if (searchTerm != null) {
            mSearchEditText!!.setText(searchTerm)
        }

        return rootView
    }

    private fun gotoMainActivity() {
        if (activity is MainActivity) {
            return;
        }
        val searchTerm = mSearchEditText?.text.toString()
        val intent = Intent(activity as Context, MainActivity::class.java)
        intent.putExtra(SearchResultsActivity.SEARCH_TERM_EXTRA, searchTerm)
        startActivity(intent)
    }

    private fun executeSearch() {
        val searchTerm = mSearchEditText?.text.toString()
        Toast.makeText(activity, "Search button clicked $searchTerm", Toast.LENGTH_LONG).show()
        val searchIntent = Intent(activity, SearchResultsActivity::class.java)
        searchIntent.putExtra(SearchResultsActivity.SEARCH_TERM_EXTRA, searchTerm)
        startActivity(searchIntent)
    }
}