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

    override fun getMoves(board: Board): List<CellInfo> {
        val resRook = rook.getMoves(board)
        val resBishop = bishop.getMoves(board)
        return resRook.plus(resBishop)
    }
}