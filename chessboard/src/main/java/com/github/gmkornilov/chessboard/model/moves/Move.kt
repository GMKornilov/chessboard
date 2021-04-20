package com.github.gmkornilov.chessboard.model.moves

import com.github.gmkornilov.chessboard.model.AnimationInfo
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo

interface Move {
    fun move(board: Board)

    fun undo(board: Board)

    fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo>

    fun getDisplayedCell(isWhite: Boolean): CellInfo
}