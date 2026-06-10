package com.interfast.ui.rear

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.interfast.ui.theme.JetBrainsMono
import java.time.LocalDate

/**
 * The back of the unit. Like a Pocket Operator, flipping the device over
 * reveals the bare board: the app's real component diagram silkscreened in
 * print colors, the operator's manual, and the config switches — which are
 * all printed in the OFF position because they are not switches at all.
 *
 * Always paper-cream, in both themes: this is print, not a screen.
 */

private val Paper = Color(0xFFEFE9DC)
private val Ink = Color(0xFF161616)
private val InkFaint = Color(0xFF8C8678)
private val PrintRed = Color(0xFFE0472E)
private val PrintCyan = Color(0xFF1099B5)

@Composable
fun RearPanel(
    edition: String,
    onFlipBack: () -> Unit,
) {
    val context = LocalContext.current
    val version = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
        } catch (e: Exception) {
            "?"
        }
    }
    val serial = remember {
        "SN-" + LocalDate.now().dayOfYear.toString().padStart(4, '0') + "-TX1"
    }

    val labelStyle = TextStyle(
        color = Ink,
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
    )
    val bodyStyle = TextStyle(
        color = Ink,
        fontFamily = JetBrainsMono,
        fontSize = 11.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 16.sp,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .clickable(onClick = onFlipBack)
            .semantics {
                contentDescription =
                    "Rear panel: operator's manual and component diagram. Tap to flip back."
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            // ── top component diagram ──
            CircuitDiagramTop()

            Spacer(modifier = Modifier.height(18.dp))

            // ── manual header ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("TX-1 — OPERATOR'S MANUAL", style = labelStyle.copy(fontSize = 13.sp))
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "⟲ FRONT",
                    style = labelStyle.copy(color = PrintRed, fontSize = 10.sp),
                    modifier = Modifier
                        .border(1.dp, PrintRed, RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(thickness = 2.dp, color = Ink)
            Spacer(modifier = Modifier.height(14.dp))

            // ── legend ──
            // NBSP inside locked phrases: a silkscreen never orphans the last
            // word of its own instruction.
            ManualRow("DRAG", PrintRed, "TAPE — SET WINDOW START · 1 TICK = 1 MIN", bodyStyle)
            ManualRow("HOLD / NOW", PrintRed, "SNAP START TO CURRENT TIME", bodyStyle)
            ManualRow("01–05", PrintCyan, "ARM MILESTONES — EXACT ALARMS, FIRE ONCE", bodyStyle)
            ManualRow("ACTIVATE", PrintRed, "LOCKS DECK · PHONE DOES THE REST", bodyStyle)
            ManualRow("STOP", PrintCyan, "ON ANY ALERT — CANCELS THE REST · NO SHAME", bodyStyle)
            ManualRow("LAST ALERT", PrintCyan, "TAPE REWINDS ITSELF — NOTHING TO RESET", bodyStyle)
            ManualRow("BRIGHTNESS", PrintCyan, "DIM = NIGHT SKIN · BRIGHT = DAY SKIN", bodyStyle)
            ManualRow("×5 FOOTER", PrintRed, "?", bodyStyle)

            Spacer(modifier = Modifier.height(18.dp))

            // ── config: printed, permanently off ──
            Text("CONFIG", style = labelStyle.copy(color = PrintRed, fontSize = 12.sp))
            Spacer(modifier = Modifier.height(8.dp))
            ConfigRow("STREAKS", labelStyle)
            ConfigRow("STATS", labelStyle)
            ConfigRow("GUILT", labelStyle)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "NOT CONFIGURABLE. THAT'S THE PRODUCT.",
                style = bodyStyle.copy(color = InkFaint, fontSize = 9.sp, letterSpacing = 1.sp),
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ── serial footer — deliberate line breaks, never a ragged wrap ──
            val footerStyle = bodyStyle.copy(
                color = InkFaint,
                fontSize = 9.sp,
                letterSpacing = 0.8.sp,
            )
            Text("$serial · FW $version", style = footerStyle, maxLines = 1)
            Text("RUNTIME ∅ — WE DON'T COUNT", style = footerStyle, maxLines = 1)
            Text(
                "EDITION $edition · MADE FOR HUMANS · EAT WHEN HUNGRY",
                style = footerStyle,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── bottom bus strip ──
            CircuitDiagramBottom()
        }

        // corner screws
        Screw(Modifier.align(Alignment.TopStart).padding(6.dp).systemBarsPadding())
        Screw(Modifier.align(Alignment.TopEnd).padding(6.dp).systemBarsPadding())
        Screw(Modifier.align(Alignment.BottomStart).padding(6.dp))
        Screw(Modifier.align(Alignment.BottomEnd).padding(6.dp))
    }
}

@Composable
private fun ManualRow(key: String, keyColor: Color, text: String, bodyStyle: TextStyle) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text(
            text = key,
            style = bodyStyle.copy(color = keyColor, fontWeight = FontWeight.Bold),
            modifier = Modifier.width(108.dp),
        )
        Text(text = text, style = bodyStyle, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ConfigRow(name: String, labelStyle: TextStyle) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(name, style = labelStyle.copy(fontSize = 10.sp), modifier = Modifier.width(108.dp))
        Text(
            "OFF",
            style = labelStyle.copy(color = Paper, fontSize = 9.sp),
            modifier = Modifier
                .background(PrintRed, RoundedCornerShape(3.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "ON",
            style = labelStyle.copy(color = InkFaint, fontSize = 9.sp),
            modifier = Modifier
                .border(1.dp, InkFaint, RoundedCornerShape(3.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun Screw(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(14.dp)) {
        val r = size.minDimension / 2f - 1f
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(InkFaint, radius = r, center = c, style = Stroke(1.5f))
        drawLine(
            InkFaint,
            Offset(c.x - r * 0.7f, c.y + r * 0.7f),
            Offset(c.x + r * 0.7f, c.y - r * 0.7f),
            1.5f,
        )
    }
}

/* ---------------- silkscreen diagrams ---------------- */

private data class Block(val label: String, val cx: Float, val cy: Float)

/**
 * The real signal path, not decoration: UI drives the ViewModel, state lives
 * in DataStore, activation programs AlarmManager, the receiver renders the
 * dithered banner and writes reached-state back.
 */
@Composable
fun CircuitDiagramTop() {
    val measurer = rememberTextMeasurer()
    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val w = size.width
        val h = size.height
        val blockW = w * 0.27f
        val blockH = 30f * density

        val rowA = h * 0.22f
        val rowB = h * 0.78f
        val blocks = listOf(
            Block("SCRUB.UI", w * 0.165f, rowA),
            Block("SCHED.VM", w * 0.5f, rowA),
            Block("DATASTORE", w * 0.835f, rowA),
            Block("ALARM.MGR", w * 0.165f, rowB),
            Block("NOTIF.RX", w * 0.5f, rowB),
            Block("DITHER.ART", w * 0.835f, rowB),
        )

        // traces underneath
        val midY = h * 0.5f
        // UI → VM → DATASTORE (red bus along row A)
        trace(PrintRed, Offset(blocks[0].cx, rowA), Offset(blocks[2].cx, rowA))
        // VM ↓ ALARM.MGR (cyan elbow)
        trace(PrintCyan, Offset(w * 0.5f, rowA), Offset(w * 0.5f, midY))
        trace(PrintCyan, Offset(w * 0.5f, midY), Offset(w * 0.165f, midY))
        trace(PrintCyan, Offset(w * 0.165f, midY), Offset(w * 0.165f, rowB))
        // ALARM.MGR → NOTIF.RX → DITHER.ART (cyan bus along row B)
        trace(PrintCyan, Offset(blocks[3].cx, rowB), Offset(blocks[5].cx, rowB))
        // NOTIF.RX ↑ DATASTORE (red elbow — markReached writes back)
        trace(PrintRed, Offset(w * 0.835f, rowB), Offset(w * 0.835f, rowA))

        // junction dots
        listOf(
            Offset(w * 0.5f, rowA), Offset(w * 0.165f, midY),
            Offset(w * 0.5f, midY), Offset(w * 0.835f, rowB),
        ).forEach { drawCircle(Ink, radius = 3f, center = it) }

        blocks.forEach { drawBlock(it, blockW, blockH, measurer) }
    }
}

/** Support bus: the receivers and senses that keep the unit honest. */
@Composable
fun CircuitDiagramBottom() {
    val measurer = rememberTextMeasurer()
    Canvas(modifier = Modifier.fillMaxWidth().height(56.dp)) {
        val w = size.width
        val y = size.height / 2f
        trace(PrintCyan, Offset(0f, y), Offset(w, y))
        val labels = listOf("BOOT.RX", "STOP.RX", "HAPTX", "LUX.SENSE")
        val xs = listOf(0.14f, 0.38f, 0.62f, 0.86f)
        labels.zip(xs).forEach { (label, fx) ->
            drawBlock(Block(label, w * fx, y), w * 0.21f, 26f * density, measurer)
        }
    }
}

private fun DrawScope.trace(color: Color, from: Offset, to: Offset) {
    drawLine(color, from, to, strokeWidth = 2f)
}

private fun DrawScope.drawBlock(
    block: Block,
    blockW: Float,
    blockH: Float,
    measurer: TextMeasurer,
) {
    val topLeft = Offset(block.cx - blockW / 2f, block.cy - blockH / 2f)
    // drop shadow, then body — the silkscreen "sticker" look
    drawRect(InkFaint, topLeft + Offset(3f, 3f), androidx.compose.ui.geometry.Size(blockW, blockH))
    drawRect(Ink, topLeft, androidx.compose.ui.geometry.Size(blockW, blockH))
    val layout = measurer.measure(
        text = block.label,
        style = TextStyle(
            color = Paper,
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp,
        ),
    )
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            block.cx - layout.size.width / 2f,
            block.cy - layout.size.height / 2f,
        ),
    )
}
