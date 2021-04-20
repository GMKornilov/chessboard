package com.github.fertilewaif.chessboard.model.moves

import com.github.fertilewaif.chessboard.model.AnimationInfo
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.pieces.Piece

open class CaptureMove(val capturePiece: Piece, val killedPiece: Piece, val from: CellInfo, val to: CellInfo) : Move {
    override fun move(board: Board) {
        board.removePiece(from)
        board.removePiece(to)
        board.addPiece(capturePiece, to)
        capturePiece.position = to
        killedPiece.position = CellInfo(-1, -1)
    }

    override fun undo(board: Board) {
        board.removePiece(to)
        board.removePiece(from)
        board.addPiece(capturePiece, from)
        board.addPiece(killedPiece, to)
        capturePiece.position = from
        killedPiece.position = to
    }

    override fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo> {
        return listOf(
                AnimationInfo(killedPiece.drawableRes,
                        CellInfo.toAnimationIndexes(to, isWhite),
                        null),
                AnimationInfo(capturePiece.drawableRes,
                        CellInfo.toAnimationIndexes(from, isWhite),
                        CellInfo.toAnimationIndexes(to, isWhite))
        )
    }

    override fun getDisplayedCell(isWhite: Boolean): CellInfo {
        return CellInfo.toAnimationIndexes(to, isWhite)
    }

}