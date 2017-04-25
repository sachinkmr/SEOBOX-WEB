/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.web;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 *
 * @author sku202
 */
public class JenkinsBuilder {

    private final String jenkinsUrl;
    private final String jenkinsJob;
    private final String builds;
    private JenkinsServer js;

    public JenkinsBuilder(String jenkinsUrl, String jenkinsJob) {
        this.jenkinsUrl = jenkinsUrl;
        this.jenkinsJob = jenkinsJob;
        this.builds = jenkinsUrl + "job/" + jenkinsJob;
        js = new JenkinsServer(URI.create(jenkinsUrl));
    }

    public JSONObject build(Map<String, String> map) {
        JSONObject json = new JSONObject();
        try {
            json.put("builds", builds);
            js = new JenkinsServer(URI.create(jenkinsUrl));
            QueueReference queueRef = js.getJob(jenkinsJob).build(map, true);
            QueueItem queueItem = js.getQueueItem(queueRef);
            int buildNumber = getBuildNumber(js);
            Thread.sleep(2000);
            if (js.getJob(jenkinsJob).isInQueue()) {
                json.put("buildID", buildNumber);
                json.put("status", "inQueue");
                json.put("reason", queueItem.getWhy());
                json.put("url", queueItem.getUrl());
            } else if (queueItem.isCancelled()) {
                json.put("buildID", buildNumber);
                json.put("status", "cancel");
                json.put("reason", queueItem.getWhy());
                json.put("url", queueItem.getUrl());
            } else {
                com.offbytwo.jenkins.model.Build build = js.getJob(jenkinsJob).getLastBuild();
                if (build.details().isBuilding()) {
                    json.put("buildID", build.getNumber());
                    json.put("url", build.getUrl() + "/console");
                    json.put("reason", "");
                    json.put("status", "Executing");
                }
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(JenkinsBuilder.class.getName()).log(Level.SEVERE, null, ex);
            json.put("url", "");
            json.put("reason", ex.getMessage());
            json.put("status", "error");
        }
        return json;
    }

    public JSONObject getBuildInfo(int buildID, int start, String queueUrl) {
        JSONObject json = new JSONObject();
        try {
            BuildWithDetails build = js.getJob(jenkinsJob).getLastBuild().details();
            if (!js.getJob(jenkinsJob).isInQueue() && !build.isBuilding()) {
                HttpResponse res = getInfo(js.getJob(jenkinsJob).getLastBuild().getUrl() + "logText/progressiveHtml",
                        Integer.toString(start));
                String info = EntityUtils.toString(res.getEntity(), "utf-8").replaceAll("\\r\\n", "<br/>");
                json.put("info", info);
                EntityUtils.consumeQuietly(res.getEntity());
                HttpClientUtils.closeQuietly(res);
                json.put("status", "Complete");
                json.put("buildID", buildID);
                json.put("polling", false);
                json.put("url", build.getUrl() + "console");
//                json.put("report", getReportUrl(build.details().getConsoleOutputText()));
            } else if (js.getJob(jenkinsJob).isInQueue() && build.getNumber() < buildID) {
                QueueItem queueItem = getQueueItemFromList(js.getQueue().getItems(), queueUrl);
                json.put("status", "inQueue");
                json.put("buildID", buildID);
                json.put("reason", queueItem.getWhy());
                json.put("url", queueItem.getUrl());
                json.put("polling", true);
                if (queueItem.isCancelled()) {
                    json.put("status", "Canceled");
                }
            } else if (build.isBuilding() && build.getNumber() == buildID) {
                HttpResponse res = getInfo(js.getJob(jenkinsJob).getLastBuild().getUrl() + "logText/progressiveHtml",
                        Integer.toString(start));
                int length = Integer.parseInt(res.getFirstHeader("X-Text-Size").getValue());
                json.put("buildID", buildID);
                json.put("url", builds + "/" + json.getInt("buildID") + "/console");
                json.put("reason", "");
                json.put("polling", true);
                json.put("status", "Executing");
                json.put("length", length);
                String info = EntityUtils.toString(res.getEntity(), "utf-8").replaceAll("\\r\\n", "<br/>");
                json.put("info", info);
                EntityUtils.consumeQuietly(res.getEntity());
                HttpClientUtils.closeQuietly(res);
            } 
        } catch (IOException ex) {
            Logger.getLogger(JenkinsBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    private int getBuildNumber(JenkinsServer js) throws IOException {
        if (js.getJob(jenkinsJob).isInQueue()) {
            try {
                List<QueueItem> list = new ArrayList<>();
                for (QueueItem item : js.getQueue().getItems()) {
                    if (item.getTask().getName().equals(js.getJob(jenkinsJob).getDisplayName())) {
                        list.add(item);
                    }
                }
                return list.size() + js.getJob(jenkinsJob).getNextBuildNumber() - 1;
            } catch (IOException ex) {
                Logger.getLogger(JenkinsBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    private HttpResponse getInfo(String url, String start) throws IOException {
        return Request.Post(url).bodyForm(Form.form().add("start", start).build()).execute().returnResponse();
    }

    private QueueItem getQueueItemFromList(List<QueueItem> items, String url) {
        for (QueueItem item : items) {
            if (url.equalsIgnoreCase(item.getUrl())) {
                return item;
            }
        }
        return null;
    }

    public JenkinsServer getJs() {
        return js;
    }

    public String getJenkinsUrl() {
        return jenkinsUrl;
    }

    public String getJenkinsJob() {
        return jenkinsJob;
    }

    public String getBuilds() {
        return builds;
    }
}
