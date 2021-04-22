package com.github.gmkornilov.chessboard.model.moves

import com.github.gmkornilov.chessboard.model.AnimationInfo
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.pieces.King
import com.github.gmkornilov.chessboard.model.pieces.Pawn
import com.github.gmkornilov.chessboard.model.pieces.Piece
import java.util.*

open class TransitionMove(val piece: Piece, val from: CellInfo, val to: CellInfo) : Move {
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
            AnimationInfo(
                piece,
                CellInfo.toAnimationIndexes(from, isWhite),
                CellInfo.toAnimationIndexes(to, isWhite)
            )
        )
    }

    override fun getDisplayedCell(isWhite: Boolean): CellInfo {
        return CellInfo.toAnimationIndexes(to, isWhite)
    }

    override fun getMoveNotation(board: Board): String {
        if (piece !is Pawn) {
            return piece.toString().toUpperCase(Locale.ROOT) + board.getExtraNotation(piece, to)
        } else {
            return to.notation
        }
    }
}