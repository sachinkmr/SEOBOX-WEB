/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.web;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 *
 * @author sku202
 */
public class Polling extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        try (PrintWriter out = response.getWriter()) {
            String jenkinsUrl = getServletContext().getInitParameter("jenkinsUrl");
            String jenkinsJob = getServletContext().getInitParameter("jenkinsJob");
            String builds = jenkinsUrl + "job/" + jenkinsJob;
            JenkinsServer js = new JenkinsServer(URI.create(jenkinsUrl));
            JobWithDetails job = js.getJob(jenkinsJob);
            int buildID = Integer.parseInt(request.getParameter("buildID"));
            BuildWithDetails build = job.getLastBuild().details();
            if (!job.isInQueue() && !build.isBuilding()) {
                json.put("status", "Complete");
                json.put("buildID", buildID);
                json.put("polling", false);
                json.put("url", job.getLastBuild().getUrl() + "console");
                json.put("report", getReportUrl(job.getLastBuild().details().getConsoleOutputText()));
//                json.put("info", job.getLastBuild().details().getConsoleOutputText().replaceAll("\\r\\n", "<br/>"));
            } else if (job.isInQueue() || build.getNumber() < buildID) {
                String url = request.getParameter("queueUrl");
                QueueItem queueItem = getQueueItemFromList(js.getQueue().getItems(), url);
                json.put("status", "inQueue");
                json.put("buildID", buildID);
                json.put("reason", queueItem.getWhy());
                json.put("url", queueItem.getUrl());
                json.put("polling", true);
                if (queueItem.isCancelled()) {
                    json.put("status", "Canceled");
                }
            } else if (build.isBuilding() && build.getNumber() == buildID) {
                int start = Integer.parseInt(request.getParameter("start"));
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
            } else {

            }
            out.print(json);
        }
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

    private String getReportUrl(String text) {
        if (text.contains("Report Generated")) {
            text = text.substring(text.indexOf("Report Generated:") + 17);
            return text.substring(0, text.indexOf("Report.html") + 11);
        }
        return "";
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
