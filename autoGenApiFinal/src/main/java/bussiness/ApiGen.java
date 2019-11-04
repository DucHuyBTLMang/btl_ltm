package bussiness;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import utils.JsonUtil.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Modifier;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import static utils.JsonUtil.json;
import static utils.JsonUtil.toJson;
import static utils.MongoUtils.getMongoClient;

/**
 *
 * @author NguyenDuc
 */
public class ApiGen {

    public static MethodSpec genApiGet(String objectName) {
        ClassName arrayList = ClassName.get("com.mongodb", "MongoClient");
        return MethodSpec.methodBuilder("apiGet" + objectName)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC) // pham vi cua ham co the dung truy cap
                .beginControlFlow("try")
                .beginControlFlow("post(\"/getInfo$L\", (request, response)-> ", objectName)
                .addStatement("$T mongoClient = getMongoClient()", MongoClient.class)
                .addStatement("$T db = mongoClient.getDB(\"api_test\")", DB.class)
                .addStatement("$T peopleCol = db.getCollection(\"$L\")", DBCollection.class, objectName)
                .addStatement("$T parser = new $T()", JSONParser.class, JSONParser.class)
                .addCode("$T whereBuilder = $T.start();\n"
                        + "$T body = ($T) parser.parse(request.body());\n"
                        + "for (Object k: body.keySet()){\n"
                        + "     whereBuilder.append((String) k, (String)body.get((String)k));\n"
                        + "}\n"
                        + "$T filter = whereBuilder.get();\n",
                        BasicDBObjectBuilder.class,
                        BasicDBObjectBuilder.class,
                        JSONObject.class,
                        JSONObject.class,
                        DBObject.class
                )
                .addStatement("$T cursor = peopleCol.find(filter)", DBCursor.class)
                .addStatement("$T jsonArray = new $T()", JSONArray.class, JSONArray.class)
                .beginControlFlow("while (cursor.hasNext())")
                .addStatement("$T jsonTemp = (JSONObject) parser.parse(cursor.next().toString())", JSONObject.class)
                .addStatement("jsonArray.add(jsonTemp)")
                .endControlFlow()
                .addStatement("System.out.println(jsonArray.toString())")
                .beginControlFlow("if (jsonArray.size() == 0)")
                .addStatement("String res = \"{\\n\"\n"
                        + "                            + \"    \\\"data\\\": \" + jsonArray.toJSONString()\n"
                        + "                            + \"    \\\"message\\\": \\\"Không lấy được dữ liệu\\\",\\n\"\n"
                        + "                            + \"    \\\"status\\\": \\\"0\\\"\\n\"\n"
                        + "                            + \"}\";\n"
                        + "                    return (JSONObject) new JSONParser().parse(res)")
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement("String res = \"{\\n\"\n"
                        + "                            + \"    \\\"data\\\": \" + jsonArray.toJSONString()\n"
                        + "                            + \"    \\\"message\\\": \\\"Lấy dữ liệu thành công\\\",\\n\"\n"
                        + "                            + \"    \\\"status\\\": \\\"1\\\"\\n\"\n"
                        + "                            + \"}\";\n"
                        + "                    return (JSONObject) new JSONParser().parse(res)")
                .endControlFlow()
                .endControlFlow(", json())")
                .endControlFlow()
                .beginControlFlow("catch (Exception e)")
                .addStatement("System.out.println(e)")
                .endControlFlow()
                .build();
    }

    public static MethodSpec genApiInsert(String objectName) {
        ClassName arrayList = ClassName.get("com.mongodb", "MongoClient");
        return MethodSpec.methodBuilder("apiInsert" + objectName)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC) // pham vi cua ham co the dung truy cap
                .beginControlFlow("try")
                .beginControlFlow("post(\"/insertInfo$L\", (request, response)-> ", objectName)
                .beginControlFlow("try")
                .addStatement("$T mongoClient = getMongoClient()", MongoClient.class)
                .addStatement("$T db = mongoClient.getDB(\"api_test\")", DB.class)
                .addStatement("$T peopleCol = db.getCollection(\"$L\")", DBCollection.class, objectName)
                .addStatement("$T parser = new $T()", JSONParser.class, JSONParser.class)
                .addCode("$T body = ($T) parser.parse(request.body());\n"
                        + "$T doc = new $T();\n"
                        + "for (Object k: body.keySet()){\n"
                        + "     doc.append((String) k, (String) body.get((String) k));\n"
                        + "}\n",
                        JSONObject.class,
                        JSONObject.class,
                        BasicDBObject.class,
                        BasicDBObject.class
                )
                .addStatement("peopleCol.insert(doc)", DBCursor.class)
                .addStatement("String res = \"{\\n\"\n"
                        + "                            + \"	\\\"status\\\": \\\"1\\\",\\n\"\n"
                        + "                            + \"	\\\"message\\\": \\\"Thêm dữ liệu thành công\\\"\\n\"\n"
                        + "                            + \"}\";\n"
                        + "                    return (JSONObject) parser.parse(res)")
                .endControlFlow()
                .beginControlFlow("catch (Exception e)")
                .addStatement("String res = \"{\\n\"\n"
                        + "                            + \"	\\\"status\\\": \\\"0\\\",\\n\"\n"
                        + "                            + \"	\\\"message\\\": \\\"Thêm dữ liệu thất bại\\\"\\n\"\n"
                        + "                            + \"}\";\n"
                        + "                    return (JSONObject) new JSONParser().parse(res)")
                .endControlFlow()
                .endControlFlow(", json())")
                .endControlFlow()
                .beginControlFlow("catch (Exception e)")
                .addStatement("System.out.println(e.toString())")
                .endControlFlow()
                .build();
    }

    public static MethodSpec genApiUpdate(String objectName) {
        ClassName arrayList = ClassName.get("com.mongodb", "MongoClient");
        return MethodSpec.methodBuilder("apiUpdate" + objectName)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC) // pham vi cua ham co the dung truy cap
                .beginControlFlow("try")
                .beginControlFlow("post(\"/updateInfo$L\", (request, response)-> ", objectName)
                .beginControlFlow("try")
                .addStatement("$T mongoClient = getMongoClient()", MongoClient.class)
                .addStatement("$T db = mongoClient.getDB(\"api_test\")", DB.class)
                .addStatement("$T peopleCol = db.getCollection(\"$L\")", DBCollection.class, objectName)
                .addStatement("$T parser = new $T()", JSONParser.class, JSONParser.class)
                .addStatement("$T body = ($T) parser.parse(request.body());", JSONObject.class, JSONObject.class)
                .addCode("// tach query tu body\n"
                        + "                    $T whereBuilder = $T.start();",
                        BasicDBObjectBuilder.class,
                        BasicDBObjectBuilder.class
                )
                .addCode("JSONObject query = (JSONObject) parser.parse(body.get(\"query\").toString());\n"
                        + "                    for (Object k : query.keySet()) {\n"
                        + "                        whereBuilder.append((String) k, (String) query.get((String) k));\n"
                        + "                    }\n"
                        + "                    DBObject filter = whereBuilder.get();\n"
                        + "\n"
                        + "                    // tach value tu body\n"
                        + "                    BasicDBObjectBuilder valueBuilder = BasicDBObjectBuilder.start();\n"
                        + "                    JSONObject valueJson = (JSONObject) parser.parse(body.get(\"values\").toString());\n"
                        + "                    for (Object k : valueJson.keySet()) {\n"
                        + "                        valueBuilder.append((String) k, (String) valueJson.get((String) k));\n"
                        + "                    }\n"
                        + "                    DBObject values = valueBuilder.get();\n"
                        + "\n"
                        + "                    // bat dau update\n"
                        + "                    DBObject valuesWithSet = new BasicDBObject();\n"
                        + "                    valuesWithSet.put(\"$$set\", values);\n"
                        + "\n"
                        + "                    $T result = peopleCol.update(filter, valuesWithSet);\n"
                        + "                    String res = \"{\\n\"\n"
                        + "                            + \"	\\\"status\\\": \\\"1\\\",\\n\"\n"
                        + "                            + \"	\\\"message\\\": \\\"Cập nhật dữ liệu thành công\\\"\\n\"\n"
                        + "                            + \"}\";\n"
                        + "                    return (JSONObject) parser.parse(res);",
                        WriteResult.class)
                .endControlFlow()
                .beginControlFlow("catch (Exception e)")
                .addStatement("String res = \"{\\n\"\n"
                        + "                            + \"	\\\"status\\\": \\\"0\\\",\\n\"\n"
                        + "                            + \"	\\\"message\\\": \\\"Cập nhật dữ liệu thất bại\\\"\\n\"\n"
                        + "                            + \"}\";\n"
                        + "                    return (JSONObject) new JSONParser().parse(res)")
                .endControlFlow()
                .endControlFlow(", json())")
                .endControlFlow()
                .beginControlFlow("catch (Exception e)")
                .addStatement("System.out.println(e.toString())")
                .endControlFlow()
                .build();
    }

    public static MethodSpec genApiDelete(String objectName) {
        ClassName arrayList = ClassName.get("com.mongodb", "MongoClient");
        return MethodSpec.methodBuilder("apiDelete" + objectName)
                .returns(void.class)
                .addModifiers(Modifier.PUBLIC) // pham vi cua ham co the dung truy cap
                .beginControlFlow("try")
                .beginControlFlow("post(\"/deleteInfo$L\", (request, response)-> ", objectName)
                .beginControlFlow("try")
                .addStatement("$T mongoClient = getMongoClient()", MongoClient.class)
                .addStatement("$T db = mongoClient.getDB(\"api_test\")", DB.class)
                .addStatement("$T peopleCol = db.getCollection(\"$L\")", DBCollection.class, objectName)
                .addStatement("$T parser = new $T()", JSONParser.class, JSONParser.class)
                .addStatement("$T body = ($T) parser.parse(request.body());", JSONObject.class, JSONObject.class)
                .addCode("// tach query tu body\n"
                        + "                    $T whereBuilder = $T.start();",
                        BasicDBObjectBuilder.class,
                        BasicDBObjectBuilder.class
                )
                .addCode("JSONObject query = (JSONObject) parser.parse(body.get(\"query\").toString());\n"
                        + "                for (Object k : query.keySet()) {\n"
                        + "                    whereBuilder.append((String) k, (String) query.get((String) k));\n"
                        + "                }\n"
                        + "                DBObject filter = whereBuilder.get();\n"
                        + "                // remove du lieu\n"
                        + "                $T cursor = peopleCol.remove(filter);",
                        WriteResult.class)
                .addCode("String res = \"{\\n\"\n"
                        + "                            + \"	\\\"status\\\": \\\"1\\\",\\n\"\n"
                        + "                            + \"	\\\"message\\\": \\\"Xóa dữ liệu thành công\\\"\\n\"\n"
                        + "                            + \"}\";\n"
                        + "                    return (JSONObject) parser.parse(res);")
                .endControlFlow()
                .beginControlFlow("catch (Exception e)")
                .addStatement("String res = \"{\\n\"\n"
                        + "                            + \"	\\\"status\\\": \\\"0\\\",\\n\"\n"
                        + "                            + \"	\\\"message\\\": \\\"Xóa dữ liệu thất bại\\\"\\n\"\n"
                        + "                            + \"}\";\n"
                        + "                    return (JSONObject) new JSONParser().parse(res)")
                .endControlFlow()
                .endControlFlow(", json())")
                .endControlFlow()
                .beginControlFlow("catch (Exception e)")
                .addStatement("System.out.println(e.toString())")
                .endControlFlow()
                .build();
    }

    public static MethodSpec genMain(String objectName) {
        return MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("new GenApiFinal().apiDelete$L();\n"
                        + "        new GenApiFinal().apiGet$L();\n"
                        + "        new GenApiFinal().apiInsert$L();\n"
                        + "        new GenApiFinal().apiUpdate$L();", objectName, objectName, objectName, objectName)
                .build();
    }
}
