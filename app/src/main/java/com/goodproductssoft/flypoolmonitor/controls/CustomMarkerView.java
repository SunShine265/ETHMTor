package com.goodproductssoft.flypoolmonitor.controls;

import android.app.Activity;
import android.graphics.Canvas;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.goodproductssoft.flypoolmonitor.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 5/2/2018.
 */

public class CustomMarkerView extends MarkerView {
    TextView value_current, value_average, timer;
    ArrayList<Entry> yValueCurrents, yValueAverages;
    private int uiScreenWidth, uiScreenHeight;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param activity
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomMarkerView(final Activity activity, int layoutResource,
                            ArrayList<Entry> valueCurrents, ArrayList<Entry> valueAverages) {
        super(activity, layoutResource);
        value_current = (TextView) findViewById(R.id.value_current);
        value_average = (TextView) findViewById(R.id.value_average);
        timer = (TextView) findViewById(R.id.timer);
        this.yValueCurrents = valueCurrents;
        this.yValueAverages = valueAverages;
        uiScreenWidth = getResources().getDisplayMetrics().widthPixels;
        uiScreenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long time = (long) e.getX();
        String valueCurrent = "", valueAverage = "";
        for(int i = 0; i < yValueCurrents.size(); i++){
            if((long)yValueCurrents.get(i).getX() == time){
                valueCurrent = String.valueOf(ChangeHashrateUnit(yValueCurrents.get(i).getY()));
                valueAverage = String.valueOf(ChangeHashrateUnit(yValueAverages.get(i).getY()));
                break;
            }
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time * 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());

        timer.setText(formattedDate);
        value_current.setText(valueCurrent);
        value_average.setText(valueAverage);
        super.refreshContent(e, highlight);
    }

    @Override
    public void setOffset(MPPointF offset) {
        super.setOffset(offset);
    }

    @Override
    public void setOffset(float offsetX, float offsetY) {
        super.setOffset(offsetX, offsetY);
    }

    @Override
    public MPPointF getOffset() {
        return super.getOffset();
    }

    @Override
    public void setChartView(Chart chart) {
        super.setChartView(chart);
    }

    @Override
    public Chart getChartView() {
        return super.getChartView();
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return super.getOffsetForDrawingAtPoint(posX, posY);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        int w = getWidth();
        if((uiScreenWidth-posX-60) < w) {
            posX -= w;
        }
        posY = 100;
        canvas.translate(posX, posY);
        draw(canvas);
    }
    public String ChangeHashrateUnit(float value){
        String strTempValue = new DecimalFormat("#.##").format(value) + " H/s";
        if(value / 1000 >= 1){
            double lTempHS = value / 1000;
            strTempValue = new DecimalFormat("#.##").format(lTempHS) + " KH/s";
            if(lTempHS / 1000 >= 1){
                double lTempKH = lTempHS / 1000;
                strTempValue = new DecimalFormat("#.##").format(lTempKH) + " MH/s";
                if(lTempKH / 1000 >= 1){
                    double lTempMH = lTempKH / 1000;
                    strTempValue =  new DecimalFormat("#.##").format(lTempMH) + " GH/s";
                    if(lTempMH / 1000 >= 1){
                        double lTempGH = lTempMH / 1000;
                        strTempValue = new DecimalFormat("#.##").format(lTempGH) + " TH/s";
                    }
                }
            }
        }
        return strTempValue;
    }
}
