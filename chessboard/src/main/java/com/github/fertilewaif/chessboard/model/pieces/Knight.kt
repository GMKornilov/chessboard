package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo

class Knight(isWhite: Boolean) : Piece(isWhite) {
    companion object {
        val deltas = listOf(
                Pair(-1, -2),
                Pair(-1, 2),
                Pair(-2, -1),
                Pair(-2, 1),
                Pair(1, -2),
                Pair(1, 2),
                Pair(2, -1),
                Pair(2, 1)
        )
    }

    override fun getLegalMoves(board: Board): List<CellInfo> {
        return if (board.isPinned(position, isWhite) != null) {
            listOf()
        } else {
            getMoves(board)
        }
    }

    override fun getMoves(board: Board): List<CellInfo> {
        return getHitMoves(board)
    }

    override fun getHitMoves(board: Board): List<CellInfo> {
        val res = mutableListOf<CellInfo>()
        for ((deltaI, deltaJ) in deltas) {
            val row = position.row + deltaI
            val col = position.col + deltaJ
            if (row < 0 || row > 7 || col < 0 || col > 7) {
                continue
            }
            val piece = board.board[row][col]
            if (piece == null || (piece.isWhite != isWhite && piece !is King)) {
                res.add(CellInfo(row, col))
            }
        }
        return res
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val deltaRow = cellInfo.row - position.row
        val deltaCol = cellInfo.col - position.col
        return Pair(deltaRow, deltaCol) in deltas
    }

    override fun toString(): String {
        return if(isWhite) {
            "K"
        } else {
            "k"
        }
    }
}