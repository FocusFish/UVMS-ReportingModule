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
import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.reporting.service.entities.DateRange;
import fish.focus.uvms.reporting.service.entities.CommonFilter;
import fish.focus.uvms.reporting.service.entities.Position;
import fish.focus.uvms.reporting.service.entities.PositionSelector;
import fish.focus.uvms.reporting.service.entities.Selector;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class CommonFilterTest {

    final String now = "2013-02-28 12:24:56 +0100";

    @Test
    @Parameters(method = "rangeCriteria")
    public void shouldReturnRangeCriteria(CommonFilter filter, RangeCriteria rangeCriteria){

        Iterator<RangeCriteria> iterator = filter.movementRangeCriteria( DateUtils.stringToDate(now)).iterator();

        if (iterator.hasNext()) {
            assertEquals(rangeCriteria, iterator.next());
        }
    }

    @Test
    @Parameters(method = "listCriteria")
    public void shouldReturnListCriteria(CommonFilter filter, ListCriteria listCriteria){

        assertEquals(listCriteria, filter.movementListCriteria().iterator().next());

    }

    protected Object[] listCriteria(){

        CommonFilter filter = CommonFilter.builder()
                .positionSelector(PositionSelector.builder()
                        .selector(Selector.last)
                        .position(Position.positions).value(100.1F).build())
                .build();

        ListCriteria criteria = new ListCriteria();
        criteria.setKey(SearchKey.NR_OF_LATEST_REPORTS);
        criteria.setValue(String.valueOf(100));

        return $(
                $(filter, criteria)
        );
    }

    protected Object[] rangeCriteria(){

        String fromMinus24Hours = "2013-02-27 12:24:56 +0100";

        CommonFilter filter1 = new CommonFilter(){
            protected Instant nowUTC() {
                return DateUtils.stringToDate(now);
            }
        };

        filter1.setPositionSelector(PositionSelector.builder()
                        .selector(Selector.last).value(24F)
                        .position(Position.hours).build()
        );
        RangeCriteria expectedCriteria = new RangeCriteria();
        expectedCriteria.setKey(RangeKeyType.DATE);
        expectedCriteria.setFrom(fromMinus24Hours);
        expectedCriteria.setTo(now);
        setDefaultValues(expectedCriteria);

        String to = "2014-02-28 12:24:56 +0100";
        CommonFilter filter2 = CommonFilter.builder()
                .positionSelector(PositionSelector.builder()
                        .selector(Selector.all).build())
                .dateRange(new DateRange(Date.from(DateUtils.stringToDate(now)), Date.from(DateUtils.stringToDate(to))))
                .build();

        RangeCriteria expectedCriteria2 = new RangeCriteria();
        expectedCriteria2.setKey(RangeKeyType.DATE);
        expectedCriteria2.setFrom(now);
        expectedCriteria2.setTo(to);
        setDefaultValues(expectedCriteria2);

        final Float hours = 100F;

        CommonFilter filter3 = new CommonFilter(){
            @Override
            protected Instant nowUTC() {
                return DateUtils.stringToDate(now);
            }
        };

        filter3.setPositionSelector(PositionSelector.builder()
                .selector(Selector.last).position(Position.positions).value(hours).build());

        RangeCriteria expectedCriteria3 = new RangeCriteria();
        expectedCriteria3.setKey(RangeKeyType.DATE);
        expectedCriteria3.setFrom("1970-01-01 00:00:00 Z");
        expectedCriteria3.setTo(DateUtils.dateToHumanReadableString(DateUtils.stringToDate(now)));

        return $(
                $(filter1, expectedCriteria),
                $(filter2, expectedCriteria2),
                $(filter3, expectedCriteria3)
        );
    }

    @Test
    public void shouldBeEqualWhenMerging() {

        CommonFilter filter;

        final Calendar calendar = new GregorianCalendar(2013, 1, 28, 13, 24, 56);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        Date endDate = calendar.getTime();

        CommonFilter incoming = CommonFilter.builder()
                .positionSelector(PositionSelector.builder().selector(Selector.all).build())
                .dateRange(new DateRange(startDate, endDate))
                .build();

        filter = CommonFilter.builder()
                .positionSelector(PositionSelector.builder()
                        .selector(Selector.last).value(1F).position(Position.hours).build())
                .build();

        assertNotEquals(filter, incoming);

        filter.merge(incoming);

        assertEquals(filter.toString(),incoming.toString());

    }

    private void setDefaultValues(final RangeCriteria date) {
        if (date.getTo() == null) {
            date.setFrom(DateUtils.dateToHumanReadableString(DateUtils.nowUTC())); // FIXME use offset
        }
        if (date.getFrom() == null) {
            date.setFrom(DateUtils.dateToHumanReadableString(Instant.EPOCH));
        }
    }


}