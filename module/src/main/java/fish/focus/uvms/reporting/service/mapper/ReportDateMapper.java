/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.service.mapper;

import fish.focus.uvms.commons.date.DateUtils;
import fish.focus.uvms.reporting.model.schemas.ReportGetStartAndEndDateRS;
import fish.focus.uvms.reporting.service.entities.CommonFilter;
import fish.focus.uvms.reporting.service.entities.Position;
import fish.focus.uvms.reporting.service.entities.Selector;
import lombok.Builder;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ReportDateMapper {

    private Date now;

    @Builder(builderMethodName = "ReportDateMapperBuilder")
    public ReportDateMapper(Date now) {
        this.now = now;
    }

    public ReportGetStartAndEndDateRS getReportDates(CommonFilter filter) {
        ReportGetStartAndEndDateRS response = new ReportGetStartAndEndDateRS();
        Date startDate = null;
        Date endDate = null;
        if (filter.getPositionSelector().getSelector().equals(Selector.all)) {
            startDate = filter.getDateRange().getStartDate();
            endDate = filter.getDateRange().getEndDate();
        } else if (filter.getPositionSelector().getSelector().equals(Selector.last)) {
            if (filter.getPositionSelector().getPosition().equals(Position.hours)) {
                Instant dateTime = now.toInstant();
                dateTime = dateTime.minus(filter.getPositionSelector().getValue().intValue(), ChronoUnit.HOURS);
                startDate = Date.from(dateTime);
                endDate = now;
            } else if (filter.getPositionSelector().getPosition().equals(Position.positions)) {
                startDate = filter.getDateRange().getStartDate();
                endDate = now;
            }
        }
        response.setStartDate(startDate == null ? null : new SimpleDateFormat(DateUtils.DATE_TIME_UI_FORMAT).format(startDate));
        response.setEndDate(new SimpleDateFormat(DateUtils.DATE_TIME_UI_FORMAT).format(endDate));

        return response;
    }
}