package org.reactnative.frame;

import android.graphics.Bitmap;

import org.reactnative.camera.utils.ImageDimensions;
import com.google.mlkit.vision.common.InputImage;

import java.nio.ByteBuffer;

public class RNFrameFactory {
  public static RNFrame buildFrame(byte[] bitmapData, int width, int height, int rotation) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(bitmapData);
    ImageDimensions dimensions = new ImageDimensions(width, height, rotation);
    return new RNFrame(InputImage.fromByteBuffer(byteBuffer, width, height, rotation,InputImage.IMAGE_FORMAT_NV21), dimensions);
  }

  public static RNFrame buildFrame(Bitmap bitmap) {
    ImageDimensions dimensions = new ImageDimensions(bitmap.getWidth(), bitmap.getHeight());
    return new RNFrame(InputImage.fromBitmap(bitmap,0), dimensions);
  }
}
