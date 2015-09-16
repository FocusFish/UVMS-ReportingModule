package eu.europa.ec.fisheries.uvms.reporting.message.producer.bean;

import eu.europa.ec.fisheries.uvms.message.AbstractProducer;
import eu.europa.ec.fisheries.uvms.message.MessageConstants;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

/**
 * //TODO create test
 */
@Stateless
@LocalBean
public class MovementProducerBean extends AbstractProducer {

    @Resource(mappedName = MessageConstants.QUEUE_MODULE_MOVEMENT)
    private Destination movementModuleQ;

    @Resource(lookup = MessageConstants.CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;

    @Override
    protected ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    protected String getModuleName() {
        return "reporting";
    }

    @Override
    public Destination getDestination() {
        return movementModuleQ;
    }
}
