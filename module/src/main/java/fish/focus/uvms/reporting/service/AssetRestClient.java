package fish.focus.uvms.reporting.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fish.focus.uvms.asset.client.model.AssetDTO;
import fish.focus.uvms.asset.client.model.AssetListResponse;
import fish.focus.uvms.asset.client.model.search.SearchBranch;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequestScoped
@Slf4j
public class AssetRestClient {

    @Inject
    private InternalRestTokenHandler tokenHandler;

    private WebTarget webTarget;
    private ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .findAndRegisterModules();

    @Resource(name = "java:global/asset_endpoint")
    String assetUrl;

    @PostConstruct
    private void setUpClient() {
        Client client = ClientBuilder.newClient();
        webTarget = client.target(assetUrl);
    }

    public List<AssetDTO> getAssetList(SearchBranch query) {
        String response = webTarget
                .path("/internal/query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(query), String.class);

        try {
            AssetListResponse assetListResponse = mapper.readValue(response, AssetListResponse.class);
            return assetListResponse.getAssetList();
        } catch (IOException e) {
            log.error("Error when retrieving GetMovementMapByQueryResponse.", e);
            throw new RuntimeException(e);
        }
    }

    public List<AssetDTO> getAssetsByGroupIds(List<UUID> idList) {
        String response = webTarget
                .path("/internal/group/asset")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, tokenHandler.createAndFetchToken("user"))
                .post(Entity.json(idList), String.class);

        try {
            return Arrays.asList(mapper.readValue(response, AssetDTO[].class));
        } catch (IOException e) {
            log.error("Error when retrieving List<AssetDTO>.", e);
            throw new RuntimeException(e);
        }
    }
}
