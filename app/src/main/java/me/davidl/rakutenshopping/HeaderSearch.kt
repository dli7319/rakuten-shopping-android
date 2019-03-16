package me.davidl.rakutenshopping

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.*

class HeaderSearch{

    constructor(context: Context, searchString: String = "") {

        var act = context as Activity

        var searchButton = act.findViewById<ImageButton>(R.id.ib_header_search)
        searchButton.setOnClickListener(HeaderSearch.SearchButtonClickListener(context));

        var headerLogo = act.findViewById<ImageView>(R.id.iv_header_logo)
        headerLogo.setOnClickListener(HeaderSearch.LogoClickListener(context))

        var textInput = act.findViewById<EditText>(R.id.et_header_search)
        textInput.setText(searchString, TextView.BufferType.EDITABLE)
    }

    class LogoClickListener(var context: Context) : View.OnClickListener {
        override fun onClick(v: View?) {
            Toast.makeText(context, "You clicked on the logo", Toast.LENGTH_SHORT).show()
        }
    }

    class SearchButtonClickListener(var context: Context) : View.OnClickListener {
        override fun onClick(v: View?) {
            var inputView: EditText = (context as Activity).findViewById(R.id.et_header_search) as EditText
            var searchQuery = inputView.text

            var destinationActivity = SearchResultsActivity::class.java
            var toSearchResults = Intent(context, destinationActivity)
            toSearchResults.putExtra(Intent.EXTRA_TEXT, searchQuery)
            context.startActivity(toSearchResults)
        }
    }
}