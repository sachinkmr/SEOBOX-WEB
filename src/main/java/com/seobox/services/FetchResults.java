/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.services;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.seobox.db.DBManager;

/**
 *
 * @author sku202
 */
public class FetchResults extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
            String testName = request.getParameter("test_name");
            int limit = Integer.parseInt(request.getParameter("limit"));
            String report = request.getParameter("report");
            String jsonp = request.getParameter("jsonp");
            int skip = Integer.parseInt(request.getParameter("skip"));
            BasicDBObject query = new BasicDBObject("test_name", testName);
            JSONObject json = new JSONObject();
            JSONArray arr = new JSONArray();

            try {
                DBManager mngr = new DBManager();
                MongoDatabase db = mngr.getMongoDB();
                FindIterable<Document> find = db.getCollection(report).find(query).sort(new BasicDBObject("_id", 1))
                        .skip(skip).limit(limit);
                for (Document url : find) {
                    if (url.containsKey("status")) {
                        arr.put(new JSONObject().put("status", url.getString("status")).put("time", url.getString("time"))
                                .put("step", url.getString("step")).put("detail", url.getString("detail"))
                                .put("test_name", url.getString("test_name")));
                    }
                }
                json.put("rows", arr);
                json.put("total_rows", arr.length());
                mngr.close();
            } catch (Exception ex) {
                out.print("error in fetching results from DB. " + ex);
            }
            out.print(jsonp + "(" + json + ")");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on
    // the + sign on the left to edit the code.">
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
