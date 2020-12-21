package org.cchao.common.utils

import android.graphics.Bitmap
import android.text.TextUtils
import okhttp3.ResponseBody
import org.cchao.common.IApplication
import java.io.*

object FileUtils {

    /**
     * 创建文件夹
     */
    fun createFileFolder(folderName: String? = null): File {
        val storageDir: File
        val storagePath: String = IApplication.instance.externalCacheDir!!.absolutePath
        storageDir = File(storagePath + (if (TextUtils.isEmpty(folderName)) "" else (File.separator + folderName)))
        if (!storageDir.exists()) {
            if (storageDir.mkdirs()) {
                return storageDir
            }
        }
        return storageDir
    }

    /**
     * 创建文件
     */
    fun createFile(fileName: String, folderName: String? = null): File {
        val dir = createFileFolder(folderName)
        return File(dir, fileName)
    }

    /**
     * 保存Bitmap对象为图片到本地
     */
    fun writeImageToDisk(bitmap: Bitmap, imageName: String, folderName: String? = null): String? {
        try {
            val imageFile = createFile(imageName, folderName)
            val out = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            if (imageFile.exists()) {
                return imageFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun writeResponseBodyToDisk(fileName: String, body: ResponseBody, folderName: String? = null): String? {
        return try {
            val savedFile = File(createFileFolder(folderName), fileName)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(2048)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(savedFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                }
                outputStream.flush()
                savedFile.absolutePath
            } catch (e: IOException) {
                null
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            null
        }
    }

    /**
     * 删除文件
     */
    fun deleteFile(filePath: String?): Boolean {
        if (TextUtils.isEmpty(filePath)) {
            return true
        }
        val file = File(filePath!!)
        return file.isFile && file.exists() && file.delete()
    }
}