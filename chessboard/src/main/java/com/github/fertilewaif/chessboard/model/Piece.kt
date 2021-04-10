package com.github.fertilewaif.chessboard.model

import androidx.annotation.DrawableRes

abstract class Piece(val isWhite: Boolean) {
    @DrawableRes var drawableRes: Int = 0

    open var position = CellInfo(0, 0)

    /**
     * returns list of cells, where piece can legally move
     */
    abstract fun getLegalMoves(board: Board): List<CellInfo>

    /**
     * returns list of cells, where piece can move(including illegal moves)
     */
    abstract fun getMoves(board: Board): List<CellInfo>

    /**
     * returns list of cells, where piece can hit (legally or illegally)
     */
    abstract fun getHitMoves(board: Board): List<CellInfo>

    /**
     * checks if piece can hit given cell
     */
    abstract fun canHit(cellInfo: CellInfo, board: Board): Boolean
}