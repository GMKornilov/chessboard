package com.github.fertilewaif.chessboard.model

data class CellInfo(var col: Int, var row: Int) {

    companion object {
        fun fromAnimationIndexes(row: Int, col: Int, isWhitePerspective: Boolean): CellInfo {
            return if (isWhitePerspective) {
                CellInfo(col, 7 - row)
            } else {
                CellInfo(7 - col, row)
            }
        }

        fun toAnimationIndexes(cellInfo: CellInfo, isWhitePerspective: Boolean): CellInfo {
            return if(isWhitePerspective) {
                CellInfo(7 - cellInfo.row, cellInfo.col)
            } else {
                CellInfo(cellInfo.row, 7 - cellInfo.col)
            }
        }
    }
}