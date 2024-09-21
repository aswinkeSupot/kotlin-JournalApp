package com.aswin.journalapp

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(view: ImageView, imageUrl: String?) {
        Glide.with(view.context)
            .load(imageUrl)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder)  // Your placeholder image
                    .error(R.drawable.placeholder)              // Your error image
            )
            .into(view)
    }
}