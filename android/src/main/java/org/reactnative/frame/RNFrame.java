package org.reactnative.frame;

import com.google.mlkit.vision.common.InputImage;

import org.reactnative.camera.utils.ImageDimensions;

/**
 * Wrapper around Frame allowing us to track Frame dimensions.
 * Tracking dimensions is used in RNBarcodeDetector to provide painless BarcodeDetector recreation
 * when image dimensions change.
 */

public class RNFrame {
  private InputImage mFrame;
  private ImageDimensions mDimensions;

  public RNFrame(InputImage frame, ImageDimensions dimensions) {
    mFrame = frame;
    mDimensions = dimensions;
  }

  public InputImage getInputImage() {
    return mFrame;
  }

  public ImageDimensions getDimensions() {
    return mDimensions;
  }
}
