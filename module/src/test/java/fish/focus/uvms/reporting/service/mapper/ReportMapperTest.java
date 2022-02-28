/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package fish.focus.uvms.reporting.service.mapper;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;
import fish.focus.uvms.reporting.service.dto.FilterDTO;
import fish.focus.uvms.reporting.service.dto.report.ReportDTO;
import fish.focus.uvms.reporting.service.dto.report.VisibilityEnum;
import fish.focus.uvms.reporting.service.entities.ExecutionLog;
import fish.focus.uvms.reporting.service.entities.Report;
import fish.focus.uvms.reporting.service.entities.ReportDetails;
import fish.focus.uvms.reporting.service.mapper.ReportMapper;

//TODO implement more tests
public class ReportMapperTest extends UnitilsJUnit4 {

    @TestedObject
    private ReportMapper mapper = ReportMapper.ReportMapperBuilder().filters(false).build();

    private Report report;

    @Before
    public void before(){
        report = Report.builder().createdBy("you").details(
                ReportDetails.builder().description("desc").scopeName("scopeName").name("name").withMap(true).build())
                .executionLogs(new HashSet<ExecutionLog>())
                .build();
    }

    @Test
    public void testReportDTOToReportWithNull(){
        Report report = mapper.reportDTOToReport(null);

        assertNull(report);
    }

    @Test
    @Ignore
    public void testReportToReportDTOWithNoFilters(){

        ReportDTO expectedDTO = ReportDTO.builder()
                .id(1L)
                .createdBy("you")
                .description("desc")
                .name("name")
                .withMap(true)
                .visibility(VisibilityEnum.PRIVATE)
                .scopeName("scopeName")
                .filters(new ArrayList<FilterDTO>())
                .build();

        ReportDTO dto = mapper.reportToReportDTO(report);

        assertTrue(expectedDTO.equals(dto));

    }

    @Test
    public void testReportToReportDTOWithReportNull(){

        ReportDTO dto = mapper.reportToReportDTO(null);

        assertNull(dto);

    }
}