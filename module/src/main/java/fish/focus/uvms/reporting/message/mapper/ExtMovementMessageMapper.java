/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.reporting.message.mapper;

import fish.focus.schema.movement.search.v1.ListCriteria;
import fish.focus.schema.movement.search.v1.MovementMapResponseType;
import fish.focus.schema.movement.search.v1.SearchKey;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExtMovementMessageMapper {

    public static Map<String, MovementMapResponseType> getMovementMap(List<MovementMapResponseType> movementMapResponseTypes) {
        return movementMapResponseTypes.stream().collect(Collectors.toMap(MovementMapResponseType::getKey, Function.identity()));
    }

    public static Collection<? extends ListCriteria> movementListCriteria(Set<String> connectIds) {
        List<ListCriteria> criteria = new ArrayList<>();
        for (String id : connectIds) {
            ListCriteria movementType = new ListCriteria();
            movementType.setKey(SearchKey.CONNECT_ID);
            movementType.setValue(id);
            criteria.add(movementType);
        }
        return criteria;
    }
}
