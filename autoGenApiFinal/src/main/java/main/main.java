/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import bussiness.ApiGen;
import static bussiness.ApiGen.genApiDelete;
import static bussiness.ApiGen.genApiGet;
import static bussiness.ApiGen.genApiInsert;
import static bussiness.ApiGen.genApiUpdate;
import static bussiness.ApiGen.genMain;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.Modifier;
import static utils.MongoUtils.getMongoClient;

/**
 *
 * @author NguyenDuc
 */
public class main {

    public static void main(String[] args) {
        try {
            // Get text qua phan Object name
            String objName = "Employee";

            // tuong ung voi Button GEN API
            MongoClient mongoClient = getMongoClient();
            DB db = mongoClient.getDB("api_test");

            try {
                DBCollection collection = db.createCollection(objName, new BasicDBObject());
            } catch (Exception e) {
                System.out.println("database is created");
            } finally {
                TypeSpec genApiCrud = TypeSpec.classBuilder("GenApiFinal")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(genApiGet(objName))
                        .addMethod(genApiInsert(objName))
                        .addMethod(genApiUpdate(objName))
                        .addMethod(genApiDelete(objName))
                        .addMethod(genMain(objName))
                        .build();

                ClassName MongoUtils = ClassName.get("utils", "MongoUtils");

                JavaFile javaFile1 = JavaFile.builder("storeApi", genApiCrud)
                        .addStaticImport(MongoUtils, "getMongoClient")
                        .addStaticImport(ClassName.get("spark", "Spark"), "*")
                        .addStaticImport(ClassName.get("utils", "JsonUtil"), "*")
                        .build();
                javaFile1.writeTo(System.out);
                javaFile1.writeTo(Paths.get("./src/main/java"));

                // tuong ung voi Button DEPLOY API
                Runtime.getRuntime().exec("cmd /c start cmd.exe /K \"set path=%path%;C:\\apache-maven-3.6.2\\bin && mvn compile && mvn install && mvn exec:java -Dexec.mainClass=storeApi.GenApiFinal\"");
                System.out.println("**********");
            }
        } catch (IOException ex) {
            Logger.getLogger(ApiGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
