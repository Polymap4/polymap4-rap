/*
 * polymap.org and individual contributors as indicated by the @authors tag.
 * Copyright (C) 2009-2015 All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.types;

import org.json.JSONArray;

import org.polymap.rap.openlayers.base.Jsonable;

/**
 * An array of numbers representing an xy coordinate. Example: [16, 48].
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.html#Coordinate
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class Coordinate
        implements Jsonable {

    public double   x;

    public double   y;


    public Coordinate( double x, double y ) {
        this.x = x;
        this.y = y;
    }


    public double x() {
        return x;
    }


    public double y() {
        return y;
    }


    @Override
    public Object toJson() {
        return new JSONArray().put( x ).put( y );
    }

}
