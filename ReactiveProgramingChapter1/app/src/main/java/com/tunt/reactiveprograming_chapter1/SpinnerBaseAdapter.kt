package com.tunt.reactiveprograming_chapter1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * Created by TuNT on 9/4/18.
 * tunt.program.04098@gmail.com
 */
class SpinnerBaseAdapter(context: Context?, val items: ArrayList<Pair<String, String>>) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var view: View? = null
        if (convertView == null) {
            view = inflater.inflate(R.layout.layout_spinner_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.bind(getItem(position).first)

        return view ?: convertView!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var view: View? = null
        if (convertView == null) {
            view = inflater.inflate(R.layout.layout_spinner_dropdown_item, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.bind(getItem(position).first)

        return view ?: convertView!!
    }

    override fun getItem(position: Int): Pair<String, String> = items[position]

    override fun getItemId(id: Int): Long = id.toLong()

    override fun getCount(): Int = items.size

    inner class ViewHolder(itemView: View) {
        private var tvTitle: TextView
        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
        }

        fun bind(text: String) {
            tvTitle.text = text
        }
    }
}