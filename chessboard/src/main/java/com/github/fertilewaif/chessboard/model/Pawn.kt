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
            if (board.board[forwardRow][position.col] == null) {
                res.add(CellInfo(position.col, forwardRow))
            }
            if (isWhite && position.row == 1 && board.board[position.row + 2][position.col] == null) {
                res.add(CellInfo(position.row + 2, position.col))
            }
            if (!isWhite && position.row == 6 && board.board[position.row - 2][position.col] == null) {
                res.add(CellInfo(position.row - 2, position.col))
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

        if (board.board[forwardRow][position.col] == null) {
            res.add(CellInfo(forwardRow, position.col))
        }
        if (position.col != Board.BOARD_SIZE - 1 && board.board[forwardRow][position.col + 1]?.isWhite != isWhite) {
            res.add(CellInfo(forwardRow, position.col + 1))
        }
        if (position.col != 0 && board.board[forwardRow][position.col - 1]?.isWhite != isWhite) {
            res.add(CellInfo(forwardRow, position.col - 1))
        }

        if (board.canEnPassant) {
            if (board.enPassantCellInfo.col - 1 == position.col) {
                res.add(CellInfo(forwardRow, position.col + 1))
            }
            if (board.enPassantCellInfo.col + 1 == position.col) {
                res.add(CellInfo(forwardRow, position.col - 1))
            }
        }

        if (isWhite && position.row == 1 && board.board[position.row + 2][position.col] == null) {
            res.add(CellInfo(position.row + 2, position.col))
        }
        if (!isWhite && position.row == 6 && board.board[position.row - 2][position.col] == null) {
            res.add(CellInfo(position.row - 2, position.col))
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
        if (position.col != Board.BOARD_SIZE && board.board[forwardRow][position.col + 1]?.isWhite != isWhite) {
            res.add(CellInfo(forwardRow, position.col + 1))
        }
        if (position.col != 0 && board.board[forwardRow][position.col - 1]?.isWhite != isWhite) {
            res.add(CellInfo(forwardRow, position.col - 1))
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