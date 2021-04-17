package com.github.fertilewaif.chessboard.model.moves

import com.github.fertilewaif.chessboard.model.AnimationInfo
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.pieces.Pawn

class EnPassantMove(val capturePawn: Pawn, val killedPawn: Pawn, val from: CellInfo, val to: CellInfo, val enPassantCell: CellInfo) : Move {
    override fun move(board: Board) {
        board.removePiece(enPassantCell)
        board.removePiece(from)
        board.addPiece(capturePawn, to)

        capturePawn.position = to
    }

    override fun undo(board: Board) {
        board.removePiece(to)
        board.addPiece(capturePawn, from)
        board.addPiece(killedPawn, enPassantCell)

        capturePawn.position = from
        killedPawn.position = enPassantCell
    }

    override fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo> {
        return listOf(
                AnimationInfo(capturePawn.drawableRes, CellInfo.toAnimationIndexes(from, isWhite), CellInfo.toAnimationIndexes(to, isWhite)),
                AnimationInfo(killedPawn.drawableRes, CellInfo.toAnimationIndexes(enPassantCell, isWhite), null)
        )
    }

    override fun getDisplayedCell(isWhite: Boolean): CellInfo {
        return CellInfo.toAnimationIndexes(to, isWhite)
    }
}