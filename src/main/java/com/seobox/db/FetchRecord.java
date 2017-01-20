/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.db;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
 * @author Sachin
 */
public class FetchRecord extends HttpServlet {

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
            String id = request.getParameter("id");
            String report = request.getParameter("report");
            int limit = Integer.parseInt(request.getParameter("limit"));
            int skip = Integer.parseInt(request.getParameter("skip"));
            BasicDBObject query = new BasicDBObject("id", id);            
            try (DBManager mngr = new DBManager()) {
                MongoDatabase db = mngr.getMongoDB();
                Document doc = db.getCollection(report).find(query).first();
                List<Document> logs = (List<Document>) doc.get("logs");
                JSONArray arr = new JSONArray();
                int size = logs.size() > limit + skip ? limit + skip : logs.size();
                for (Document d : logs.subList(skip, size)) {
                    arr.put(d.toJson());
                }   
                JSONObject json = new JSONObject();
                json.put("logs", arr.toString());
                json.put("totalRecords", logs.size());
                json.put("name", doc.get("name"));
                json.put("id", doc.get("id"));
                out.print(json.toString());      
            }
                 
        } catch (Exception ex) {
            Logger.getLogger(FetchRecord.class.getName()).log(Level.SEVERE, null, ex);
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
