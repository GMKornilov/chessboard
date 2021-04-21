package com.github.gmkornilov.chessboard.model.pieces

import androidx.annotation.DrawableRes
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.moves.Move

abstract class Piece(val isWhite: Boolean) {
    @get:DrawableRes abstract var drawableRes: Int

    open var position = CellInfo(0, 0)

    /**
     * returns list of cells, where piece can legally move
     */
    fun getLegalMoves(board: Board): List<Move> {
        val moves = getMoves(board)
        val res = mutableListOf<Move>()
        for (move in moves) {
            move.move(board)
            val kingPos = if (isWhite) {
                board.whiteKingPosition
            } else {
                board.blackKingPosition
            }
            if (!board.isHit(kingPos, isWhite)) {
                res.add(move)
            }
            move.undo(board)
        }
        return res
    }

    /**
     * returns list of cells, where piece can move(including illegal moves)
     */
    abstract fun getMoves(board: Board): List<Move>

    /**
     * checks if piece can hit given cell
     */
    abstract fun canHit(cellInfo: CellInfo, board: Board): Boolean
}