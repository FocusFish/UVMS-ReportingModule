/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.service.dao;

import static fish.focus.uvms.reporting.service.dao.QueryParameter.with;
import static fish.focus.uvms.reporting.service.entities.Filter.DELETE_BY_ID;
import static fish.focus.uvms.reporting.service.entities.Filter.LIST_BY_REPORT_ID;
import fish.focus.uvms.reporting.service.exception.ServiceException;
import fish.focus.uvms.reporting.service.entities.Filter;
import java.util.List;
import javax.persistence.EntityManager;

public class FilterDAO extends AbstractDAO<Filter> {

    private EntityManager em;

    public FilterDAO(EntityManager em){
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Filter> listByReportId(final Long reportId) throws ServiceException {
        return findEntityByNamedQuery(Filter.class, LIST_BY_REPORT_ID, with("reportId", reportId).parameters());
    }

    public void deleteBy(final Long id) throws ServiceException {
        deleteEntityByNamedQuery(Filter.class, DELETE_BY_ID, with("id", id).parameters());
    }
}