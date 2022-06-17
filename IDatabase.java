package nl.ardemium;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public interface IDatabase {

    String getId();
    void setId(String id);

    static void initDb(String dbname) throws IOException {
        File file = new File("src/main/resources/database");
        file.mkdir();
        FileWriter fileWriter = new FileWriter("src/main/resources/database/" + dbname + ".json");
        fileWriter.append("[]");
        fileWriter.close();
    }

    static <Generic> Generic getFromDb(Generic type, String dbname, String id) throws FileNotFoundException {
        Gson gson = new Gson();
        FileReader fileReader = new FileReader("src/main/resources/database/" + dbname + ".json");
        JsonArray db = gson.fromJson(fileReader, JsonArray.class);
        for (JsonElement jsonElement :
                db) {
            IDatabase object = gson.fromJson(jsonElement, (Type) type.getClass());
            if(object.getId().equals(id)){
                return (Generic) object;
            }
        }
        return null;
    }

    static <Generic> void removeFromDb(Generic type, String dbname, String id) throws IOException {
        Gson gson = new Gson();
        ArrayList<Generic> newdb = new ArrayList<>();
        FileReader fileReader = new FileReader("src/main/resources/database/" + dbname + ".json");
        JsonArray db = gson.fromJson(fileReader, JsonArray.class);
        boolean removed = false;
        for (int i = 0; i < db.size(); i++) {
            IDatabase iDatabase = gson.fromJson(db.get(i).getAsJsonObject(), (Type) type.getClass());
            if(removed){
                iDatabase.setId(String.valueOf(i-1));
                newdb.add((Generic) iDatabase);
            }
            else if (id.equals(iDatabase.getId())){
                removed = true;
            }
            else{
                iDatabase.setId(String.valueOf(i));
                newdb.add((Generic) iDatabase);
            }
        }
        setDb(newdb, dbname);
    }

    static <Generic> void addToDb(Generic object, String dbname) throws IOException {
        ArrayList<Generic> db = getDb(object,dbname);
        IDatabase iDatabase = (IDatabase) object;
        iDatabase.setId(String.valueOf(getDbSize(dbname)));
        db.add((Generic) iDatabase);
        setDb(db, dbname);
    }

    static <Generic> int getDbSize(String dbname) throws FileNotFoundException {
        Gson gson = new Gson();
        FileReader fileReader = new FileReader("src/main/resources/database/" + dbname + ".json");
        JsonArray db = gson.fromJson(fileReader, JsonArray.class);
        return db.size();
    }

    static <Generic> ArrayList<Generic> getDb(Generic type, String dbname) throws FileNotFoundException {
        Gson gson = new Gson();
        ArrayList<Generic> output = new ArrayList<>();
        FileReader fileReader = new FileReader("src/main/resources/database/" + dbname + ".json");
        JsonArray db = gson.fromJson(fileReader, JsonArray.class);
        for (JsonElement jsonElement :
                db) {
            output.add(gson.fromJson(jsonElement, (Type) type.getClass()));
        }
        return output;
    }

    static <Generic> void setDb(ArrayList<Generic> db, String dbname) throws IOException {
        Gson gson = new Gson();
        FileWriter fileWriter = new FileWriter("src/main/resources/database/" + dbname + ".json");
        gson.toJson(db, fileWriter);
        fileWriter.close();
    }
}
