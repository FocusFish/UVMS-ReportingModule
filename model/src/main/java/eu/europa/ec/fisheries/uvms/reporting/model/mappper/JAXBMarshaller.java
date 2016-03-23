package eu.europa.ec.fisheries.uvms.reporting.model.mappper;

import eu.europa.ec.fisheries.uvms.reporting.model.exception.ReportingModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

public final class JAXBMarshaller {

    final static Logger LOG = LoggerFactory.getLogger(JAXBMarshaller.class);

    private JAXBMarshaller() {}

    /**
     * Marshalls a JAXB Object to a XML String representation
     *
     * @param <T>
     * @param data
     * @return
     * @throws ReportingModelException
     */
    public static <T> String marshall(T data) throws ReportingModelException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{data.getClass()});
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            StringWriter sw = new StringWriter();
            marshaller.marshal(data, sw);
            return sw.toString();
        } catch (JAXBException e) {
            throw new ReportingModelException("Error when marshalling " , e);
        }
    }

    /**
     * Unmarshalls A textMessage to the desired Object. The object must be the
     * root object of the unmarshalled message!
     *
     * @param <R>
     * @param textMessage
     * @param clazz       clazz
     * @return
     * @throws ReportingModelException
     */
    public static <R> R unmarshall(TextMessage textMessage, Class clazz) throws ReportingModelException {
        try {
            JAXBContext jc = JAXBContext.newInstance(new Class[]{clazz});
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            StringReader sr = new StringReader(textMessage.getText());
            return (R) unmarshaller.unmarshal(sr);
        } catch (JMSException | JAXBException e) {
            throw new ReportingModelException("Error when unmarshalling response in ResponseMapper: ", e);
        }
    }

}