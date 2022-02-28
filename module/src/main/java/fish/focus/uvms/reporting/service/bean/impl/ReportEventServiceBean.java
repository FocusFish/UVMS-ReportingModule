/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.service.bean.impl;

import fish.focus.uvms.reporting.model.mappper.ReportingModuleResponseMapper;
import fish.focus.uvms.reporting.model.schemas.ReportGetStartAndEndDateRQ;
import fish.focus.uvms.reporting.model.schemas.ReportGetStartAndEndDateRS;
import fish.focus.uvms.reporting.message.bean.ReportingMessageServiceBean;
import fish.focus.uvms.reporting.message.event.GetReportStartAndEndDateEvent;
import fish.focus.uvms.reporting.message.event.ReportingMessageErrorEvent;
import fish.focus.uvms.reporting.message.event.ReportingMessageEvent;
import fish.focus.uvms.reporting.service.bean.ReportEventService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Stateless
@Slf4j
public class ReportEventServiceBean implements ReportEventService {

    private static final int REPORTING_MESSAGE = 1700;

    @Inject
    @ReportingMessageErrorEvent
    private Event<ReportingMessageEvent> reportingErrorEvent;

    @EJB
    private ReportingMessageServiceBean messageProducer;

    @EJB
    private ReportServiceBean reportService;

    @Override
    public void getReportDates(@Observes @GetReportStartAndEndDateEvent ReportingMessageEvent event) {
        log.info("Getting Report Start and End date");
        try {
            ReportGetStartAndEndDateRQ request = event.getReportGetStartAndEndDateRQ();
            ReportGetStartAndEndDateRS response = reportService.getReportDates(request.getNow(), request.getId(), request.getUserName(), request.getScopeName());
            messageProducer.sendResponseMessageToSender(event.getMessage(), ReportingModuleResponseMapper.mapReportGetStartAndEndDateRS(response));
        } catch (Exception e) {
            sendError(event, e);
        }
    }

    private void sendError(ReportingMessageEvent event, Exception e) {
        log.error("[ Error in Reporting Module. ]", e);
        reportingErrorEvent.fire(new ReportingMessageEvent(event.getMessage(), ReportingModuleResponseMapper.createFaultMessage(REPORTING_MESSAGE, "Exception in reporting [ " + e.getMessage())));
    }
}