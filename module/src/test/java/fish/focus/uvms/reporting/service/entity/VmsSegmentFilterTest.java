/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package fish.focus.uvms.reporting.service.entity;

import fish.focus.schema.movement.search.v1.ListCriteria;
import fish.focus.schema.movement.search.v1.RangeCriteria;
import fish.focus.schema.movement.search.v1.RangeKeyType;
import fish.focus.schema.movement.search.v1.SearchKey;
import fish.focus.schema.movement.v1.SegmentCategoryType;
import fish.focus.uvms.reporting.service.entities.DurationRange;
import fish.focus.uvms.reporting.service.entities.Filter;
import fish.focus.uvms.reporting.service.entities.VmsSegmentFilter;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

@RunWith(JUnitParamsRunner.class)
@Ignore
public class VmsSegmentFilterTest {

    @Test
    @Parameters(method = "criteriaValues")
    public void shouldReturnListCriteria(VmsSegmentFilter filter, List<ListCriteria> expected){

        assertLenientEquals(expected, filter.movementListCriteria());

    }

    @Test
    @Parameters(method = "rangeCriteriaValues")
    public void shouldReturnListRangeCriteria(VmsSegmentFilter filter, List<RangeCriteria> expected){

        assertLenientEquals(expected, filter.movementRangeCriteria(null));

    }

    @Test
    public void shouldReturnId(){

        Filter segmentFilter = VmsSegmentFilter.builder().build();

        segmentFilter.getUniqKey();

        assertEquals(1L, segmentFilter.getUniqKey());

    }

    @Test
    public void shouldReturnSameAfterMerge(){

        Filter segmentFilter = VmsSegmentFilter.builder()
                .category(SegmentCategoryType.ANCHORED)
                .durationRange(new DurationRange(10F, 100F))
                .maximumSpeed(50F).minimumSpeed(5F)
                .build();
        Filter merged = VmsSegmentFilter.builder().build();

        segmentFilter.merge(merged);

        assertEquals(merged, segmentFilter);

    }

    protected Object[] criteriaValues(){

        Filter filter = VmsSegmentFilter.builder().category(SegmentCategoryType.ANCHORED).build();
        Filter filter2 = VmsSegmentFilter.builder().build();

        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setKey(SearchKey.CATEGORY);
        listCriteria.setValue(SegmentCategoryType.ANCHORED.value());

        return $(
               $(filter, Arrays.asList(listCriteria)),
               $(filter2, null)
        );
    }

    protected Object[] rangeCriteriaValues(){

        Filter filter1 = VmsSegmentFilter.builder().durationRange(new DurationRange(12F, 13F)).build();
        RangeCriteria rangeCriteria = new RangeCriteria();
        rangeCriteria.setKey(RangeKeyType.SEGMENT_DURATION);
        rangeCriteria.setFrom("12.0");
        rangeCriteria.setTo("13.0");

        Filter filter2 = VmsSegmentFilter.builder().minimumSpeed(6.45F).maximumSpeed(15.5F).build();
        RangeCriteria rangeCriteria2 = new RangeCriteria();
        rangeCriteria2.setKey(RangeKeyType.SEGMENT_SPEED);
        rangeCriteria2.setFrom("6.45");
        rangeCriteria2.setTo("15.5");

        Filter filter3 = VmsSegmentFilter.builder().maximumSpeed(15.5F).build();
        RangeCriteria rangeCriteria3 = new RangeCriteria();
        rangeCriteria2.setKey(RangeKeyType.SEGMENT_SPEED);
        rangeCriteria2.setTo("15.5");

        Filter filter4 =  VmsSegmentFilter.builder().durationRange(new DurationRange(0F, 13F)).build();
        RangeCriteria rangeCriteria4 = new RangeCriteria();
        rangeCriteria4.setKey(RangeKeyType.SEGMENT_DURATION);
        rangeCriteria4.setFrom("0.0");
        rangeCriteria4.setTo("13.0");

        return $(
            $(filter1, Arrays.asList(rangeCriteria)),
            $(filter2, Arrays.asList(rangeCriteria2)),
            $(filter3, Arrays.asList(rangeCriteria3)),
            $(filter4, Arrays.asList(rangeCriteria4))
        );
    }
}