package com.github.fertilewaif.chessboard.model.moves

import com.github.fertilewaif.chessboard.model.AnimationInfo
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.pieces.Piece

class TransitionMove(val piece: Piece, val from: CellInfo, val to: CellInfo) : Move {
    override fun move(board: Board) {
        board.removePiece(from)
        board.addPiece(piece, to)
        piece.position = to
    }

    override fun undo(board: Board) {
        board.removePiece(to)
        board.addPiece(piece, from)
        piece.position = from
    }

    override fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo> {
        return listOf(
                AnimationInfo(piece.drawableRes,
                        CellInfo.toAnimationIndexes(from, isWhite),
                        CellInfo.toAnimationIndexes(to, isWhite))
        )
    }

    override fun getDisplayedCell(isWhite: Boolean): CellInfo {
        return CellInfo.toAnimationIndexes(to, isWhite)
    }
}