package com.goodproductssoft.minningpool.controls;

import android.app.Activity;
import android.graphics.Canvas;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.goodproductssoft.minningpool.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by user on 5/2/2018.
 */

public class CustomMarkerView extends MarkerView {
    TextView value_report, value_current, value_average, value_worker, timer;
    ArrayList<Entry> yValueReports, yValueCurrents, yValueAverages, yValueWorkers;
    private int uiScreenWidth, uiScreenHeight;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param activity
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public CustomMarkerView(final Activity activity, int layoutResource, ArrayList<Entry> valueReports,
                            ArrayList<Entry> valueCurrents, ArrayList<Entry> valueAverages,
                            ArrayList<Entry> valueWorkers) {
        super(activity, layoutResource);
        value_report = (TextView) findViewById(R.id.value_report);
        value_current = (TextView) findViewById(R.id.value_current);
        value_average = (TextView) findViewById(R.id.value_average);
        value_worker = (TextView) findViewById(R.id.value_worker);
        timer = (TextView) findViewById(R.id.timer);
        this.yValueReports = valueReports;
        this.yValueCurrents = valueCurrents;
        this.yValueAverages = valueAverages;
        this.yValueWorkers = valueWorkers;
        uiScreenWidth = getResources().getDisplayMetrics().widthPixels;
        uiScreenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        long time = (long) e.getX();
        String valueReport = "", valueCurrent = "", valueAverage = "", valueWorker = "";
        for(int i = 0; i < yValueReports.size(); i++){
            if((long)yValueReports.get(i).getX() == time){
                valueReport = String.valueOf(ChangeHashrateUnit(yValueReports.get(i).getY()));
                valueCurrent = String.valueOf(ChangeHashrateUnit(yValueCurrents.get(i).getY()));
                valueAverage = String.valueOf(ChangeHashrateUnit(yValueAverages.get(i).getY()));
                valueWorker = String.valueOf((long) yValueWorkers.get(i).getY());
                break;
            }
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time * 1000);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());

        timer.setText(ConvertTimestampToTime(time));
        value_report.setText(valueReport);
        value_current.setText(valueCurrent);
        value_average.setText(valueAverage);
        value_worker.setText(valueWorker);
        super.refreshContent(e, highlight);
    }

    private String ConvertTimestampToTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(time * 1000);
        Date dateTime = cal.getTime();
        String date = DateFormat.format("yyyy-MM-dd HH:mm", dateTime).toString();
        return date;
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
