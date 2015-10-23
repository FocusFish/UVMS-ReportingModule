package eu.europa.ec.fisheries.uvms.reporting.service.bean;

import com.google.common.collect.ImmutableMap;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementMapResponseType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementSegment;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTrack;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.uvms.common.MockingUtils;
import eu.europa.ec.fisheries.uvms.exception.ProcessorException;
import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingServiceException;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.MovementDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.SegmentDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.TrackDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.VmsDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.Report;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.FilterProcessor;
import eu.europa.ec.fisheries.uvms.reporting.service.mock.MockVesselData;
import eu.europa.ec.fisheries.uvms.reporting.service.mock.util.MockPointsReader;
import eu.europa.ec.fisheries.wsdl.vessel.types.Vessel;
import org.apache.commons.lang3.NotImplementedException;
import org.geotools.feature.DefaultFeatureCollection;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Local(value = VmsService.class)
public class VmsServiceBean implements VmsService {

    @EJB
    private ReportRepository repository;

    @EJB
    private VesselServiceBean vessel;

    @EJB
    private MovementServiceBean movement;

    @Override
    @Transactional
    public VmsDTO getVmsDataByReportId(final String username, final String scopeName, final Long id) throws ReportingServiceException {

        VmsDTO vmsDto;

        try {
            Report reportByReportId = repository.findReportByReportId(id, username, scopeName);
            validate(id, reportByReportId);

            FilterProcessor processor = FilterProcessor.FilterProcessorBuilder().filters(reportByReportId.getFilters()).build();

            if (processor.hasVesselsOrVesselGroups()) {
                ImmutableMap<String, Vessel> vesselMapByGuid = vessel.getVesselMapByGuid(processor);
                List<MovementMapResponseType> movementMap = movement.getMovementMapResponseTypes(processor);
                vmsDto = VmsDTO.getVmsDto(vesselMapByGuid, movementMap);
            } else {
                throw new NotImplementedException("Please handle that case");
            }

            reportByReportId.updateExecutionLog(username);

        } catch (ProcessorException e) {
            throw new ReportingServiceException("Error while processing reporting filters.", e);
        }

        return vmsDto;
    }

    @Override
    public VmsDTO getVmsMockData(Long id) throws ReportingServiceException {
        DefaultFeatureCollection movementFeatureCollection = new DefaultFeatureCollection(null, MovementDTO.MOVEMENTYPE);
        DefaultFeatureCollection segmentsFeatureCollection = new DefaultFeatureCollection(null, SegmentDTO.SEGMENT);
        MockPointsReader mockPointsReader = new MockPointsReader();
        List<MovementSegment> segmentList = mockPointsReader.segmentList;
        List<MovementType> movementList = mockPointsReader.movementTypeList;
        List<MovementTrack> movementTrackList = mockPointsReader.movementTrackList;
        List<Vessel> vesselDtoList = MockVesselData.getVesselDtoList(2);
        List<TrackDTO> tracks = new ArrayList<>();
        int trackid = 1;

        for (Vessel vessel : vesselDtoList) {
            int r = MockingUtils.randIntOdd(0, movementList.size());
            List<MovementType> subList = movementList.subList(0, r);
            for (MovementType movementType : subList) {
                movementFeatureCollection.add(new MovementDTO(movementType, vessel).toFeature());
            }
            int i = r / 2;
            List<MovementSegment> segmentList1 = segmentList.subList(0, i);
            for (MovementSegment segment : segmentList1) {
                segment.setTrackId(String.valueOf(trackid));
                segmentsFeatureCollection.add(new SegmentDTO(segment, vessel).toFeature());
            }

            List<MovementTrack> movementTracks = movementTrackList.subList(0, vesselDtoList.size());
            for (MovementTrack movementTrack : movementTracks) {
                movementTrack.setId(String.valueOf(trackid));
                tracks.add(new TrackDTO(movementTrack, vessel));
            }

            movementTrackList.removeAll(movementTracks);
            movementList.removeAll(subList);
            segmentList.removeAll(segmentList1);
        }
        return new VmsDTO(movementFeatureCollection, segmentsFeatureCollection, tracks);
    }

    private void validate(Long id, Report reportByReportId) throws ReportingServiceException {
        if (reportByReportId == null) {
            throw new ReportingServiceException("No report found with id " + id);
        }
    }

}