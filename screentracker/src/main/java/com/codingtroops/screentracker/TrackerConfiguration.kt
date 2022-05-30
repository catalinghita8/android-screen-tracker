package com.codingtroops.screentracker

import android.graphics.Color
import android.os.Parcelable
import android.view.Gravity
import kotlinx.parcelize.Parcelize
import java.lang.NumberFormatException

@Parcelize
class TrackerConfiguration private constructor(
    val gravity: TrackerTextGravity,
    val textSize: Float,
    val textHexColor: String,
    val textBackgroundColor: String,
    val filteringLibFragments: Boolean,
    val trackFragments: Boolean,
) : Parcelable {

    open class Builder {
        private var gravity = TrackerTextGravity.BOTTOM
        private var textSize = 15f
        private var filteringLibFragments = true
        private var trackFragments = true
        private var textColor: String = "#000000"
        private var textBackgroundColor: String = "#A1FFFFFF"


        fun setTextGravity(gravity: TrackerTextGravity): Builder {
            this.gravity = gravity
            return this
        }

        fun setTextSize(size: Float): Builder {
            this.textSize = size
            return this
        }

        fun setTextHexColor(color: String): Builder {
            try {
                Color.parseColor(color)
                this.textColor = color
            } catch (e: NumberFormatException) {
                this.textColor = "#000000"
            }
            return this
        }

        fun setTextBackgroundHexColor(color: String): Builder {
            try {
                Color.parseColor(color)
                this.textBackgroundColor = color
            } catch (e: NumberFormatException) {
                this.textBackgroundColor = "#A1FFFFFF"
            }
            return this
        }

        fun setIsFilteringLibFragments(value: Boolean): Builder {
            this.filteringLibFragments = value
            return this
        }

        fun setIsTrackingFragments(value: Boolean): Builder {
            this.trackFragments = value
            return this
        }

        fun build() = TrackerConfiguration(
            gravity = gravity,
            textSize = textSize,
            filteringLibFragments = filteringLibFragments,
            trackFragments = trackFragments,
            textHexColor = textColor,
            textBackgroundColor = textBackgroundColor
        )
    }

    companion object {
        val DEFAULT = Builder().build()
    }
}

enum class TrackerTextGravity(val value: Int) {
    TOP(Gravity.TOP),
    BOTTOM(Gravity.BOTTOM)
}