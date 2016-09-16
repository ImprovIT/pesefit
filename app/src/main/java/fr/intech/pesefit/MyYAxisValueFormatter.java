package fr.intech.pesefit;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

/**
 * Created by Alexandre on 16/09/2016.
 */
public class MyYAxisValueFormatter implements IAxisValueFormatter, AxisValueFormatter {

    private String[] mValues;

    public MyYAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mValues[(int) value];
    }

    /** this is only needed if numbers are returned, else return 0 */
    @Override
    public int getDecimalDigits() { return 0; }
}
