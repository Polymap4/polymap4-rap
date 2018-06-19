/*
 * polymap.org 
 * Copyright (C) 2009-2018 Polymap GmbH. All rights reserved.
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
package org.polymap.rap.openlayers.geom;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Mandatory;

import org.polymap.rap.openlayers.base.OlPropertyAndSetter;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.types.Coordinate;

/**
 * Circle geometry.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.geom.Circle.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class CircleGeometry
        extends SimpleGeometry {

    @Mandatory
    @Concern(OlPropertyConcern.class)
    @OlPropertyAndSetter(property="center", setter="setCenter")
    public Config2<CircleGeometry,Coordinate>   center;

    @Mandatory
    @Concern(OlPropertyConcern.class)
    @OlPropertyAndSetter(property="radius", setter="setRadius")
    public Config2<CircleGeometry,Double>       radius;


    public CircleGeometry( Coordinate center, double radius ) {
        super( "ol.geom.Circle" );
        this.center.set( center );
        this.radius.set( radius );
    }

    @Override
    protected void doCreate() {
        // special ctor of ol.geom.Circle
        super.createWithOptions( center.get(), radius.get() );
    }

}
