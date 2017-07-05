package cz.ufal.udapi.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mvojtek on 05/07/2017.
 *
 * Serves as map with lazily synchronized string representation.
 */
public class Misc {

    private Map<String, String> map = new HashMap<>();
    private String stringRepresentation;

    private static final String PIPE = "|";
    private static final String EQUAL = "=";
    private static final String UNDERSCORE = "_";

    public Misc(String value) {
        setMapping(value);
    }

    public void setMapping(String value) {
        if (null == value) {
            map.clear();
        } else {
            map.clear();
            if ("".equals(value)) {
                stringRepresentation = UNDERSCORE;
            } else {
                stringRepresentation = value;
            }
        }
    }

    public void setMapping(Map<String, String> value) {
        if (null == value) {
            map.clear();
        } else {
            map.clear();
            map.putAll(value);
            stringRepresentation = null;
        }
    }

    public String toStringFormat() {
        if (null == stringRepresentation) {
            //build string

            if (map.isEmpty()) {
                stringRepresentation = UNDERSCORE;
            } else {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                int size = map.size();
                for (Map.Entry<String, String> item : map.entrySet()) {
                    if (null == item.getValue()) {
                        sb.append(item.getKey().toLowerCase());
                    } else {
                        sb.append(item.getKey().toLowerCase());
                        sb.append(EQUAL);
                        sb.append(item.getValue());
                    }
                    if (i < size-1) {
                        sb.append(PIPE);
                    }
                    i++;
                }

                stringRepresentation = sb.toString();
            }

        }

        return stringRepresentation;
    }
}
