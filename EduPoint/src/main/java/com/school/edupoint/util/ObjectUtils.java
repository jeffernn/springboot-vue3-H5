package com.school.edupoint.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author lwlianghehe@gmail.com
 * @date 2025/05/29 11:43
 */
public class ObjectUtils {
    public static Map objToMap(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(obj, Map.class);
    }
}
