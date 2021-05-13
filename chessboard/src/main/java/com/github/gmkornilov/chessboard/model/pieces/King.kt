package com.github.gmkornilov.chessboard.model.pieces

import com.github.gmkornilov.chessboard.R
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.moves.*
import kotlin.math.abs
import kotlin.math.max

internal class King(isWhite: Boolean) : Piece(isWhite) {
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
            if (newRow >= 0 && newCol >= 0 && newRow < Board.BOARD_SIZE && newCol < Board.BOARD_SIZE) {
                val piece = board.board[newRow][newCol]
                val to = CellInfo(newCol, newRow)
                if (piece == null) {
                    res.add(KingTransitionMove(this, position, to))
                } else if (piece.isWhite != isWhite) {
                    res.add(KingCaptureMove(this, piece, position, to))
                }
            }
        }
        val canCastleLong = if (isWhite) {
            board.canWhiteCastleLong
        } else {
            board.canBlackCastleLong
        }
        val canCastleShort = if (isWhite) {
            board.canWhiteCastleShort
        } else {
            board.canBlackCastleShort
        }

        val kingPos = if (isWhite) {
            CastleMove.e1CellInfo
        } else {
            CastleMove.e8CellInfo
        }
        val rookShortPos = if (isWhite) {
            CastleMove.h1CellInfo
        } else {
            CastleMove.h8CellInfo
        }
        val rookLongPos = if (isWhite) {
            CastleMove.a1CellInfo
        } else {
            CastleMove.a8CellInfo
        }

        val boardKing = board.board[kingPos.row][kingPos.col]
        val boardShortRook = board.board[rookShortPos.row][rookShortPos.col]
        val boardLongRook = board.board[rookLongPos.row][rookLongPos.col]

        // I think that in a few weeks I wont understand what I wrote here, so message for all newcomers:

        // Here we check that king piece can castle in a short or a long way
        // first we need to check that we can castle: we check that condition from board and that there are
        // needed pieces at specified positions and our king is not hit (this 2 big if's)

        // Then we need to check that all cells between king and rook are not taken by other pieces and
        // that all cells are not hit by opposite pieces (this 2 inner if's)

        // (yeah, I don't like this spaghetti myself)

        if (canCastleLong && boardKing is King && boardLongRook is Rook && !board.isHit(position, isWhite)) {
            if ((position.col - 1 downTo rookLongPos.col + 1).all { col -> board.board[position.row][col] == null && !board.isHit(CellInfo(col, position.row), isWhite) }) {
                res.add(CastleMove(this, boardLongRook, isWhite, false))
            }
        }
        if (canCastleShort && boardKing is King && boardShortRook is Rook && !board.isHit(position, isWhite)) {
            if ((position.col + 1 until rookShortPos.col).all { col -> board.board[position.row][col] == null && !board.isHit(CellInfo(col, position.row), isWhite) }) {
                res.add(CastleMove(this, boardShortRook, isWhite, true))
            }
        }
        return res
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val deltaRow = position.row - cellInfo.row
        val deltaCol = position.col - cellInfo.col
        return max(abs(deltaCol), abs(deltaRow)) == 1
    }

    override fun toString(): String {
        return if (isWhite) {
            "K"
        } else {
            "k"
        }
    }
}