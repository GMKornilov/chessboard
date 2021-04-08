package com.github.fertilewaif.chessboard.model

import kotlin.math.min

class Bishop(isWhite: Boolean) : Piece(isWhite) {
    override fun getMoves(board: Board): List<CellInfo> {
        // yeah, I don't like that copy paste too
        val res = mutableListOf<CellInfo>()
        for (deltaUpRight in 1..min(7 - position.row, 'h' - position.col)) {
            val row = position.row + deltaUpRight
            val col = (position.col - 'a') + deltaUpRight
            val piece = board.board[row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CellInfo.fromIndexes(row, col , true))
                }
                break
            }
            res.add(CellInfo.fromIndexes(row, col, true))
        }
        for (deltaDownRight in 1..min(position.row, 'h' - position.col)) {
            val row = position.row + deltaDownRight
            val col = (position.col - 'a') + deltaDownRight
            val piece = board.board[row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CellInfo.fromIndexes(row, col , true))
                }
                break
            }
            res.add(CellInfo.fromIndexes(row, col, true))
        }
        for (deltaUpLeft in 1..min(7 - position.row, position.col - 'a')) {
            val row = position.row + deltaUpLeft
            val col = (position.col - 'a') + deltaUpLeft
            val piece = board.board[row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CellInfo.fromIndexes(row, col , true))
                }
                break
            }
            res.add(CellInfo.fromIndexes(row, col, true))
        }
        for (deltaDownLeft in 1..min(position.row, position.col - 'a')) {
            val row = position.row + deltaDownLeft
            val col = (position.col - 'a') + deltaDownLeft
            val piece = board.board[row][col]
            if (piece != null) {
                if (piece.isWhite != isWhite && piece !is King) {
                    res.add(CellInfo.fromIndexes(row, col , true))
                }
                break
            }
            res.add(CellInfo.fromIndexes(row, col, true))
        }
        return res
    }
}