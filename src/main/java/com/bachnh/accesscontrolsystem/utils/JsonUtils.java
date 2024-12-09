//package com.bachnh.accesscontrolsystem.utils;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.json.JsonWriteFeature;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.google.gson.Gson;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//
//public class JsonUtils {
//    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
//    private static Gson gson = new Gson();
//
//    private JsonUtils() {
//        throw new IllegalStateException("Utility class");
//    }
//
//    public static String toJson(Object object) {
//        String jsonString = "";
//
//        try {
//            jsonString = getMapper().writeValueAsString(object);
//        } catch (JsonProcessingException var3) {
//            LOGGER.error("Can't build json from object");
//        }
//
//        return jsonString;
//    }
//
//    public static <T> T readFromJson(String json, TypeReference<T> valueTypeRe) {
//        if (json == null) {
//            return null;
//        } else {
//            T object = null;
//
//            try {
//                object = getMapper().readValue(json, valueTypeRe);
//            } catch (IOException var4) {
////                 var4 = var4;
//                LOGGER.error(var4.getMessage());
//            }
//
//            return object;
//        }
//    }
//
//    public static <T> T fromJson(String json, Class<T> valueType) {
//        if (json == null) {
//            return null;
//        } else {
//            T object = null;
//
//            try {
//                object = getMapper().readValue(json, valueType);
//            } catch (IOException var4) {
//                IOException e = var4;
//                LOGGER.error(e.getMessage());
//            }
//
//            return object;
//        }
//    }
//
//    public static String toJsonDateAsTimestamps(final Object object) {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        String jsonString = "";
//
//        try {
//            jsonString = mapper.writeValueAsString(object);
//        } catch (JsonProcessingException var4) {
//            JsonProcessingException e = var4;
//            e.printStackTrace();
//            jsonString = "Can't build json from object";
//        }
//
//        return jsonString;
//    }
//
//    public static Class<?> typeToClass(final Type type) {
//        if (type instanceof Class) {
//            return (Class)type;
//        } else if (type instanceof ParameterizedType) {
//            ParameterizedType parameterizedType = (ParameterizedType)type;
//            return (Class)parameterizedType.getRawType();
//        } else {
//            return null;
//        }
//    }
//
//    public static String toJsonFromObject(Object object) {
//        if (object == null) {
//            return null;
//        } else {
//            String res = null;
//
//            try {
//                res = gson.toJson(object);
//            } catch (Exception var3) {
//                Exception e = var3;
//                LOGGER.error(e.getMessage());
//            }
//
//            return res;
//        }
//    }
//
//    public static <T> T toObject(String json, Type type) {
//        if (json == null) {
//            return null;
//        } else {
//            T object = null;
//
//            try {
//                object = gson.fromJson(json, type);
//            } catch (Exception var4) {
//                Exception e = var4;
//                LOGGER.error(e.getMessage());
//            }
//
//            return object;
//        }
//    }
//
//    public static ObjectMapper getMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        mapper.configure(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.mappedFeature(), false);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
//        return mapper;
//    }
//}
//
