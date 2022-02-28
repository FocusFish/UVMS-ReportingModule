/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */


package fish.focus.uvms.reporting.service.util;

import fish.focus.schema.movement.search.v1.ListCriteria;
import fish.focus.schema.movement.search.v1.MovementQuery;
import fish.focus.schema.movement.search.v1.RangeCriteria;
import fish.focus.schema.movement.search.v1.SearchKey;
import fish.focus.uvms.activity.model.schemas.ListValueTypeFilter;
import fish.focus.uvms.activity.model.schemas.SingleValueTypeFilter;
import fish.focus.uvms.reporting.service.exception.ProcessorException;
import fish.focus.uvms.spatial.model.schemas.AreaIdentifierType;
import fish.focus.wsdl.asset.types.AssetListCriteria;
import fish.focus.wsdl.asset.types.AssetListCriteriaPair;
import fish.focus.wsdl.asset.types.AssetListPagination;
import fish.focus.wsdl.asset.types.AssetListQuery;
import fish.focus.uvms.reporting.service.entities.AreaFilter;
import fish.focus.uvms.reporting.service.entities.Filter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class FilterProcessor {

    private final Set<ListCriteria> movementListCriteria = new HashSet<>();
    private final Set<RangeCriteria> rangeCriteria = new HashSet<>();
    private final Set<AssetListCriteriaPair> assetListCriteriaPairs = new HashSet<>();
//    private final Set<AssetGroup> assetGroupList = new HashSet<>();
    private final Set<AreaIdentifierType> areaIdentifierList = new HashSet<>();
    private final List<SingleValueTypeFilter> singleValueTypeFilters = new ArrayList<>();
    private final List<ListValueTypeFilter> listValueTypeFilters = new ArrayList<>();
    private Instant now;

    public FilterProcessor(Set<Filter> filters, Instant now) throws ProcessorException {
        this.now = now;
        if (isEmpty(filters)) {
            throw new ProcessorException("");
        }
        for (Filter filter : filters) {
            if(filter != null){
                addCriteria(filter);
                addAreaIdentifier(filter);
            }
        }
    }

    public void addAreaCriteria(String areaWkt) {
        if (areaWkt != null) {
            ListCriteria areaCriteria = new ListCriteria();
            areaCriteria.setKey(SearchKey.AREA);
            areaCriteria.setValue(areaWkt);
            movementListCriteria.add(areaCriteria);
        }
    }

    private void addCriteria(Filter filter) {
        assetListCriteriaPairs.addAll(filter.assetCriteria());
        rangeCriteria.addAll(filter.movementRangeCriteria(now));
        movementListCriteria.addAll(filter.movementListCriteria());
        singleValueTypeFilters.addAll(filter.getSingleValueFilters(now));
        listValueTypeFilters.addAll(filter.getListValueFilters(now));
    }

    private void addAreaIdentifier(Filter filter) {
        if (filter instanceof AreaFilter) {
            areaIdentifierList.add(filter.getAreaIdentifierType());
        }
    }

    public MovementQuery toMovementQuery() {
        MovementQuery movementQuery = new MovementQuery();
        movementQuery.getMovementSearchCriteria().addAll(movementListCriteria);
        movementQuery.getMovementRangeSearchCriteria().addAll(rangeCriteria);
        movementQuery.setExcludeFirstAndLastSegment(true);
        return movementQuery;
    }

    public AssetListQuery toAssetListQuery() {
        AssetListQuery query = new AssetListQuery();
        if (isNotEmpty(assetListCriteriaPairs)) {
            query.setAssetSearchCriteria(createListCriteria());
            AssetListPagination pagination = new AssetListPagination();
            pagination.setPage(1);
            pagination.setListSize(1000);
            query.setPagination(pagination);
        }
        return query;
    }

    private AssetListCriteria createListCriteria() {
        AssetListCriteria assetListCriteria = new AssetListCriteria();
        assetListCriteria.setIsDynamic(false);
        assetListCriteria.getCriterias().addAll(assetListCriteriaPairs);
        return assetListCriteria;
    }
//
//    public Set<AssetGroup> getAssetGroupList() {
//        return assetGroupList;
//    }

    public boolean hasAssetsOrAssetGroups() {
        return hasAssets() || hasAssetGroups();
    }

    public boolean hasAssets() {
        return !assetListCriteriaPairs.isEmpty();
    }

    public boolean hasAssetGroups() {
        return false;
    }

    public Set<ListCriteria> getMovementListCriteria() {
        return movementListCriteria;
    }

    public Set<AssetListCriteriaPair> getAssetListCriteriaPairs() {
        return assetListCriteriaPairs;
    }

    public Set<RangeCriteria> getRangeCriteria() {
        return rangeCriteria;
    }

    public Set<AreaIdentifierType> getAreaIdentifierList() {
        return areaIdentifierList;
    }

    public List<SingleValueTypeFilter> getSingleValueTypeFilters() {
        return singleValueTypeFilters;
    }

    public List<ListValueTypeFilter> getListValueTypeFilters() {
        return listValueTypeFilters;
    }

    public Instant getNow() {
        return now;
    }

    public void setNow(Instant now) {
        this.now = now;
    }

}