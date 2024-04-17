package com.almaszy.inzynierka.controllers;

import com.almaszy.inzynierka.services.MapService;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/optimizePath")
public class MapController {

  Gson gson = new Gson();
  private final MapService mapService;

  public MapController(MapService mapService) {
    this.mapService = mapService;
  }

  @PostMapping
  public ResponseEntity<?> optimizePath(@RequestBody String distanceMatrixString)
  {
    int[] result = mapService.calculatePath(distanceMatrixString);
    return ResponseEntity.ok(gson.toJson(result));
  }
}
