/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.service.bean.impl;

import fish.focus.uvms.audit.model.mapper.AuditLogModelMapper;
import fish.focus.uvms.commons.service.interceptor.AuditActionEnum;
import fish.focus.uvms.reporting.model.exception.ReportingServiceException;
import fish.focus.uvms.reporting.message.service.AuditMessageServiceBean;
import fish.focus.uvms.reporting.service.bean.AuditService;
import fish.focus.uvms.reporting.service.bean.ReportingServiceConstants;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;

@Stateless
@Local(value = AuditService.class)
@Slf4j
public class AuditServiceBean implements AuditService {

	@EJB
	private transient AuditMessageServiceBean auditProducerBean;

	@Override
	public void sendAuditReport(final AuditActionEnum auditActionEnum, final String objectId, final String userName) throws ReportingServiceException {
        log.debug("Audit report request received for type = {} ", auditActionEnum.getAuditType());
        try {
			String msgToSend = AuditLogModelMapper.mapToAuditLog(ReportingServiceConstants.REPORTING_MODULE, auditActionEnum.getAuditType(), objectId, userName);
            log.trace("Sending JMS message to Audit {} ", msgToSend);
            auditProducerBean.sendModuleMessage(msgToSend, null);
        } catch (JMSException e) {
            log.error("Exception in Sending Message to Audit Queue", e);
            throw new ReportingServiceException(e);
		}
	}
}