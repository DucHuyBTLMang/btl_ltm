/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author NguyenDuc
 */
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoUtils {

    private static final String HOST = "localhost";
    private static final int PORT = 27017;

    public static MongoClient getMongoClient() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(HOST, PORT);
        return mongoClient;
    }
}
