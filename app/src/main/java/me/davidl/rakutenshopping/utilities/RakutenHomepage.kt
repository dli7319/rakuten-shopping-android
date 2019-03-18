package me.davidl.rakutenshopping.utilities

import android.util.Log
import org.jsoup.Jsoup
import java.net.URL

class RakutenHomepage() {
    val carouselImages: ArrayList<String> = ArrayList(0)
    val carouselLinks: ArrayList<String> = ArrayList(0)

    companion object {
        private const val RAKUTEN_HOMEPAGE_URL = "https://www.rakuten.com/shop/"
        private const val TAG = "RakutenHomepage"
    }

    init {
        val doc = Jsoup.connect(RAKUTEN_HOMEPAGE_URL)
            .cookie("X-QaVc6qmo", "AEAxmo5pAQAAQIiYHL_vbZionkY-rKJlDHHDve_3oH1xNC9Ui11lYKrCGX6v").get()
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