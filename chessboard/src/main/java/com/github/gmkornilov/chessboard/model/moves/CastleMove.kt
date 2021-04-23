package com.github.gmkornilov.chessboard.model.moves

import com.github.gmkornilov.chessboard.model.AnimationInfo
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.pieces.King
import com.github.gmkornilov.chessboard.model.pieces.Rook

class CastleMove(val king: King, val rook: Rook, val isWhite: Boolean, val isShort: Boolean) : Move {
    companion object {
        // Kings uncastled positions CellInfo
        val e1CellInfo = CellInfo(4, 0)
        val e8CellInfo = CellInfo(4, 7)

        // White rooks initial positions
        val a1CellInfo = CellInfo(0, 0)
        val h1CellInfo = CellInfo(7, 0)

        // Black rooks initial positions
        val a8CellInfo = CellInfo(0, 7)
        val h8CellInfo = CellInfo(7, 7)
    }

    val kingPosition = if (isWhite) {
        e1CellInfo
    } else {
        e8CellInfo
    }

    val rookPosition = if (isWhite) {
        if (isShort) {
            h1CellInfo
        } else {
            a1CellInfo
        }
    } else {
        if (isShort) {
            h8CellInfo
        } else {
            a8CellInfo
        }
    }

    val newKingPosition = if (isShort) {
        CellInfo(kingPosition.col + 2, kingPosition.row)
    } else {
        CellInfo(kingPosition.col - 2, kingPosition.row)
    }

    val newRookPosition = if (isShort) {
        CellInfo(rookPosition.col - 2, rookPosition.row)
    } else {
        CellInfo(rookPosition.col + 3, rookPosition.row)
    }

    override fun move(board: Board) {
        board.removePiece(kingPosition)
        board.removePiece(rookPosition)

        board.addPiece(king, newKingPosition)
        board.addPiece(rook, newRookPosition)

        king.position = newKingPosition
        rook.position = newRookPosition
    }

    override fun undo(board: Board) {
        board.removePiece(newKingPosition)
        board.removePiece(newRookPosition)

        board.addPiece(king, kingPosition)
        board.addPiece(rook, rookPosition)

        king.position = kingPosition
        rook.position = rookPosition
    }

    override fun getAnimationsInfo(isWhite: Boolean): List<AnimationInfo> {
        return listOf(
            AnimationInfo(
                king,
                CellInfo.toAnimationIndexes(kingPosition, isWhite),
                CellInfo.toAnimationIndexes(newKingPosition, isWhite),
            ),
            AnimationInfo(
                rook,
                CellInfo.toAnimationIndexes(rookPosition, isWhite),
                CellInfo.toAnimationIndexes(newRookPosition, isWhite)
            ),
        )
    }

    override fun getMoveCell(): CellInfo {
        return newKingPosition
    }

    override fun getMoveNotation(board: Board): String {
        return if (isShort) "O-O" else "O-O-O"
    }

}