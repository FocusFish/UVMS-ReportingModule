/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.service.bean.impl;

import fish.focus.uvms.reporting.service.exception.ServiceException;
import fish.focus.uvms.reporting.model.exception.ReportingServiceException;
import fish.focus.uvms.reporting.service.bean.ReportRepository;
import fish.focus.uvms.reporting.service.dao.ExecutionLogDAO;
import fish.focus.uvms.reporting.service.dao.FilterDAO;
import fish.focus.uvms.reporting.service.dao.ReportDAO;
import fish.focus.uvms.reporting.service.dto.FilterDTO;
import fish.focus.uvms.reporting.service.dto.report.ReportDTO;
import fish.focus.uvms.reporting.service.dto.report.VisibilityEnum;
import fish.focus.uvms.reporting.service.entities.Report;
import fish.focus.uvms.reporting.service.util.merger.FilterMerger;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

@Stateless
@Local(ReportRepository.class)
@Slf4j
public class ReportRepositoryBean implements ReportRepository {

    private ReportDAO reportDAO;
    private FilterDAO filterDAO;
    private ExecutionLogDAO executionLogDAO;
    private FilterMerger filterMerger;
    private EntityManager em;

    @PersistenceContext(unitName = "reportingPUposgres")
    private EntityManager postgres;
	
    private void initEntityManager() {
        em = postgres;
    }
	
    @PostConstruct
    public void postConstruct(){
		initEntityManager();	
        reportDAO = new ReportDAO(em);
        filterDAO = new FilterDAO(em);
        executionLogDAO = new ExecutionLogDAO(em);
        filterMerger = new FilterMerger(em);
    }

    @Override
    @Transactional
    public boolean update(final ReportDTO reportDTO) throws ReportingServiceException {
        try {
            Report entityById = reportDAO.findEntityById(Report.class, reportDTO.getId());
            entityById.getDetails().setDescription(reportDTO.getDescription());
            entityById.getDetails().setName(reportDTO.getName());
            entityById.getDetails().setWithMap(reportDTO.getWithMap());
            entityById.setVisibility(reportDTO.getVisibility());
            List<FilterDTO> filters = reportDTO.getFilters();
            if (CollectionUtils.isNotEmpty(filters)) {
                filterMerger.merge(filters);
          }
        } catch (ServiceException e) {
            String message = "UPDATE FAILED";
            log.error(message, e);
            throw new ReportingServiceException(message, e);
        }
        return true;
    }

    @Override
    public Report findReportByReportId(Long reportId, String username, String scopeName, Boolean isAdmin) throws ReportingServiceException {
        return reportDAO.findReportByReportId(reportId, username, scopeName, isAdmin);
    }

    @Override
    public Report findReportByReportId(Long reportId) throws ReportingServiceException, ServiceException {
        return reportDAO.findEntityById(Report.class, reportId);
    }

    @Override
    public List<Report> listByUsernameAndScope(String username, String scopeName, Boolean existent, Boolean isAdmin) throws ReportingServiceException {
        return reportDAO.listByUsernameAndScope(username, scopeName, existent, isAdmin);
    }

    @Override
    public List<Report> listTopExecutedReportByUsernameAndScope(String username, String scopeName, Boolean existent, boolean isAdmin, Integer numberOfReport) throws ReportingServiceException {
        return reportDAO.listTopExecutedReportByUsernameAndScope(username, scopeName, existent, isAdmin, numberOfReport);
    }

    @Override
    @Transactional
    public void remove(Long reportId, String username, String scopeName, Boolean isAdmin) throws ReportingServiceException {
        reportDAO.softDelete(reportId, username, scopeName, isAdmin);
    }

    @Override
    @Transactional
    public Report createEntity(Report report) throws ReportingServiceException {
        try {
            return reportDAO.createEntity(report);
        } catch (Exception e) {
            log.error("createEntity failed", e);
            throw new ReportingServiceException("createEntity failed", e);
        }
    }

    @Override
    @Transactional
    public void changeVisibility(Long reportId, VisibilityEnum newVisibility, String username, String scopeName, Boolean isAdmin) throws ReportingServiceException {
        reportDAO.changeVisibility(reportId, newVisibility, username, scopeName, isAdmin);
    }
}