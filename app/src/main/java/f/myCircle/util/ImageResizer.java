package f.myCircle.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;

import com.f.myCircle.BuildConfig;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import f.myCircle.models.ContactModel;

/**
 * A simple subclass of {@link ImageWorker} that resizes images from resources given a target width
 * and height. Useful for when the input images might be too large to simply load directly into
 * memory.
 */
public class ImageResizer extends ImageWorker {
    private static final String TAG = "ImageResizer";
    protected int mImageWidth;
    protected int mImageHeight;

    // taken from flatuicolors lol
    private static final int[] colors = {
            Color.rgb(26, 188, 156),
            Color.rgb(155, 89, 182),
            Color.rgb(46, 204, 113),
            Color.rgb(241, 196, 15),
            Color.rgb(230, 126, 34),
            Color.rgb(231, 76, 60)
    };

    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageWidth
     * @param imageHeight
     */
    public ImageResizer(Context context, int imageWidth, int imageHeight) {
        super(context);
        setImageSize(imageWidth, imageHeight);
    }

    /**
     * Initialize providing a single target image size (used for both width and height);
     *
     * @param context
     * @param imageSize
     */
    public ImageResizer(Context context, int imageSize) {
        super(context);
        setImageSize(imageSize);
    }

    /**
     * Set the target image width and height.
     *
     * @param width
     * @param height
     */
    public void setImageSize(int width, int height) {
        mImageWidth = width;
        mImageHeight = height;
    }

    /**
     * Set the target image size (width and height will be the same).
     *
     * @param size
     */
    public void setImageSize(int size) {
        setImageSize(size, size);
    }

    /**
     * The main processing method. This happens in a background task. In this case we are just
     * sampling down the bitmap and returning it from a resource.
     *
     * @param data
     * @return
     */
    @Override
    protected Bitmap processBitmap(Object data) {
        return decodeSampledBitmapFromUri(mContext, (ContactModel)data, mImageWidth, mImageHeight, getImageCache());
    }

    /**
     * Decode and sample down a bitmap from an input contact URI to the requested width and height.
     *
     * @param cm The ContactModel with the image data
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param cache The ImageCache used to find candidate bitmaps for use with inBitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     *         that are equal to or greater than the requested width and height
     */
    public Bitmap decodeSampledBitmapFromUri(Context context, ContactModel cm,
                                                       int reqWidth, int reqHeight, ImageCache cache) {

        // BEGIN_INCLUDE (read_bitmap_dimensions)
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        InputStream stream = openDisplayPhoto(context, cm.getContactUri());
        if (stream != null) {
            BitmapFactory.decodeStream(openDisplayPhoto(context, cm.getContactUri()), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // END_INCLUDE (read_bitmap_dimensions)

            // If we're running on Honeycomb or newer, try to use inBitmap
            addInBitmapOptions(options, cache);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(openDisplayPhoto(context, cm.getContactUri()));
        }
        else {
            Bitmap bitmap = Bitmap.createBitmap(context.getResources().getDisplayMetrics(), reqWidth, reqHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
            paint.setFilterBitmap(true);
            paint.setDither(true);
            paint.setColor(getRandomColor(cm.getName()));    //peter river lel
            canvas.drawCircle(reqWidth / 2, reqHeight / 2, Math.min(reqWidth, reqHeight) / 2, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics()));
            String text = cm.getName().substring(0, 1).toUpperCase();

            RectF bounds = new RectF(canvas.getClipBounds());
// measure text width
            bounds.right = paint.measureText(text, 0, text.length());
// measure text height
            bounds.bottom = paint.descent() - paint.ascent();

            bounds.left += (canvas.getClipBounds().width() - bounds.right) / 2.0f;
            bounds.top += (canvas.getClipBounds().height() - bounds.bottom) / 2.0f;

            canvas.drawText(text, bounds.left, bounds.top - paint.ascent(), paint);
            return bitmap;
        }
    }

    private int getRandomColor(String name) {
        return colors[(int)(name.hashCode() & 0xffffffffl) % colors.length];
    }

    private static InputStream openDisplayPhoto(Context context, Uri contactUri) {
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd = context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Decode and sample down a bitmap from a file input stream to the requested width and height.
     *
     * @param fileDescriptor The file descriptor to read from
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param cache The ImageCache used to find candidate bitmaps for use with inBitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     *         that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromDescriptor(
            FileDescriptor fileDescriptor, int reqWidth, int reqHeight, ImageCache cache) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // If we're running on Honeycomb or newer, try to use inBitmap
        addInBitmapOptions(options, cache);

        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void addInBitmapOptions(BitmapFactory.Options options, ImageCache cache) {
        //BEGIN_INCLUDE(add_bitmap_options)
        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (cache != null) {
            // Try and find a bitmap to use for inBitmap
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
        //END_INCLUDE(add_bitmap_options)
    }

    /**
     * Calculate an inSampleSize for use in a {@link android.graphics.BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link android.graphics.BitmapFactory}. This implementation calculates
     * the closest inSampleSize that is a power of 2 and will result in the final decoded bitmap
     * having a width and height equal to or larger than the requested width and height.
     *
     * @param options An options object with out* params already populated (run through a decode*
     *            method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }
}
