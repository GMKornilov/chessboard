package com.github.fertilewaif.chessboard.model.moves

import com.github.fertilewaif.chessboard.model.AnimationInfo
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.pieces.Pawn
import com.github.fertilewaif.chessboard.model.pieces.Piece

class PromotionMove(val pawn: Pawn, val from: CellInfo, val to: CellInfo, val promotedPiece: Piece) : Move {
    override fun move(board: Board) {
        board.removePiece(from)
        board.addPiece(promotedPiece, to)
        promotedPiece.position = to
    }

    override fun undo(board: Board) {
        board.removePiece(to)
        board.addPiece(pawn, from)
        pawn.position = from
    }

    override fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo> {
        return listOf(
                AnimationInfo(pawn.drawableRes, CellInfo.toAnimationIndexes(from, isWhite), CellInfo.toAnimationIndexes(to, isWhite)),
                AnimationInfo(pawn.drawableRes, CellInfo.toAnimationIndexes(to, isWhite), null),
                AnimationInfo(promotedPiece.drawableRes, null, CellInfo.toAnimationIndexes(to, isWhite))
        )
    }

    override fun getDisplayedCell(isWhite: Boolean): CellInfo {
        return CellInfo.toAnimationIndexes(to, isWhite)
    }
}