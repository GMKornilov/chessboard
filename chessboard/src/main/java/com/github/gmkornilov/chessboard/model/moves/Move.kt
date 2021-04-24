package com.github.gmkornilov.chessboard.model.moves

import com.github.gmkornilov.chessboard.model.AnimationInfo
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo

internal interface Move {
    fun move(board: Board)

    fun undo(board: Board)

    fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo>

    fun getUndoAnimationsInfo(isWhite: Boolean): List<AnimationInfo> {
        val animationInfos = getAnimationsInfo(isWhite)
        val res = mutableListOf<AnimationInfo>()

        for (animationInfo in animationInfos.reversed()) {
            res.add(AnimationInfo(animationInfo.piece, animationInfo.to, animationInfo.from))
        }
        return res
    }

    fun getMoveCell(): CellInfo

    fun getDisplayedCell(isWhite: Boolean): CellInfo {
        return CellInfo.toAnimationIndexes(getMoveCell(), isWhite)
    }

    fun getMoveNotation(board: Board): String
}