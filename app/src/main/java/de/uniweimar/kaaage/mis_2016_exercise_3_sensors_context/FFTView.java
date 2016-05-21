package de.uniweimar.kaaage.mis_2016_exercise_3_sensors_context;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public class FFTView extends View
{
	private Path path = new Path();
	private Paint paint = new Paint();
	private int height;
	private int width;
	float min, max;
	float plotCenter;
	public ArrayBlockingQueue<Double> data;
	int plotHeight;
	int fftSize = 512;
	FFT fft = new FFT(fftSize);

	public FFTView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
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
		plotHeight = height;

		data = new ArrayBlockingQueue<Double>(fftSize);

		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLUE);

		path = new Path();
		plotCenter = plotHeight / 2;
		path.moveTo( 0, plotCenter);
	}

	public void update(float magnitude)
	{
		if (data.remainingCapacity() == 0)
		{
			data.poll();
			path.reset();
		}

		data.add((double) magnitude);

		if (data.remainingCapacity() > 0)
			return;

		double[] re = new double[fftSize];
		double[] im = new double[fftSize];
		double[] fft_m = new double[fftSize];

		Iterator itr = data.iterator();
		int j = 0;
		while (itr.hasNext())
		{
			re[j] = (double) itr.next();
			j++;
		}

		fft.fft(re, im);

		for (int i = 0; i < this.fftSize; ++i)
		{
			double m = Math.sqrt(re[i]*re[i] + im[i]*im[i]);

			fft_m[i] = m;

			if (min > m) min = (float) m;
			if (max < m) max = (float) m;

			float y = (float) m;
			y = (y - min) / (max - min);
			y = y * plotHeight + plotCenter;

			path.lineTo(i, y);
		}

		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		canvas.drawPath(path, paint);
	}}