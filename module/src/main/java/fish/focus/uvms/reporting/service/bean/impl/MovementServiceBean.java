/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.reporting.service.bean.impl;

import fish.focus.schema.movement.module.v1.GetMovementMapByQueryResponse;
import fish.focus.schema.movement.search.v1.MovementMapResponseType;
import fish.focus.schema.movement.search.v1.MovementQuery;
import fish.focus.uvms.commons.service.interceptor.SimpleTracingInterceptor;
import fish.focus.uvms.reporting.message.mapper.ExtMovementMessageMapper;
import fish.focus.uvms.reporting.service.MovementClient;
import fish.focus.uvms.reporting.service.util.FilterProcessor;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.Map;

@Stateless
@Slf4j
public class MovementServiceBean {

    @Inject
    private MovementClient movementClient;

    @Interceptors(SimpleTracingInterceptor.class)
    public Map<String, MovementMapResponseType> getMovementMap(FilterProcessor processor) {
        return ExtMovementMessageMapper.getMovementMap(getMovementMapResponseTypes(processor));
    }

    public List<MovementMapResponseType> getMovement(FilterProcessor processor) {
        log.trace("getMovement({})", processor.toString());
        return getMovementMapResponseTypes(processor);
    }

    private List<MovementMapResponseType> getMovementMapResponseTypes(FilterProcessor processor) {
        MovementQuery movementQuery = processor.toMovementQuery();
        GetMovementMapByQueryResponse movementMapResponseTypes = movementClient.getMovementMapResponseTypes(movementQuery);
        return movementMapResponseTypes.getMovementMap();
    }
}
