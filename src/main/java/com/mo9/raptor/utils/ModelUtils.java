package com.mo9.raptor.utils;

import com.mo9.raptor.utils.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gqwu on 2018/1/23.
 */
public class ModelUtils {

    private static Logger logger = Log.get();

    private static final String regex = "\\$\\{.*?}";

    /**
     * 缺少所需参数时，返回null
     * @param variables
     * @return
     */
    public static String process (String model, Map<String, String> variables) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(model);

        StringBuffer out = new StringBuffer();
        while (matcher.find()) {
            String variable = matcher.group();
            variable = variable.substring(2, variable.length() -1).trim();
            String data = variables.get(variable);
            if (data == null) {
                logger.error("********************** 没有模板所需该参数 {} ***********************", variable);
                return null;
            } else {
                matcher.appendReplacement(out, data);
            }
        }
        matcher.appendTail(out);
        return out.toString();
    }

    public static List<String> variableAnalyze (String model) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(model);
        List<String> variables = new ArrayList<String>();

        while (matcher.find()) {
            String variable = matcher.group();
            variable = variable.substring(2, variable.length() -1).trim();
            variables.add(variable);
        }
        return variables;
    }
}
