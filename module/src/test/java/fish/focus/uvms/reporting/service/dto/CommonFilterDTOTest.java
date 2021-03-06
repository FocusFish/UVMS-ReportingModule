/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package fish.focus.uvms.reporting.service.dto;


import org.junit.Test;
import fish.focus.uvms.reporting.service.dto.CommonFilterDTO;
import fish.focus.uvms.reporting.service.entities.Selector;
import java.util.Date;
import static fish.focus.uvms.reporting.service.dto.CommonFilterDTO.CommonFilterDTOBuilder;
import static fish.focus.uvms.reporting.service.dto.PositionSelectorDTO.PositionSelectorDTOBuilder;
import static junit.framework.TestCase.assertTrue;

public class CommonFilterDTOTest {

    @Test
    public void testEquals(){

        CommonFilterDTO build = CommonFilterDTOBuilder()
                .endDate(new Date(2004, 3, 26, 12, 1, 1))
                .startDate(new Date(2005, 3, 26, 12, 1, 1))
                .reportId(45L)
                .positionSelector(PositionSelectorDTOBuilder().selector(Selector.all).build())
                .build();

        CommonFilterDTO build2 = CommonFilterDTOBuilder()
                .endDate(new Date(2004, 3, 26, 12, 1, 1))
                .startDate(new Date(2005, 3, 26, 12, 1, 1))
                .reportId(38L)
                .positionSelector(PositionSelectorDTOBuilder().selector(Selector.all).build())
                .build();

        assertTrue(build.equals(build2));
    }
}