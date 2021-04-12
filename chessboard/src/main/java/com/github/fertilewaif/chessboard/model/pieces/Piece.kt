package com.github.fertilewaif.chessboard.model.pieces

import androidx.annotation.DrawableRes
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.moves.Move

abstract class Piece(val isWhite: Boolean) {
    @get:DrawableRes abstract var drawableRes: Int

    open var position = CellInfo(0, 0)

    /**
     * returns list of cells, where piece can legally move
     */
    abstract fun getLegalMoves(board: Board): List<Move>

    /**
     * returns list of cells, where piece can move(including illegal moves)
     */
    abstract fun getMoves(board: Board): List<Move>

    /**
     * checks if piece can hit given cell
     */
    abstract fun canHit(cellInfo: CellInfo, board: Board): Boolean
}