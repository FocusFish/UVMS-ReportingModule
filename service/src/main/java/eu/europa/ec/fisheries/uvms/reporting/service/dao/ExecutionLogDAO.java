package eu.europa.ec.fisheries.uvms.reporting.service.dao;

import eu.europa.ec.fisheries.uvms.reporting.service.entities.ExecutionLog;
import eu.europa.ec.fisheries.uvms.service.AbstractDAO;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;

@Slf4j
public class ExecutionLogDAO extends AbstractDAO<ExecutionLog> {

    private EntityManager em;

    public ExecutionLogDAO(EntityManager em){
        this.em = em;
    }

    public ExecutionLog findById(long id) {
        log.debug("getting ReportExecutionLogEntity instance with id: " + id);
        try {
            ExecutionLog instance = em.find(ExecutionLog.class, id);
            log.debug("get successful");
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}