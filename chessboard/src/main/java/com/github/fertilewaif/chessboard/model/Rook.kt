package com.github.fertilewaif.chessboard.model

class Rook(isWhite: Boolean) : Piece(isWhite) {
    override fun getMoves(board: Board): List<CellInfo> {
        val res = mutableListOf<CellInfo>()
        for (col in position.col - 1 downTo 'a') {
            val piece = board.board[position.row][col - 'a']
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CellInfo.fromIndexes(position.row, col - 'a', true))
                }
                break
            }
            res.add(CellInfo.fromIndexes(position.row, col - 'a', true))
        }
        for (col in position.col + 1..'h') {
            val piece = board.board[position.row][col - 'a']
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CellInfo.fromIndexes(position.row, col - 'a', true))
                }
                break
            }
            res.add(CellInfo.fromIndexes(position.row, col - 'a', true))
        }
        for (row in position.row downTo 0) {
            val piece = board.board[row][position.col - 'a']
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CellInfo.fromIndexes(row, position.col - 'a', true))
                }
                break
            }
            res.add(CellInfo.fromIndexes(row, position.col - 'a', true))
        }
        for (row in position.row + 1..7) {
            val piece = board.board[row][position.col - 'a']
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CellInfo.fromIndexes(row, position.col - 'a', true))
                }
                break
            }
            res.add(CellInfo.fromIndexes(row, position.col - 'a', true))
        }
        return res
    }
}