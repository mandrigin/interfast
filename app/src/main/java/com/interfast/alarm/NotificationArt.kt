package com.interfast.alarm

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

/**
 * Renders a stylized notification banner for a fasting milestone.
 *
 * Aesthetic: Teenage Engineering / Nothing — void-black with a subtle
 * dot-matrix grid, then enormous condensed-bold numerals as the
 * dominant visual element of the image.
 */
object NotificationArt {

    private const val VOID_BLACK = 0xFF0A0A0A.toInt()
    private const val DOT_GRAY = 0xFF1F1F1F.toInt()
    private const val GRID_GRAY = 0xFF141414.toInt()
    private const val PURE_WHITE = 0xFFFFFFFF.toInt()
    private const val GRAY_50 = 0xFF7A7A7A.toInt()
    private const val GLYPH_RED = 0xFFFF3B30.toInt()

    fun renderHourBanner(hour: Int): Bitmap {
        val w = 1024
        val h = 512
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Void-black background.
        paint.color = VOID_BLACK
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)

        // 2. Dot-matrix grid — denser than before so it reads as texture.
        paint.color = DOT_GRAY
        val spacing = 14f
        var y = spacing
        while (y < h) {
            var x = spacing
            while (x < w) {
                canvas.drawCircle(x, y, 1.2f, paint)
                x += spacing
            }
            y += spacing
        }

        // 3. Faint horizontal guide rule through the center — instrument feel.
        paint.color = GRID_GRAY
        paint.strokeWidth = 1f
        canvas.drawLine(0f, h / 2f, w.toFloat(), h / 2f, paint)

        // 4. Top brand row — small mono labels.
        val condensedBold = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        val mono = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)

        paint.color = GLYPH_RED
        canvas.drawRect(36f, 36f, 96f, 44f, paint) // small red bar

        paint.typeface = mono
        paint.textSize = 22f
        paint.color = PURE_WHITE
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("INTERFAST", 110f, 56f, paint)

        paint.color = GRAY_50
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("FAST · COMPLETE", w - 36f, 56f, paint)

        // 5. The numerals — condensed bold sans, massive, vertically dominant.
        //    Pulled tight horizontally with textScaleX < 1, slightly negative
        //    letter spacing to stamp them like an OP-1 display.
        paint.typeface = condensedBold
        paint.textScaleX = 0.82f
        paint.letterSpacing = -0.04f
        paint.textSize = 480f
        paint.color = PURE_WHITE
        paint.textAlign = Paint.Align.CENTER

        val numText = hour.toString().padStart(2, '0')
        // Vertical center: account for ascent/descent so the numerals sit
        // optically centered within the canvas.
        val metrics = paint.fontMetrics
        val numY = h / 2f - (metrics.ascent + metrics.descent) / 2f
        canvas.drawText(numText, w / 2f, numY, paint)

        // Reset paint defaults that we mutated above.
        paint.textScaleX = 1f
        paint.letterSpacing = 0f

        // 6. Tiny "H" suffix anchored to the right of the numerals — TE
        //    instrument units feel.
        paint.typeface = condensedBold
        paint.textSize = 96f
        paint.textAlign = Paint.Align.LEFT
        paint.color = GLYPH_RED
        canvas.drawText("H", w * 0.78f, h / 2f + 36f, paint)

        // 7. Bottom segment strip — 24 segments, lit proportional to hour/24.
        val segCount = 24
        val segGap = 5f
        val sideMargin = 36f
        val totalW = w - sideMargin * 2f
        val segW = (totalW - segGap * (segCount - 1)) / segCount
        val stripY = h - 28f
        val stripH = 5f
        for (i in 0 until segCount) {
            paint.color = if (i < hour) GLYPH_RED else DOT_GRAY
            val x = sideMargin + i * (segW + segGap)
            canvas.drawRect(x, stripY, x + segW, stripY + stripH, paint)
        }

        return bitmap
    }
}
