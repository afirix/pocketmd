package org.coursera.android.capstone.client.service;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.coursera.android.capstone.client.oauth.SecuredRestBuilder;
import org.coursera.android.capstone.client.ui.LoginActivity;
import org.coursera.android.capstone.client.unsafe.EasyHttpClient;
import org.joda.time.format.ISODateTimeFormat;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import retrofit.converter.GsonConverter;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PocketMdService {

	public static final String CLIENT_ID = "pocket-md-android";

	private static PocketMdServiceApi pocketMdService;

	public static synchronized PocketMdServiceApi getOrShowLogin(Context ctx) {
		if (pocketMdService != null) {
			return pocketMdService;
		} else {
			final Intent i = new Intent(ctx, LoginActivity.class);
			ctx.startActivity(i);
			return null;
		}
	}

	public static synchronized PocketMdServiceApi init(
			final String server,
			final String user,
			final String pass) {

		pocketMdService = new SecuredRestBuilder()
				.setLoginEndpoint(server + PocketMdServiceApi.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
				.setClient(new ApacheClient(new EasyHttpClient()))
				.setEndpoint(server)
				.setLogLevel(LogLevel.FULL)
				.setConverter(gsonConverter())
				.build()
				.create(PocketMdServiceApi.class);

		return pocketMdService;
	}
	
	private static GsonConverter gsonConverter() {
		final Gson gson = new GsonBuilder()
        	.registerTypeAdapter(Date.class, new DateConverter())
        	.create();
		return new GsonConverter(gson);
	}
	
	private static final String[] DATE_FORMATS = new String[] {  
	       "yyyy-MM-dd'T'HH:mm:ssZ",  
	       "yyyy-MM-dd"
	   };

	private static class DateConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        @Override
        public JsonElement serialize(
                final Date src,
                final Type typeOf,
                final JsonSerializationContext context) {
            return new JsonPrimitive(ISODateTimeFormat.dateTime().print(src.getTime()));
        }

		@Override
		public Date deserialize(
                final JsonElement jsonElement,
                final Type typeOf,
				final JsonDeserializationContext context) throws JsonParseException {
			final String dateString = jsonElement.getAsString();
			try {
				return new Date(Long.parseLong(dateString));
			} catch (final NumberFormatException e) {
			}
			for (final String format : DATE_FORMATS) {
				try {
					return new SimpleDateFormat(format, Locale.US)
							.parse(dateString);
				} catch (final ParseException e) {
				}
			}
			throw new JsonParseException("Unparseable date: \""
					+ jsonElement.getAsString() + "\". Supported formats: "
					+ Arrays.toString(DATE_FORMATS));
		}
	}
}
