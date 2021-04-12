package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.R
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.moves.CaptureMove
import com.github.fertilewaif.chessboard.model.moves.Move
import com.github.fertilewaif.chessboard.model.moves.TransitionMove

class Knight(isWhite: Boolean) : Piece(isWhite) {
    companion object {
        val deltas = listOf(
                Pair(-1, -2),
                Pair(-1, 2),
                Pair(-2, -1),
                Pair(-2, 1),
                Pair(1, -2),
                Pair(1, 2),
                Pair(2, -1),
                Pair(2, 1)
        )
    }

    override var drawableRes = if (isWhite) {
        R.drawable.ic_wn
    } else {
        R.drawable.ic_bn
    }

    override fun getMoves(board: Board): List<Move> {
        val res = mutableListOf<Move>()
        for ((deltaI, deltaJ) in deltas) {
            val row = position.row + deltaI
            val col = position.col + deltaJ
            if (row < 0 || row > 7 || col < 0 || col > 7) {
                continue
            }
            val piece = board.board[row][col]
            val to = CellInfo(row, col)
            if (piece == null) {
                res.add(TransitionMove(this, position, to))
            } else if(piece.isWhite != isWhite && piece !is King) {
                res.add(CaptureMove(this, piece, position, to))
            }
        }
        return res
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val deltaRow = cellInfo.row - position.row
        val deltaCol = cellInfo.col - position.col
        return Pair(deltaRow, deltaCol) in deltas
    }

    override fun toString(): String {
        return if(isWhite) {
            "K"
        } else {
            "k"
        }
    }
}