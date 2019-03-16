package me.davidl.rakutenshopping.utilities

import android.net.Uri;
import android.util.Log
import org.json.JSONObject
import java.io.BufferedWriter

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

class NetworkUtils {

    val RAKUTEN_API_URL = "https://www.rakuten.com/graphql";
    private val RAKUTEN_DO_SEARCH_QUERY = """
        query doSearch(${'$'}k: String, ${'$'}c: String, ${'$'}fa: String, ${'$'}fs: String, ${'$'}b: String, ${'$'}ic: String, ${'$'}rcpa: String, ${'$'}minp: String, ${'$'}maxp: String, ${'$'}bo: String, ${'$'}p: String, ${'$'}search_conjunction: String, ${'$'}h: String, ${'$'}s: String, ${'$'}safe_search: Boolean, ${'$'}sale: String, ${'$'}po_c: String, ${'$'}spellcheck: Boolean) {\nsearchResults(k: ${'$'}k, c: ${'$'}c, fa: ${'$'}fa, fs: ${'$'}fs, b: ${'$'}b, ic: ${'$'}ic, rcpa: ${'$'}rcpa, minp: ${'$'}minp, maxp: ${'$'}maxp, bo: ${'$'}bo, p: ${'$'}p, search_conjunction: ${'$'}search_conjunction, h: ${'$'}h, s: ${'$'}s, safe_search: ${'$'}safe_search, sale: ${'$'}sale, po_c: ${'$'}po_c, spellcheck: ${'$'}spellcheck) {\n  categoryList\n  leftNaviOrder\n  brandList\n  attributeList\n  itemConditionList\n  hits\n  queryConjunction\n  queryId\n  querySubId\n  spellcheckCorrection\n  sponsoredProducts {\n    isFreeShipping\n    isSponsored\n    itemImageUrl\n    itemName\n    itemListPrice\n    itemPrice\n    itemUrl\n    cannotBeAddedToWishlist\n    __typename\n  }\n  docs {\n    brand\n    baseSku\n    campaignType\n    categoryId\n    itemId\n    itemName\n    itemUrl\n    rcpProductMasterId\n    shortShopUrl\n    shopId\n    isAvailable\n    isSoldOut\n    unavailableMsg\n    itemImageUrl\n    itemListPrice\n    itemPrice\n    itemOriginalPrice\n    mapType\n    merchantId\n    itemCondition\n    isFreeShipping\n    bvProductKey\n    __typename\n  }\n  __typename\n}\n}
    """.trimIndent()

    fun submitSearchQuery(query: String): String? {
        var data = JSONObject()
                .put("operationName", "doSearch")
                .put("query=", RAKUTEN_DO_SEARCH_QUERY)
        var variables = JSONObject()
                .put("b", "")
                .put("c", "")
                .put("fa", "")
                .put("fs", "")
                .put("h", "2")
                .put("ic", "")
                .put("k", query)
                .put("mapx", "")
                .put("minp", "")
                .put("p", "")
                .put("po_c", "")
                .put("s", "1")
                .put("safe_search", true)
                .put("sale", "0")
                .put("sortby", "relevancy_ab")
        data.put("variables", variables)
        val jsonData = data.toString()
        Log.i("me.davidl.networkutils", "JSON DATA: $jsonData")
        var url = URL(RAKUTEN_API_URL)
        return submitPostRequest(url, jsonData);
    }

    fun submitPostRequest(url: URL, data: String): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "POST"
        urlConnection.doInput = true
        urlConnection.doOutput = true
        try {
            // Set data for POST request
            val outStream = urlConnection.outputStream
            val writer = BufferedWriter(OutputStreamWriter(outStream, "UTF-8"))
            writer.write(data)
            writer.flush()
            writer.close()
            outStream.close()

            // Read response
            val inStream = urlConnection.inputStream
            val scanner = Scanner(inStream)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            return if (hasInput) {
                return scanner.next()
            } else {
                return null
            }
        } finally {
            urlConnection.disconnect()
        }
    }
}