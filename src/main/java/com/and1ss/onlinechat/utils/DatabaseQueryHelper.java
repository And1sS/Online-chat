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
        try {
            return UUID.fromString((String) tuple.get(key));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Timestamp getTimestampFromTupleOrNull(Tuple tuple, String key) {
        try {
            return (Timestamp) tuple.get(key);
        } catch (Exception ignored) {
            return null;
        }
    }
}
