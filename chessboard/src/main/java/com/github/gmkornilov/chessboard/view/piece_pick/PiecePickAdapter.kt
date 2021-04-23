package com.github.gmkornilov.chessboard.view.piece_pick

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.gmkornilov.chessboard.R
import com.github.gmkornilov.chessboard.model.moves.PromotionMove
import com.github.gmkornilov.chessboard.model.pieces.Piece

class PiecePickAdapter(private val moves: List<PromotionMove>, private val callback: (PromotionMove) -> Unit) :
    RecyclerView.Adapter<PiecePickAdapter.ViewHolder>() {
    class ViewHolder(view: View, val callback: (PromotionMove) -> Unit) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.pieceImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.piece_item, parent, false)
        return ViewHolder(view, callback)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val move = moves[position]
        holder.imageView.setImageResource(move.promotedPiece.drawableRes)
        holder.itemView.setOnClickListener { holder.callback(move) }
    }

    override fun getItemCount(): Int {
        return moves.size
    }
}