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


package eu.europa.ec.fisheries.uvms.reporting.service.mapper;

import eu.europa.ec.fisheries.uvms.reporting.service.dto.FaFilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.FaFilter;
import eu.europa.ec.fisheries.uvms.reporting.service.entities.FaWeight;
import javax.persistence.Column;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FaFilterMapper {

    FaFilterMapper INSTANCE = Mappers.getMapper(FaFilterMapper.class);

    FaFilter faFilterDtoToFaFilter(FaFilterDTO faFilterDTO);

    FaFilterDTO faFilterToFaFilterDto(FaFilter faFilter);

    @Mappings({
            @Mapping(target = "min", source = "weightMin"),
            @Mapping(target = "max", source = "weightMax"),
            @Mapping(target = "unit", source = "weightUnit")

    })
    eu.europa.ec.fisheries.uvms.reporting.service.dto.FaWeight faWeightToFaWeightDTO(FaWeight faWeight);

    @Mappings({
            @Mapping(target = "report", ignore = true),
            @Mapping(target = "reportId", ignore = true)
    })
    void merge(FaFilter filter, @MappingTarget FaFilter faFilter);

    FaFilter faFilterDtoToFaFilter(eu.europa.ec.fisheries.uvms.reporting.service.dto.FaFilter faFilterDTO);

    @Mappings({
            @Mapping(target = "weightMin", source = "min"),
            @Mapping(target = "weightMax", source = "max"),
            @Mapping(target = "weightUnit", source = "unit")
    })
    FaWeight faWeightDtoToFaWeight(eu.europa.ec.fisheries.uvms.reporting.service.dto.FaWeight faWeightDTO);
}
