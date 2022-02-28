package fish.focus.uvms.reporting.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fish.focus.schema.movement.module.v1.GetMovementMapByQueryResponse;
import fish.focus.schema.movement.search.v1.MovementQuery;
import fish.focus.uvms.rest.security.InternalRestTokenHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@RequestScoped
@Slf4j
public class MovementClient {

    private WebTarget webTarget;
    private ObjectMapper mapper = new ObjectMapper();

    @Resource(name = "java:global/movement_endpoint")
    String movementUrl;

    @Inject
    private InternalRestTokenHandler tokenHandler;

    @PostConstruct
    private void setUpClient() {
        Client client = ClientBuilder.newClient();
        webTarget = client.target(movementUrl);
    }

    public GetMovementMapByQueryResponse getMovementMapResponseTypes(MovementQuery query) {
        String response = webTarget
                .path("internal/")
                .path("movementMapByQuery")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(query), String.class);

        try {
            return mapper.readValue(response, GetMovementMapByQueryResponse.class);
        } catch (IOException e) {
            log.error("Error when retrieving GetMovementMapByQueryResponse.", e);
            throw new RuntimeException(e);
        }
    }
}
