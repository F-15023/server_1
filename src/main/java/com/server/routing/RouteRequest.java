package com.server.routing;

import com.graphhopper.util.shapes.GHPoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteRequest {

    private List<GHPoint> points = new ArrayList<>();

    public RouteRequest parseRouteRequestFromJsonStrong(String jsonString) throws Exception {
        try {
            JSONObject routeReq = new JSONObject(jsonString);
            System.out.println();
            JSONArray pointsArray = routeReq.getJSONArray("points");
            for (int i = 0; i < pointsArray.length(); i++) {
                JSONObject pointJson = pointsArray.getJSONObject(i);
                double lat = Double.parseDouble(pointJson.get("lat").toString());
                double lon = Double.parseDouble(pointJson.get("lon").toString());
                points.add(new GHPoint(lat, lon));
            }
            return this;
        } catch (Exception e) {
            throw new Exception("Can't parse route request!");
        }
    }

    public List<GHPoint> getPoints(){
        return points;
    }


}
