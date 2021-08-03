package com.moshi.impl.captcha;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.color.ColorGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

public class CurvedLineNoiseBackground implements BackgroundGenerator {
    private int lineWidth = 3;
    private ColorGenerator noiseColor;
    private BufferedImage background;
    private int height = 100;
    private int width = 200;

    Random myRandom = new SecureRandom();
    private ColorGenerator colorGenerator = null;

    public CurvedLineNoiseBackground(Integer width, Integer height) {
        this(width, height, Color.white, Color.black, 3);
    }

    public CurvedLineNoiseBackground(Integer width, Integer height, Color bgColor, Color noiseColor, int lineWidth) {

        this(width, height, new SingleColorGenerator(bgColor), new SingleColorGenerator(noiseColor), lineWidth);
    }

    public CurvedLineNoiseBackground(Integer width, Integer height, ColorGenerator colorGenerator,  ColorGenerator noiseColor, int lineWidth) {
        this.width = width;
        this.height = height;
        this.colorGenerator = colorGenerator;
        this.noiseColor = noiseColor;
        this.lineWidth = lineWidth;
    }

    @Override
    public int getImageHeight() {
        return height;
    }

    @Override
    public int getImageWidth() {
        return width;
    }

    /**
     * Generates a backround image on wich text will be paste. Implementations must take into account the imageHeigt and
     * imageWidth.
     *
     * @return the background image
     */
    public BufferedImage getBackground() {
        background = new BufferedImage(getImageWidth(), getImageHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D pie = (Graphics2D) background.getGraphics();
        Color color = colorGenerator.getNextColor();

        pie.setColor(color != null ? color : Color.white);
        pie.setBackground(color != null ? color : Color.white);
        pie.fillRect(0, 0, getImageWidth(), getImageHeight());

        pie.dispose();
        makeNoise(background);
        return background;
    }

    public void makeNoise(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // the curve from where the points are taken
        CubicCurve2D cc = new CubicCurve2D.Float(width * .1f, height
                * myRandom.nextFloat(), width * .1f, height
                * myRandom.nextFloat(), width * .25f, height
                * myRandom.nextFloat(), width * .9f, height
                * myRandom.nextFloat());

        // creates an iterator to define the boundary of the flattened curve
        PathIterator pi = cc.getPathIterator(null, 2);
        Point2D tmp[] = new Point2D[200];
        int i = 0;

        // while pi is iterating the curve, adds points to tmp array
        while (!pi.isDone()) {
            float[] coords = new float[6];
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    tmp[i] = new Point2D.Float(coords[0], coords[1]);
            }
            i++;
            pi.next();
        }

        // the points where the line changes the stroke and direction
        Point2D[] pts = new Point2D[i];
        // copies points from tmp to pts
        System.arraycopy(tmp, 0, pts, 0, i);

        Graphics2D graph = (Graphics2D) image.getGraphics();
        graph.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        graph.setColor(noiseColor.getNextColor());

        // for the maximum 3 point change the stroke and direction
        for (i = 0; i < pts.length - 1; i++) {
            if (i < 3) {
                graph.setStroke(new BasicStroke(lineWidth));
            }
            graph.drawLine((int) pts[i].getX(), (int) pts[i].getY(),
                    (int) pts[i + 1].getX(), (int) pts[i + 1].getY());
        }

        graph.dispose();
    }
}
