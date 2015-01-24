package org.coursera.android.capstone.client.infrastructure;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.common.collect.Maps;

public class PropertiesManager {
	
	public static final String APP_SERVER = "app.server";
    public static final String MIN_CHECKINS = "min.checkins";

	private static final String TAG = PropertiesManager.class.getName();

	private static final String[] PROPERTIES_FILES = { "app.config" };

	private static final Map<Object, Object> loadedProperties = Maps
			.newHashMap();

	public static String getProperty(final Context context, final String key) {
		if (loadedProperties.isEmpty()) {
			load(context);
		}
		return loadedProperties.get(key).toString();
	}

	private static void load(final Context context) {
		final AssetManager assetManager = context.getResources().getAssets();
		for (final String propertyFile : PROPERTIES_FILES) {
			try {
				final Properties properties = new Properties();
				properties.load(assetManager.open(propertyFile));
				for (final Map.Entry<Object, Object> entry : properties
						.entrySet()) {
					loadedProperties.put(entry.getKey(), entry.getValue());
				}
			} catch (final IOException e) {
				Log.e(TAG, String.format("Cannot read %s", propertyFile), e);
				throw new RuntimeException(e);
			}
		}
	}
}
