package com.example.pagoflex.ui.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import com.example.pagoflex.ui.theme.Dimens

object EstilosCampo {
    val forma = RoundedCornerShape(Dimens.radioCampo)

    @Composable
    fun colores(): TextFieldColors {
        val c = MaterialTheme.colorScheme
        return OutlinedTextFieldDefaults.colors(

            unfocusedBorderColor = c.outline,
            unfocusedLabelColor = c.onSurfaceVariant,
            unfocusedSupportingTextColor = c.onSurfaceVariant,
            focusedSupportingTextColor = c.onSurfaceVariant,
            unfocusedLeadingIconColor = c.onSurfaceVariant,

            focusedBorderColor = c.primary,
            focusedLabelColor = c.primary,
            focusedLeadingIconColor = c.primary,
            cursorColor = c.primary,

            errorBorderColor = c.error,
            errorLabelColor = c.error,
            errorSupportingTextColor = c.error,
            errorLeadingIconColor = c.error,
            errorTrailingIconColor = c.error,
            errorCursorColor = c.error
        )
    }
}