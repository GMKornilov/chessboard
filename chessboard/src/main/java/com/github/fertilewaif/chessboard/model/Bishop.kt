package com.github.fertilewaif.chessboard.model

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

class Bishop(isWhite: Boolean) : Piece(isWhite) {
    override fun getLegalMoves(board: Board): List<CellInfo> {
        // TODO: add pinning
        val moves = getMoves(board)
        val pinnerCell = board.isPinned(position, isWhite)
        if (pinnerCell == null) {
            return moves
        }
        val rowDiff = abs(pinnerCell.row - position.row)
        val colDiff = abs(pinnerCell.col - position.col)

        val signRow = sign((pinnerCell.row - position.row).toDouble()).toInt()
        val signCol = sign((pinnerCell.col - position.col).toDouble()).toInt()

        if (signRow == 0 || signCol == 0) {
            return listOf()
        }

        val res = mutableListOf<CellInfo>()

        for (move in moves) {
            val moveRowDiff = abs(move.row - position.row)
            val moveColDiff = abs(move.col - position.col)

            val moveRowSign = sign((move.row - position.row).toDouble()).toInt()
            val moveColSign = sign((move.col - position.col).toDouble()).toInt()

            if (moveRowSign == signRow && moveColSign == signCol && moveRowDiff <= rowDiff && moveColDiff <= colDiff) {
                res.add(move)
            }
        }
        return res
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

    override fun toString(): String {
        return if (isWhite) {
            "B"
        } else {
            "b"
        }
    }
}