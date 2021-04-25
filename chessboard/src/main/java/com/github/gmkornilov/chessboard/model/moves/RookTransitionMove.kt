package com.github.gmkornilov.chessboard.model.moves

import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.pieces.Piece
import com.github.gmkornilov.chessboard.model.pieces.Rook

internal class RookTransitionMove(piece: Rook, from: CellInfo, to: CellInfo) : TransitionMove(piece, from, to) {
    var oldCanWhiteCastleShort = false
    var oldCanWhiteCastleLong = false
    var oldCanBlackCastleShort = false
    var oldCanBlackCastleLong = false

    override fun move(board: Board) {
        super.move(board)

        oldCanWhiteCastleShort = board.canWhiteCastleShort
        oldCanWhiteCastleLong = board.canWhiteCastleLong
        oldCanBlackCastleShort = board.canBlackCastleShort
        oldCanBlackCastleLong = board.canBlackCastleLong

        if (piece.isWhite) {
            if (piece.position == CastleMove.a1CellInfo && board.canWhiteCastleLong) {
                board.canWhiteCastleLong = false
            } else if (piece.position == CastleMove.h1CellInfo && board.canWhiteCastleShort) {
                board.canWhiteCastleShort = false
            }
        } else {
            if (piece.position == CastleMove.a8CellInfo && board.canBlackCastleLong) {
                board.canBlackCastleLong = false
            } else if (piece.position == CastleMove.h8CellInfo && board.canBlackCastleShort) {
                board.canBlackCastleShort = false
            }
        }
    }
}