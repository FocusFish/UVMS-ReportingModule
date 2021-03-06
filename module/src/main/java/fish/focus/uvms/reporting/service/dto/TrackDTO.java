/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package fish.focus.uvms.reporting.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.operation.distance.DistanceOp;
import fish.focus.schema.movement.v1.MovementTrack;
import fish.focus.uvms.reporting.model.exception.ReportingServiceException;
import fish.focus.wsdl.asset.types.Asset;
import fish.focus.uvms.reporting.service.util.GeometryUtil;
import lombok.Setter;

import javax.measure.UnitConverter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static systems.uom.common.USCustomary.KNOT;
import static systems.uom.common.USCustomary.NAUTICAL_MILE;

public class TrackDTO {

    @JsonIgnore
    private MovementTrack track;
    @JsonIgnore
    private Geometry geometry;
    @JsonUnwrapped
    private AssetDTO asset;
    private List<Double> nearestPoint;
    private List<Double> extent;
    @Setter
    private UnitConverter velocityConverter = KNOT.getConverterTo(KNOT);
    @Setter
    private UnitConverter lengthConverter = NAUTICAL_MILE.getConverterTo(NAUTICAL_MILE);

    public TrackDTO(MovementTrack track, Asset asset) throws ReportingServiceException, ParseException {
        this.track = track;
        this.asset = new AssetDTO(asset);
        geometry = GeometryUtil.toGeometry(track.getWkt());
        computeEnvelope();
        computerNearestPoint();
    }

    public TrackDTO(MovementTrack track, Asset asset, DisplayFormat format) throws ReportingServiceException, ParseException {
        this(track, asset);

        if (format != null) {
            if (format.getLengthType() != null) {
                lengthConverter = format.getLengthType().getConverter();
            }
            if (format.getVelocityType() != null) {
                velocityConverter = format.getVelocityType().getConverter();
            }
        }

    }

    private void computerNearestPoint() {
        nearestPoint = new ArrayList<>();
        if (geometry != null && geometry.getCentroid() != null) {
            if (geometry.isValid()) {
                DistanceOp distanceOp = new DistanceOp(geometry.getCentroid(), geometry);
                Coordinate[] nearestPoints = distanceOp.nearestPoints();
                nearestPoint.add(nearestPoints[0].x);
                nearestPoint.add(nearestPoints[0].y);
            } else if (geometryContainsOnlyOnePoint()) {
                nearestPoint.add(geometry.getCoordinate().x);
                nearestPoint.add(geometry.getCoordinate().y);
            }
        }
    }

    private boolean geometryContainsOnlyOnePoint() {
        return BigDecimal.valueOf(geometry.getLength()).compareTo(BigDecimal.valueOf(0)) == 0;
    }

    private void computeEnvelope() {
        extent = new ArrayList<>();
        if (geometry != null && geometry.getEnvelopeInternal() != null) {
            Envelope internal = geometry.getEnvelopeInternal();
            extent.add(internal.getMinX());
            extent.add(internal.getMinY());
            extent.add(internal.getMaxX());
            extent.add(internal.getMaxY());
        }
    }

    public List<Double> getNearestPoint() {
        return nearestPoint;
    }

    public AssetDTO getAsset() {
        return asset;
    }

    public List<Double> getExtent() {
        return extent;
    }

    public Double getTotalTimeAtSea() {

        return track.getTotalTimeAtSea();
    }

    public Double getDistance() {

        return lengthConverter.convert(track.getDistance() != null ? track.getDistance() : 0);
    }

    public Double getDuration() {

        return track.getDuration();
    }

    public String getId() {

        return track.getId();

    }
}