package com.swolo.daodian.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @ClassName: PerferenceUtil
 * @Description: TODO
 * @author: huangyuan
 * @date:2014年11月14日 下午5:31:09
 */
public class PreferenceUtil {

	public static final String SYSTEM_SETTING_NAME = "SYSTEM_SETTING";

	// 是否同意接受消息推送;
	public static final String ALLOW_REVEICE_NOTICE_KEY = "ALLOW_REVEICE_NOTICE";

	// 是否使用省流量模式;
	public static final String SAVE_TRAFFIC_MODE_KEY = "SAVE_TRAFFIC_MODE";

	// 是否显示电话广告;
	public static final String SHOW_PHONE_AD_KEY = "SHOW_PHONE_AD";

	// 上一次定位城市;
	public static final String LAST_LOCATION_CITY = "last_location_city";

	// 上一次定位坐标经度;
	public static final String LAST_LOCATION_LONGITUDE = "last_location_longitude";

	// 上一次定位坐标纬度;
	public static final String LAST_LOCATION_LATITUDE = "last_location_latitude";
	// 保存session信息;
	public static final String SESSION_KEY = "session_key";
	// 保存用户电话号码;
	public static final String USER_PHONE_KEY = "user_phone_key";
	/** 闪屏图 */
	// 服务端下发闪屏图路径
	public static final String KEY_LOADING_PATH = "key_loading_path";
	// 服务端下发闪屏图有效期（开始）
	public static final String KEY_LOADING_START_TIME = "key_loading_start_time";
	// 服务端下发闪屏图有效期（结束）
	public static final String KEY_LOADING_END_TIME = "key_loading_end_time";

	//保存输入框的高度；
	public static final String KEY_INPUT_VIEW_HEIGHT_LOCATION_Y = "key_input_view_y";
	/**************************保存获取数据方法********************/

	public static SharedPreferences getSharedPreferences(Context ctx) {
		return ctx.getSharedPreferences(SYSTEM_SETTING_NAME, Activity.MODE_PRIVATE | Activity.MODE_PRIVATE);
	}

	public static SharedPreferences getSharedPreferences(Context ctx, String filename) {
		return ctx.getSharedPreferences(filename, Activity.MODE_PRIVATE | Activity.MODE_PRIVATE);
	}


	public static String getString(Context ctx, String key) {
		return getSharedPreferences(ctx).getString(key, "");
	}

	public static String getString(Context ctx, String key, String defaultValue) {
		return getSharedPreferences(ctx).getString(key, defaultValue);
	}

	public static void putString(Context ctx, String key, String value) {
		getSharedPreferences(ctx).edit().putString(key, value).commit();
	}

	public static boolean getBoolean(Context ctx, String key) {
		return getSharedPreferences(ctx).getBoolean(key, false);
	}

	public static boolean getBoolean(Context ctx, String key, boolean defValue) {
		return getSharedPreferences(ctx).getBoolean(key, defValue);
	}

	public static void putBoolean(Context ctx, String key, boolean value) {

		getSharedPreferences(ctx).edit().putBoolean(key, value).commit();
	}

	public static long getLong(Context ctx, String key, long defValue) {
		return getSharedPreferences(ctx).getLong(key, defValue);
	}

	public static void putLong(Context ctx, String key, long value) {
		getSharedPreferences(ctx).edit().putLong(key, value).commit();
	}

	public static int getInteger(Context ctx, String key, int defValue) {
		return getSharedPreferences(ctx).getInt(key, defValue);
	}

	public static void putInteger(Context ctx, String key, int value) {
		getSharedPreferences(ctx).edit().putInt(key, value).commit();
	}

}
