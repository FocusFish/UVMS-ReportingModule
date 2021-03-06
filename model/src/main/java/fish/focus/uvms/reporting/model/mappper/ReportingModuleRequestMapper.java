/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package fish.focus.uvms.reporting.model.mappper;

import fish.focus.uvms.reporting.model.schemas.ReportGetStartAndEndDateRQ;
import fish.focus.uvms.reporting.model.schemas.ReportingModuleMethod;
import fish.focus.uvms.reporting.model.exception.ReportingModelException;
import fish.focus.uvms.reporting.model.util.JAXBMarshaller;

/**
 * Created by padhyad on 3/18/2016.
 */
public class ReportingModuleRequestMapper {

    public static String mapToSpatialSaveOrUpdateMapConfigurationRQ(String now, Long id, String userName, String scopeName) throws ReportingModelException {
        try {
            ReportGetStartAndEndDateRQ request = new ReportGetStartAndEndDateRQ(ReportingModuleMethod.GET_REPORT_START_END_DATE, now, id, userName, scopeName);
            return JAXBMarshaller.marshall(request);
        } catch (ReportingModelException ex) {
            throw new ReportingModelException("[ Error when marshalling Object to String ]", ex);
        }
    }
}