package com.javaindoku.yotaniniaga.utilities

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.javaindoku.yotaniniaga.utilities.StringFormat.getRealPathFromURI
import java.io.InputStream
import java.lang.Exception

object CustomProfile {
    fun setImageTakeAPicture(
        activity: Activity,
        context: Context,
        uri: Uri,
        imageView: ImageView
    ): String {
        var stringResult = ""

        var options = BitmapFactory.Options()
        options.inSampleSize = 4
        var filePath = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor = context!!.contentResolver.query(
            uri,
            filePath,
            null,
            null,
            null
        )!!
        cursor.moveToFirst()

        var mImagePath: String =
            cursor.getString(cursor.getColumnIndex(filePath[0]))

        var stream: InputStream =
            context!!.contentResolver.openInputStream(uri)!!

        var yourSelectedImage: Bitmap =
            BitmapFactory.decodeStream(stream, null, options)!!
        stream.close()

        //orientation
        try {
            var rotate = 0
            try {
                var exif = ExifInterface(mImagePath)
                var orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                );

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_270 ->
                        rotate = 270
                    ExifInterface.ORIENTATION_ROTATE_180 ->
                        rotate = 180
                    ExifInterface.ORIENTATION_ROTATE_90 ->
                        rotate = 90

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            var matrix = Matrix()
            matrix.postRotate(rotate.toFloat())
            yourSelectedImage = Bitmap.createBitmap(
                yourSelectedImage,
                0,
                0,
                yourSelectedImage.width,
                yourSelectedImage.height,
                matrix,
                true
            )
            val limageUrii = getRealPathFromURI(uri, activity)
            stringResult = limageUrii

            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
                .skipMemoryCache(true)

            Glide.with(context!!)
                .load(stringResult)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply(requestOptions)
                .into(imageView)
        } catch (e: Exception) {
        }

        return stringResult
    }

    fun setImageFromGallery(
        activity: Activity,
        context: Context,
        uri: Uri,
        imageView: ImageView
    ): String {
        var stringResult = ""
        var selectedImage = uri

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE) // because file name is always same
            .skipMemoryCache(true)

        Glide.with(context!!)
            .load(selectedImage)
            .timeout(5000)
            .apply(requestOptions)
            .into(imageView)

        val limageUrii = getRealPathFromURI(uri, activity)
        stringResult = limageUrii

        return stringResult
    }

    fun showRemoteImageUsingGlide(context: Context, view: ImageView, url: String?, placeholder: Int) {
        var imageLoader = CircularProgressDrawable(context);
        imageLoader.strokeWidth = 5f
        imageLoader.centerRadius = 30f
        imageLoader.start()

        Glide.with(context)
            .load(url)
            .placeholder(imageLoader)
            .error(placeholder)
            .into(view)
    }
}