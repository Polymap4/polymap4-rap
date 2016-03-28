/* 
 * polymap.org
 * Copyright (C) 2015, Falko Bräutigam. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.base;

import java.util.ArrayList;
import java.util.Collection;

/**
 * {@link OlPropertyConcern} has code to handle {@link Collection} properties.
 * However, maybe it is better to directly handle this so that different target
 * encodings are allowed for Collection!?
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class JsonableList<T extends Jsonable>
        extends ArrayList
        implements Jsonable {

    @Override
    public Object toJson() {
        throw new RuntimeException( "not yet implementd" );
        // JSONArray result = new JSONArray();
        // forEach( elm -> OlPropertyConcern.propertyAsJson( elm ) );
    }

}
