package com.aksara.membership.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.aksara.membership.util.rememberFileBitmap

/** Avatar lingkaran: tampilkan foto bila ada, jika tidak tampilkan inisial nama. */
@Composable
fun AvatarImage(
    name: String?,
    photoPath: String?,
    size: Dp,
    bgColor: Color,
    textColor: Color = Color.White
) {
    val bitmap = rememberFileBitmap(photoPath)
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Foto profil",
                modifier = Modifier.size(size).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                avatarInitials(name),
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value / 2.6f).sp,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

fun avatarInitials(name: String?): String {
    if (name.isNullOrBlank()) return "?"
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
        else -> parts[0].take(2).uppercase()
    }
}
