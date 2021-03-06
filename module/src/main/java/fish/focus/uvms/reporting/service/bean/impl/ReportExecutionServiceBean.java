/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.reporting.service.bean.impl;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.locationtech.jts.io.ParseException;
import fish.focus.schema.movement.search.v1.MovementMapResponseType;
import fish.focus.schema.movement.v1.MovementSegment;
import fish.focus.schema.movement.v1.MovementTrack;
import fish.focus.schema.movement.v1.MovementType;
import fish.focus.uvms.activity.model.schemas.*;
import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.reporting.service.exception.ProcessorException;
import fish.focus.uvms.commons.service.interceptor.AuditActionEnum;
import fish.focus.uvms.commons.service.interceptor.IAuditInterceptor;
import fish.focus.uvms.commons.service.interceptor.SimpleTracingInterceptor;
import fish.focus.uvms.commons.service.interceptor.TracingInterceptor;
import fish.focus.uvms.reporting.model.exception.ReportingServiceException;
import fish.focus.uvms.spatial.model.schemas.AreaIdentifierType;
import fish.focus.wsdl.asset.types.Asset;
import fish.focus.uvms.reporting.message.mapper.ExtAssetMessageMapper;
import fish.focus.uvms.reporting.message.mapper.ExtMovementMessageMapper;
import fish.focus.uvms.reporting.service.bean.*;
import fish.focus.uvms.reporting.service.dto.*;
import fish.focus.uvms.reporting.service.entities.Filter;
import fish.focus.uvms.reporting.service.entities.GroupCriteriaFilter;
import fish.focus.uvms.reporting.service.entities.Report;
import fish.focus.uvms.reporting.service.entities.comparator.GroupCriteriaFilterSequenceComparator;
import fish.focus.uvms.reporting.service.enums.GroupCriteriaType;
import fish.focus.uvms.reporting.service.enums.ReportTypeEnum;
import fish.focus.uvms.reporting.service.mapper.*;
import fish.focus.uvms.reporting.service.util.FilterProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.geotools.feature.DefaultFeatureCollection;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import static fish.focus.uvms.reporting.service.Constants.ADDITIONAL_PROPERTIES;
import static fish.focus.uvms.reporting.service.Constants.TIMESTAMP;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Stateless
@Local(value = ReportExecutionService.class)
@Slf4j
public class ReportExecutionServiceBean implements ReportExecutionService {

    @EJB
    private ReportRepository repository;

    @EJB
    private AuditService auditService;

    @Inject
    private AssetServiceBean assetModule;

    @EJB
    private MovementServiceBean movementModule;

    @EJB
    private SpatialService spatialModule;

    @EJB
    private ActivityService activityService;

    @Override
    @Transactional
    @IAuditInterceptor(auditActionType = AuditActionEnum.EXECUTE)
    @Interceptors(TracingInterceptor.class)
    public ExecutionResultDTO getReportExecutionByReportId(final Long id, final String username, final String scopeName, final List<AreaIdentifierType> areaRestrictions, final Instant now, Boolean isAdmin, Boolean withActivity, DisplayFormat displayFormat) throws ReportingServiceException {
        Report reportByReportId = repository.findReportByReportId(id, username, scopeName, isAdmin);
        if (reportByReportId == null) {
            final String error = "No report found with id " + id;
            log.error("No report found with id " + id);
            throw new ReportingServiceException(error);
        }
        ExecutionResultDTO resultDTO = executeReport(reportByReportId, now, areaRestrictions, withActivity, displayFormat);
        reportByReportId.updateExecutionLog(username);
        return resultDTO;
    }

    @Override
    public ExecutionResultDTO getReportExecutionWithoutSave(final fish.focus.uvms.reporting.service.dto.report.Report report, final List<AreaIdentifierType> areaRestrictions, String userName, Boolean withActivity, DisplayFormat displayFormat) throws ReportingServiceException {
        Map additionalProperties = (Map) report.getAdditionalProperties().get(ADDITIONAL_PROPERTIES);
        Instant dateTime = DateUtils.stringToDate((String) additionalProperties.get(TIMESTAMP));
        Report toReport = ReportMapperV2.INSTANCE.reportDtoToReport(report);
        ExecutionResultDTO resultDTO = executeReport(toReport, dateTime, areaRestrictions, withActivity, displayFormat);
        auditService.sendAuditReport(AuditActionEnum.EXECUTE, report.getName(), userName);
        return resultDTO;
    }

    @SneakyThrows
    private ExecutionResultDTO executeReport(Report report, Instant dateTime, List<AreaIdentifierType> areaRestrictions, Boolean userActivityAllowed, DisplayFormat format) {
        try {
            Set<Filter> filters = report.getFilters();
            FilterProcessor processor = new FilterProcessor(report.getFilters(), dateTime);
            ExecutionResultDTO resultDTO = new ExecutionResultDTO();
            String wkt = getFilterAreaWkt(processor, areaRestrictions);
            boolean hasAssets = processor.hasAssetsOrAssetGroups();
            MovementData movementData = fetchPositionalData(processor, wkt);
            List<SingleValueTypeFilter> singleValueTypeFilters = extractSingleValueFilters(processor, wkt);
            List<ListValueTypeFilter> listValueTypeFilters = extractListValueFilters(processor, movementData.getAssetMap(), hasAssets);
            if (ReportTypeEnum.STANDARD == report.getReportType()) {
                if (userActivityAllowed && !report.isLastPositionSelected()) {
                    FishingTripResponse tripResponse = activityService.getFishingTrips(singleValueTypeFilters, listValueTypeFilters);
                    if (tripResponse != null && CollectionUtils.isNotEmpty(tripResponse.getFishingTripIdLists())){
                        for (FishingTripIdWithGeometry fishingTripIdWithGeometry : tripResponse.getFishingTripIdLists()) {
                            TripDTO trip = FishingTripMapper.INSTANCE.fishingTripToTripDto(fishingTripIdWithGeometry);
                            updateTripWithVmsPositionCount(trip, movementData.getMovementMap());
                            resultDTO.getTrips().add(trip);
                        }
                        List<FishingActivitySummaryDTO> activitySummaryDTOs = new ArrayList<>();
                        for (FishingActivitySummary summary : tripResponse.getFishingActivityLists()) {
                            activitySummaryDTOs.add(FishingActivityMapper.INSTANCE.mapToFishingActivity(summary));
                        }
                        resultDTO.setActivityList(activitySummaryDTOs);
                        populateActivityesAsDefaultCollections(resultDTO);
                    }
                }
                DefaultFeatureCollection movements = new DefaultFeatureCollection(null, MovementDTO.SIMPLE_FEATURE_TYPE);
                DefaultFeatureCollection segments = new DefaultFeatureCollection(null, SegmentDTO.SEGMENT);
                List<TrackDTO> tracks = new ArrayList<>();
                if (isNotEmpty(movementData.getMovementMap())) {
                    for (MovementMapResponseType map : movementData.getMovementMap()) {
                        Asset asset = movementData.getAssetMap().get(map.getKey());
                        if (asset != null) {
                            for (MovementType movement : map.getMovements()) {
                                movements.add(new MovementDTO(movement, asset, format).toFeature());
                            }
                            for (MovementSegment segment : map.getSegments()) {
                                segments.add(new SegmentDTO(segment, asset, format).toFeature());
                            }
                            for (MovementTrack track : map.getTracks()) {
                                tracks.add(new TrackDTO(track, asset, format));
                            }
                        }
                    }
                }
                resultDTO.setTracks(tracks);
                resultDTO.setMovements(movements);
                resultDTO.setSegments(segments);
            } else if (userActivityAllowed && ReportTypeEnum.SUMMARY == report.getReportType()) {
                List<GroupCriteria> groupCriteriaList = extractGroupCriteriaList(filters);
                FACatchSummaryReportResponse faCatchSummaryReport = activityService.getFaCatchSummaryReport(singleValueTypeFilters, listValueTypeFilters, groupCriteriaList);
                FACatchSummaryDTO faCatchSummaryDTO = FACatchSummaryMapper.mapToFACatchSummaryDTO(faCatchSummaryReport);
                resultDTO.setFaCatchSummaryDTO(faCatchSummaryDTO);
                populateActivityesAsDefaultCollections(resultDTO);
            }
            return resultDTO;
        } catch (ProcessorException e) {
            String error = "Error while processing reporting filters";
            log.error(error, e);
            throw new ReportingServiceException(error, e);
        }
    }

    @Interceptors(SimpleTracingInterceptor.class)
    private void populateActivityesAsDefaultCollections(ExecutionResultDTO resultDTO) throws ParseException {
        DefaultFeatureCollection activities = new DefaultFeatureCollection(null, ActivityDTO.ACTIVITY);
        if (isNotEmpty(resultDTO.getActivityList())) {
            for (FishingActivitySummaryDTO summary : resultDTO.getActivityList()) {
                activities.add(new ActivityDTO(summary).toFeature());
            }
        }
        resultDTO.setActivities(activities);
    }

    @Interceptors(SimpleTracingInterceptor.class)
    private void updateTripWithVmsPositionCount(TripDTO trip, Collection<MovementMapResponseType> movementMap) {
        Integer count = 0;
        if (trip != null && (trip.getRelativeFirstFaDateTime() != null && trip.getRelativeLastFaDateTime() != null)) {
            for (MovementMapResponseType map : movementMap) {
                for (MovementType movement : map.getMovements()) {
                    if (movement.getPositionTime() != null) {
                        Date movementDate = movement.getPositionTime();
                        if (movementDate.after(trip.getRelativeFirstFaDateTime()) && movementDate.before(trip.getRelativeLastFaDateTime())) {
                            count++;
                        }
                    }
                }
            }
            trip.setVmsPositionCount(count);
        }
    }

    private List<GroupCriteria> extractGroupCriteriaList(Set<Filter> filters) {
        ImmutableList<GroupCriteriaFilter> groupCriteriaFilters = FluentIterable.from(filters).filter(GroupCriteriaFilter.class).toSortedList(new GroupCriteriaFilterSequenceComparator());
        List<GroupCriteriaType> types = new ArrayList<>();
        for (GroupCriteriaFilter filter : groupCriteriaFilters) {
            types.addAll(filter.getValues());
        }
        return GroupCriteriaFilterMapper.INSTANCE.mapGroupCriteriaTypeListToGroupCriteriaList(types);
    }

    @Interceptors(SimpleTracingInterceptor.class)
    private MovementData fetchPositionalData(FilterProcessor processor, String wkt) {
        MovementData movementData = new MovementData();
        Collection<MovementMapResponseType> movementMap;
        Map<String, MovementMapResponseType> responseTypeMap = null;
        Map<String, Asset> assetMap;
        processor.addAreaCriteria(wkt);
        log.trace("Running report {} assets or asset groups.", processor.hasAssetsOrAssetGroups() ? "has" : "doesn't have");
        if (processor.hasAssetsOrAssetGroups()) {
            assetMap = assetModule.getAssetMap(processor);
            processor.getMovementListCriteria().addAll(ExtMovementMessageMapper.movementListCriteria(assetMap.keySet()));
            movementMap = movementModule.getMovement(processor);
        } else {
            responseTypeMap = movementModule.getMovementMap(processor);
            Set<String> assetGuids = responseTypeMap.keySet();
            movementMap = responseTypeMap.values();
            processor.getAssetListCriteriaPairs().addAll(ExtAssetMessageMapper.assetCriteria(assetGuids));
            assetMap = assetModule.getAssetMap(processor);
        }
        movementData.setAssetMap(assetMap);
        movementData.setMovementMap(movementMap);
        movementData.setResponseTypeMap(responseTypeMap);
        return movementData;
    }

    private List<SingleValueTypeFilter> extractSingleValueFilters(FilterProcessor processor, String areaWkt) {
        // Add all the Fa filter criteria from the filters
        List<SingleValueTypeFilter> filterTypes = new ArrayList<>(processor.getSingleValueTypeFilters());
        if (areaWkt != null && !areaWkt.isEmpty()) {
            filterTypes.add(new SingleValueTypeFilter(SearchFilter.AREA_GEOM, areaWkt));
        }
        return filterTypes;
    }

    private List<ListValueTypeFilter> extractListValueFilters(FilterProcessor processor, Map<String, Asset> assetMap, Boolean isAssetsExist) {
        List<ListValueTypeFilter> filterTypes = new ArrayList<>(processor.getListValueTypeFilters());
        if (isAssetsExist && assetMap != null) {
            Collection<Asset> assets = assetMap.values();
            List<String> assetNames = new ArrayList<>();
            for (Asset asset : assets) {
                assetNames.add(asset.getName());
            }
            if (isNotEmpty(assetNames)) {
                filterTypes.add(new ListValueTypeFilter(SearchFilter.VESSEL_NAME, assetNames));
            }
        }
        return filterTypes;
    }


    private String getFilterAreaWkt(FilterProcessor processor, List<AreaIdentifierType> scopeAreaIdentifierList) throws ReportingServiceException {
        final Set<AreaIdentifierType> areaIdentifierList = processor.getAreaIdentifierList();
        if (isNotEmpty(areaIdentifierList) || isNotEmpty(scopeAreaIdentifierList)) {
            HashSet<AreaIdentifierType> areaIdentifierTypes = null;
            if (isNotEmpty(scopeAreaIdentifierList)) {
                areaIdentifierTypes = new HashSet<>(scopeAreaIdentifierList);
            }
            return spatialModule.getFilterArea(areaIdentifierTypes, areaIdentifierList);
        }
        return null;
    }
}
