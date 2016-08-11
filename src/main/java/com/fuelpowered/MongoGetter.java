package com.fuelpowered;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by shea on 03/08/16.
 */
public class MongoGetter {
    private static final Config conf = ConfigFactory.load();
    private static MongoDatabase db = null;

    public static MongoDatabase getDB() {
        if (db == null) {
            MongoClient mongoClient = new MongoClient(new MongoClientURI(conf.getString("mongoUri")));
            db = mongoClient.getDatabase(conf.getString("mongoDB"));
            System.out.println(db);
        }
        return db;
    }
}
