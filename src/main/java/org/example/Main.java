package org.example;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello and welcome!");
        JSONArray parentJson = new JSONArray();

        try {
            String url = "https://access.redhat.com/support/policy/updates/rhel-app-streams-life-cycle";
            Document doc = Jsoup.connect(url).timeout(30 * 1000).get();

            System.out.println("Proceeding with Data retrieval");

            Element subheadingRHEL8 = doc.select("h2.subheading:contains(RHEL 8 Application Streams Release Life Cycle)").first();
            //Element subheadingRHEL9 = doc.select("h2.subheading:contains(RHEL 9 Application Streams Release Life Cycle)").first();

            if (subheadingRHEL8 != null) {
                retrieveData(subheadingRHEL8, parentJson, "RHEL 8 Application Streams");
            } else {
                System.out.println("Subheading not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void retrieveData(Element subheading, JSONArray parentJson, String key) throws JSONException {
        // Find the parent table under the subheading
        Element table = subheading.closest("div").select("table.table").first();

        // Iterate over the rows of the table (skip the header row)
        Elements rows = table.select("tbody tr");
        for (Element row: rows) {
            // Extract data from the table cells, first to fourth column
            String appStream = row.select("td").get(0).text();
            String releaseDate = row.select("td").get(1).text();
            String retirementDate = row.select("td").get(2).text();
            String release = row.select("td").get(3).text();

            JSONObject rowObj = new JSONObject();
            rowObj.put("Application Stream", appStream);
            rowObj.put("Release Date", releaseDate);
            rowObj.put("Retirement Date", retirementDate);
            rowObj.put("Release", release);

            parentJson.put(rowObj);
        }

        String filePath = "output_" + key + ".json";
        exportJsonToFile(parentJson, filePath);
    }

    public static void exportJsonToFile(JSONArray jsonArr, String filePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
            writer.write(jsonArr.toString(4));

            writer.close();
            System.out.println("JSON data has been successfully written to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}