package com.almaszy.inzynierka.controllers;

import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/optimizePath")
public class MapController {

  Gson gson = new Gson();
  private final MapService mapService;

  public MapController(MapService mapService) {
    this.mapService = mapService;
  }

  @PostMapping
  public ResponseEntity<String> optimizePath(@RequestBody String distanceMatrixString)
  {
    int[] result = mapService.calculatePath(distanceMatrixString);
    return ResponseEntity.ok(gson.toJson(result));
  }
}
