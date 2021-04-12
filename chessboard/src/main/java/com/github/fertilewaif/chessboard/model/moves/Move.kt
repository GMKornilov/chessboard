package com.github.fertilewaif.chessboard.model.moves

import com.github.fertilewaif.chessboard.model.AnimationInfo
import com.github.fertilewaif.chessboard.model.Board

interface Move {
    fun move(board: Board)

    fun undo(board: Board)

    fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo>
}