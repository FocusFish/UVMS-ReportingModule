/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.reporting.service.mapper;

import java.util.List;
import java.util.Set;
import fish.focus.uvms.spatial.model.schemas.AreaIdentifierType;
import fish.focus.uvms.reporting.service.dto.Area;
import fish.focus.uvms.reporting.service.dto.AreaFilterDTO;
import fish.focus.uvms.reporting.service.entities.AreaFilter;
import fish.focus.uvms.reporting.service.util.ObjectFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(uses = ObjectFactory.class)
public interface AreaFilterMapper {

    AreaFilterMapper INSTANCE = Mappers.getMapper(AreaFilterMapper.class);

    AreaFilterDTO areaFilterToAreaFilterDTO(AreaFilter areaFilter); // TODO user Area from model

    AreaFilter areaFilterDTOToAreaFilter(AreaFilterDTO areaFilterDTO); // TODO user Area from model

    @Mappings({
            @Mapping(source="gid", target = "areaId")
    })
    AreaFilter areaToAreaFilter(Area area);

    Set<AreaFilter> arealistToAreaFilterSet(List<Area> areaList);

    @Mappings({
            @Mapping(source="areaId", target = "id")
    })
    AreaIdentifierType areaIdentifierTypeToAreaFilter(AreaFilter areaFilter);

    @Mappings({
            @Mapping(target = "report", ignore = true),
            @Mapping(target = "reportId", ignore = true)
    })
    void merge(AreaFilter incoming, @MappingTarget AreaFilter current);

}