package com.github.fertilewaif.chessboard.model

class Board {
    /**
     * cell-centered representation of the board
     * contains null if there is no figure on the cell
     */
    private var board: List<List<Piece?>> = MutableList(8) { List(8) { null } }

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
    var enPassantCellInfo: CellInfo? = null
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

    fun isPinned(pos: CellInfo, isWhite: Boolean): Boolean {
        val posIndexes = pos.toIndexes(true)
        if (board[posIndexes.first][posIndexes.second] == null) {
            return false
        }
        return true
    }

    fun move(from: CellInfo, to: CellInfo): Boolean {
        return true
    }
}