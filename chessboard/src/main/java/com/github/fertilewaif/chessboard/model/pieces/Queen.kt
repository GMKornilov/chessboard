package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.R
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.moves.Move

class Queen(isWhite: Boolean) : Piece(isWhite) {
    val rook = Rook(isWhite)
    val bishop = Bishop(isWhite)

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

    override fun getLegalMoves(board: Board): List<Move> {
        val resRook = rook.getLegalMoves(board)
        val resBishop = bishop.getLegalMoves(board)
        return resRook + resBishop
    }

    override fun getMoves(board: Board): List<Move> {
        val resRook = rook.getMoves(board)
        val resBishop = bishop.getMoves(board)
        return resRook + resBishop
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