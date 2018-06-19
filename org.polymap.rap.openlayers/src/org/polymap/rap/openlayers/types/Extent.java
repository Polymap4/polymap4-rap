/*
 * polymap.org 
 * Copyright 2009-2015, Polymap GmbH. All rights reserved.
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
 * An array of numbers representing an extent: [minx, miny, maxx, maxy].
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.html#Extent">OpenLayer Doc</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class Extent
        implements Jsonable {

    public double   minx;

    public double   miny;

    public double   maxx;

    public double   maxy;


    public Extent( double minx, double miny, double maxx, double maxy ) {
        this.minx = minx;
        this.miny = miny;
        this.maxx = maxx;
        this.maxy = maxy;
    }


    @Override
    public Object toJson() {
        return new JSONArray().put( minx ).put( miny ).put( maxx ).put( maxy );
    }

}
