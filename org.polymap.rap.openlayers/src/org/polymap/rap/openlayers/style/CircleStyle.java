/*
 * polymap.org 
 * Copyright (C) 2009-2018 Polymap GmbH and individual contributors as
 * indicated by the @authors tag. All rights reserved.
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

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;

import org.polymap.rap.openlayers.base.OlPropertyConcern;

/**
 * Set circle style for vector features.
 * 
 * @see <a href="http://openlayers.org/en/v3.20.1/apidoc/ol.style.Circle.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class CircleStyle
        extends ImageStyle {

    @Concern(OlPropertyConcern.class)
    public Config2<CircleStyle,StrokeStyle> stroke;

    @Immutable
    @Mandatory
    @Concern(OlPropertyConcern.class)
    public Config2<CircleStyle,Float>       radius;

    @Concern(OlPropertyConcern.class)
    public Config2<CircleStyle,FillStyle>   fill;

    @Immutable
    @Concern(OlPropertyConcern.class)
    public Config2<CircleStyle,Boolean>     snapToPixel;


    public CircleStyle( Float radius ) {
        super( "ol.style.Circle" );
        this.radius.set( radius );
    }
}
