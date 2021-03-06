package com.github.gmkornilov.chessboard.model.moves

import com.github.gmkornilov.chessboard.model.AnimationInfo
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.pieces.Pawn
import com.github.gmkornilov.chessboard.model.pieces.Piece
import java.util.*

internal open class CaptureMove(val capturePiece: Piece, val killedPiece: Piece, val from: CellInfo, val to: CellInfo) : Move {
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
            AnimationInfo(
                capturePiece,
                CellInfo.toAnimationIndexes(from, isWhite),
                CellInfo.toAnimationIndexes(to, isWhite)
            ),
            AnimationInfo(
                killedPiece,
                CellInfo.toAnimationIndexes(to, isWhite),
                null
            )
        )
    }

    override fun getMoveCell(): CellInfo {
        return to
    }

    override fun getMoveNotation(board: Board): String {
        var res = ""
        if (capturePiece !is Pawn) {
            res += capturePiece.toString().toUpperCase(Locale.ROOT)
            res += board.getExtraNotation(capturePiece, to)
        } else {
            res += ('a' + capturePiece.position.col).toString()
        }
        res += "x" + to.notation
        return res
    }

}