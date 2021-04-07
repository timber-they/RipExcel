package de.teemze;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Adapted from the Numericat project
 */
public class Numericanvas extends JPanel
{
    private List<List<Coordinate>> coordinates = new ArrayList<>(2);
    List<Color> colors = Arrays.asList(Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA);
    private final static int padding = 1;
    private final static int labelPadding = 1;
    private final static int pointWidth = 4;
    private final static int numberYDivisions = 10;
    private final static Color lineColor = new Color(44, 102, 230, 180);
    private final static Color pointColor = new Color(100, 100, 100, 180);
    private final static Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke graphStroke = new BasicStroke(2f);
    private List<Integer> values = new ArrayList<>(10);
    private List<String> labels = new ArrayList<>();

    private void paintBackGround(Graphics2D g, int width, int height)
    {
        // draw white background
        g.setColor(Color.WHITE);
        g.fillRect(
                padding + labelPadding,
                padding,
                width - (2 * padding) - labelPadding,
                height - 2 * padding - labelPadding);
    }

    private void paintXandYAxis(Graphics2D g, int width, int height)
    {
        // create x and y axes
        g.drawLine(padding + labelPadding, height - padding - labelPadding, padding + labelPadding, padding);
        g.drawLine(
                padding + labelPadding,
                height - padding - labelPadding,
                width - padding,
                height - padding - labelPadding);
    }

    private void painHatchY(Graphics2D g, int width, int height, int minScore, int scoreRange, int length, FontMetrics fontMetrics)
    {
        final int fontHeight = fontMetrics.getHeight();
        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            final int x1 = padding + labelPadding;
            final int x2 = pointWidth + padding + labelPadding;
            final int y = height - ((i * (height - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            if (length > 0) {
                g.setColor(gridColor);
                g.drawLine(padding + labelPadding + 1 + pointWidth, y, width - padding, y);
                g.setColor(Color.BLACK);
                final int tickValue = (int) (minScore + ((scoreRange * i) / numberYDivisions));
                final String yLabel = tickValue + "";
                final int labelWidth = fontMetrics.stringWidth(yLabel);
                g.drawString(yLabel, x1 - labelWidth - 5, y + (fontHeight / 2) - 3);
            }
            g.drawLine(x1, y, x2, y);
        }
    }

    private void paintHatchX(Graphics2D g, int width, int height, int minScore, int scoreRange, int length, FontMetrics fontMetrics)
    {
        final int fontHeight = fontMetrics.getHeight();
        if (length > 1) {
            for (int i = 0; i < length; i++) {
                final int x = i * (width - padding * 2 - labelPadding) / (length - 1) + padding + labelPadding;
                final int y1 = height - padding - labelPadding;
                final int y2 = y1 - pointWidth;
                if ((i % ((int) ((length / 20.0)) + 1)) == 0) {
                    g.setColor(gridColor);
                    g.drawLine(x, height - padding - labelPadding - 1 - pointWidth, x, padding);
                    g.setColor(Color.BLACK);
                    final String xLabel = i + "";
                    final int labelWidth = fontMetrics.stringWidth(xLabel);
                    g.drawString(xLabel, x - labelWidth / 2, y1 + fontHeight + 3);
                }
                g.drawLine(x, y1, x, y2);
            }
        }
    }

    Dimension size;
    @Override
    public void paintComponent(Graphics graphics) {
        if (size == null || !size.equals(getSize())) {
            size = getSize();
            Main.repaint();
            return;
        }

        super.paintComponent(graphics);
        if (!(graphics instanceof Graphics2D)) {
            graphics.drawString("Graphics is not Graphics2D, unable to render", 0, 0);
            return;
        }
        final Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int width = getWidth();
        final int height = getHeight();

        paintBackGround(g, width, height);

        paintXandYAxis(g, width, height);

        for (int j = 0; j < coordinates.size(); j++)
        {
            if (coordinates.get(j) == null)
                continue;
            List<Coordinate> coordinates = this.coordinates.get(j);
            g.setColor(colors.get(j));
            for (int i = 1; i < coordinates.size(); i++)
                g.drawLine(coordinates.get(i - 1).roundX(), getSize().height - coordinates.get(i - 1).roundY(),
                        coordinates.get(i).roundX(), getSize().height - coordinates.get(i).roundY());
        }

        // Draw Legend
        for (int i = 0; i < colors.size() && i < labels.size(); i++) {
            g.setColor(colors.get(i));
            g.drawString(labels.get(i), 20, 30 + 20 * i);
        }
    }

    public void drawData(List<Coordinate> coordinates) {
        this.coordinates.clear();
        this.coordinates.add(coordinates);
        repaint();
    }

    public void drawMultipleData(List<List<Coordinate>> coordinates) {
        this.coordinates = coordinates;
        repaint();
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
