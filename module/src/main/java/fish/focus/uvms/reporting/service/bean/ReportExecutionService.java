/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.service.bean;

import fish.focus.uvms.reporting.model.exception.ReportingServiceException;
import fish.focus.uvms.spatial.model.schemas.AreaIdentifierType;
import fish.focus.uvms.reporting.service.dto.DisplayFormat;
import fish.focus.uvms.reporting.service.dto.ExecutionResultDTO;
import fish.focus.uvms.reporting.service.dto.report.Report;
import java.time.Instant;
import java.util.List;

public interface ReportExecutionService {

    ExecutionResultDTO getReportExecutionByReportId(Long id, String username, String scopeName, List<AreaIdentifierType> areaRestrictions, Instant now, Boolean isAdmin, Boolean withActivity, DisplayFormat format) throws ReportingServiceException;

    ExecutionResultDTO getReportExecutionWithoutSave(Report report, List<AreaIdentifierType> areaRestrictions, String userName, Boolean withActivity, DisplayFormat format) throws ReportingServiceException;

}