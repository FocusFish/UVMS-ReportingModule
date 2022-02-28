package fish.focus.uvms.reporting.service.mapper;

import java.util.List;

import fish.focus.uvms.activity.model.schemas.GroupCriteria;
import fish.focus.uvms.reporting.service.dto.CriteriaFilterDTO;
import fish.focus.uvms.reporting.service.entities.GroupCriteriaFilter;
import fish.focus.uvms.reporting.service.enums.GroupCriteriaType;
import fish.focus.uvms.reporting.service.util.ObjectFactory;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ObjectFactory.class})
public interface GroupCriteriaFilterMapper {

    GroupCriteriaFilterMapper INSTANCE = Mappers.getMapper(GroupCriteriaFilterMapper.class);

    GroupCriteriaFilter mapCriteriaFilterDTOToCriteriaFilter(CriteriaFilterDTO dto);

    List<GroupCriteriaFilter> mapCriteriaFilterDTOToCriteriaFilter(List<CriteriaFilterDTO> dto);

    @InheritInverseConfiguration
    CriteriaFilterDTO mapCriteriaFilterToCriteriaFilterDTO(GroupCriteriaFilter entity);

    @Mappings({
            @Mapping(target = "report", ignore = true),
            @Mapping(target = "reportId", ignore = true),
    })
    void merge(GroupCriteriaFilter incoming, @MappingTarget GroupCriteriaFilter current);

    @IterableMapping(elementTargetType = String.class)
    List<String> mapStringListToGroupCriteriaTypeList(List<GroupCriteriaType> value);

    @InheritInverseConfiguration
    List<GroupCriteriaType> mapGroupCriteriaTypeListToStringList(List<String> value);

    List<GroupCriteria> mapGroupCriteriaTypeListToGroupCriteriaList(List<GroupCriteriaType> value);

    @InheritInverseConfiguration
    List<GroupCriteriaType> mapGroupCriteriaListToGroupCriteriaTypeList(List<GroupCriteria> value);

    @ValueMappings({
            @ValueMapping(source = "DATE",target = "<NULL>")
    })
    GroupCriteriaType mapGroupCriteriaToGroupCriteriaType(GroupCriteria value);

    @InheritInverseConfiguration
    GroupCriteria mapGroupCriteriaTypeToGroupCriteria(GroupCriteriaType value);

}
