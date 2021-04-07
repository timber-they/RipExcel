# RipExcel
I was annoyed with excels options to plot stuff.

## How to use
The first argument should point to the data, the other parameters should be in the following format:

    <type>=<index>

It always assumes the first row to be made of labels.
The type is either "axis" (for the axis labels) or "data" (for the data).
The indices are obviously zero-based.

Example parameters:

    data.csv axis=0 data=2 data=3 data=4 data=8
Example result:

![Example image](example.png)