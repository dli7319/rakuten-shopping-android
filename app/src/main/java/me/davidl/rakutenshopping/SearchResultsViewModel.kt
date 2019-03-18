package me.davidl.rakutenshopping

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONArray

class SearchResultsViewModel(application: Application) : AndroidViewModel(application) {
    private val arrayOfItems: MutableLiveData<JSONArray> = MutableLiveData()
    private val arrayOfImages: MutableLiveData<Array<Bitmap?>> = MutableLiveData()
    var lastSearchQuery = ""
    var scrollPosition = 0

    fun getArrayOfItems(): MutableLiveData<JSONArray> {
        return arrayOfItems
    }

    fun getArrayOfImages(): MutableLiveData<Array<Bitmap?>> {
        return arrayOfImages
    }
}