package com.github.fertilewaif.chessboard.model

import androidx.annotation.DrawableRes

abstract class Piece(val isWhite: Boolean) {
    @DrawableRes var drawableRes: Int = 0

    open var position = CellInfo('a', 1)

    abstract fun getMoves(board: Board): List<CellInfo>
}