/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.services;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import com.seobox.db.DBManager;
import com.seobox.helpers.HelperUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author sku202
 */
public class StructuredData extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String type = request.getParameter("type");
            String report = request.getParameter("report");
            String jsonp = request.getParameter("jsonp");
            String key = request.getParameter("key");
            BasicDBObject query = new BasicDBObject("key", key);
            JSONObject json = new JSONObject();
            JSONArray arr = new JSONArray();
            try {
                try (DBManager mngr = new DBManager()) {
                    MongoDatabase db = mngr.getMongoDB();
                    Document url = db.getCollection(report).find(query).first();
//                    json=new JSONObject().put("key", key).put("data", HelperUtils.parsePageSpeedResponse(url.getString(type)))
//                            .put("type", type);                  
                }
            } catch (Exception ex) {
                out.print("error in fetching results from DB. " + ex);
                Logger.getLogger(PageSpeedInsight.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.print(jsonp + "(" + json + ")");
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

}
