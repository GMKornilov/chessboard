package com.github.fertilewaif.chessboard.model

class Pawn(isWhite: Boolean) : Piece(isWhite) {
    override fun getMoves(board: Board): List<CellInfo> {
        val forwardRow = if (isWhite) {
            position.row + 1
        } else {
            position.row - 1
        }

        val res = mutableListOf<CellInfo>()

        if (board.board[forwardRow][position.col - 'a'] == null) {
            res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a', true))
        }
        if (position.col != 'h' && board.board[forwardRow][position.col - 'a' + 1]?.isWhite != isWhite) {
            res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a' + 1, true))
        }
        if (position.col != 'a' && board.board[forwardRow][position.col - 'a' - 1]?.isWhite != isWhite) {
            res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a' - 1, true))
        }

        if (board.canEnPassant) {
            if (board.enPassantCellInfo.col.dec() == position.col) {
                res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a' + 1, true))
            }
            if (board.enPassantCellInfo.col.inc() == position.col) {
                res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a' - 1, true))
            }
        }

        if (isWhite && position.row == 1) {
            res.add(CellInfo.fromIndexes(position.row + 2, position.col - 'a', true))
        }
        if (!isWhite && position.row == 6) {
            res.add(CellInfo.fromIndexes(position.row - 2, position.col - 'a', true))
        }
        return res
    }
}