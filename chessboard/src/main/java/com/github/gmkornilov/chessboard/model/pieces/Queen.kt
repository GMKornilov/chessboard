package com.github.gmkornilov.chessboard.model.pieces

import com.github.gmkornilov.chessboard.R
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.moves.CaptureMove
import com.github.gmkornilov.chessboard.model.moves.Move
import com.github.gmkornilov.chessboard.model.moves.TransitionMove

internal class Queen(isWhite: Boolean) : Piece(isWhite) {
    private val rook = Rook(isWhite)
    private val bishop = Bishop(isWhite)

    override var drawableRes = if (isWhite) {
        R.drawable.ic_wq
    } else {
        R.drawable.ic_bq
    }

    override var position: CellInfo
        get() = super.position
        set(value) {
            super.position = value
            rook.position = value
            bishop.position = value
        }

    override fun getMoves(board: Board): List<Move> {
        val resRook = rook.getMoves(board)
        val resBishop = bishop.getMoves(board)
        val res = mutableListOf<Move>()
        for (move in resRook + resBishop) {
            when(move) {
                is CaptureMove -> res.add(CaptureMove(this, move.killedPiece, move.from, move.to))
                is TransitionMove -> res.add(TransitionMove(this, move.from, move.to))
            }
        }
        return res
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        return bishop.canHit(cellInfo, board) || rook.canHit(cellInfo, board)
    }

    override fun toString(): String {
        return if(isWhite) {
            "Q"
        } else {
            "q"
        }
    }
}