package de.teemze;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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
        return getValues(axisIndices);
    }

    public List<List<Double>> getDataValues() {
        return getValues(dataIndices);
    }

    private List<List<Double>> getValues(List<Integer> indices){
        FileInputStream inputStream = null;
        Scanner scanner = null;
        try {
            inputStream = new FileInputStream(fileName);
            scanner = new Scanner(inputStream);

            List<List<Double>> res = indices.stream().map(i -> new LinkedList<Double>()).collect(Collectors.toList());

            if (!scanner.hasNextLine()) {
                System.err.println("Empty data file");
                return null;
            }

            // Skip first line, as it only contains the header
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("" + SEPARATOR);
                for (int i = 0; i < indices.size(); i++) {
                    Integer index = indices.get(i);
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
        // For now we only use the first entry
        List<Double> axisData = getAxisValues().get(0);
        if (data == null || data.size() <= 0) {
            System.err.println("No data given");
            return null;
        }
        if (axisData == null || axisData.size() <= 0){
            System.err.println("No axis data given");
            return null;
        }
        double minX = axisData.stream().min(Double::compareTo).get();
        double scaleX = width / (axisData.stream().max(Double::compareTo).get() - minX);
        double scaleY = height / getDataMaximum();
        return data.stream().map(values -> {
            List<Coordinate> res = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++)
                res.add(new Coordinate((axisData.get(i) - minX) * scaleX, values.get(i) * /*scaleY*/ height / values.stream().max(Double::compareTo).get()));
            res = averageDuplicates(res);
            res.sort(Coordinate::compareTo);
            return res;
        }).collect(Collectors.toList());
    }

    private List<Coordinate> averageDuplicates(List<Coordinate> coordinates){
        Map<Integer, Set<Double>> table = new HashMap<>(coordinates.size());
        for (Coordinate coordinate : coordinates) {
            if (!table.containsKey(coordinate.roundX()))
                table.put(coordinate.roundX(), new HashSet<>());
            table.get(coordinate.roundX()).add(coordinate.getY());
        }
        return table.entrySet().stream()
                .map(entry -> new Coordinate(entry.getKey(), average(entry.getValue())))
                .collect(Collectors.toList());
    }

    private double average(Collection<Double> values){
        return values.stream().reduce(Double::sum).get() / values.size();
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

    public List<String> getLabels(){
        FileInputStream inputStream = null;
        Scanner scanner = null;
        try {
            inputStream = new FileInputStream(fileName);
            scanner = new Scanner(inputStream);

            if (!scanner.hasNextLine()) {
                System.err.println("Empty data file");
                return null;
            }

            // Skip first line, as it only contains the header
            String header = scanner.nextLine();
            final String[] splitHeader = header.split("" + SEPARATOR);
            return dataIndices.stream()
                    .map(index -> index > splitHeader.length ? "?" : splitHeader[index])
                    .collect(Collectors.toList());

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
}
