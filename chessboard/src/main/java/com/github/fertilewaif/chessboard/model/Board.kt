package com.github.fertilewaif.chessboard.model

import kotlin.math.abs
import kotlin.math.sign

class Board {
    companion object {
        const val BOARD_SIZE = 8
    }

    /**
     * cell-centered representation of the board
     * contains null if there is no figure on the cell
     */
    val board: List<List<Piece?>> = MutableList(BOARD_SIZE) { List(BOARD_SIZE) { null } }

    val whitePieces = mutableListOf<Piece>()
    val blackPieces = mutableListOf<Piece>()

    var canWhiteCastleShort = true
        private set
    var canWhiteCastleLong = true
        private set
    var canBlackCastleShort = true
        private set
    var canBlackCastleLong = true
        private set

    var canEnPassant = false
        private set
    var enPassantCellInfo: CellInfo = CellInfo('a', 0)
        private set

    var isWhiteTurn = true
        private set

    var fiftyMovesRule = 0
        private set

    var turnNumber = 1
        private set

    var whiteKingPosition = CellInfo('e', 1)
        private set
    var blackKingPosition = CellInfo('e', 8)
        private set

    /**
     * checks if given cell is hit by opposite side figures
     */
    fun isHit(pos: CellInfo, isWhite: Boolean): Boolean {
        val oppositePiecesList = if (isWhite) {
            blackPieces
        } else {
            whitePieces
        }
        for (piece in oppositePiecesList) {
            if (piece.canHit(pos, this)) {
                return true
            }
        }
        return false
    }

    /**
     * checks if figure on given cell is pinned by opposite pieces,
     * return null, if piece is not pinned
     */
    fun isPinned(position: CellInfo, isWhite: Boolean): CellInfo? {
        val kingPosition = (if (isWhite) {
            whiteKingPosition
        } else {
            blackKingPosition
        })
        val rowDiff = kingPosition.row - position.row
        val colDiff = kingPosition.col - position.col
        // if king and piece are not on same diagonal/horizontal/vertical, it can't be pinned
        if (abs(rowDiff) != 0 && abs(colDiff) != 0 && abs(colDiff) != abs(rowDiff)) {
            return null
        }
        val deltaRow = sign(rowDiff.toDouble()).toInt()
        val deltaCol = sign(colDiff.toDouble()).toInt()
        //check if there are no pieces between king and given cell
        var row = kingPosition.row + deltaRow
        var col = kingPosition.col - 'a' + deltaCol
        while (row != position.row || col != position.col - 'a') {
            if (board[row][col] != null) {
                // there is a piece between king and given cell, thus it can't be pinned
                return null
            }
            row += deltaRow
            col += deltaCol
        }
        row = position.row + deltaRow
        col = position.col - 'a' + deltaCol
        while (row >= 0 && col >= 0 && row < BOARD_SIZE && col < BOARD_SIZE) {
            val piece = board[row][col]
            if (piece == null) {
                row += deltaRow
                col += deltaCol
                continue
            }
            if (piece.isWhite == isWhite) {
                // piece can't be pinned by piece of the same color
                return null
            }
            if (piece is Queen) {
                // queen can pin to all directions
                return piece.position
            }
            if (deltaRow == 0 || deltaCol == 0 && piece is Rook) {
                // only rook can pin horizontally/vertically
                return piece.position
            }
            if (deltaRow != 0 && deltaCol != 0 && piece is Bishop) {
                // only bishop can pin diagonally
                return piece.position
            }
        }
        return null
    }

    fun move(from: CellInfo, to: CellInfo): Boolean {
        TODO("implement me")
    }
}