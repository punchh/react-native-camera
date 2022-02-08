package org.reactnative.camera.tasks;

import android.graphics.Rect;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.reactnative.barcodedetector.BarcodeFormatUtils;
import org.reactnative.camera.utils.ImageDimensions;
import org.reactnative.barcodedetector.RNBarcodeDetector;

import java.util.List;

public class BarcodeDetectorAsyncTask extends android.os.AsyncTask<Void, Void, Void> {

  private byte[] mImageData;
  private int mWidth;
  private int mHeight;
  private int mRotation;
  private RNBarcodeDetector mBarcodeDetector;
  private BarcodeDetectorAsyncTaskDelegate mDelegate;
  private double mScaleX;
  private double mScaleY;
  private ImageDimensions mImageDimensions;
  private int mPaddingLeft;
  private int mPaddingTop;

  public BarcodeDetectorAsyncTask(
          BarcodeDetectorAsyncTaskDelegate delegate,
          RNBarcodeDetector barcodeDetector,
          byte[] imageData,
          int width,
          int height,
          int rotation,
          float density,
          int facing,
          int viewWidth,
          int viewHeight,
          int viewPaddingLeft,
          int viewPaddingTop) {
    mImageData = imageData;
    mWidth = width;
    mHeight = height;
    mRotation = rotation;
    mDelegate = delegate;
    mBarcodeDetector = barcodeDetector;
    mImageDimensions = new ImageDimensions(width, height, rotation, facing);
    mScaleX = (double) (viewWidth) / (mImageDimensions.getWidth() * density);
    mScaleY = (double) (viewHeight) / (mImageDimensions.getHeight() * density);
    mPaddingLeft = viewPaddingLeft;
    mPaddingTop = viewPaddingTop;
  }

  @Override
  protected Void doInBackground(Void... ignored) {
    if (isCancelled() || mDelegate == null || mBarcodeDetector == null) {
      return null;
    }
    InputImage image = InputImage.fromByteArray(mImageData, mWidth, mHeight, BarcodeFormatUtils.getFirebaseRotation(mRotation), InputImage.IMAGE_FORMAT_YV12);
    BarcodeScanner barcode = mBarcodeDetector.getDetector();
    barcode.process(image).addOnSuccessListener(barcodes -> {
      WritableArray serializedBarcodes = serializeEventData(barcodes);
      mDelegate.onBarcodesDetected(serializedBarcodes, mWidth, mHeight, mImageData);
      mDelegate.onBarcodeDetectingTaskCompleted();
    }).addOnFailureListener(e -> {
      mDelegate.onBarcodeDetectionError(mBarcodeDetector);
    });
    return null;
  }

  private WritableArray serializeEventData(List<Barcode> barcodes) {
    WritableArray barcodesList = Arguments.createArray();

    for (int i = 0; i < barcodes.size(); i++) {
      Barcode barcode = barcodes.get(i);
      WritableMap serializedBarcode = Arguments.createMap();

      serializedBarcode.putString("data", barcode.getDisplayValue());
      serializedBarcode.putString("rawData", barcode.getRawValue());
      serializedBarcode.putString("type", BarcodeFormatUtils.get(barcode.getFormat()));
      serializedBarcode.putMap("bounds", processBounds(barcode.getBoundingBox()));
      barcodesList.pushMap(serializedBarcode);
    }

    return barcodesList;
  }

  private WritableMap processBounds(Rect frame) {
    WritableMap origin = Arguments.createMap();
    int x = frame.left;
    int y = frame.top;

    if (frame.left < mWidth / 2) {
      x = x + mPaddingLeft / 2;
    } else if (frame.left > mWidth /2) {
      x = x - mPaddingLeft / 2;
    }

    if (frame.top < mHeight / 2) {
      y = y + mPaddingTop / 2;
    } else if (frame.top > mHeight / 2) {
      y = y - mPaddingTop / 2;
    }

    origin.putDouble("x", x * mScaleX);
    origin.putDouble("y", y * mScaleY);

    WritableMap size = Arguments.createMap();
    size.putDouble("width", frame.width() * mScaleX);
    size.putDouble("height", frame.height() * mScaleY);

    WritableMap bounds = Arguments.createMap();
    bounds.putMap("origin", origin);
    bounds.putMap("size", size);
    return bounds;
  }



}
