package com.github.fertilewaif.chessboard.model

data class CellInfo(var col: Int, var row: Int) {

    companion object {
        fun fromIndexes(row: Int, col: Int, isWhitePerspective: Boolean): CellInfo {
            return if (isWhitePerspective) {
                CellInfo(col, row)
            } else {
                CellInfo(7 - col, 7 - row)
            }
        }

        fun toAnimationIndexes(cellInfo: CellInfo, isWhitePerspective: Boolean): CellInfo {
            return if(isWhitePerspective) {
                CellInfo(cellInfo.row, cellInfo.col)
            } else {
                CellInfo(7 - cellInfo.row, 7 - cellInfo.col)
            }
        }
    }
}