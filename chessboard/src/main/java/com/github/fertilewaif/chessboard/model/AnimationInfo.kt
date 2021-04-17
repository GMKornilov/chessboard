package com.github.fertilewaif.chessboard.model

import androidx.annotation.DrawableRes

data class AnimationInfo(@DrawableRes val drawable: Int, val from: CellInfo?, val to: CellInfo?)