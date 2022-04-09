package com.goldman.test.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.goldman.test.R
import com.goldman.test.data.ApodResponse
import com.goldman.test.databinding.ItemFavoriteBinding
import com.goldman.test.utils.AutoPlayWebViewClient
import com.goldman.test.utils.Utils

class FavoriteAdapter(
    private val mList: MutableList<ApodResponse>,
    private val callback: (ApodResponse) -> Unit,
    private val onDelete: (ApodResponse, Int) -> Unit,
    private val emptyListCallback: () -> Unit,
    private val context: Context
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

        holder.tvName.text = item.title
        holder.tvDate.text = Utils.parseDate(item.date)

        if (item.mediaType == Utils.VIDEO) {
            holder.webView.visibility = View.VISIBLE
            holder.imageApod.visibility = View.GONE

            holder.webView.settings.javaScriptEnabled = true
            holder.webView.settings.pluginState = WebSettings.PluginState.ON
            holder.webView.loadUrl( item.url + "?autoplay=1&vq=small")
            holder.webView.webViewClient = AutoPlayWebViewClient()
        } else {
            holder.webView.visibility = View.GONE
            holder.imageApod.visibility = View.VISIBLE
            Glide
                .with(context)
                .load(item.url)
                .centerCrop()
                .placeholder(Utils.getCircularProgress(context))
                .into(holder.imageApod)
        }

        holder.container.setOnClickListener { callback(item) }
        holder.btnDelete.setOnClickListener { onDelete(item, position) }
    }

    override fun getItemCount() = mList.size

    class ViewHolder(ItemView: ItemFavoriteBinding) : RecyclerView.ViewHolder(ItemView.root) {
       val tvName: TextView = itemView.findViewById(R.id.name)
       val btnDelete: AppCompatButton = itemView.findViewById(R.id.btn_delete)
       val tvDate: TextView = itemView.findViewById(R.id.date)
       val imageApod: ImageView = itemView.findViewById(R.id.apod_image)
       val webView: WebView = itemView.findViewById(R.id.web_view)
       val container: CardView = itemView.findViewById(R.id.container)
    }

    fun deleteItem(position: Int){
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
        if (mList.isNullOrEmpty()) emptyListCallback()
    }
}
