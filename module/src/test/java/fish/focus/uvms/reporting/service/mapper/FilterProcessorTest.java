/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package fish.focus.uvms.reporting.service.mapper;

import fish.focus.schema.movement.search.v1.ListCriteria;
import fish.focus.schema.movement.search.v1.SearchKey;
import fish.focus.uvms.reporting.service.exception.ProcessorException;
import fish.focus.wsdl.asset.types.AssetListCriteriaPair;
import fish.focus.wsdl.asset.types.ConfigSearchField;
import fish.focus.uvms.reporting.service.entities.AssetFilter;
import fish.focus.uvms.reporting.service.entities.AssetGroupFilter;
import fish.focus.uvms.reporting.service.entities.Filter;
import fish.focus.uvms.reporting.service.util.FilterProcessor;
import lombok.SneakyThrows;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class FilterProcessorTest extends UnitilsJUnit4 {

    @TestedObject
    private FilterProcessor processor;

    @Test
    @SneakyThrows
    public void testInitWithAssetFilter() {

        Set<Filter> filterList = new HashSet<>();

        AssetFilter assetFilter = AssetFilter.builder().guid("guid").build();
        filterList.add(assetFilter);

        processor = new FilterProcessor(filterList, Instant.now());

        assertEquals(1, processor.getAssetListCriteriaPairs().size());
        assertEquals(0, processor.getRangeCriteria().size());
        assertEquals(1, processor.getMovementListCriteria().size());

        List<AssetListCriteriaPair> assetListCriteriaPairList = new ArrayList<>();
        assetListCriteriaPairList.addAll(processor.getAssetListCriteriaPairs());

        List<ListCriteria> listCriteria = new ArrayList<>();
        listCriteria.addAll(processor.getMovementListCriteria());

        processor.getAssetListCriteriaPairs();
        assertEquals(assetListCriteriaPairList.get(0).getKey(), ConfigSearchField.HIST_GUID);
        assertEquals(assetListCriteriaPairList.get(0).getValue(), "guid");

        assertEquals(listCriteria.get(0).getKey(), SearchKey.CONNECT_ID);
        assertEquals(listCriteria.get(0).getValue(), "guid");

    }

    @Test(expected = ProcessorException.class)
    @SneakyThrows
    public void shouldThrowExceptionWhenEmptyFilterList() {
        Set<Filter> filterList = new HashSet<>();
        processor = new FilterProcessor(filterList, Instant.now());
    }

    @Test(expected = ProcessorException.class)
    @SneakyThrows
    public void testSanityFilterSetNull() {
        processor = new FilterProcessor(null, Instant.now());
    }

}