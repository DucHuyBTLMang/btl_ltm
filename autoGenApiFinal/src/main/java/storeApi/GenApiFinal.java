package storeApi;

import static spark.Spark.*;
import static utils.JsonUtil.*;
import static utils.MongoUtils.getMongoClient;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import java.lang.String;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GenApiFinal {
  public void apiGetEmployee() {
    try {
      post("/getInfoEmployee", (request, response)->  {
        MongoClient mongoClient = getMongoClient();
        DB db = mongoClient.getDB("api_test");
        DBCollection peopleCol = db.getCollection("Employee");
        JSONParser parser = new JSONParser();
        BasicDBObjectBuilder whereBuilder = BasicDBObjectBuilder.start();
        JSONObject body = (JSONObject) parser.parse(request.body());
        for (Object k: body.keySet()){
             whereBuilder.append((String) k, (String)body.get((String)k));
        }
        DBObject filter = whereBuilder.get();
        DBCursor cursor = peopleCol.find(filter);
        JSONArray jsonArray = new JSONArray();
        while (cursor.hasNext()) {
          JSONObject jsonTemp = (JSONObject) parser.parse(cursor.next().toString());
          jsonArray.add(jsonTemp);
        }
        System.out.println(jsonArray.toString());
        if (jsonArray.size() == 0) {
          String res = "{\n"
                                          + "    \"data\": " + jsonArray.toJSONString()
                                          + "    \"message\": \"Không lấy được dữ liệu\",\n"
                                          + "    \"status\": \"0\"\n"
                                          + "}";
                                  return (JSONObject) new JSONParser().parse(res);
        }
        else {
          String res = "{\n"
                                          + "    \"data\": " + jsonArray.toJSONString()
                                          + "    \"message\": \"Lấy dữ liệu thành công\",\n"
                                          + "    \"status\": \"1\"\n"
                                          + "}";
                                  return (JSONObject) new JSONParser().parse(res);
        }
      } , json());
    }
    catch (Exception e) {
      System.out.println(e);
    }
  }

  public void apiInsertEmployee() {
    try {
      post("/insertInfoEmployee", (request, response)->  {
        try {
          MongoClient mongoClient = getMongoClient();
          DB db = mongoClient.getDB("api_test");
          DBCollection peopleCol = db.getCollection("Employee");
          JSONParser parser = new JSONParser();
          JSONObject body = (JSONObject) parser.parse(request.body());
          BasicDBObject doc = new BasicDBObject();
          for (Object k: body.keySet()){
               doc.append((String) k, (String) body.get((String) k));
          }
          peopleCol.insert(doc);
          String res = "{\n"
                                          + "	\"status\": \"1\",\n"
                                          + "	\"message\": \"Thêm dữ liệu thành công\"\n"
                                          + "}";
                                  return (JSONObject) parser.parse(res);
        }
        catch (Exception e) {
          String res = "{\n"
                                          + "	\"status\": \"0\",\n"
                                          + "	\"message\": \"Thêm dữ liệu thất bại\"\n"
                                          + "}";
                                  return (JSONObject) new JSONParser().parse(res);
        }
      } , json());
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  public void apiUpdateEmployee() {
    try {
      post("/updateInfoEmployee", (request, response)->  {
        try {
          MongoClient mongoClient = getMongoClient();
          DB db = mongoClient.getDB("api_test");
          DBCollection peopleCol = db.getCollection("Employee");
          JSONParser parser = new JSONParser();
          JSONObject body = (JSONObject) parser.parse(request.body());;
          // tach query tu body
                              BasicDBObjectBuilder whereBuilder = BasicDBObjectBuilder.start();JSONObject query = (JSONObject) parser.parse(body.get("query").toString());
                              for (Object k : query.keySet()) {
                                  whereBuilder.append((String) k, (String) query.get((String) k));
                              }
                              DBObject filter = whereBuilder.get();

                              // tach value tu body
                              BasicDBObjectBuilder valueBuilder = BasicDBObjectBuilder.start();
                              JSONObject valueJson = (JSONObject) parser.parse(body.get("values").toString());
                              for (Object k : valueJson.keySet()) {
                                  valueBuilder.append((String) k, (String) valueJson.get((String) k));
                              }
                              DBObject values = valueBuilder.get();

                              // bat dau update
                              DBObject valuesWithSet = new BasicDBObject();
                              valuesWithSet.put("$set", values);

                              WriteResult result = peopleCol.update(filter, valuesWithSet);
                              String res = "{\n"
                                      + "	\"status\": \"1\",\n"
                                      + "	\"message\": \"Cập nhật dữ liệu thành công\"\n"
                                      + "}";
                              return (JSONObject) parser.parse(res);}
        catch (Exception e) {
          String res = "{\n"
                                          + "	\"status\": \"0\",\n"
                                          + "	\"message\": \"Cập nhật dữ liệu thất bại\"\n"
                                          + "}";
                                  return (JSONObject) new JSONParser().parse(res);
        }
      } , json());
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  public void apiDeleteEmployee() {
    try {
      post("/deleteInfoEmployee", (request, response)->  {
        try {
          MongoClient mongoClient = getMongoClient();
          DB db = mongoClient.getDB("api_test");
          DBCollection peopleCol = db.getCollection("Employee");
          JSONParser parser = new JSONParser();
          JSONObject body = (JSONObject) parser.parse(request.body());;
          // tach query tu body
                              BasicDBObjectBuilder whereBuilder = BasicDBObjectBuilder.start();JSONObject query = (JSONObject) parser.parse(body.get("query").toString());
                          for (Object k : query.keySet()) {
                              whereBuilder.append((String) k, (String) query.get((String) k));
                          }
                          DBObject filter = whereBuilder.get();
                          // remove du lieu
                          WriteResult cursor = peopleCol.remove(filter);String res = "{\n"
                                      + "	\"status\": \"1\",\n"
                                      + "	\"message\": \"Xóa dữ liệu thành công\"\n"
                                      + "}";
                              return (JSONObject) parser.parse(res);}
        catch (Exception e) {
          String res = "{\n"
                                          + "	\"status\": \"0\",\n"
                                          + "	\"message\": \"Xóa dữ liệu thất bại\"\n"
                                          + "}";
                                  return (JSONObject) new JSONParser().parse(res);
        }
      } , json());
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  public static void main(String[] args) {
    new GenApiFinal().apiDeleteEmployee();
                new GenApiFinal().apiGetEmployee();
                new GenApiFinal().apiInsertEmployee();
                new GenApiFinal().apiUpdateEmployee();;
  }
}
