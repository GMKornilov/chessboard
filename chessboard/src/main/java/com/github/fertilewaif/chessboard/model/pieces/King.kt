package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.R
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.moves.CaptureMove
import com.github.fertilewaif.chessboard.model.moves.CastleMove
import com.github.fertilewaif.chessboard.model.moves.Move
import com.github.fertilewaif.chessboard.model.moves.TransitionMove
import kotlin.math.abs
import kotlin.math.max

class King(isWhite: Boolean) : Piece(isWhite) {
    companion object {
        val deltas = listOf(
                Pair(-1, -1),
                Pair(-1, 0),
                Pair(-1, 1),
                Pair(0, -1),
                Pair(0, 1),
                Pair(1, -1),
                Pair(1, 0),
                Pair(1, 1)
        )
    }

    override var drawableRes = if (isWhite) {
        R.drawable.ic_wk
    } else {
        R.drawable.ic_bk
    }

    override fun getMoves(board: Board): List<Move> {
        val res = mutableListOf<Move>()
        for ((deltaRow, deltaCol) in deltas) {
            val newRow = position.row + deltaRow
            val newCol = position.col + deltaCol
            if (newRow >= 0 && newCol >= 0 && newRow < Board.BOARD_SIZE && newCol <= Board.BOARD_SIZE) {
                val piece = board.board[newRow][newCol]
                val to = CellInfo(newCol, newRow)
                if (piece == null) {
                    res.add(TransitionMove(this, position, to))
                } else if(piece.isWhite != isWhite) {
                    res.add(CaptureMove(this, piece, position, to))
                }
            }
        }
        val canCastleLong = if(isWhite) {
            board.canWhiteCastleLong
        } else {
            board.canBlackCastleLong
        }
        val canCastleShort = if (isWhite) {
            board.canWhiteCastleShort
        } else {
            board.canBlackCastleShort
        }

        if (canCastleLong) {
            val rookPos = if (isWhite) {
                CastleMove.a1CellInfo
            } else {
                CastleMove.h1CellInfo
            }
            val rook = board.board[rookPos.row][rookPos.col] as Rook
            // TODO: check if cells between are free and not hit
            res.add(CastleMove(this, rook, isWhite, false))
        }
        if (canCastleShort) {
            val rookPos = if (isWhite) {
                CastleMove.a1CellInfo
            } else {
                CastleMove.h1CellInfo
            }
            val rook = board.board[rookPos.row][rookPos.col] as Rook
            // TODO: check if cells between are free and not hit
            res.add(CastleMove(this, rook, isWhite, false))
        }
        return res
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val deltaRow = position.row - cellInfo.row
        val deltaCol = position.col - cellInfo.col
        return (deltaCol != 0 && deltaRow != 0) && max(abs(deltaCol), abs(deltaRow)) == 1
    }

    override fun toString(): String {
        return if(isWhite) {
            "K"
        } else {
            "k"
        }
    }
}