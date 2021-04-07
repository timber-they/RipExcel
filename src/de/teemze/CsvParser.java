package de.teemze;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CsvParser {
    private List<Integer> axisIndices;
    private List<Integer> dataIndices;
    private String fileName;

    // For now hardcoded
    private static final char SEPARATOR = ',';

    public CsvParser(List<Integer> axisIndices, List<Integer> dataIndices, String fileName) {
        this.axisIndices = axisIndices;
        this.dataIndices = dataIndices;
        this.fileName = fileName;
    }

    public List<List<Double>> getAxisValues() {
        // TODO
        return null;
    }

    public List<List<Double>> getDataValues() {
        FileInputStream inputStream = null;
        Scanner scanner = null;
        try {
            inputStream = new FileInputStream(fileName);
            scanner = new Scanner(inputStream);

            List<List<Double>> res = dataIndices.stream().map(i -> new LinkedList<Double>()).collect(Collectors.toList());

            if (!scanner.hasNextLine()) {
                System.err.println("Empty data file");
                return null;
            }

            // Skip first line, as it only contains the header - for now we ignore that
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("" + SEPARATOR);
                for (int i = 0; i < dataIndices.size(); i++) {
                    Integer index = dataIndices.get(i);
                    if (index >= split.length) {
                        System.err.println("Index too high: " + index);
                        continue;
                    }
                    double value;
                    try {
                        value = Double.parseDouble(split[index]);
                    } catch (NumberFormatException e) {
                        value = 0;
                    }
                    res.get(i).add(value);
                }
            }

            return res;
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't open file - failed with exception:");
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (scanner != null)
                scanner.close();
        }
    }

    public List<List<Coordinate>> getDataCoordinates(double width, double height) {
        List<List<Double>> data = getDataValues();
        if (data == null || data.size() <= 0) {
            System.err.println("No data given");
            return null;
        }
        double inc = width / data.get(0).size();
        double scale = height / getDataMaximum();
        return data.stream().map(values -> {
            List<Coordinate> res = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++)
                res.add(new Coordinate(i * inc, values.get(i) * scale));
            return res;
        }).collect(Collectors.toList());
    }

    private double getDataMaximum() {
        double max = -Double.MAX_VALUE;
        for (List<Double> values : getDataValues()) {
            for (Double value : values) {
                if (value > max)
                    max = value;
            }
        }
        return max;
    }
}
