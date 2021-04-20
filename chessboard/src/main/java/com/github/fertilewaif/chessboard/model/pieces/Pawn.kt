package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.R
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.moves.*
import kotlin.math.abs

class Pawn(isWhite: Boolean) : Piece(isWhite) {
    override var drawableRes = if (isWhite) {
        R.drawable.ic_wp
    } else {
        R.drawable.ic_bp
    }

    override fun getMoves(board: Board): List<Move> {
        val forwardRow = if (isWhite) {
            position.row + 1
        } else {
            position.row - 1
        }

        val endRow = if (isWhite) 7 else 0
        val startRow = if (isWhite) 1 else 6
        val deltaStart = if(isWhite) 2 else -2

        val res = mutableListOf<Move>()

        if (board.board[forwardRow][position.col] == null) {
            val to = CellInfo(position.col, forwardRow)
            if (forwardRow == endRow) {
                res.add(PromotionMove(this, null, position, to, Queen(isWhite)))
                res.add(PromotionMove(this, null, position, to, Rook(isWhite)))
                res.add(PromotionMove(this, null, position, to, Bishop(isWhite)))
                res.add(PromotionMove(this, null, position, to, Knight(isWhite)))
            }
            else {
                res.add(TransitionMove(this, position, to))
            }
        }
        if (position.col != Board.BOARD_SIZE - 1 && board.board[forwardRow][position.col + 1]?.isWhite != isWhite) {
            // board.board[forwardRow][position.col + 1] is not null here, because then it wont pass
            // condition above
            val capturedPiece = board.board[forwardRow][position.col + 1]
            val to = CellInfo(position.col + 1, forwardRow)
            if (forwardRow == endRow) {
                res.add(PromotionMove(this, capturedPiece, position, to, Queen(isWhite)))
                res.add(PromotionMove(this, capturedPiece, position, to, Rook(isWhite)))
                res.add(PromotionMove(this, capturedPiece, position, to, Bishop(isWhite)))
                res.add(PromotionMove(this, capturedPiece, position, to, Knight(isWhite)))
            } else if (capturedPiece != null) {
                res.add(CaptureMove(this, capturedPiece, position, to))
            }
        }
        if (position.col != 0 && board.board[forwardRow][position.col - 1]?.isWhite != isWhite) {
            val capturedPiece = board.board[forwardRow][position.col - 1]
            val to = CellInfo(position.col - 1, forwardRow)
            if (forwardRow == endRow) {
                res.add(PromotionMove(this, capturedPiece, position, to, Queen(isWhite)))
                res.add(PromotionMove(this, capturedPiece, position, to, Rook(isWhite)))
                res.add(PromotionMove(this, capturedPiece, position, to, Bishop(isWhite)))
                res.add(PromotionMove(this, capturedPiece, position, to, Knight(isWhite)))
            } else if (capturedPiece != null){
                res.add(CaptureMove(this, capturedPiece, position, to))
            }
        }

        if (board.canEnPassant) {
            val enPassantPawn = board.board[board.enPassantCellInfo.row][board.enPassantCellInfo.col] as Pawn
            if (board.enPassantCellInfo.col - 1 == position.col) {
                res.add(
                        EnPassantMove(this,
                                enPassantPawn,
                                position,
                                CellInfo(position.col + 1, forwardRow),
                                board.enPassantCellInfo
                        )
                )
            }
            if (board.enPassantCellInfo.col + 1 == position.col) {
                res.add(
                        EnPassantMove(this,
                                enPassantPawn,
                                position,
                                CellInfo(position.col - 1, forwardRow),
                                board.enPassantCellInfo
                        )
                )
            }
        }

        if (position.row == startRow && board.board[position.row + deltaStart][position.col] == null) {
            res.add(TransitionMove(this, position, CellInfo(position.col, position.row + deltaStart)))
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