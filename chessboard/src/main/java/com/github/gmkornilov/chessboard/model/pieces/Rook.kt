package com.github.gmkornilov.chessboard.model.pieces

import com.github.gmkornilov.chessboard.R
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.moves.*
import kotlin.math.abs
import kotlin.math.sign

internal class Rook(isWhite: Boolean) : Piece(isWhite) {
    override var drawableRes = if (isWhite) {
        R.drawable.ic_wr
    } else {
        R.drawable.ic_br
    }

    override fun getMoves(board: Board): List<Move> {
        val res = mutableListOf<Move>()
        for (col in position.col - 1 downTo 0) {
            val piece = board.board[position.row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(RookCaptureMove(this, piece, position, CellInfo(col, position.row)))
                }
                break
            }
            res.add(RookTransitionMove(this, position, CellInfo(col, position.row)))
        }
        for (col in position.col + 1 until Board.BOARD_SIZE) {
            val piece = board.board[position.row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(RookCaptureMove(this, piece, position, CellInfo(col, position.row)))
                }
                break
            }
            res.add(RookTransitionMove(this, position, CellInfo(col, position.row)))
        }
        for (row in position.row - 1 downTo 0) {
            val piece = board.board[row][position.col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(RookCaptureMove(this, piece, position, CellInfo(position.col, row)))
                }
                break
            }
            res.add(RookTransitionMove(this, position, CellInfo(position.col, row)))
        }
        for (row in position.row + 1..7) {
            val piece = board.board[row][position.col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(RookCaptureMove(this, piece, position, CellInfo(position.col, row)))
                }
                break
            }
            res.add(RookTransitionMove(this, position, CellInfo(position.col, row)))
        }
        return res
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val rowDiff = cellInfo.row - position.row
        val colDiff = cellInfo.col - position.col
        if (abs(rowDiff) != 0 && abs(colDiff) != 0 || (abs(colDiff) == 0 && abs(rowDiff) == 0)) {
            return false
        }
        val deltaRow = sign(rowDiff.toDouble()).toInt()
        val deltaCol = sign(colDiff.toDouble()).toInt()

        var curRow = position.row + deltaRow
        var curCol = position.col + deltaCol
        while (curRow != cellInfo.row || curCol != cellInfo.col) {
            if (board.board[curRow][curCol] != null) {
                return false
            }
            curRow += deltaRow
            curCol += deltaCol
        }
        return true
    }

    override fun toString(): String {
        return if(isWhite) {
            "R"
        } else {
            "r"
        }
    }
}