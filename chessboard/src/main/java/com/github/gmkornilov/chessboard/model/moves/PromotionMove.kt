package com.github.gmkornilov.chessboard.model.moves

import com.github.gmkornilov.chessboard.model.AnimationInfo
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.pieces.Pawn
import com.github.gmkornilov.chessboard.model.pieces.Piece
import java.util.*

internal class PromotionMove(val pawn: Pawn, val killedPiece: Piece?, val from: CellInfo, val to: CellInfo, val promotedPiece: Piece) : Move {
    override fun move(board: Board) {
        board.removePiece(from)
        if (killedPiece != null) {
            board.removePiece(to)
        }
        board.addPiece(promotedPiece, to)
        promotedPiece.position = to
    }

    override fun undo(board: Board) {
        board.removePiece(to)
        if (killedPiece != null) {
            board.addPiece(killedPiece, to)
        }
        board.addPiece(pawn, from)
        pawn.position = from
    }

    override fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo> {
        val plusList = if (killedPiece != null) {
            listOf(
                AnimationInfo(
                    killedPiece,
                    CellInfo.toAnimationIndexes(to, isWhite),
                    null
                )
            )
        } else {
            emptyList()
        }
        return listOf(
            AnimationInfo(
                pawn,
                CellInfo.toAnimationIndexes(from, isWhite),
                CellInfo.toAnimationIndexes(to, isWhite)
            ),
            AnimationInfo(
                pawn,
                CellInfo.toAnimationIndexes(to, isWhite),
                null
            ),
            AnimationInfo(
                promotedPiece,
                null,
                CellInfo.toAnimationIndexes(to, isWhite)
            )
        ) + plusList
    }

    override fun getMoveCell(): CellInfo {
        return to
    }

    override fun getMoveNotation(board: Board): String {
        var res = ""
        if (killedPiece != null) {
            res += 'a' + from.col + "x"
        }
        res += to.notation + "=" + promotedPiece.toString().toUpperCase(Locale.ROOT)
        return res
    }
}