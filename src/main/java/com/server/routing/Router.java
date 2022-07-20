package com.server.routing;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.*;
import com.graphhopper.util.shapes.GHPoint;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class Router {

    private String working_dir = System.getProperty("user.dir");
    private String pbf_dir = working_dir + File.separator + "pbf/chelny.osm.pbf";
    private GraphHopper hopper;

    public Router() {
        hopper = createGraphHopperInstance(pbf_dir);

    }

    private GraphHopper createGraphHopperInstance(String ghLoc) {
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(ghLoc);
        // specify where to store graphhopper files
        hopper.setGraphHopperLocation(working_dir + File.separator + "pbf/routing-graph-cache");
        // see docs/core/profiles.md to learn more about profiles
        hopper.setProfiles(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
        // this enables speed mode for the profile we called car
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));
        // now this can take minutes if it imports or a few seconds for loading of course this is dependent on the area you import
        hopper.importOrLoad();
        return hopper;
    }

    public PointList getRoute(List<GHPoint> points) {
        GHRequest request = new GHRequest()
            .setProfile("car") //specify which profile
            .setLocale(Locale.US);// define the language for the turn instructions
        points.forEach(request::addPoint);

        GHResponse response = hopper.route(request);
        if (response.hasErrors())
            throw new RuntimeException(response.getErrors().toString());

        // use the best path, see the GHResponse class for more possibilities.
        ResponsePath path = response.getBest();

        // points, distance in meters and time in millis of the full path
        PointList pointList = path.getPoints();
        double distance = path.getDistance();
        long timeInMs = path.getTime();

//        Translation tr = hopper.getTranslationMap().getWithFallBack(Locale.US);
//        InstructionList il = path.getInstructions();
        // iterate over all turn instructions
//        for (Instruction instruction : il) {
        // System.out.println("distance " + instruction.getDistance() + " for instruction: " + instruction.getTurnDescription(tr));
//        }
        return pointList;
    }

    public String getRouteInWKT4326(List<GHPoint> points) {
        return convertPointListToWKT4326(getRoute(points));
    }


    public String convertPointListToWKT4326(PointList pointList) {
        StringBuilder result = new StringBuilder("LINESTRING(");
        for (int i = 0; i < pointList.size(); i++) {
            double lon = pointList.getLon(i);
            double lat = pointList.getLat(i);
            result.append(lon).append(" ").append(lat).append(",");
        }

        result.replace(result.length() - 1, result.length(), ")");
        return result.toString();
    }


}
