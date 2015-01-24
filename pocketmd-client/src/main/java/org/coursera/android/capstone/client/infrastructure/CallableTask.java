/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package org.coursera.android.capstone.client.infrastructure;

import java.util.concurrent.Callable;

import android.os.AsyncTask;
import android.util.Log;

public class CallableTask<T> extends AsyncTask<Void, Double, T> {

	private static final String TAG = CallableTask.class.getName();

	public static <V> void invoke(
			final Callable<V> call,
			final TaskCallback<V> callback) {
		new CallableTask<V>(call, callback).execute();
	}

	private Callable<T> callable;

	private TaskCallback<T> callback;

	private Exception error;

	public CallableTask(final Callable<T> callable,
			final TaskCallback<T> callback) {
		this.callable = callable;
		this.callback = callback;
	}

	@Override
	protected T doInBackground(final Void... ts) {
		T result = null;
		try {
			result = callable.call();
		} catch (final Exception e) {
			Log.e(TAG, "Error invoking callable in AsyncTask callable: "
					+ callable, e);
			error = e;
		}
		return result;
	}

	@Override
	protected void onPostExecute(final T result) {
		if (error != null) {
			callback.error(error);
		} else {
			callback.success(result);
		}
	}
}
