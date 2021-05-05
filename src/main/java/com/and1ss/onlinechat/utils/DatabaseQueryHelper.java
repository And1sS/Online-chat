package com.and1ss.onlinechat.utils;

import javax.persistence.Tuple;
import java.sql.Timestamp;
import java.util.UUID;

public class DatabaseQueryHelper {
    public static Object getFromTupleOrNull(Tuple tuple, String key) {
        try {
            return tuple.get(key);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static UUID getUUIDFromTupleOrNull(Tuple tuple, String key) {
        return getUUIDFromStringOrNull((String) tuple.get(key));
    }

    public static Timestamp getTimestampFromTupleOrNull(Tuple tuple, String key) {
        try {
            return (Timestamp) tuple.get(key);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Enum getEnumFromTupleOrNull(Tuple tuple, String key, Class<? extends Enum> enumClass) {
        String stringValue = (String) getFromTupleOrNull(tuple, key);
        return getEnumFromStringOrNull(stringValue, enumClass);
    }

    public static UUID getUUIDFromStringOrNull(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Enum getEnumFromStringOrNull(String value, Class<? extends Enum> enumClass) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (Exception ignored) {
            return null;
        }
    }
}
