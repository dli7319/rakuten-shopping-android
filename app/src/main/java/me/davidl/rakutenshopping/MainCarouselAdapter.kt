package me.davidl.rakutenshopping

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import me.davidl.rakutenshopping.utilities.RakutenHomepage
import java.lang.ref.WeakReference
import java.net.URL

class MainCarouselAdapter(private val context: Context) :
    RecyclerView.Adapter<MainCarouselAdapter.CarouselViewHolder>() {
    private lateinit var homepage: RakutenHomepage
    private lateinit var arrayOfImages: Array<Bitmap?>

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CarouselViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val view = inflater.inflate(R.layout.home_carousel_item, p0, false)
        return CarouselViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (this::homepage.isInitialized) {
            return homepage.carouselImages.size
        }
        return 0
    }

    override fun onBindViewHolder(viewHolder: CarouselViewHolder, position: Int) {
        viewHolder.imageView.setImageBitmap(null)
        val url = homepage.carouselImages[position]
        viewHolder.itemImageUrl = url
        DownloadAndSaveImageTask(viewHolder, arrayOfImages, position, url).execute()
    }

    private class DownloadAndSaveImageTask(
        viewHolder: CarouselViewHolder, arrayOfImages: Array<Bitmap?>,
        val position: Int, val url: String
    ) : AsyncTask<Void, Void, Bitmap?>() {
        val viewHolderRef: WeakReference<CarouselViewHolder> = WeakReference(viewHolder)
        val arrayImagesRef: WeakReference<Array<Bitmap?>> = WeakReference((arrayOfImages))

        override fun doInBackground(vararg params: Void?): Bitmap? {
            var image: Bitmap? = null
            try {
                val inStream = URL(url).openStream()
                image = BitmapFactory.decodeStream(inStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            arrayImagesRef.get()?.set(position, image)
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result == null) {
                return
            }
            val viewHolder = viewHolderRef.get()
            if (viewHolder != null && viewHolder.itemImageUrl == url) {
                viewHolder.imageView.setImageBitmap(result)
            }
        }
    }

    fun setData(homepage: RakutenHomepage) {
        this.homepage = homepage
        arrayOfImages = Array(homepage.carouselImages.size) { null }
        notifyDataSetChanged()
    }

    fun clickedOnCarouselItem(position: Int) {
        val url = homepage.carouselLinks[position]
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val imageView: ImageView = itemView.findViewById(R.id.iv_carousel_item)
        var itemImageUrl = ""

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickedOnCarouselItem(adapterPosition)
        }
    }
}