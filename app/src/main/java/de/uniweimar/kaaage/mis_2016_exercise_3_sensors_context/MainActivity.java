package de.uniweimar.kaaage.mis_2016_exercise_3_sensors_context;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends AppCompatActivity implements SensorEventListener, SeekBar.OnSeekBarChangeListener
{
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private double sampleRate;
	private SensorsView viewSensor;
	private FFTView viewFFT;
	private TextView textViewSampleRate;
	private TextView textViewFFTSize;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		if (mSensor == null)
		{
			Toast.makeText(this, "No accelerometer found.", Toast.LENGTH_SHORT).show();
		}

		SeekBar seekBarSampleRate = (SeekBar) findViewById(R.id.seekBarSampleRate);
		seekBarSampleRate.setOnSeekBarChangeListener(this);

		SeekBar seekBarFFTSize = (SeekBar) findViewById(R.id.seekBarFFTSize);
		seekBarFFTSize.setOnSeekBarChangeListener(this);

		viewSensor = (SensorsView) findViewById(R.id.viewSensor);
		textViewSampleRate = (TextView) findViewById(R.id.textViewSampleRate);
		viewFFT = (FFTView) findViewById(R.id.viewFFT);
		textViewFFTSize = (TextView) findViewById(R.id.textViewFFTSize);
	}

	protected void onResume()
	{
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

	@Override
	public final void onSensorChanged(SensorEvent event)
	{
		float magnitude = (float) Math.sqrt(event.values[0]*event.values[0] + event.values[1]*event.values[1] + event.values[2]*event.values[2]);
		if (viewSensor.data != null && viewFFT.data != null)
		{
			viewSensor.update(event.values, magnitude);
			viewSensor.invalidate();
			viewFFT.update(magnitude);
			viewFFT.invalidate();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		sampleRate = progress;

		if (seekBar.getId() == R.id.seekBarSampleRate)
		{
			mSensorManager.unregisterListener(this);
			mSensorManager.registerListener(this, mSensor, (int) sampleRate * 1000 * 1000);
			textViewSampleRate.setText((int) sampleRate + " ms");
		}
		if (seekBar.getId() == R.id.seekBarFFTSize)
		{
			viewFFT.fftSize = (int) Math.round(Math.pow(2, progress));
			textViewFFTSize.setText(""+viewFFT.fftSize);
			viewFFT.fft = new FFT(viewFFT.fftSize);
			viewFFT.data = new ArrayBlockingQueue<Double>(viewFFT.fftSize);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
	}
}