/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.
This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package fish.focus.uvms.reporting.service.dao;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {

    private Map<String, Object> parameters = null;

    private QueryParameter(String name, Object value){
        this.parameters = new HashMap<>();
        this.parameters.put(name, value);
    }
    public static QueryParameter with(String name, Object value){
        return new QueryParameter(name, value);
    }

    public QueryParameter and(String name, Object value){
        this.parameters.put(name, value);
        return this;
    }
    public Map parameters() { // TODO check generics
        return this.parameters;
    }

}