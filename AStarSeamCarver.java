package seamcarving;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.HashMap;

public class AStarSeamCarver implements SeamCarver {
    private Picture picture;

    public AStarSeamCarver(Picture picture) {
        if (picture == null) {
            throw new NullPointerException("Picture cannot be null.");
        }
        this.picture = new Picture(picture);
    }

    public Picture picture() {
        return new Picture(picture);
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public Color get(int x, int y) {
        return picture.get(x, y);
    }

    public int[] findHorizontalSeam() {
        // Store a map of a map of vertices such that x -> y -> vertex(x, y)
        HashMap<Integer, HashMap<Integer, Vertex>> xMap = new HashMap<>();

        // iterate over the columns of pixels in the image
        for (int x = 0; x < width(); x++) {
            // and store the vertices at (x, y) such that y -> vertex(x, y)
            HashMap<Integer, Vertex> yMap = new HashMap<>();
            for (int y = 0; y < height(); y++) {
                Vertex v;
                // if it's not the left column of the picture
                if (x != 0) {
                    // then access the column left at x-1
                    // get the y index above left, direct left, or below left with the minimum energy
                    int yMin = minLeftIndex(xMap.get(x - 1), y);

                    // and use that to compute the energy for this vertex
                    double energy = energy(x, y) + xMap.get(x - 1).get(yMin).getEnergy();

                    // create vertex that stores it's minimum energy left neighbor, it's energy, and x and y coords
                    v = new Vertex(xMap.get(x - 1).get(yMin), energy, x, y);
                    // otherwise, if in the left column
                } else {
                    // the new vertex has no left neighbor
                    v = new Vertex(null, energy(x, y), x, y);
                }
                yMap.put(y, v);
            }
            xMap.put(x, yMap);
        }

        // find the minimum weight index in right column
        Vertex min = new Vertex(null, Integer.MAX_VALUE, -1, -1);
        for (int y = 0; y < height(); y++) {
            Vertex v = xMap.get(width() - 1).get(y);
            if (v.getEnergy() < min.getEnergy()) {
                min = v;
            }
        }

        // follow chain of minimum energy vertices from right to left column
        // and insert into array from last to first index
        int[] seam = new int[width()];
        int seamIndex = width() - 1;
        while (seamIndex >= 0) {
            seam[seamIndex] = min.getY();
            min = min.getMin();
            seamIndex--;
        }

        // return the seam of y values
        return seam;
    }

    public int[] findVerticalSeam() {
        // Store a map of a map of vertices such that y -> x -> vertex(x, y)
        HashMap<Integer, HashMap<Integer, Vertex>> yMap = new HashMap<>();

        // iterate over the rows of pixels in the image
        for (int y = 0; y < height(); y++) {
            // and store the vertices at (x, y) such that x -> vertex(x, y)
            HashMap<Integer, Vertex> xMap = new HashMap<>();
            for (int x = 0; x < width(); x++) {
                Vertex v;
                // if it's not the top row of the picture
                if (y != 0) {
                    // then access the row above at y-1
                    // get the x index above left, above, or above right with the minimum energy
                    int xMin = minAboveIndex(yMap.get(y - 1), x);

                    // and use that to compute the energy for this vertex
                    double energy = energy(x, y) + yMap.get(y - 1).get(xMin).getEnergy();
                    // create vertex that stores it's minimum energy above neighbor, it's energy, and x and y coords
                    v = new Vertex(yMap.get(y - 1).get(xMin), energy, x, y);
                // otherwise, if in the top row
                } else {
                    // the new vertex has no neighbor above neighbor
                    v = new Vertex(null, energy(x, y), x, y);
                }
                xMap.put(x, v);
            }
            yMap.put(y, xMap);
        }

        // find the minimum weight vertex in the last row
        Vertex min = new Vertex(null, Integer.MAX_VALUE, -1, -1);
        for (int x = 0; x < width(); x++) {
            Vertex v = yMap.get(height() - 1).get(x);
            if (v.getEnergy() < min.getEnergy()) {
                min = v;
            }
        }

        // follow chain of minimum energy vertices from top to bottom row
        // and insert into array from last to first index
        int[] seam = new int[height()];
        int seamIndex = height() - 1;
        while (seamIndex >= 0) {
            seam[seamIndex] = min.getX();
            min = min.getMin();
            seamIndex--;
        }

        // return the seam of x values
        return seam;
    }

    private class Vertex {
        // the vertex before this one with the lowest energy
        private Vertex min;

        // the energy of this vertex
        // represents the sum of it's energy and the previous shortest path to this vertex
        private double energy;

        // this vertex's x and y coordinates
        private int x;
        private int y;

        Vertex(Vertex min, double energy, int x, int y) {
            this.min = min;
            this.energy = energy;
            this.x = x;
            this.y = y;
        }

        public Vertex getMin() {
            return min;
        }

        public double getEnergy() {
            return energy;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    private int minAboveIndex(HashMap<Integer, Vertex> xMap, int x) {
        // input validation
        if (x < 0 || x > width() - 1) {
            throw new IllegalArgumentException("x out of bounds");
        }

        // store the vertices above, left, and right of the current vertex at x
        Vertex xa;
        Vertex xl;
        Vertex xr;

        // store the vertex with the minimum energy
        Vertex min;

        // assign the vertex above the current x
        xa = xMap.get(x);

        // assign the vertex above and left of the current x
        // handle the case where x is a pixel along the left edge
        if (x == 0) {
            xl = new Vertex(null, Integer.MAX_VALUE, -1, -1);
        } else {
            xl = xMap.get(x - 1);
        }

        // assign the vertex above and right of the current x
        // handle the case where x is a pixel along the right edge
        if (x == width() - 1) {
            xr = new Vertex(null, Integer.MAX_VALUE, -1, -1);
        } else {
            xr = xMap.get(x + 1);
        }

        // get the vertex with the minimum energy
        if (xa.getEnergy() < xl.getEnergy()) {
            min = xa;
        } else {
            min = xl;
        }
        if (xr.getEnergy() < min.getEnergy()) {
            min = xr;
        }

        // and return the result
        return min.getX();
    }

    private int minLeftIndex(HashMap<Integer, Vertex> yMap, int y) {
        // input validation
        if (y < 0 || y > height() - 1) {
            throw new IllegalArgumentException("y out of bounds");
        }

        // store the vertices above left, direct left, and below left of y
        Vertex ya;
        Vertex yl;
        Vertex yb;

        // store the vertex with the minimum energy
        Vertex min;

        // assign the vertex direct left of the current y
        yl = yMap.get(y);

        // assign the vertex above and left of the current y
        // handle the case where y is a pixel along the top edge
        if (y == 0) {
            ya = new Vertex(null, Integer.MAX_VALUE, -1, -1);
        } else {
            ya = yMap.get(y - 1);
        }

        // assign the vertex below and left of the current y
        // handle the case where x is a pixel along the right edge
        if (y == height() - 1) {
            yb = new Vertex(null, Integer.MAX_VALUE, -1, -1);
        } else {
            yb = yMap.get(y + 1);
        }

        // get the vertex with the minimum energy
        if (yl.getEnergy() < ya.getEnergy()) {
            min = yl;
        } else {
            min = ya;
        }
        if (yb.getEnergy() < min.getEnergy()) {
            min = yb;
        }

        // and return the result
        return min.getY();
    }
}
