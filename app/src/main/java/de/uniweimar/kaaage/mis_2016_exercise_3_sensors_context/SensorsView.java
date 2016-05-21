package de.uniweimar.kaaage.mis_2016_exercise_3_sensors_context;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.concurrent.ArrayBlockingQueue;

public class SensorsView extends View
{
	Path[] paths = new Path[4];
	Paint[] paints = new Paint[4];
	private int height;
	private int width;
	float[] values = new float[4];
	float min, max;
	float[] plotCenter = new float[4];
	public ArrayBlockingQueue<float[]> data;
	int plotHeight;


	public SensorsView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void update(float[] newValues, float magnitude)
	{
		values[0] = newValues[0];
		values[1] = newValues[1];
		values[2] = newValues[2];
		values[3] = magnitude;

//		System.out.println(values[0] + " " + values[1] + " " + values[2] + " " + values[3]);

		for (float v : values)
		{
			if (min > v) min = v;
			if (max < v) max = v;
		}

		if (data.remainingCapacity() == 0)
		{
//			data.poll();
			data.clear();

			for (Path p : paths)
				p.reset();
		}

		data.add(values);
		int x = data.size();
		for (int i = 0; i < 4; i++)
		{
			float y = values[i];
			y = (y - min) / (max - min);
			y = y * (plotHeight * (i+1) - plotHeight * i) + plotCenter[i];
			paths[i].lineTo(x, y);
		}
	}

	/**
	 * This is called during layout when the size of this view has changed. If
	 * you were just added to the view hierarchy, you're called with the old
	 * values of 0.
	 *
	 * @param w    Current width of this view.
	 * @param h    Current height of this view.
	 * @param oldw Old width of this view.
	 * @param oldh Old height of this view.
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		height = h;
		width = w;
		plotHeight = height / 5;

		data = new ArrayBlockingQueue<float[]>(width);

		Paint paint = new Paint();
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);

		for (int i = 0; i < 4; i++)
		{
			paints[i] = new Paint(paint);
			paths[i] = new Path();
			plotCenter[i] = (plotHeight * i) + (plotHeight / 2);
			paths[i].moveTo( 0, plotCenter[i]);
		}

		paints[0].setColor(Color.RED);
		paints[1].setColor(Color.GREEN);
		paints[2].setColor(Color.BLUE);
		paints[3].setColor(Color.BLACK);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		for (int i = 0; i < paths.length; i++)
		{
			canvas.drawPath(paths[i], paints[i]);
		}
	}
}