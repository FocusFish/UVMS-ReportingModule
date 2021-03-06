/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.message.bean;

import fish.focus.uvms.commons.message.api.MessageConstants;
import fish.focus.uvms.reporting.model.exception.ReportingModelException;
import fish.focus.uvms.reporting.model.mappper.ReportingModuleResponseMapper;
import fish.focus.uvms.reporting.model.schemas.ReportGetStartAndEndDateRQ;
import fish.focus.uvms.reporting.model.schemas.ReportingModuleMethod;
import fish.focus.uvms.reporting.model.schemas.ReportingModuleRequest;
import fish.focus.uvms.reporting.model.util.JAXBMarshaller;
import fish.focus.uvms.reporting.message.event.GetReportStartAndEndDateEvent;
import fish.focus.uvms.reporting.message.event.ReportingMessageErrorEvent;
import fish.focus.uvms.reporting.message.event.ReportingMessageEvent;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_TYPE_STR, propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
        @ActivationConfigProperty(propertyName = MessageConstants.DESTINATION_STR, propertyValue = MessageConstants.QUEUE_REPORTING_EVENT)
})
@Slf4j
public class ReportingMessageConsumerBean implements MessageListener {

    private static final int REPORTING_MESSAGE = 1700;

    @Inject
    @GetReportStartAndEndDateEvent
    private Event<ReportingMessageEvent> reportStartAndEndDateEvent;

    @Inject
    @ReportingMessageErrorEvent
    private Event<ReportingMessageEvent> reportingErrorEvent;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            ReportingModuleRequest request = JAXBMarshaller.unmarshall(textMessage, ReportingModuleRequest.class);
            ReportingModuleMethod method = request.getMethod();
            switch (method) {
                case GET_REPORT_START_END_DATE:
                    ReportGetStartAndEndDateRQ reportGetStartAndEndDateRQ = JAXBMarshaller.unmarshall(textMessage, ReportGetStartAndEndDateRQ.class);
                    ReportingMessageEvent reportDatesEvent = new ReportingMessageEvent(textMessage, reportGetStartAndEndDateRQ);
                    reportStartAndEndDateEvent.fire(reportDatesEvent);
                    break;
                default:
                    log.error("[ Not implemented method consumed: {} ]", method);
                    reportingErrorEvent.fire(new ReportingMessageEvent(textMessage, ReportingModuleResponseMapper.createFaultMessage(REPORTING_MESSAGE, "Method not implemented")));
            }
        } catch (ReportingModelException e) {
            log.error("[ Error when receiving message in Reporting Module. ]", e);
            reportingErrorEvent.fire(new ReportingMessageEvent(textMessage, ReportingModuleResponseMapper.createFaultMessage(REPORTING_MESSAGE, "Method not implemented")));
        }
    }
}