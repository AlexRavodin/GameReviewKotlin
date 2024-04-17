package com.example.gamereview.adapters

import com.example.gamereview.R
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.gamereview.activities.GameDetailsActivity
import com.example.gamereview.models.Game

class GameListAdapter(
    private val context: Context,
    private val games: List<Game>,
    private val userId: String
) : BaseAdapter() {

    override fun getCount(): Int {
        return games.size
    }

    override fun getItem(position: Int): Game {
        return games[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val game = getItem(position)
        viewHolder.titleTextView.text = game.name

        view.setOnClickListener {
            val intent = Intent(context, GameDetailsActivity::class.java)
            intent.putExtra("gameId", game.id)
            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
    }
}