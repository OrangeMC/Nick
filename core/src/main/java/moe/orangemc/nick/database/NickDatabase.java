package moe.orangemc.nick.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NickDatabase {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public NickDatabase(String host) {
        mongoClient = MongoClients.create("mongodb://" + host + "/?uuidRepresentation=STANDARD");
        mongoDatabase = mongoClient.getDatabase("orangemc");
    }

    public void setNick(UUID playerUid, String nick, String originName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("nick");
        Document selector = new Document("uuid", playerUid);
        boolean has = collection.find(selector).first() != null;
        Map<String, Object> data = new HashMap<>();
        data.put("uuid", playerUid);
        data.put("nick", nick);
        data.put("originName", originName);
        if (has) {
            BasicDBObject setData = new BasicDBObject("$set", new Document(data));
            collection.updateOne(selector, setData);
        } else {
            collection.insertOne(new Document(data));
        }
    }

    public String getNick(UUID playerUid) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("nick");
        Document selector = new Document("uuid", playerUid);
        Document doc = collection.find(selector).first();
        if (doc == null) {
            return null;
        }
        return doc.getString("nick");
    }

    public String getOriginName(UUID playerUid) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("nick");
        Document selector = new Document("uuid", playerUid);
        Document doc = collection.find(selector).first();
        if (doc == null) {
            return null;
        }
        return doc.getString("originName");
    }

    public void resetNick(UUID playerUid) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("nick");
        Document selector = new Document("uuid", playerUid);
        boolean has = collection.find(selector).first() != null;
        if (has) {
            collection.deleteOne(selector);
        }
    }

    public void close() {
        mongoClient.close();
    }
}
