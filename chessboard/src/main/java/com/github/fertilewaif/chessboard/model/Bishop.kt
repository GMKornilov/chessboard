package com.github.fertilewaif.chessboard.model

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

class Bishop(isWhite: Boolean) : Piece(isWhite) {
    override fun getLegalMoves(board: Board): List<CellInfo> {
        // TODO: add pinning
        return getMoves(board)
    }

    override fun getMoves(board: Board): List<CellInfo> {
        return getHitMoves(board)
    }

    override fun getHitMoves(board: Board): List<CellInfo> {
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

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val rowDiff = cellInfo.row - position.row
        val colDiff = cellInfo.col - position.col
        if (abs(rowDiff) == 0 || abs(colDiff) == 0 || abs(colDiff) != abs(rowDiff)) {
            return false
        }
        val deltaRow = sign(rowDiff.toDouble()).toInt()
        val deltaCol = sign(colDiff.toDouble()).toInt()

        var curRow = position.row + deltaRow
        var curCol = position.col - 'a' + deltaCol
        while (curRow != cellInfo.row && curCol != cellInfo.col - 'a') {
            if (board.board[curRow][curCol] != null) {
                return false
            }
            curRow += deltaRow
            curCol += deltaCol
        }
        return true
    }
}