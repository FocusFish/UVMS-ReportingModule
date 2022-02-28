/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.reporting.service.bean.impl;

import fish.focus.uvms.asset.client.model.AssetDTO;
import fish.focus.uvms.asset.client.model.search.SearchBranch;
import fish.focus.uvms.commons.service.interceptor.SimpleTracingInterceptor;
import fish.focus.wsdl.asset.types.Asset;
import fish.focus.wsdl.asset.types.AssetListQuery;
import fish.focus.uvms.reporting.service.AssetRestClient;
import fish.focus.uvms.reporting.service.mapper.AssetQueryMapper;
import fish.focus.uvms.reporting.service.util.FilterProcessor;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.*;

@RequestScoped
public class AssetServiceBean {

    @Inject
    private AssetRestClient assetRestClient;

    @Interceptors(SimpleTracingInterceptor.class)
    public Map<String, Asset> getAssetMap(final FilterProcessor processor) {

        Map<String, Asset> assetMap = new HashMap<>();
        List<Asset> assetList;

        if (processor.hasAssets()) {
            AssetListQuery assetListQuery = processor.toAssetListQuery();
            SearchBranch query = AssetQueryMapper.assetListQueryToAssetQuery(assetListQuery);

            List<AssetDTO> assetDTOList = assetRestClient.getAssetList(query);
            assetList = AssetQueryMapper.dtoListToAssetList(assetDTOList);
            assetMap = AssetQueryMapper.assetListToAssetMap(assetList);
        }

//        if (processor.hasAssetGroups()) {
//            Set<AssetGroup> assetGroups = processor.getAssetGroupList();
//            List<UUID> idList = AssetQueryMapper.getGroupListIds(assetGroups);
//
//            List<AssetDTO> assetDTOList = assetRestClient.getAssetsByGroupIds(idList);
//            assetList = AssetQueryMapper.dtoListToAssetList(assetDTOList);
//            assetMap = AssetQueryMapper.assetListToAssetMap(assetList);
//        }
        return assetMap;
    }

    public List<Asset> getAssets(AssetListQuery assetListQuery) {
        SearchBranch query = AssetQueryMapper.assetListQueryToAssetQuery(assetListQuery);
        List<AssetDTO> list = assetRestClient.getAssetList(query);
        return AssetQueryMapper.dtoListToAssetList(list);
    }
}
