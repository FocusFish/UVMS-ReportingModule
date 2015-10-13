package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Position {

    POSITIONS("position"),
    HOURS("hours");

    private String name;

    private Position(final String name){
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
