package de.teemze;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CsvParser {
    private List<Integer> axisIndices;
    private List<Integer> dataIndices;
    private String fileName;

    public CsvParser(List<Integer> axisIndices, List<Integer> dataIndices, String fileName) {
        this.axisIndices = axisIndices;
        this.dataIndices = dataIndices;
        this.fileName = fileName;
    }

    public List<List<Double>> getAxisValues(){
        // TODO
        return null;
    }

    public List<List<Double>> getDataValues(){
        // TODO
        return null;
    }

    public List<List<Coordinate>> getDataCoordinates(double width, double height){
        List<List<Double>> data = getDataValues();
        if (data == null)
            return null;
        double inc = width / data.size();
        double scale = height / getDataMaximum();
        return data.stream().map(values -> {
            List<Coordinate> res = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++)
                res.add(new Coordinate(i * inc, values.get(i) * scale));
            return res;
        }).collect(Collectors.toList());
    }

    private double getDataMaximum(){
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
