package com.lenovo.sdimobileclient.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.lenovo.sdimobileclient.Constants;

public class JsonDataFactory
{
    final static String                                               LOG_TAG = "JsonDataFactory";
    private static final HashMap<String, Class<? extends AbsApiData>> MAP_OBJECTS;
    private static final HashMap<String, Class<? extends AbsApiData>> ARRAY_OBJECTS;
    static {
        MAP_OBJECTS = new HashMap<String, Class<? extends AbsApiData>>();
        MAP_OBJECTS.put("OrderCounts", OrderCounts.class);
        MAP_OBJECTS.put("MachineInfo", Machine.class);
        MAP_OBJECTS.put("OrderDetail", OrderInfo.class);
        MAP_OBJECTS.put("Engineer", EnginnerInfo.class);
        MAP_OBJECTS.put("Apk", Apk.class);
        MAP_OBJECTS.put("OrderOperations", OrderOperations.class);
        ARRAY_OBJECTS = new HashMap<String, Class<? extends AbsApiData>>();
        ARRAY_OBJECTS.put("Orders", OrderInfo.class);
        ARRAY_OBJECTS.put("MatelialClasss", MatelialClass.class);
        ARRAY_OBJECTS.put("Actions", Action.class);
        ARRAY_OBJECTS.put("Unchanges", Unchange.class);
        ARRAY_OBJECTS.put("WarrantyInfo", Warranty.class);
        ARRAY_OBJECTS.put("CustomerPhones", CustomerPhone.class);
        ARRAY_OBJECTS.put("KnowledgeInfos", KnowledgeInfo.class);
        ARRAY_OBJECTS.put("AttachmentTypes", AttachmentTypes.class);
        ARRAY_OBJECTS.put("SystemNotifications", SystemNotification.class);
        ARRAY_OBJECTS.put("Material", Material.class);
        ARRAY_OBJECTS.put("OrderTasks", OrderTask.class);
        ARRAY_OBJECTS.put("TaskHistorys", TaskHistory.class);
        ARRAY_OBJECTS.put("UpChanges", UpChange.class);
        ARRAY_OBJECTS.put("TaskSources", TaskSource.class);
        ARRAY_OBJECTS.put("SourceOptions", SourceOption.class);
        ARRAY_OBJECTS.put("Attachments", Attachment.class);
        ARRAY_OBJECTS.put("Changes", Change.class);
        ARRAY_OBJECTS.put("ChangeHistories", ChangeHistory.class);
        ARRAY_OBJECTS.put("ChangeCategories", ChangeCategory.class);
        ARRAY_OBJECTS.put("ChangeTypes", ChangeType.class);
        ARRAY_OBJECTS.put("ProductBoxingList", ProductBox.class);
        ARRAY_OBJECTS.put("ReplacedPartsFlags", ReplacedPartsFlags.class);
        ARRAY_OBJECTS.put("ReplacedPartsDescs", ReplacedPartsDescs.class);
        ARRAY_OBJECTS.put("BreakDownInfos", BreakDownInfo.class);
        ARRAY_OBJECTS.put("BreakDowns", BreakDown.class);

    }

    static private <T extends AbsApiData> T getData(Class<T> c, JSONObject json) throws InstantiationException, IllegalAccessException, IllegalArgumentException, JSONException {
        T data = c.newInstance();
        data.parser(json);
        if (!data.isValid()) {
            if (Constants.DEBUG)
                Log.w(LOG_TAG, "Data not valid.\nJson:" + json);
            data = null;
        }

        return data;
    }

    static AbsApiData getData(String key, JSONObject json) {
        final Class<? extends AbsApiData> c = MAP_OBJECTS.get(key);
        if (c == null) {
            if (Constants.DEBUG) {
                Log.w(LOG_TAG, "Key not support:" + key);
                Log.w(LOG_TAG, "Json:" + json);
            }
            return null;
        }

        AbsApiData data = null;
        try {
            data = getData(c, json);
        } catch (Exception e) {
            if (Constants.DEBUG)
                Log.e(LOG_TAG, "Meet error when create ApiData from JSON.\nKey:" + key + ", Class:" + c, e);
        }

        return data;
    }

    static Class<? extends AbsApiData> getArrayClass(String key) {
        return ARRAY_OBJECTS.get(key);
    }

    static ArrayList<AbsApiData> getDataArray(String key, JSONArray jsonArray) {
        final Class<? extends AbsApiData> c = ARRAY_OBJECTS.get(key);
        if (c == null) {
            if (Constants.DEBUG) {
                Log.w(LOG_TAG, "Key not support:" + key);
                Log.w(LOG_TAG, "JsonArray:" + jsonArray);
            }
            return null;
        }

        final ArrayList<AbsApiData> list = new ArrayList<AbsApiData>();
        final int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                AbsApiData data = getData(c, json);
                if (data != null) {
                    list.add(data);
                }
            } catch (Exception e) {
                if (Constants.DEBUG)
                    Log.e(LOG_TAG, "Meet error when create ApiData from JSONArray.\nKey:" + key + ", Class:" + c, e);
            }
        }

        return list;
    }

    public static <T extends AbsApiData> ArrayList<T> getDataArray(Class<T> c, JSONArray jsonArray) {
        if (c == null) {
            return null;
        }

        final ArrayList<T> list = new ArrayList<T>();
        final int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                T data = getData(c, json);
                if (data != null) {
                    list.add(data);
                }
            } catch (Exception e) {
            }
        }

        return list;
    }

    public static RootData getData(JSONObject json) {
        RootData data = null;
        try {
            data = getData(RootData.class, json);
        } catch (Exception e) {
            if (Constants.DEBUG)
                Log.e(LOG_TAG, "Meet error when create RootData from JSON.\nJson:" + json, e);
        }
        return data;
    }

    public static RootData getDataOrThrow(JSONObject json) throws IllegalArgumentException, InstantiationException, IllegalAccessException, JSONException {
        return getData(RootData.class, json);
    }
}
