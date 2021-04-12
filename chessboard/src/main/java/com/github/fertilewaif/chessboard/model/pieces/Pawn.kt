package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.R
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.moves.CaptureMove
import com.github.fertilewaif.chessboard.model.moves.EnPassantMove
import com.github.fertilewaif.chessboard.model.moves.Move
import com.github.fertilewaif.chessboard.model.moves.TransitionMove
import kotlin.math.abs

class Pawn(isWhite: Boolean) : Piece(isWhite) {
    override var drawableRes = if (isWhite) {
        R.drawable.ic_wp
    } else {
        R.drawable.ic_bp
    }

    override fun getLegalMoves(board: Board): List<Move> {
        // TODO: add pin
        return getMoves(board)
    }

    override fun getMoves(board: Board): List<Move> {
        val forwardRow = if (isWhite) {
            position.row + 1
        } else {
            position.row - 1
        }

        val res = mutableListOf<Move>()

        if (board.board[forwardRow][position.col] == null) {
            res.add(TransitionMove(this, position, CellInfo(forwardRow, position.col)))
        }
        if (position.col != Board.BOARD_SIZE - 1 && board.board[forwardRow][position.col + 1]?.isWhite != isWhite) {
            // board.board[forwardRow][position.col + 1] is not null here, because then it wont pass if
            res.add(CaptureMove(this, board.board[forwardRow][position.col + 1]!!, position, CellInfo(forwardRow, position.col + 1)))
        }
        if (position.col != 0 && board.board[forwardRow][position.col - 1]?.isWhite != isWhite) {
            res.add(CaptureMove(this, board.board[forwardRow][position.col - 1]!!, position, CellInfo(forwardRow, position.col - 1)))
        }

        if (board.canEnPassant) {
            val enPassantPawn = board.board[board.enPassantCellInfo.row][board.enPassantCellInfo.col] as Pawn
            if (board.enPassantCellInfo.col - 1 == position.col) {
                res.add(
                        EnPassantMove(this,
                                enPassantPawn,
                                position,
                                CellInfo(forwardRow, position.col + 1),
                                board.enPassantCellInfo
                        )
                )
            }
            if (board.enPassantCellInfo.col + 1 == position.col) {
                res.add(
                        EnPassantMove(this,
                                enPassantPawn,
                                position,
                                CellInfo(forwardRow, position.col - 1),
                                board.enPassantCellInfo
                        )
                )
            }
        }

        if (isWhite && position.row == 1 && board.board[position.row + 2][position.col] == null) {
            res.add(TransitionMove(this, position, CellInfo(position.row + 2, position.col)))
        }
        if (!isWhite && position.row == 6 && board.board[position.row - 2][position.col] == null) {
            res.add(TransitionMove(this, position, CellInfo(position.row - 2, position.col)))
        }
        return res
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        if (abs(position.col - cellInfo.col) != 1) {
            return false
        }
        return if (isWhite) {
            cellInfo.row - position.row == 1
        } else {
            position.row - cellInfo.row == 1
        }
    }

    override fun toString(): String {
        return if (isWhite) {
            "P"
        } else {
            "p"
        }
    }
}