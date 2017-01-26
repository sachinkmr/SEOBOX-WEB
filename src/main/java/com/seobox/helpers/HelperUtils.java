/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.helpers;

import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Sachin
 */
public class HelperUtils {

    public static JSONObject parsePageSpeedResponse(String str) {
        JSONObject json = new JSONObject(str);
        JSONObject result = new JSONObject();
        JSONObject failed = new JSONObject();
        JSONObject passed = new JSONObject();
        result.put("pageStats", json.getJSONObject("pageStats"));
        json = json.getJSONObject("formattedResults").getJSONObject("ruleResults");
        Set<String> names = json.keySet();
        for (String key : names) {
            JSONObject rule = json.getJSONObject(key);
            rule.remove("groups");
            if (rule.has("summary")) {
                rule.put("summary", updateSummary(rule.getJSONObject("summary")));
            }
            if (rule.has("urlBlocks")) {
                updateURLBlocks(rule);
            }
            if (rule.getDouble("ruleImpact") > 0) {
                failed.put(key, rule);
            } else {
                passed.put(key, rule);
            }
        }
        result.put("passed", passed);
        result.put("failed", failed);
        return result;
    }

    private static JSONArray updateURLBlocks(JSONObject rule) {
        JSONArray blocks = rule.getJSONArray("urlBlocks");
        for (int i = 0; i < blocks.length(); i++) {
            JSONObject block = blocks.getJSONObject(i);
            block.put("header", updateSummary(block.getJSONObject("header")));
            if (block.has("urls")) {
                JSONArray urls = block.getJSONArray("urls");
                JSONArray newUrls = new JSONArray();
                for (int j = 0; j < urls.length(); j++) {
                    JSONObject url = urls.getJSONObject(j);
                    newUrls.put(updateURL(url));
                }
                block.put("urls", newUrls);
            }

        }
        return blocks;
    }

    private static String updateURL(JSONObject url) {
        JSONObject result = url.getJSONObject("result");
        return updateSummary(result);
    }

    private static String updateSummary(JSONObject summary) {
        if (summary.has("args")) {
            String format = summary.getString("format");
            int i = 0;
            while (format.contains("{{") && format.contains("}}")) {
                String param = format.substring(format.indexOf("{{") + 2, format.indexOf("}}"));
                String value = summary.getJSONArray("args").getJSONObject(i).getString("value");
                if (param.equals("BEGIN_LINK")) {
                    format = format.replaceFirst("\\{\\{BEGIN_LINK\\}\\}", "<a href='" + value + "'>");
                    format = format.replaceFirst("\\{\\{END_LINK\\}\\}", "</a>");
                } else {
                    format = format.replaceFirst("\\{\\{" + param + "\\}\\}", value);
                }
                i++;
            }
            return format;
        } else {
            return summary.getString("format");
        }
    }
}
