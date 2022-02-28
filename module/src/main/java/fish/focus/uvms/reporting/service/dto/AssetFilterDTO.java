/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.reporting.service.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import fish.focus.uvms.reporting.service.entities.Filter;
import fish.focus.uvms.reporting.service.entities.FilterType;
import fish.focus.uvms.reporting.service.mapper.AssetFilterMapper;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true, of = {"guid"})
public class AssetFilterDTO extends FilterDTO {

    private static final int MAX_SIZE = 255;
    private static final int MIN_SIZE = 1;

    @Size(min = MIN_SIZE, max = MAX_SIZE)
    @NotNull
    private String guid;

    @Size(min = MIN_SIZE, max = MAX_SIZE)
    @NotNull
    private String name;

    public AssetFilterDTO() {
        super(FilterType.asset);
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Filter convertToFilter() {
        return AssetFilterMapper.INSTANCE.assetFilterDTOToAssetFilter(this);
    }
}