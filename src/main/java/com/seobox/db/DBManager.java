/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seobox.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Sachin
 */
public class DBManager implements AutoCloseable {
private final MongoClient mongo;
	private final MongoDatabase mongoDB;

	public DBManager() {
		String host = "localhost";
		int port = 27017;
		String db = "SEOBOX";
		try {
			Context ctx = new InitialContext();
			Context env = (Context) ctx.lookup("java:comp/env");
			host = (String) env.lookup("dbHost");
			port = (Integer) env.lookup("dbPort");
			db = (String) env.lookup("dbName");
			String user = (String) env.lookup("dbUsername");
			String pass = (String) env.lookup("dbPassword");
		} catch (NamingException ex) {
			Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		mongo = new MongoClient(host, port);
		mongoDB = mongo.getDatabase(db);
	}

	public MongoDatabase getMongoDB() {
		return mongoDB;
	}

	public void close() {
		mongo.close();
	}

}
