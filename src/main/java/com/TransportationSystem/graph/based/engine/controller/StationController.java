package com.TransportationSystem.graph.based.engine.controller;


import com.TransportationSystem.graph.based.engine.model.Station;
import com.TransportationSystem.graph.based.engine.queryProjection.PathResult;
import com.TransportationSystem.graph.based.engine.service.StationService;
import com.TransportationSystem.graph.based.exception.StationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/station")
public class StationController {
    @Autowired
    private StationService stationService;

    @GetMapping("/all")
    public ResponseEntity<Iterable<Station>> getAllStations(){
        Iterable<Station> allStations = stationService.getAllStations();
        return ResponseEntity.ok(allStations);
    }

    @PostMapping("/connect-two-stations/{stationOneId}/to/{stationTwoId}")
    public ResponseEntity<String> addConnection(@PathVariable Long stationOneId,
                                                @PathVariable Long stationTwoId,
                                                @RequestParam int weight) {
        try {
            stationService.addConnection(stationOneId, stationTwoId, weight);
            return ResponseEntity.ok("Connection added successfully.");
        } catch (StationNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/by-name/{stationName}")
    public ResponseEntity<Station> findStationByName(@PathVariable String stationName){
        Station stationByName = new Station();

        try{
            stationByName = stationService.findStationByName(stationName);
            return ResponseEntity.ok(stationByName);
        }catch (StationNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/by-id/{stationId}")
    public ResponseEntity<Station> findStationById(@PathVariable Long stationId){
        try{
            Station stationById = stationService.findStationById(stationId);
            return ResponseEntity.ok(stationById);
        }catch (StationNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStation(@RequestBody Station station) {


        System.out.println(station.getName());
        if(stationService.checkForStation(station.getName())){
            stationService.addStation(station.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body("Successfully Created");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already exist");
    }

    @DeleteMapping("/delete-by-id/{stationId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long stationId){
        stationService.deleteStation(stationId);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/shortest-path")
    public Map<String, Object> getShortestPath(@RequestParam String start, @RequestParam String end) {
        return stationService.findShortestPath(start, end);
    }

}
