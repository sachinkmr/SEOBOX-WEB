/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sku202
 */
public class Build extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        String username = "", password = "", email = "";
        String SiteAddress = request.getParameter("siteURL");
        String CrawlerConfigFile = request.getParameter("CrawlerConfigFile").isEmpty() ? "" : new File(request.getParameter("CrawlerConfigFile")).getAbsolutePath();
        if (request.getParameterMap().containsKey("setAuthentication")
                && "on".equalsIgnoreCase(request.getParameter("setAuthentication"))) {
            username = request.getParameter("username");
            password = request.getParameter("password");
        }
        if (request.getParameterMap().containsKey("email")) {
            email = request.getParameter("email");
        }
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        // build param
        Map<String, String> map = new HashMap<>();
        map.put("SiteAddress", SiteAddress);
        map.put("Username", username);
        map.put("Password", password);
        map.put("CrawlerConfigFileLocation", CrawlerConfigFile);
        map.put("email", email);
        map.put("machine", ipAddress);
        map.put("WEB", "yes");
        String jenkinsUrl = getServletContext().getInitParameter("jenkinsUrl");
        String jenkinsJob = getServletContext().getInitParameter("jenkinsJob");
        JenkinsBuilder builder = new JenkinsBuilder(jenkinsUrl, jenkinsJob);
        out.print(builder.build(map));
        out.close();
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
