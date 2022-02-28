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


package fish.focus.uvms.reporting.service.bean;

import fish.focus.uvms.activity.model.schemas.FACatchSummaryReportResponse;
import fish.focus.uvms.activity.model.schemas.FishingTripResponse;
import fish.focus.uvms.activity.model.schemas.GroupCriteria;
import fish.focus.uvms.activity.model.schemas.ListValueTypeFilter;
import fish.focus.uvms.activity.model.schemas.SingleValueTypeFilter;
import fish.focus.uvms.reporting.model.exception.ReportingServiceException;
import java.util.List;

public interface ActivityService {

    FishingTripResponse getFishingTrips(List<SingleValueTypeFilter> singleValueTypeFilters, List<ListValueTypeFilter> listValueTypeFilters) throws ReportingServiceException;

    FACatchSummaryReportResponse getFaCatchSummaryReport(List<SingleValueTypeFilter> singleValueTypeFilters, List<ListValueTypeFilter> listValueTypeFilters, List<GroupCriteria> groupCriteriaList) throws ReportingServiceException;
}
