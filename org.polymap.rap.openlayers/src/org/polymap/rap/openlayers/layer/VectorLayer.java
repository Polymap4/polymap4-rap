/*
 * polymap.org Copyright (C) 2009-2015, Polymap GmbH. All rights reserved.
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

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.source.VectorSource;
import org.polymap.rap.openlayers.style.Base;

/**
 * Vector data that is rendered client-side. Note that any property set in the
 * options is set as a ol.Object property on the layer object; for example, setting
 * title: 'My Title' in the options means that title is observable, and has get/set
 * accessors.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.layer.Vector.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class VectorLayer
        extends Layer<VectorSource> {

    /**
     * Layer style. See
     * <a href="http://openlayers.org/en/master/apidoc/ol.style.html">ol.style</a>
     * for default style which will be used if this is not defined.
     */
    @Immutable
    @Concern(OlPropertyConcern.class)
    public Config2<VectorLayer,Base> style;


    //
    // /**
    // * Constructs a new instance.
    // *
    // * @param initializers Initialize at least all {@link Mandatory} properties.
    // */
    // public VectorLayer( Consumer<VectorLayer>... initializers ) {
    // super( "ol.layer.Vector" );
    //// Arrays.asList( initializers ).forEach( initializer -> initializer.accept(
    // this ) );
    // }
    public VectorLayer() {
        this( "ol.layer.Vector" );
    }


    protected VectorLayer( String jsClassname ) {
        super( jsClassname );
    }

}
