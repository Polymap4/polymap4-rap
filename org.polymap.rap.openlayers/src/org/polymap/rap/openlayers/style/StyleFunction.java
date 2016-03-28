/*
 * polymap.org Copyright (C) 2009-2015 Polymap GmbH and individual contributors as indicated
 * by the @authors tag. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package org.polymap.rap.openlayers.style;

import org.polymap.rap.openlayers.base.Jsonable;

/**
 * A function that takes an ol.Feature and a {number} representing the view's
 * resolution. The function should return an array of ol.style.Style. This way e.g. a
 * vector layer can be styled.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.style.html#StyleFunction">
 *      OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class StyleFunction
        implements Style, Jsonable {

    /**
     * The function must return a style. For an example see
     * <a href="http://openlayers.org/en/master/examples/cluster.html">Cluster
     * Example</a>
     */
    private String function;


    public StyleFunction( String function ) {
        this.function = function;
    }


    @Override
    public Object toJson() {
        return "function(feature) { " + function + "}";
    }

}
