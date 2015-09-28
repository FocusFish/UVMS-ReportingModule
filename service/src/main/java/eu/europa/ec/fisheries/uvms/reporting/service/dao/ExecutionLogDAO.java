package eu.europa.ec.fisheries.uvms.reporting.service.reporsitory;

import eu.europa.ec.fisheries.uvms.reporting.service.entities.ExecutionLog;
import eu.europa.ec.fisheries.uvms.service.AbstractCrudService;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;

@Slf4j
public class ExecutionLogDAO extends AbstractCrudService {

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