package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.R
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.moves.CaptureMove
import com.github.fertilewaif.chessboard.model.moves.Move
import com.github.fertilewaif.chessboard.model.moves.TransitionMove
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

class Bishop(isWhite: Boolean) : Piece(isWhite) {
    override var drawableRes = if (isWhite) {
        R.drawable.ic_wb
    } else {
        R.drawable.ic_bb
    }

    override fun getMoves(board: Board): List<Move> {
        // yeah, I don't like that copy paste too
        val res = mutableListOf<Move>()
        for (deltaUpRight in 1..min(7 - position.row, 7 - position.col)) {
            val row = position.row + deltaUpRight
            val col = position.col + deltaUpRight
            val piece = board.board[row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CaptureMove(this, piece, position, CellInfo(col, row)))
                }
                break
            }
            res.add(TransitionMove(this, position, CellInfo(col, row)))
        }
        for (deltaDownRight in 1..min(position.row, 7 - position.col)) {
            val row = position.row - deltaDownRight
            val col = position.col + deltaDownRight
            val piece = board.board[row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CaptureMove(this, piece, position, CellInfo(col, row)))
                }
                break
            }
            res.add(TransitionMove(this, position, CellInfo(col, row)))
        }
        for (deltaUpLeft in 1..min(7 - position.row, position.col)) {
            val row = position.row + deltaUpLeft
            val col = position.col - deltaUpLeft
            val piece = board.board[row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CaptureMove(this, piece, position, CellInfo(col, row)))
                }
                break
            }
            res.add(TransitionMove(this, position, CellInfo(col, row)))
        }
        for (deltaDownLeft in 1..min(position.row, position.col)) {
            val row = position.row - deltaDownLeft
            val col = position.col - deltaDownLeft
            val piece = board.board[row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CaptureMove(this, piece, position, CellInfo(col, row)))
                }
                break
            }
            res.add(TransitionMove(this, position, CellInfo(col, row)))
        }
        return res
    }


    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val rowDiff = cellInfo.row - position.row
        val colDiff = cellInfo.col - position.col
        if (abs(rowDiff) == 0 || abs(colDiff) == 0 || abs(colDiff) != abs(rowDiff)) {
            return false
        }
        val deltaRow = sign(rowDiff.toDouble()).toInt()
        val deltaCol = sign(colDiff.toDouble()).toInt()

        var curRow = position.row + deltaRow
        var curCol = position.col + deltaCol
        while (curRow != cellInfo.row && curCol != cellInfo.col) {
            if (board.board[curRow][curCol] != null) {
                return false
            }
            curRow += deltaRow
            curCol += deltaCol
        }
        return true
    }

    override fun toString(): String {
        return if (isWhite) {
            "B"
        } else {
            "b"
        }
    }
}