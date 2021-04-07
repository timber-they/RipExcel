package de.teemze;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {

    private static Numericanvas canvas;

    private static String[] arguments;

    /**
     * The main entry point. The first argument should point to the data, the other parameters should be in the following format:
     * <br/>
     * &lt;type&gt;=&lt;index&gt;
     * <br/>
     * It always assumes the first row to be made of labels.
     * The type is either "axis" (for the axis labels) or "data" (for the data).
     * The indices are obviously zero-based.
     * <br/>
     * Example parameters:
     * <br/>
     * data.csv axis=0 data=1 data=2 data=3
     *
     * @param args The parameters in the format as specified above.
     */
    public static void main(String[] args) {
        if (args.length <= 2) {
            System.err.println("Too few arguments");
            return;
        }
        createWindow();
        arguments = args;
    }

    public static void repaint(){
        CsvParser parser = new CsvParser(getAxisIndices(arguments), getDataIndices(arguments), arguments[0]);
        canvas.drawMultipleData(parser.getDataCoordinates(canvas.getWidth(), canvas.getHeight()));
    }

    private static void createWindow() {

        //Create and set up the window
        JFrame frame = new JFrame("Visualisation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        canvas = new Numericanvas();
        frame.getContentPane().add(canvas);

        //Display the window
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(new Dimension(1000, 700));
        frame.pack();
        frame.setVisible(true);
    }

    private static List<Integer> getAxisIndices(String[] args) {
        return getIndicesStartingWith(args, "index=");
    }

    private static List<Integer> getDataIndices(String[] args) {
        return getIndicesStartingWith(args, "data=");
    }

    private static List<Integer> getIndicesStartingWith(String[] args, String start) {
        List<Integer> res = new LinkedList<>();
        Arrays.stream(args)
                .skip(1)
                .filter(arg -> arg.startsWith(start))
                .forEach(arg -> {
                    try {
                        res.add(Integer.parseInt(arg.substring(start.length())));
                    } catch (NumberFormatException e) {
                        System.err.println("Couldn't parse number " + arg + " - failed with exception:");
                        e.printStackTrace();
                    }
                });
        return res;
    }
}
