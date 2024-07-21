package com.TransportationSystem.graph.based.engine.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;
@Node("Station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station {
    @Id
    @GeneratedValue
    private Long id;

    @Property("Name")
    private String name;

    @Relationship(type = "HAVE_CONNECTION", direction = Relationship.Direction.OUTGOING)
    private Set<Connection> connection = new HashSet<>();


}
