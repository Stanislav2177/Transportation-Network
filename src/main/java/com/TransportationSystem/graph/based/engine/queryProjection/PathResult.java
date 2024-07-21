package com.TransportationSystem.graph.based.engine.queryProjection;
import java.util.List;
import java.util.Map;

public interface PathResult {
    List<String> getStationNames();
    List<Map<String, Object>> getPath();
    double getWeight();
}

