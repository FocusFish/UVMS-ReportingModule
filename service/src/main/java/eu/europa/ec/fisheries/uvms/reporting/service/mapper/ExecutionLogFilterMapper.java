package eu.europa.ec.fisheries.uvms.reporting.service.mapper;

import eu.europa.ec.fisheries.uvms.reporting.service.dto.ExecutionLogDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.ExecutionLog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * //TODO create test
 */
@Mapper
public interface ExecutionLogFilterMapper {

    ExecutionLogFilterMapper INSTANCE = Mappers.getMapper(ExecutionLogFilterMapper.class);

    ExecutionLogDTO executionLogFilterToExecutionLogFilterDTO(ExecutionLog executionLog);

}
