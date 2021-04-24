package com.github.gmkornilov.chessboard.model.moves

import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.pieces.King
import com.github.gmkornilov.chessboard.model.pieces.Piece

class KingCaptureMove(capturePiece: King, killedPiece: Piece, from: CellInfo, to: CellInfo) : CaptureMove(capturePiece, killedPiece, from, to) {
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

        if (capturePiece.isWhite) {
            board.canWhiteCastleLong = false
            board.canWhiteCastleShort = false

            board.whiteKingPosition = to
        } else {
            board.canBlackCastleShort = false
            board.canBlackCastleLong = false

            board.blackKingPosition = to
        }
    }

    override fun undo(board: Board) {
        super.undo(board)

        board.canWhiteCastleShort = oldCanWhiteCastleShort
        board.canWhiteCastleLong = oldCanWhiteCastleLong
        board.canBlackCastleShort = oldCanBlackCastleShort
        board.canBlackCastleLong = oldCanBlackCastleLong

        if (capturePiece.isWhite) {
            board.whiteKingPosition = from
        } else {
            board.blackKingPosition = from
        }
    }
}