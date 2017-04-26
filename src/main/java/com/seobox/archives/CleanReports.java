/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.archives;

import com.seobox.web.JenkinsBuilder;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author sku202
 */
public class CleanReports extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            JenkinsBuilder builder = new JenkinsBuilder(getServletContext().getInitParameter("jenkinsUrl"), getServletContext().getInitParameter("jenkinsJob"));
            if (!builder.getJs().getJob(builder.getJenkinsJob()).isBuildable() || builder.getJs().getJob(builder.getJenkinsJob()).isInQueue()) {
                return;
            }
            File data = new File(getServletContext().getRealPath("Reports"));
            List<String> list = new ArrayList<>();
            filter(data, list);
            out.println(list);
            for (String name : list) {
                FileUtils.deleteQuietly(new File(name));
            }
        }
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

    private void filter(File data, List<String> list) {
        if (data.isDirectory()) {
            String reg = "\\d{2}-\\w{3,}-\\d{4}_\\d{2}-\\d{2}-\\d{2}-\\d{3}[A|P]M";
            File[] files = data.listFiles();
            if (data.getName().matches(reg)) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".html")) {
                        return;
                    }
                }
                list.add(data.getAbsolutePath());
            } else {
                if (files.length == 0) {
                    list.add(data.getAbsolutePath());
                } else {
                    for (File file : files) {
                        filter(file, list);
                    }
                }
            }
        }
    }
}
