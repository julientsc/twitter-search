package com.tscherrig.twitter.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tscherrig.twitter.data.Data;

import java.io.*;

/**
 * Created by Julien on 26.05.15.
 */
public class FileAccess {

    private static final String path ="data.json";
    private static FileAccess INSTANCE = null;
    private static Data data;

    private FileAccess() {
        try {
            this.data = FileAccess.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            this.data = new Data();
            save(path);
        }
    }

    public static FileAccess getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileAccess();
        }
        return INSTANCE;
    }

    synchronized public static void clear() {
        INSTANCE = new FileAccess();
        INSTANCE.save(path);
    }

    public static void save() {
        System.out.println("(Save as " + path + ")");
        save(path);
    }

    synchronized static private void save(String pathj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String content = gson.toJson(data);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(path);
            pw.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null)
                pw.close();
        }
    }

    synchronized private static Data load(String path) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BufferedReader br = new BufferedReader(new FileReader(path));
        Data obj = gson.fromJson(br, Data.class);
        br.close();
        return obj;
    }

    synchronized public Data getData() {
        return data;
    }
}
