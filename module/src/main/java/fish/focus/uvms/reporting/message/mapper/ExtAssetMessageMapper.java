/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.message.mapper;

import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import fish.focus.uvms.asset.model.exception.AssetException;
import fish.focus.uvms.asset.model.mapper.AssetModuleRequestMapper;
import fish.focus.uvms.asset.model.mapper.AssetModuleResponseMapper;
import fish.focus.wsdl.asset.types.Asset;
import fish.focus.wsdl.asset.types.AssetListCriteriaPair;
import fish.focus.wsdl.asset.types.AssetListQuery;
import fish.focus.wsdl.asset.types.ConfigSearchField;

public class ExtAssetMessageMapper {

    private ExtAssetMessageMapper() {
    }

    public static String mapToGetAssetListByQueryRequest(final AssetListQuery query) throws AssetException {
        if (query == null) {
            throw new IllegalArgumentException("AssetListQuery can not be null.");
        }
        return AssetModuleRequestMapper.createAssetListModuleRequest(query);
    }

    public static List<Asset> mapToAssetListFromResponse(final TextMessage textMessage, final String correlationId) throws AssetException {
        if (textMessage == null) {
            throw new IllegalArgumentException("TextMessage can not be null.");
        }
        if (correlationId == null) {
            throw new IllegalArgumentException("CorrelationId can not be null.");
        }
        return AssetModuleResponseMapper.mapToAssetListFromResponse(textMessage, correlationId);
    }

    public static Map<String, Asset> getAssetMap(Set<Asset> assetList) {
        Map<String, Asset> map = new HashMap<>();

        for (Asset asset : assetList) {
            map.put(asset.getEventHistory().getEventId(), asset);
        }
        return map;
    }

    public static List<AssetListCriteriaPair> assetCriteria(Set<String> guids) {
        List<AssetListCriteriaPair> pairList = new ArrayList<>();
        for (String guid : guids) {
            AssetListCriteriaPair criteriaPair = new AssetListCriteriaPair();
            criteriaPair.setKey(ConfigSearchField.GUID);
            criteriaPair.setValue(guid);
            pairList.add(criteriaPair);
        }
        return pairList;
    }

    public static String createAssetListModuleRequest(AssetListQuery query) throws AssetException {
        if (query == null) {
            throw new IllegalArgumentException("AssetListQuery can not be null.");
        }
        return AssetModuleRequestMapper.createAssetListModuleRequest(query);
    }

//    public static String createAssetListModuleRequest(Set<AssetGroup> assetGroup) throws AssetException {
//        if (assetGroup == null) {
//            throw new IllegalArgumentException("List<AssetGroup> can not be null.");
//        }
//        List<AssetGroup> assetGroupList = new ArrayList<>();
//        assetGroupList.addAll(assetGroup);
//        return AssetModuleRequestMapper.createAssetListModuleRequest(assetGroupList);
//    }
}