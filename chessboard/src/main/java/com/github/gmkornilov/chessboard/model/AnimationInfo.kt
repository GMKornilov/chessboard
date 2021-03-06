package com.github.gmkornilov.chessboard.model

import androidx.annotation.DrawableRes
import com.github.gmkornilov.chessboard.model.pieces.Piece

internal data class AnimationInfo(val piece: Piece, val from: CellInfo?, val to: CellInfo?)