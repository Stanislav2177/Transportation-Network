package com.TransportationSystem.graph.based.engine.repository;

import com.TransportationSystem.graph.based.engine.model.Station;
import com.TransportationSystem.graph.based.engine.queryProjection.PathResult;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StationRepository extends Neo4jRepository<Station, Long> {
    // Define custom query methods if needed

//    @Query("MATCH (s:Station) WHERE s.Name = $name RETURN s")
//    Station findByName(@Param("name") String name);

    @Query("MATCH (e:Station {Name: $name}) RETURN e")
    Station findByName(@Param("name") String name);

}