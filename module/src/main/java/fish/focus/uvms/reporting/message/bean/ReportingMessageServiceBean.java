/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.message.bean;

import fish.focus.uvms.commons.message.impl.AbstractProducer;
import fish.focus.uvms.reporting.model.exception.ReportingModelException;
import fish.focus.uvms.reporting.model.util.JAXBMarshaller;
import fish.focus.uvms.reporting.message.event.ReportingMessageErrorEvent;
import fish.focus.uvms.reporting.message.event.ReportingMessageEvent;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.jms.Destination;
import javax.jms.JMSException;
import lombok.extern.slf4j.Slf4j;

@Stateless
@LocalBean
@Slf4j
public class ReportingMessageServiceBean extends AbstractProducer {

    @Override
    public Destination getDestination() {
        return null;
    }

    public void sendModuleErrorResponseMessage(@Observes @ReportingMessageErrorEvent ReportingMessageEvent messageWrap) {
        try {
            String data = JAXBMarshaller.marshall(messageWrap.getFault());
            sendResponseMessageToSender(messageWrap.getMessage(), data);
        } catch (JMSException | ReportingModelException e) {
            log.error("[ Error when returning module spatial request. ] {} {}", e.getMessage(), e.getStackTrace(), e);
        }
    }
}