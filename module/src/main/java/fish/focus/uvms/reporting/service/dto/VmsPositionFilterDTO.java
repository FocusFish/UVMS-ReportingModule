/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package fish.focus.uvms.reporting.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fish.focus.schema.movement.v1.MovementActivityTypeType;
import fish.focus.schema.movement.v1.MovementTypeType;
import fish.focus.uvms.reporting.service.entities.Filter;
import fish.focus.uvms.reporting.service.entities.FilterType;
import fish.focus.uvms.reporting.service.mapper.VmsPositionFilterMapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true, of = {"movementType", "movementActivity", "minimumSpeed", "maximumSpeed", "movementSources"})
public class VmsPositionFilterDTO extends FilterDTO {

    public static final String MOV_MIN_SPEED = "movMinSpeed";
    public static final String MOV_MAX_SPEED = "movMaxSpeed";
    public static final String MOV_TYPE = "movType";
    public static final String MOV_ACTIVITY = "movActivity";
    public static final String VMS_POSITION = "vmsposition";
    public static final String MOV_SOURCES = "movsources";

    @JsonProperty(MOV_MIN_SPEED)
    private Float minimumSpeed;

    @JsonProperty(MOV_MAX_SPEED)
    private Float maximumSpeed;

    @JsonProperty(MOV_TYPE)
    private MovementTypeType movementType;

    @JsonProperty(MOV_ACTIVITY)
    private MovementActivityTypeType movementActivity;

    @JsonProperty(MOV_SOURCES)
    private List<String> movementSources;

    public VmsPositionFilterDTO() {
        super(FilterType.vmspos);
    }

    public VmsPositionFilterDTO(Long id, Long reportId) {
        super(FilterType.vmspos, id, reportId);
    }

    @Builder(builderMethodName = "VmsPositionFilterDTOBuilder")
    public VmsPositionFilterDTO(Long reportId, Long id, Float minimumSpeed, Float maximumSpeed,
                                MovementTypeType movementType, MovementActivityTypeType movementActivity,
                                List<String> movementSources) {
        this(id, reportId);
        this.minimumSpeed = minimumSpeed;
        this.maximumSpeed = maximumSpeed;
        this.movementType = movementType;
        this.movementActivity = movementActivity;
        this.movementSources = movementSources;
        validate();
    }

    public Float getMinimumSpeed() {
        return minimumSpeed;
    }

    public void setMinimumSpeed(Float minimumSpeed) {
        this.minimumSpeed = minimumSpeed;
    }

    public Float getMaximumSpeed() {
        return maximumSpeed;
    }

    public void setMaximumSpeed(Float maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

    @Override
    public Filter convertToFilter() {
        return VmsPositionFilterMapper.INSTANCE.vmsPositionFilterDTOToVmsPositionFilter(this);
    }

    public MovementTypeType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementTypeType movementType) {
        this.movementType = movementType;
    }

    public MovementActivityTypeType getMovementActivity() {
        return movementActivity;
    }

    public void setMovementActivity(MovementActivityTypeType movementActivity) {
        this.movementActivity = movementActivity;
    }

    public List<String> getMovementSources() {
        return movementSources;
    }

    public void setMovementSources(List<String> movementSources) {
        this.movementSources = movementSources;
    }
}