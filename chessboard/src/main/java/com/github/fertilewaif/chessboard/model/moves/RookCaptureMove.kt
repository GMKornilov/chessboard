package com.github.fertilewaif.chessboard.model.moves

import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.pieces.Piece
import com.github.fertilewaif.chessboard.model.pieces.Rook

class RookCaptureMove(capturePiece: Rook, killedPiece: Piece, from: CellInfo, to: CellInfo) : CaptureMove(capturePiece, killedPiece, from, to) {
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
            if (capturePiece.position == CastleMove.a1CellInfo && board.canWhiteCastleLong) {
                board.canWhiteCastleLong = false
            } else if (capturePiece.position == CastleMove.h1CellInfo && board.canWhiteCastleShort) {
                board.canBlackCastleShort = false
            }
        } else {
            if (capturePiece.position == CastleMove.a8CellInfo && board.canBlackCastleLong) {
                board.canBlackCastleLong = false
            } else if (capturePiece.position == CastleMove.h8CellInfo && board.canBlackCastleShort) {
                board.canBlackCastleShort = false
            }
        }
    }

    override fun undo(board: Board) {
        super.undo(board)

        board.canWhiteCastleShort = oldCanWhiteCastleShort
        board.canWhiteCastleLong = oldCanWhiteCastleLong
        board.canBlackCastleShort = oldCanBlackCastleShort
        board.canBlackCastleLong = oldCanBlackCastleLong
    }
}