package com.github.fertilewaif.chessboard.model

import kotlin.math.abs

class Pawn(isWhite: Boolean) : Piece(isWhite) {
    override fun getLegalMoves(board: Board): List<CellInfo> {
        // TODO: add pin
        val pinnerCell = board.isPinned(position, isWhite)
        if (pinnerCell == null) {
            return getMoves(board)
        }
        val forwardRow = if (isWhite) {
            position.row + 1
        } else {
            position.row - 1
        }
        if (pinnerCell.col == pinnerCell.col) {
            val res = mutableListOf<CellInfo>()
            if (board.board[forwardRow][position.col - 'a'] == null) {
                res.add(CellInfo(position.col, forwardRow))
            }
            if (isWhite && position.row == 1 && board.board[position.row + 2][position.col - 'a'] == null) {
                res.add(CellInfo.fromIndexes(position.row + 2, position.col - 'a', true))
            }
            if (!isWhite && position.row == 6 && board.board[position.row - 2][position.col - 'a'] == null) {
                res.add(CellInfo.fromIndexes(position.row - 2, position.col - 'a', true))
            }
            return res
        }
        if (pinnerCell.row != forwardRow || abs((pinnerCell.col - position.col)) != 1) {
            return listOf()
        }
        return listOf(pinnerCell)
    }

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
            if (board.enPassantCellInfo.col - 1 == position.col) {
                res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a' + 1, true))
            }
            if (board.enPassantCellInfo.col + 1 == position.col) {
                res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a' - 1, true))
            }
        }

        if (isWhite && position.row == 1 && board.board[position.row + 2][position.col - 'a'] == null) {
            res.add(CellInfo.fromIndexes(position.row + 2, position.col - 'a', true))
        }
        if (!isWhite && position.row == 6 && board.board[position.row - 2][position.col - 'a'] == null) {
            res.add(CellInfo.fromIndexes(position.row - 2, position.col - 'a', true))
        }
        return res
    }

    override fun getHitMoves(board: Board): List<CellInfo> {
        val forwardRow = if (isWhite) {
            position.row + 1
        } else {
            position.row - 1
        }
        val res = mutableListOf<CellInfo>()
        if (position.col != 'h' && board.board[forwardRow][position.col - 'a' + 1]?.isWhite != isWhite) {
            res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a' + 1, true))
        }
        if (position.col != 'a' && board.board[forwardRow][position.col - 'a' - 1]?.isWhite != isWhite) {
            res.add(CellInfo.fromIndexes(forwardRow, position.col - 'a' - 1, true))
        }
        return res
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        if (abs(position.col - cellInfo.col) != 1) {
            return false
        }
        return if (isWhite) {
            cellInfo.row - position.row == 1
        } else {
            position.row - cellInfo.row == 1
        }
    }

    override fun toString(): String {
        return if (isWhite) {
            "P"
        } else {
            "p"
        }
    }
}