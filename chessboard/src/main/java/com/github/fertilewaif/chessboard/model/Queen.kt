package com.github.fertilewaif.chessboard.model

class Queen(isWhite: Boolean) : Piece(isWhite) {
    val rook = Rook(isWhite)
    val bishop = Bishop(isWhite)

    override var position: CellInfo
        get() = super.position
        set(value) {
            super.position = value
            rook.position = value
            bishop.position = value
        }

    override fun getLegalMoves(board: Board): List<CellInfo> {
        val resRook = rook.getLegalMoves(board)
        val resBishop = bishop.getLegalMoves(board)
        return resRook + resBishop
    }

    override fun getMoves(board: Board): List<CellInfo> {
        val resRook = rook.getMoves(board)
        val resBishop = bishop.getMoves(board)
        return resRook + resBishop
    }

    override fun getHitMoves(board: Board): List<CellInfo> {
        val resRook = rook.getHitMoves(board)
        val resBishop = rook.getHitMoves(board)
        return resRook + resBishop
    }

    override fun canHit(cellInfo: CellInfo, board: Board): Boolean {
        return bishop.canHit(cellInfo, board) || rook.canHit(cellInfo, board)
    }

    override fun toString(): String {
        return if(isWhite) {
            "Q"
        } else {
            "q"
        }
    }
}