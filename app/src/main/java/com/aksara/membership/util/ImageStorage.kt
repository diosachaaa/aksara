package com.aksara.membership.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

/** Menyalin gambar terpilih dari galeri ke penyimpanan internal aplikasi. */
object ImageStorage {
    fun copyToInternal(context: Context, uri: Uri, memberId: Long): String? {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.filesDir, "profile_${memberId}_${System.currentTimeMillis()}.jpg")
            input.use { i -> file.outputStream().use { o -> i.copyTo(o) } }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}

/** Memuat bitmap dari path file (di-downsample agar hemat memori). */
@Composable
fun rememberFileBitmap(path: String?): ImageBitmap? = remember(path) {
    if (path.isNullOrBlank()) {
        null
    } else {
        try {
            val opts = BitmapFactory.Options().apply { inSampleSize = 2 }
            BitmapFactory.decodeFile(path, opts)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
