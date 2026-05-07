package com.interfast.alarm

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface

/**
 * Renders a stylized notification banner for a milestone.
 *
 * Aesthetic: TE / Nothing OS at low-res. Drawn at 200×100 with anti-aliasing
 * disabled, then upscaled 4× with nearest-neighbor filtering so every pixel
 * stays crisp and chunky — the OP-1 LCD / Game Boy feel.
 */
object NotificationArt {

    private const val VOID_BLACK = 0xFF0A0A0A.toInt()
    private const val DOT_GRAY = 0xFF1F1F1F.toInt()
    private const val GRID_DARK = 0xFF141414.toInt()
    private const val PURE_WHITE = 0xFFFFFFFF.toInt()
    private const val GRAY_50 = 0xFF7A7A7A.toInt()
    private const val GLYPH_RED = 0xFFFF3B30.toInt()

    private const val SRC_W = 200
    private const val SRC_H = 100
    private const val SCALE = 4 // → 800×400 upscaled

    fun renderHourBanner(hour: Int): Bitmap =
        renderBanner(
            big = hour.toString().padStart(2, '0'),
            topLeft = "INTERFAST",
            topRight = "FAST · COMPLETE",
            litSegments = hour.coerceIn(0, 24),
            suffix = "H",
        )

    fun renderHelloBanner(): Bitmap =
        renderBanner(
            big = "HI",
            topLeft = "INTERFAST",
            topRight = "EASTER · EGG",
            litSegments = 12,
            suffix = ".",
        )

    private fun renderBanner(
        big: String,
        topLeft: String,
        topRight: String,
        litSegments: Int,
        suffix: String,
    ): Bitmap {
        val src = Bitmap.createBitmap(SRC_W, SRC_H, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(src)
        // anti-alias OFF — every glyph and shape stays on the pixel grid.
        val paint = Paint().apply { isAntiAlias = false }

        // 1. void background
        paint.color = VOID_BLACK
        canvas.drawRect(0f, 0f, SRC_W.toFloat(), SRC_H.toFloat(), paint)

        // 2. dot-matrix grid — single pixels, every 5px.
        paint.color = DOT_GRAY
        var y = 2f
        while (y < SRC_H) {
            var x = 2f
            while (x < SRC_W) {
                canvas.drawRect(x, y, x + 1f, y + 1f, paint)
                x += 5f
            }
            y += 5f
        }

        // 3. faint horizontal mid-rule
        paint.color = GRID_DARK
        canvas.drawRect(0f, SRC_H / 2f - 0.5f, SRC_W.toFloat(), SRC_H / 2f + 0.5f, paint)

        // 4. red brand bar (top-left)
        paint.color = GLYPH_RED
        canvas.drawRect(6f, 6f, 30f, 9f, paint)

        // 5. Mono caps labels — small, pixelated.
        val mono = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        paint.typeface = mono
        paint.textSize = 6f

        paint.color = PURE_WHITE
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(topLeft, 32f, 12f, paint)

        paint.color = GRAY_50
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(topRight, SRC_W - 6f, 12f, paint)

        // 6. The numerals — condensed bold sans, dominant. Drawn small so
        //    nearest-neighbor upscaling produces chunky pixel edges.
        val condensedBold = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        paint.typeface = condensedBold
        paint.textScaleX = 0.82f
        paint.letterSpacing = -0.04f
        paint.textSize = 70f
        paint.color = PURE_WHITE
        paint.textAlign = Paint.Align.CENTER

        val metrics = paint.fontMetrics
        val numY = SRC_H / 2f - (metrics.ascent + metrics.descent) / 2f + 4f
        canvas.drawText(big, SRC_W / 2f, numY, paint)

        // reset paint mutations
        paint.textScaleX = 1f
        paint.letterSpacing = 0f

        // 7. Tiny red "H" anchored to the lower-right of the numerals.
        paint.typeface = condensedBold
        paint.textSize = 14f
        paint.textAlign = Paint.Align.LEFT
        paint.color = GLYPH_RED
        canvas.drawText(suffix, SRC_W * 0.78f, SRC_H / 2f + 8f, paint)

        // 8. 24-segment progress strip at bottom — Lit count = hour.
        val segCount = 24
        val segGap = 1f
        val sideMargin = 6f
        val totalW = SRC_W - sideMargin * 2f
        val segW = (totalW - segGap * (segCount - 1)) / segCount
        val stripY = SRC_H - 6f
        val stripH = 2f
        for (i in 0 until segCount) {
            paint.color = if (i < litSegments) GLYPH_RED else DOT_GRAY
            val x = sideMargin + i * (segW + segGap)
            canvas.drawRect(x, stripY, x + segW, stripY + stripH, paint)
        }

        // Quantize to a tiny fixed palette with Floyd-Steinberg dithering —
        // genuine low-color-space lo-fi look.
        ditherToPalette(src)

        // Upscale with nearest-neighbor for crisp pixel edges.
        val out = Bitmap.createBitmap(SRC_W * SCALE, SRC_H * SCALE, Bitmap.Config.ARGB_8888)
        val outCanvas = Canvas(out)
        val upscalePaint = Paint().apply {
            isFilterBitmap = false
            isAntiAlias = false
            isDither = false
        }
        val matrix = Matrix().apply { setScale(SCALE.toFloat(), SCALE.toFloat()) }
        outCanvas.drawBitmap(src, matrix, upscalePaint)
        return out
    }

    /**
     * Floyd-Steinberg dither to a fixed 9-color palette in place.
     * Cheap (under a frame at 200×100) and gives the OP-1 / Game Boy LCD feel.
     */
    private fun ditherToPalette(bmp: Bitmap) {
        val w = bmp.width
        val h = bmp.height
        val n = w * h
        val pixels = IntArray(n)
        bmp.getPixels(pixels, 0, w, 0, 0, w, h)

        val r = FloatArray(n)
        val g = FloatArray(n)
        val b = FloatArray(n)
        for (i in 0 until n) {
            val c = pixels[i]
            r[i] = ((c shr 16) and 0xFF).toFloat()
            g[i] = ((c shr 8) and 0xFF).toFloat()
            b[i] = (c and 0xFF).toFloat()
        }

        for (y in 0 until h) {
            for (x in 0 until w) {
                val i = y * w + x
                val rv = r[i].coerceIn(0f, 255f)
                val gv = g[i].coerceIn(0f, 255f)
                val bv = b[i].coerceIn(0f, 255f)
                val nearest = nearestPaletteColor(rv, gv, bv)
                val nr = ((nearest shr 16) and 0xFF).toFloat()
                val ng = ((nearest shr 8) and 0xFF).toFloat()
                val nb = (nearest and 0xFF).toFloat()
                pixels[i] = (0xFF shl 24) or (nearest and 0x00FFFFFF)

                val er = rv - nr
                val eg = gv - ng
                val eb = bv - nb

                if (x + 1 < w) {
                    r[i + 1] += er * 7f / 16f
                    g[i + 1] += eg * 7f / 16f
                    b[i + 1] += eb * 7f / 16f
                }
                if (y + 1 < h) {
                    if (x > 0) {
                        r[i + w - 1] += er * 3f / 16f
                        g[i + w - 1] += eg * 3f / 16f
                        b[i + w - 1] += eb * 3f / 16f
                    }
                    r[i + w] += er * 5f / 16f
                    g[i + w] += eg * 5f / 16f
                    b[i + w] += eb * 5f / 16f
                    if (x + 1 < w) {
                        r[i + w + 1] += er * 1f / 16f
                        g[i + w + 1] += eg * 1f / 16f
                        b[i + w + 1] += eb * 1f / 16f
                    }
                }
            }
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h)
    }

    private val PALETTE = intArrayOf(
        VOID_BLACK,
        GRID_DARK,
        DOT_GRAY,
        0xFF333333.toInt(),
        0xFF666666.toInt(),
        GRAY_50,
        0xFFCCCCCC.toInt(),
        PURE_WHITE,
        GLYPH_RED,
    )

    private fun nearestPaletteColor(r: Float, g: Float, b: Float): Int {
        var best = PALETTE[0]
        var bestDist = Float.MAX_VALUE
        for (p in PALETTE) {
            val pr = ((p shr 16) and 0xFF).toFloat()
            val pg = ((p shr 8) and 0xFF).toFloat()
            val pb = (p and 0xFF).toFloat()
            val dr = r - pr
            val dg = g - pg
            val db = b - pb
            val d = dr * dr + dg * dg + db * db
            if (d < bestDist) {
                bestDist = d
                best = p
            }
        }
        return best
    }
}
