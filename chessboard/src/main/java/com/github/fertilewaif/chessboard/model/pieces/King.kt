package com.github.fertilewaif.chessboard.model.pieces

import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import kotlin.math.abs
import kotlin.math.max

class King(isWhite: Boolean) : Piece(isWhite) {
    companion object {
        val deltas = listOf(
                Pair(-1, -1),
                Pair(-1, 0),
                Pair(-1, 1),
                Pair(0, -1),
                Pair(0, 1),
                Pair(1, -1),
                Pair(1, 0),
                Pair(1, 1)
        )
    }

    override fun getLegalMoves(board: Board): List<CellInfo> {
        // TODO: add castle
        val res = mutableListOf<CellInfo>()
        val moves = getMoves(board)
        for (move in moves) {
            if (!board.isHit(move, isWhite)) {
                res.add(move)
            }
        }
        return res
    }

    override fun getMoves(board: Board): List<CellInfo> {
        val res = mutableListOf<CellInfo>()
        for ((deltaRow, deltaCol) in deltas) {
            val newRow = position.row + deltaRow
            val newCol = position.col + deltaCol
            if (newRow >= 0 && newCol >= 0 && newRow < Board.BOARD_SIZE && newCol <= Board.BOARD_SIZE) {
                res.add(CellInfo(newCol, newRow))
            }
        }
        return res
    }

    override fun getHitMoves(board: Board): List<CellInfo> {
        return getMoves(board)
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        val deltaRow = position.row - cellInfo.row
        val deltaCol = position.col - cellInfo.col
        return (deltaCol != 0 && deltaRow != 0) && max(abs(deltaCol), abs(deltaRow)) == 1
    }

    override fun toString(): String {
        return if(isWhite) {
            "K"
        } else {
            "k"
        }
    }
}