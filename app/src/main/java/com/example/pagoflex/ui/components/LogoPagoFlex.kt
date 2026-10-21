package com.example.pagoflex.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.ui.theme.Acento
import com.example.pagoflex.ui.theme.Blanco
import com.example.pagoflex.ui.theme.Primario
import com.example.pagoflex.ui.theme.PrimarioOscuro
import com.example.pagoflex.ui.theme.Secundario

@Composable
fun IconoPagoFlex(
    modifier: Modifier = Modifier,
    tamano: Dp = 64.dp
) {
    Canvas(
        modifier = modifier
            .size(tamano)
            .semantics { contentDescription = "Logo de PagoFlex" }
    ) {
        val w = size.width
        val h = size.height

        // Fondo con degradado corporativo
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(PrimarioOscuro, Primario, Secundario),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            ),
            cornerRadius = CornerRadius(w * 0.26f, w * 0.26f)
        )

        // Tarjeta inclinada
        rotate(degrees = -12f, pivot = Offset(w / 2f, h / 2f)) {
            drawRoundRect(
                color = Blanco,
                topLeft = Offset(w * 0.16f, h * 0.26f),
                size = Size(w * 0.68f, h * 0.44f),
                cornerRadius = CornerRadius(w * 0.07f, w * 0.07f)
            )
            drawRect(
                color = PrimarioOscuro,
                topLeft = Offset(w * 0.16f, h * 0.36f),
                size = Size(w * 0.68f, h * 0.08f)
            )
            drawRoundRect(
                color = Acento,
                topLeft = Offset(w * 0.23f, h * 0.54f),
                size = Size(w * 0.20f, h * 0.07f),
                cornerRadius = CornerRadius(w * 0.035f, w * 0.035f)
            )
        }

        // Estela de "flujo" (la parte "Flex")
        val curva = Path().apply {
            moveTo(w * 0.16f, h * 0.80f)
            cubicTo(w * 0.38f, h * 0.62f, w * 0.60f, h * 0.92f, w * 0.82f, h * 0.70f)
        }
        drawPath(
            path = curva,
            color = Acento,
            style = Stroke(width = w * 0.07f, cap = StrokeCap.Round)
        )
        drawCircle(
            color = Blanco,
            radius = w * 0.045f,
            center = Offset(w * 0.82f, h * 0.70f)
        )
    }
}

@Composable
fun NombrePagoFlex(
    modifier: Modifier = Modifier,
    tamanoFuente: TextUnit = 24.sp,
    colorPago: Color = Blanco,
    colorFlex: Color = Acento
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = colorPago, fontWeight = FontWeight.Bold)) {
                append("Pago")
            }
            withStyle(
                SpanStyle(
                    color = colorFlex,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic
                )
            ) {
                append("Flex")
            }
        },
        modifier = modifier,
        fontSize = tamanoFuente,
        letterSpacing = (-0.5).sp
    )
}

@Composable
fun MarcaHorizontalPagoFlex(
    modifier: Modifier = Modifier,
    tamanoIcono: Dp = 40.dp,
    colorPago: Color = Blanco,
    colorFlex: Color = Acento
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconoPagoFlex(tamano = tamanoIcono)
        Spacer(Modifier.width(10.dp))
        NombrePagoFlex(
            tamanoFuente = (tamanoIcono.value * 0.56f).sp,
            colorPago = colorPago,
            colorFlex = colorFlex
        )
    }
}