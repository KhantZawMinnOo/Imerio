package dabria.jinx.imerio;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ImerioCalendarView extends View {

    private Paint headerPaint, weekPaint, dayPaint, weekendPaint;
    private Paint circlePaint, todayPaint;

    private int normalCellColor = Color.parseColor("#EEEEEE");
    private int currentDateCellColor = Color.parseColor("#4FC3F7");
    private int textColor = Color.BLACK;
    private int weekendColor = Color.RED;

    
    private Calendar shownMonth;
    private Calendar today;
    private GestureDetector detector;
    private float cellSize;
    private float radius;

    private final String[] week = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};

    private final SimpleDateFormat format =
	new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    public ImerioCalendarView(Context c) {
        super(c);
        init(null);
    }

    public ImerioCalendarView(Context c, AttributeSet a) {
        super(c, a);
        init(a);
    }

    public ImerioCalendarView(Context c, AttributeSet a, int d) {
        super(c, a, d);
        init(a);
    }

    private void init(AttributeSet attrs) {

        shownMonth = Calendar.getInstance();
        today = Calendar.getInstance();

        
        if (attrs != null) {

            TypedArray ta =
				getContext().obtainStyledAttributes(
				attrs,
				R.styleable.ImerioCalendarView
			);

            normalCellColor =
				ta.getColor(
				R.styleable.ImerioCalendarView_normal_cell_color,
				normalCellColor
			);

            currentDateCellColor =
				ta.getColor(
				R.styleable.ImerioCalendarView_current_date_cell_color,
				currentDateCellColor
			);

            weekendColor =
				ta.getColor(
				R.styleable.ImerioCalendarView_weekend_text_color,
				weekendColor
			);

            textColor =
				ta.getColor(
				R.styleable.ImerioCalendarView_text_color,
				textColor
			);

            ta.recycle();
        }

        
        headerPaint = basePaint(60, true, textColor);
        weekPaint = basePaint(34, true, textColor);
        dayPaint = basePaint(42, false, textColor);

        weekendPaint = basePaint(42, true, weekendColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(normalCellColor);

        todayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        todayPaint.setColor(currentDateCellColor);

        
        detector = new GestureDetector(getContext(),
			new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
									   float vx, float vy) {

					if (Math.abs(vx) > Math.abs(vy)) {

						if (Math.abs(vx) > 300) {

							if (vx < 0) shownMonth.add(Calendar.MONTH, 1);
							else shownMonth.add(Calendar.MONTH, -1);

							invalidate();
							return true;
						}

					} else {

						if (Math.abs(vy) > 300) {

							if (vy < 0) shownMonth.add(Calendar.YEAR, 1);
							else shownMonth.add(Calendar.YEAR, -1);

							invalidate();
							return true;
						}
					}

					return false;
				}
			});
    }

    private Paint basePaint(float size, boolean bold, int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setTextSize(size);
        p.setTextAlign(Paint.Align.CENTER);
        if (bold) {
            p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        }
        return p;
    }

    @Override
    protected void onMeasure(int w, int h) {

        int width = MeasureSpec.getSize(w);

        cellSize = width / 7f;
        radius = cellSize * 0.42f;

        int height = (int)(120 + cellSize * 6);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas c) {

        float w = getWidth();

        c.drawText(format.format(shownMonth.getTime()),
				   w / 2f, 70, headerPaint);

        float y = 140;

        for (int i = 0; i < 7; i++) {
            float x = i * cellSize + cellSize / 2f;
            c.drawText(week[i], x, y, weekPaint);
        }

        Calendar cal = (Calendar) shownMonth.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int offset = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        float startY = 180;

        for (int d = 1; d <= max; d++) {

            int pos = offset + d - 1;
            int row = pos / 7;
            int col = pos % 7;

            float cx = col * cellSize + cellSize / 2f;
            float cy = startY + row * cellSize + cellSize / 2f;

            boolean isToday =
				d == today.get(Calendar.DAY_OF_MONTH)
				&& shownMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				&& shownMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR);

            
            Paint bg = isToday ? todayPaint : circlePaint;

            c.drawCircle(cx, cy, radius, bg);

            Paint text = (col == 0 || col == 6) ? weekendPaint : dayPaint;

            c.drawText(String.valueOf(d), cx, cy + 15, text);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        detector.onTouchEvent(e);
        return true;
    }

    

    public void setNormalCellColor(int color) {
        normalCellColor = color;
        circlePaint.setColor(color);
        invalidate();
    }

    public void setCurrentDateCellColor(int color) {
        currentDateCellColor = color;
        todayPaint.setColor(color);
        invalidate();
    }
}

