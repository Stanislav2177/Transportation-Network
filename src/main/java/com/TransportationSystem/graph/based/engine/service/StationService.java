package com.TransportationSystem.graph.based.engine.service;

import com.TransportationSystem.graph.based.engine.queryProjection.PathResult;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import com.TransportationSystem.graph.based.engine.model.Connection;
import com.TransportationSystem.graph.based.engine.model.Station;
import com.TransportationSystem.graph.based.exception.*;
import com.TransportationSystem.graph.based.engine.repository.StationRepository;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class StationService {

    private StationRepository stationRepository;
    @Autowired
    private Driver driver;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional(transactionManager = "transactionManager")
    public void addConnection(Long stationOneId, Long stationTwoId, int weight) {
        Optional<Station> stationOneOptional = stationRepository.findById(stationOneId);
        Optional<Station> stationTwoOptional = stationRepository.findById(stationTwoId);

        if (stationOneOptional.isPresent() && stationTwoOptional.isPresent()) {
            Station stationOne = stationOneOptional.get();
            Station stationTwo = stationTwoOptional.get();

            Connection connection = new Connection();
            connection.setWeight(weight);
            connection.setTargetStation(stationTwo);

            stationOne.getConnection().add(connection);
            stationRepository.save(stationOne);
        } else {
            throw new StationNotFoundException("One of the stations is not found");
        }
    }

    public Station findStationById(Long id) {
        Optional<Station> station = stationRepository.findById(id);

        if(station.isPresent()){
            return station.get();
        }else{
            throw new StationNotFoundException("Station is not found");
        }
    }

    public Station findStationByName(String name){
        Station byName = stationRepository.findByName(name);

        System.out.println(byName.getName());
        System.out.println(byName.getId());
        return byName;
    }

    public boolean checkForStation(String name){
        Station byName = stationRepository.findByName(name);

        if(byName != null){
            System.out.println("found a station " + byName.getName());
            return false;
        }
        return true;
    }

    public Iterable<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public Station addStation(String name) {
        Station station = new Station();
        station.setName(name);
        return stationRepository.save(station);
    }
    public void deleteStation(Long id) {
        stationRepository.deleteById(id);
    }

    public Map<String, Object> findShortestPath(String startStation, String endStation) {
        Map<String, Object> resultMap = new HashMap<>();

        try (Session session = driver.session()) {
            String query = "MATCH (start:Station {Name: $startStation}), (end:Station {Name: $endStation}) " +
                    "CALL apoc.algo.dijkstra(start, end, 'HAVE_CONNECTION', 'weight') YIELD path, weight " +
                    "RETURN path, weight";

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("startStation", startStation);
            parameters.put("endStation", endStation);

            Result result = session.run(query, parameters);

            if (result.hasNext()) {
                Record record = result.next();
                Path neo4jPath = record.get("path").asPath();
                List<Node> nodes = new ArrayList<>();
                for (Node node : neo4jPath.nodes()) {
                    nodes.add(node);
                }

                List<Station> stations = new ArrayList<>();
                for (Node node : nodes) {
                    Station station = new Station();
                    station.setId(node.id());
                    station.setName(node.get("Name").asString());
                    stations.add(station);
                }
                // printing for test purpose
                for (Station station : stations) {
                    System.out.println(station.getName());
                }
                double weight = record.get("weight").asDouble();

                resultMap.put("path", stations);
                resultMap.put("weight", weight);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String s : resultMap.keySet()) {
            System.out.println(s);
        }

        return resultMap;
    }
}
