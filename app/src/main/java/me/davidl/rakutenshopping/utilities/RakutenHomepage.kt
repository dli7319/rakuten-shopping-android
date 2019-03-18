package me.davidl.rakutenshopping.utilities

import android.content.Context
import android.util.Log
import org.jsoup.Jsoup
import java.net.URL
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.regex.Pattern


class RakutenHomepage() {
    val carouselImages: ArrayList<String> = ArrayList(0)
    val carouselLinks: ArrayList<String> = ArrayList(0)

    companion object {
        private const val RAKUTEN_BASE_URL = "https://www.rakuten.com/"
        private const val RAKUTEN_HOMEPAGE_URL = "https://www.rakuten.com/shop/"
        private const val RAKUTEN_COOKIE_URL = "https://www.rakuten.com/eCa432UiJrqnJsU3"
        private const val TAG = "RakutenHomepage"
        private val RAKUTEN_COOKIE_PATTERN =
            Pattern.compile("xhttp\\.open\\('GET',\\s?'\\/([a-zA-Z\\d]*)',\\s?true\\);")
    }

    init {
        val emptyHomepage = NetworkUtils.submitGetRequest(URL(RAKUTEN_HOMEPAGE_URL))
        val cookieMatcher = RAKUTEN_COOKIE_PATTERN.matcher(emptyHomepage)
        var cookieUrl = RAKUTEN_COOKIE_URL
        if (cookieMatcher.find()) {
            val regexMatch = cookieMatcher.group(1)
            cookieUrl = RAKUTEN_BASE_URL + regexMatch
        }
        val cookiesToGet = Jsoup.connect(cookieUrl).execute().cookies()
        val doc = Jsoup.connect(RAKUTEN_HOMEPAGE_URL).cookies(cookiesToGet).get()
        val carouselDivs = doc.getElementsByClass("carousel-cell")
        for (carouselDiv in carouselDivs) {
            val imageElement = carouselDiv.getElementsByTag("source").first()
            val linkElement = carouselDiv.getElementsByTag("a").first()
            if (imageElement != null && linkElement != null) {
                carouselImages.add(imageElement.attr("abs:srcset"))
                carouselLinks.add(linkElement.attr("abs:href"))
            } else {
                carouselImages.add((imageElement != null).toString())
                carouselLinks.add((linkElement != null).toString())
                Log.i(TAG, "Carousel image found: ${imageElement != null}, link found: ${linkElement != null}")
            }
        }
    }

}