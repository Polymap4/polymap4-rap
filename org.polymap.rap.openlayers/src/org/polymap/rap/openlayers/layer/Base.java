/*
 * polymap.org Copyright 2009-2015, Polymap GmbH. All rights reserved.
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
package org.polymap.rap.openlayers.layer;

import org.polymap.core.runtime.config.Check;
import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.NumberRangeValidator;
import org.polymap.rap.openlayers.base.OlObject;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.types.Extent;

/**
 * Abstract base class; normally only used for creating subclasses and not
 * instantiated in apps.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.layer.Base.html">
 *      OpenLayers</a>
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class Base
        extends OlObject {

    @Check(value = NumberRangeValidator.class, args = { "0", "1" })
    @Concern(OlPropertyConcern.class)
    public Config2<Base,Float>   opacity;

    /**
     * The visibility flag for the layer and hide/show & redraw accordingly. Fire
     * event unless otherwise specified.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<Base,Boolean> visible;

    /**
     * The z-index for layer rendering. At rendering time, the layers will be
     * ordered, first by Z-index and then by position. The default Z-index is 0.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<Base,Float>   zIndex;

    /**
     * The minimum resolution (inclusive) at which this layer will be visible.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<Base,Float>   minResolution;

    /**
     * The maximun resolution (exclusive) below which this layer will be visible.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<Base,Float>   maxResolution;

    /**
     * The bounding extent for layer rendering. The layer will not be rendered
     * outside of this extent.
     */
    @Concern(OlPropertyConcern.class)
    public Config2<Base,Extent>  extent;


    protected Base( String jsClassname ) {
        super( jsClassname );
    }
}
