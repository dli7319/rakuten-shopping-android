package me.davidl.rakutenshopping

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONArray
import java.lang.ref.WeakReference
import java.net.URL

class SearchResultsAdapter(private val context: Context) : RecyclerView.Adapter<SearchResultsAdapter.ItemViewHolder>() {

    private lateinit var arrayOfItems: JSONArray
    private lateinit var arrayOfImages: Array<Bitmap?>

    companion object {
        private const val TAG = "SearchResultsAdapter"
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.search_list_item, viewGroup, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {

//        Clear the view
        viewHolder.itemImage.setImageBitmap(null)
        viewHolder.itemImage.setBackgroundColor(Color.TRANSPARENT)
        viewHolder.itemListPrice.visibility = View.INVISIBLE

        val itemInfo = arrayOfItems.getJSONObject(position)
        viewHolder.itemTitle.text = itemInfo?.getString("itemName")
        val itemPriceArr = itemInfo?.getJSONArray("itemPrice")
        if (itemPriceArr!!.length() > 0) {
            val itemPrice = itemPriceArr.getString(0)
            viewHolder.itemPrice.text = context.getString(R.string.addDollarSign, itemPrice)
        }
        val itemListPriceArr = itemInfo.getJSONArray("itemListPrice")
        if (itemListPriceArr!!.length() > 0) {
            val itemListPrice = itemListPriceArr.getString(0)
            if (itemListPrice.isNotEmpty()) {
                viewHolder.itemListPrice.text = context.getString(R.string.addDollarSign, itemListPrice)
                viewHolder.itemListPrice.visibility = View.VISIBLE
            }
        }
        val imageUrl = itemInfo.getString("itemImageUrl")
        if (imageUrl != null && imageUrl.isNotEmpty()) {
            viewHolder.itemImageUrl = imageUrl
            setItemImage(viewHolder, position)
        }
    }

    private fun setItemImage(viewHolder: ItemViewHolder, position: Int) {
        val url = viewHolder.itemImageUrl ?: return
        DownloadAndSaveImageTask(viewHolder, arrayOfImages, position, url).execute()
    }

    private class DownloadAndSaveImageTask(
        viewHolder: ItemViewHolder, arrayOfImages: Array<Bitmap?>,
        val position: Int, val url: String
    ) : AsyncTask<Void, Void, Bitmap?>() {
        val viewHolderRef: WeakReference<ItemViewHolder> = WeakReference(viewHolder)
        val arrayImagesRef: WeakReference<Array<Bitmap?>> = WeakReference(arrayOfImages)

        override fun doInBackground(vararg params: Void?): Bitmap? {
            var image: Bitmap? = null
            val arrayOfImages = arrayImagesRef.get()
            if (arrayOfImages != null && arrayOfImages[position] != null) {
                return arrayOfImages[position]
            }
            try {
                val inStream = URL(url).openStream()
                image = BitmapFactory.decodeStream(inStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (image != null) {
                arrayOfImages?.set(position, image)
            }
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result == null) {
                return
            }
            val viewHolder = viewHolderRef.get()
            if (viewHolder != null && viewHolder.itemImageUrl == url) {
                viewHolder.itemImage.setImageBitmap(result)
            }
        }
    }

    override fun getItemCount(): Int {
        if (!this::arrayOfImages.isInitialized) {
            return 0
        }
        return arrayOfItems.length()
    }

    fun setItemData(newData: JSONArray) {
        arrayOfItems = newData
        arrayOfImages = Array(arrayOfItems.length()) { null }
        notifyDataSetChanged()
    }

    fun onItemViewClicked(position: Int) {
        val item = arrayOfItems.getJSONObject(position)
        val url = item.getString("itemUrl")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {


        var itemImage: ImageView = itemView.findViewById(R.id.iv_item_image)
        var itemTitle: TextView = itemView.findViewById(R.id.tv_item_title)
        var itemPrice: TextView = itemView.findViewById(R.id.tv_item_price)
        var itemListPrice: TextView = itemView.findViewById(R.id.tv_item_list_price)
        var itemImageUrl: String? = null

        init {
            itemListPrice.paintFlags = itemListPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemViewClicked(adapterPosition)
        }
    }
}