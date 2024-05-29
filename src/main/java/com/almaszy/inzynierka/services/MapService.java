package com.almaszy.inzynierka.services;

import com.almaszy.inzynierka.models.DistanceMatrixModel;
import com.google.gson.Gson;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Service
public class MapService {

  Gson gson = new Gson();
  private static final Logger logger = Logger.getLogger(MapService.class.getName());

  public MapService()
  {
    Loader.loadNativeLibraries();
  }

  public int[] calculatePath(String distanceMatrixString)
  {
    long[][] distanceMatrix = gson.fromJson(distanceMatrixString, long[][].class);

    DistanceMatrixModel distanceMatrixModel = new DistanceMatrixModel(distanceMatrix);
    RoutingIndexManager manager = new RoutingIndexManager(
            distanceMatrixModel.getDistanceMatrix().length,
            distanceMatrixModel.getVehicleNumber(),
            distanceMatrixModel.getDepot());
    RoutingModel routing = new RoutingModel(manager);
    final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
              // Convert from routing variable Index to user NodeIndex.
              int fromNode = manager.indexToNode(fromIndex);
              int toNode = manager.indexToNode(toIndex);
              return distanceMatrixModel.getDistanceMatrix()[fromNode][toNode];
            });
    routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
    RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters()
                    .toBuilder()
                    .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                    .build();

    Assignment solution = routing.solveWithParameters(searchParameters);
    return calculateSolution(routing, manager, solution);
  }

  static int[] calculateSolution(RoutingModel routing, RoutingIndexManager manager, Assignment solution)
  {
    long routeDistance = 0;
    List<Integer> route = new ArrayList<>();
    long index = routing.start(0);
    while (!routing.isEnd(index))
    {
      route.add(manager.indexToNode(index));
      long previousIndex = index;
      index = solution.value(routing.nextVar(index));
      routeDistance += routing.getArcCostForVehicle(previousIndex, index, 0);
    }
    route.add(manager.indexToNode(routing.end(0)));
    logger.info("Route: " + route);
    logger.info("Route distance: " + routeDistance / 1000 + "kilometers");

    int[] resultArray = new int[route.size()];
    for (int i = 0; i < route.size(); i++)
    {
      resultArray[i] = route.get(i);
    }
    return resultArray;
  }

}
