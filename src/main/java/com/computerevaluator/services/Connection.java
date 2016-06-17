package com.computerevaluator.services;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
 * Created by erik on 6/15/16.
 */
public class Connection{

    private static final MongoClient client = new MongoClient(new ServerAddress("localhost", 27017));

    public static final MongoDatabase database = client.getDatabase("computerparts");

}
