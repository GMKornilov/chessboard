package com.github.fertilewaif.chessboard.model

import kotlin.math.abs
import kotlin.math.sign

class Rook(isWhite: Boolean) : Piece(isWhite) {
    override fun getLegalMoves(board: Board): List<CellInfo> {
        // TODO: add pin
        return getMoves(board)
    }

    override fun getMoves(board: Board): List<CellInfo> {
        return getHitMoves(board)
    }

    override fun getHitMoves(board: Board): List<CellInfo> {
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

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val rowDiff = cellInfo.row - position.row
        val colDiff = cellInfo.col - position.col
        if (abs(rowDiff) != 0 && abs(colDiff) != 0 || (abs(colDiff) == 0 && abs(rowDiff) == 0)) {
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