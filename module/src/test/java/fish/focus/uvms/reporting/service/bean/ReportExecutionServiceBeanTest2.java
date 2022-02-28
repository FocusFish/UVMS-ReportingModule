/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package fish.focus.uvms.reporting.service.bean;

import fish.focus.schema.movement.search.v1.MovementMapResponseType;
import fish.focus.schema.movement.v1.MovementSegment;
import fish.focus.schema.movement.v1.MovementTrack;
import fish.focus.schema.movement.v1.MovementType;
import fish.focus.uvms.activity.model.schemas.*;
import fish.focus.uvms.reporting.service.entities.DateRange;
import fish.focus.uvms.reporting.model.exception.ReportingServiceException;
import fish.focus.uvms.spatial.model.schemas.AreaIdentifierType;
import fish.focus.uvms.spatial.model.schemas.AreaType;
import fish.focus.wsdl.asset.types.Asset;
import fish.focus.wsdl.asset.types.AssetId;
import fish.focus.wsdl.asset.types.AssetIdType;
import fish.focus.uvms.reporting.service.bean.AuditService;
import fish.focus.uvms.reporting.service.bean.ReportRepository;
import fish.focus.uvms.reporting.service.bean.SpatialService;
import fish.focus.uvms.reporting.service.bean.impl.ActivityServiceBean;
import fish.focus.uvms.reporting.service.bean.impl.AssetServiceBean;
import fish.focus.uvms.reporting.service.bean.impl.MovementServiceBean;
import fish.focus.uvms.reporting.service.bean.impl.ReportExecutionServiceBean;
import fish.focus.uvms.reporting.service.dto.Area;
import fish.focus.uvms.reporting.service.dto.FilterExpression;
import fish.focus.uvms.reporting.service.dto.report.VisibilityEnum;
import fish.focus.uvms.reporting.service.entities.*;
import fish.focus.uvms.reporting.service.enums.ReportTypeEnum;
import lombok.SneakyThrows;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class ReportExecutionServiceBeanTest2 extends UnitilsJUnit4 {

    @TestedObject
    private ReportExecutionServiceBean service;

    @InjectIntoByType
    private Mock<SpatialService> spatialModule;

    @InjectIntoByType
    private Mock<AuditService> auditModule;

    @InjectIntoByType
    private Mock<ReportRepository> repository;

    @InjectIntoByType
    private Mock<AssetServiceBean> asset;

    @InjectIntoByType
    private Mock<MovementServiceBean> movement;

    @InjectIntoByType
    private Mock<ActivityServiceBean> activity;

    @Test
    @SneakyThrows
    public void testExecuteReportWithoutSaveWithFaFilters() {
        asset.returns(getMockedAssets()).getAssetMap(null);
        movement.returns(getMockedMovements()).getMovement(null);
        spatialModule.returns("MULTIPOINT(3.5 5.6, 4.8 10.5)").getFilterArea(null, null);
        activity.returns(getMockedFishingTrip()).getFishingTrips(null, null);

        AreaIdentifierType areaIdentifierType = new AreaIdentifierType();
        areaIdentifierType.setAreaType(AreaType.EEZ);
        areaIdentifierType.setId("1");
        service.getReportExecutionWithoutSave(getReportDto(), Arrays.asList(areaIdentifierType), "TEST", true, null);

        spatialModule.assertInvokedInSequence().getFilterArea(null, null);
        asset.assertInvokedInSequence().getAssetMap(null);
        movement.assertInvokedInSequence().getMovement(null);
        activity.assertInvokedInSequence().getFishingTrips(null, null);
        auditModule.assertInvokedInSequence().sendAuditReport(null, null, null);
        MockUnitils.assertNoMoreInvocations();
    }

    @Test
    @SneakyThrows
    public void testExecuteReportByReportIdWithFaFilter() {
        asset.returns(getMockedAssets()).getAssetMap(null);
        movement.returns(getMockedMovements()).getMovement(null);
        repository.returns(getMockReport()).findReportByReportId(null, "test", null, false);
        spatialModule.returns("MULTIPOINT(3.5 5.6, 4.8 10.5)").getFilterArea(null, null);
        activity.returns(getMockedFishingTrip()).getFishingTrips(null, null);

        AreaIdentifierType areaIdentifierType = new AreaIdentifierType();
        areaIdentifierType.setAreaType(AreaType.EEZ);
        areaIdentifierType.setId("1");
        service.getReportExecutionByReportId(1L, "test", "EC", Arrays.asList(areaIdentifierType), Instant.now(), false, true, null);

        spatialModule.assertInvokedInSequence().getFilterArea(null, null);
        asset.assertInvokedInSequence().getAssetMap(null);
        movement.assertInvokedInSequence().getMovement(null);
        activity.assertInvokedInSequence().getFishingTrips(null, null);
        MockUnitils.assertNoMoreInvocations();
    }

    @Test(expected = ReportingServiceException.class)
    @SneakyThrows
    public void testReportNull() {
        service.getReportExecutionByReportId(null, "test", null, null, null, false, true, null);
    }

    private fish.focus.uvms.reporting.service.dto.report.Report getReportDto() {
        fish.focus.uvms.reporting.service.dto.report.Report report = new fish.focus.uvms.reporting.service.dto.report.Report();
        report.setCreatedBy("test");
        report.setCreatedOn("2016-12-07 14:57:42 +0000");
        report.setDesc("This is test report");
        report.setId(1L);
        report.setWithMap(false);
        report.setVisibility("PUBLIC");
        report.setName("Test report");
        report.setAdditionalProperty("speedUnit", "kts");
        report.setAdditionalProperty("distanceUnit", "nm");
        report.setAdditionalProperty("timestamp", "2016-12-07 14:57:42 +0000");

        Map<String, Object> additionalProp = new HashMap<>();
        additionalProp.put("speedUnit", "kts");
        additionalProp.put("distanceUnit", "nm");
        additionalProp.put("timestamp", "2016-12-07 14:57:42 +0000");
        report.setAdditionalProperty("additionalProperties", additionalProp);

        FilterExpression filterExpression = new FilterExpression();
        filterExpression.setAreas(Arrays.asList(new Area(1L, "areas", "EEZ", 1L)));

        filterExpression.setAssets(Arrays.asList(new fish.focus.uvms.reporting.service.dto.Asset(1L, "asset", "1", "EEZ", "TEST")));

        fish.focus.uvms.reporting.service.dto.FaFilter faFilter = new fish.focus.uvms.reporting.service.dto.FaFilter();
        faFilter.setFaPorts(Arrays.asList("port1"));
        faFilter.setFaWeight(new fish.focus.uvms.reporting.service.dto.FaWeight(10.0, 20.0, "KG"));
        faFilter.setFaGears(Arrays.asList("gear1"));
        faFilter.setMasters(Arrays.asList("master1"));
        faFilter.setSpecies(Arrays.asList("Species1"));
        faFilter.setActivityTypes(Arrays.asList("DEPARTURE"));
        faFilter.setReportTypes(Arrays.asList("NOTIFICATION"));
        filterExpression.setFaFilter(faFilter);
        report.setFilterExpression(filterExpression);
        return report;
    }

    private List<MovementMapResponseType> getMockedMovements() {
        MovementMapResponseType movementMapResponseType = new MovementMapResponseType();

        MovementType movementType = new MovementType();
        movementType.setWkt("POINT(3.5 5.6)");
        movementType.setGuid("mov1");
        movementType.setCalculatedSpeed(20.0);
        movementMapResponseType.getMovements().add(movementType);

        MovementSegment segmentType = new MovementSegment();
        segmentType.setWkt("LINESTRING(3 4,10 50,20 25)");
        segmentType.setId("seg1");
        segmentType.setDistance(12.0);
        segmentType.setCourseOverGround(20.0);
        movementMapResponseType.getSegments().add(segmentType);

        MovementTrack movementTrack = new MovementTrack();
        movementTrack.setWkt("MULTILINESTRING((3 4,10 50,20 25),(-5 -8,-10 -8,-15 -4))");
        movementTrack.setDistance(30.0);
        movementTrack.setDuration(10.9);
        movementTrack.setId("track1");
        movementMapResponseType.getTracks().add(movementTrack);

        movementMapResponseType.setKey("key1");
        return Arrays.asList(movementMapResponseType);
    }

    private Report getMockReport() {
        Report report = new Report();
        report.setIsDeleted(false);
        report.setFilters(getFilters());
        report.setVisibility(VisibilityEnum.PUBLIC);
        report.setReportType(ReportTypeEnum.STANDARD);
        report.setDetails(new ReportDetails("This is Test", "Test report 1", false, "EC", "TEST"));
        return report;
    }

    private Map<String, Asset> getMockedAssets() {
        Map<String, Asset> assetsMap = new HashMap<>();
        Asset asset = new Asset();
        asset.setActive(true);

        AssetId assetId = new AssetId();
        assetId.setGuid("asset1");
        assetId.setValue("asset1");
        assetId.setType(AssetIdType.GUID);
        asset.setAssetId(assetId);

        assetsMap.put("asset1", asset);
        return assetsMap;
    }

    private FishingTripResponse getMockedFishingTrip() throws Exception {
        FishingTripResponse fishingTripResponse = new FishingTripResponse();

        FishingTripIdWithGeometry trip = new FishingTripIdWithGeometry();
        trip.setTripId("TRIP1");
        trip.setSchemeId("SCHEME1");
        trip.setGeometry("MULTIPOINT(10 20, 30 40)");

        FishingActivitySummary activity = new FishingActivitySummary();
        activity.setGeometry("MULTIPOINT(10 20, 30 40)");
        activity.setActivityType("DEPARTURE");
        activity.setReportType("DECLARATION");
        activity.setAcceptedDateTime(getDate("2012-12-12 12:12:12"));
        activity.setDataSource("FLUX");
        activity.setPurposeCode("4");
        activity.setVesselName("VESSEL1");
        activity.setAreas(new ArrayList<>(Arrays.asList("AREA1")));
        activity.setGears(new ArrayList<>(Arrays.asList("GEAR1")));
        activity.setPorts(new ArrayList<>(Arrays.asList("PORT1")));
        activity.setSpecies(new ArrayList<>(Arrays.asList("SPECIES1")));
        List<VesselIdentifierType> vesselIdentifierTypes = new ArrayList<>();
        vesselIdentifierTypes.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.CFR,"CFR1"));
        activity.setVesselIdentifiers(vesselIdentifierTypes);
        fishingTripResponse.setFishingActivityLists(new ArrayList<>(Arrays.asList(activity)));
        fishingTripResponse.setFishingTripIdLists(new ArrayList<>(Arrays.asList(trip)));
        return fishingTripResponse;
    }

    private XMLGregorianCalendar getDate(String s) throws ParseException, DatatypeConfigurationException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalendar.setTime(date);
        XMLGregorianCalendar result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        return result;
    }

    private Set<Filter> getFilters() {
        Set<Filter> filterSet = new HashSet<>();
        filterSet.add(CommonFilter.builder().dateRange(new DateRange(new Date(), new Date())).positionSelector(new PositionSelector(null, Selector.all, null)).build());
        filterSet.add(FaFilter.builder().
                reportTypes(Arrays.asList("NOTIFICATION")).
                activityTypes(Arrays.asList("DEPARTURE")).
                masters(Arrays.asList("master1")).
                species(Arrays.asList("Species1")).
                faGears(Arrays.asList("gear1")).
                faPorts(Arrays.asList("port1")).
                faWeight(new FaWeight(10.0, 20.0, "KG")).build());
        filterSet.add(AreaFilter.builder().areaId(1L).areaType("EEZ").build());
        filterSet.add(AssetFilter.builder().guid("ID1").name("asset1").build());
        return filterSet;
    }
}
