package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.R
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.moves.CaptureMove
import com.github.fertilewaif.chessboard.model.moves.Move
import com.github.fertilewaif.chessboard.model.moves.TransitionMove
import kotlin.math.abs
import kotlin.math.sign

class Rook(isWhite: Boolean) : Piece(isWhite) {
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
                    res.add(CaptureMove(this, piece, position, CellInfo(position.row, col)))
                }
                break
            }
            res.add(TransitionMove(this, position, CellInfo(position.row, col)))
        }
        for (col in position.col + 1 until Board.BOARD_SIZE) {
            val piece = board.board[position.row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CaptureMove(this, piece, position, CellInfo(position.row, col)))
                }
                break
            }
            res.add(TransitionMove(this, position, CellInfo(position.row, col)))
        }
        for (row in position.row downTo 0) {
            val piece = board.board[row][position.col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CaptureMove(this, piece, position, CellInfo(row, position.col)))
                }
                break
            }
            res.add(TransitionMove(this, position, CellInfo(row, position.col)))
        }
        for (row in position.row + 1..7) {
            val piece = board.board[row][position.col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CaptureMove(this, piece, position, CellInfo(row, position.col)))
                }
                break
            }
            res.add(TransitionMove(this, position, CellInfo(row, position.col)))
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
        return if(isWhite) {
            "R"
        } else {
            "r"
        }
    }
}