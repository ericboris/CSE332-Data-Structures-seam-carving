package seamcarving;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public interface SeamCarver {

    /** Returns the current image. (This updates whenever a seam is removed.) */
    Picture picture();

    /** Sets the current image. */
    void setPicture(Picture picture);

    /** Returns the width of the current image, in pixels. */
    int width();

    /** Returns the height of the current image, in pixels. */
    int height();

    /** Returns the color of pixel (x, y) in the current image. */
    Color get(int x, int y);

    /** Returns the energy of pixel (x, y) in the current image. */
    default double energy(int x, int y) {
        // Input validation
        if (x < 0 || x > width() - 1) {
            throw new IndexOutOfBoundsException("x is out of bounds");
        }

        if (y < 0 || y > height() - 1) {
            throw new IndexOutOfBoundsException("y is out of bounds");
        }

        // Initialize variables
        // Diff variables represent the square of the differences between the color values
        // of the pixel to the left and right or above and below, respectively
        double xRedDiff;
        double xGreenDiff;
        double xBlueDiff;

        double yRedDiff;
        double yGreenDiff;
        double yBlueDiff;

        // l for lower, as in x - 1 and h for higher as in x + 1
        int xl;
        int xh;
        int yl;
        int yh;

        // Set lower x pixel value
        if (x > 0) {
            xl = x - 1;
        } else {
            xl = width() - 1; // likely out of bounds error and needs width() - 1
        }

        // Set higher x pixel value
        if (x < width() - 1) {
            xh = x + 1;
        } else {
            xh = 0;
        }

        // Set lower y pixel value
        if (y > 0) {
            yl = y - 1;
        } else {
            yl = height() - 1; // likely out of bounds error
        }

        // Set the higher y pixel value
        if (y < height() - 1) {
            yh = y + 1;
        } else {
            yh = 0;
        }

        // compute the squares of the differences of the color components for the lower and higher x values
        xRedDiff = Math.pow(get(xl, y).getRed() - get(xh, y).getRed(), 2);
        xGreenDiff = Math.pow(get(xl, y).getGreen() - get(xh, y).getGreen(), 2);
        xBlueDiff = Math.pow(get(xl, y).getBlue() - get(xh, y).getBlue(), 2);

        // compute the squares of the differences of the color components for the lower and higher y values
        yRedDiff = Math.pow(get(x, yl).getRed() - get(x, yh).getRed(), 2);
        yGreenDiff = Math.pow(get(x, yl).getGreen() - get(x, yh).getGreen(), 2);
        yBlueDiff = Math.pow(get(x, yl).getBlue() - get(x, yh).getBlue(), 2);

        // return the square root of the sum of the squares
        return Math.sqrt(xRedDiff + xGreenDiff + xBlueDiff + yRedDiff + yGreenDiff + yBlueDiff);
    }

    /** Returns true iff pixel (x, y) is in the current image. */
    default boolean inBounds(int x, int y) {
        return (x >= 0) && (x < width()) && (y >= 0) && (y < height());
    }

    /**
     * Calculates and returns a minimum-energy horizontal seam in the current image.
     * The returned array will have the same length as the width of the image.
     * A value of v at index i of the output indicates that pixel (i, v) is in the seam.
     */
    int[] findHorizontalSeam();

    /**
     * Calculates and returns a minimum-energy vertical seam in the current image.
     * The returned array will have the same length as the height of the image.
     * A value of v at index i of the output indicates that pixel (v, i) is in the seam.
     */
    int[] findVerticalSeam();

    /** Calculates and removes a minimum-energy horizontal seam from the current image. */
    default void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("Input seam array cannot be null.");
        } else if (width() == 1) {
            throw new IllegalArgumentException("Image width is 1.");
        } else if (seam.length != width()) {
            throw new IllegalArgumentException("Seam length does not match image width.");
        }

        for (int i = 0; i < seam.length - 2; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException(
                        "Invalid seam, consecutive vertical indices are greater than one apart.");
            }
        }

        Picture carvedPicture = new Picture(width(), height() - 1);
        /* Copy over the all indices besides the index specified by the seam */
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < seam[i]; j++) {
                carvedPicture.set(i, j, get(i, j));
            }

            for (int j = seam[i] + 1; j < height(); j++) {
                carvedPicture.set(i, j - 1, get(i, j));
            }
        }

        setPicture(carvedPicture);
    }

    /** Calculates and removes a minimum-energy vertical seam from the current image. */
    default void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("Input seam array cannot be null.");
        } else if (height() == 1) {
            throw new IllegalArgumentException("Image height is 1.");
        } else if (seam.length != height()) {
            throw new IllegalArgumentException("Seam length does not match image height.");
        }

        for (int i = 0; i < seam.length - 2; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException(
                        "Invalid seam, consecutive horizontal indices are greater than one apart.");
            }
        }

        Picture carvedPicture = new Picture(width() - 1, height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < seam[i]; j++) {
                carvedPicture.set(j, i, get(j, i));
            }

            for (int j = seam[i] + 1; j < width(); j++) {
                carvedPicture.set(j - 1, i, get(j, i));
            }
        }

        setPicture(carvedPicture);
    }
}
