package com.example.docsapp2.presentation.utils

import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

object FileUtils {
    fun getFileFromUriNew(context: Context, uri: Uri) =
        copyFileToInternal(context, uri)?.let { File(it) }

    private fun copyFileToInternal(context: Context, fileUri: Uri): String? {
        val file = File(context.filesDir.toString() + "/" + getFileName(context, fileUri))
        try {
            val fileOutputStream = FileOutputStream(file)
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val buffers = ByteArray(1024)
            var read: Int
            while (inputStream!!.read(buffers).also { read = it } != -1) {
                fileOutputStream.write(buffers, 0, read)
            }
            inputStream.close()
            fileOutputStream.close()
            return file.path
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun getFileFromBitmap(context: Context, bitmap: Bitmap): File {
        val wrapper = ContextWrapper(context)
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
        stream.flush()
        stream.close()
        return file
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: 0)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result
    }

    fun clearFilesInPicturesDirectory(context: Context) {
        val picturesDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        picturesDirectory?.let { directory ->
            val files = directory.listFiles()
            files?.forEach { file ->
                file.delete()
            }
        }
    }
}