package io.userfeeds.share

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ContextListAdapter(private val contexts: List<ShareContext>, private val onContextSelect: (ShareContext) -> Unit) : RecyclerView.Adapter<ContextListAdapter.Holder>() {

    override fun getItemCount() = contexts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.context_item, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val context = contexts[position]
        val contextImageView = holder.itemView.findViewById(R.id.contextImage) as ImageView
        Glide.with(holder.itemView.context)
                .load(context.imageId)
                .into(contextImageView)
        val contextTextView = holder.itemView.findViewById(R.id.contextName) as TextView
        contextTextView.text = context.name
        holder.itemView.setOnClickListener {
            onContextSelect(context)
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
