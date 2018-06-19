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
package org.polymap.rap.openlayers.base;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;

import org.polymap.rap.openlayers.geom.Geometry;
import org.polymap.rap.openlayers.style.Base;
import org.polymap.rap.openlayers.types.Coordinate;

/**
 * A vector object for geographic features with a geometry and other attribute
 * properties, similar to the features in vector file formats like GeoJSON.
 * <p/>
 * Features can be styled individually with setStyle; otherwise they use the style of
 * their vector layer.
 * <p/>
 * Note that attribute properties are set as ol.Object properties on the feature
 * object, so they are observable, and have get/set accessors.
 * <p/>
 * Typically, a feature has a single geometry property. You can set the geometry
 * using the setGeometry method and get it with getGeometry. It is possible to store
 * more than one geometry on a feature using attribute properties. By default, the
 * geometry used for rendering is identified by the property name geometry. If you
 * want to use another geometry property for rendering, use the setGeometryName
 * method to change the attribute property associated with the geometry for the
 * feature.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.Feature.html">OpenLayers
 *      Doc</a>
 * 
 * @author <a href="http://mapzone.io">Steffen Stundzig</a>
 */
public class OlFeature
        extends OlObject {

    private final static Log log = LogFactory.getLog( OlFeature.class );

    // @Mandatory
    // @Immutable
    @Concern(OlPropertyConcern.class)
    public Config2<OlFeature,Geometry>   geometry;

    @Concern(OlPropertyConcern.class)
    public Config2<OlFeature,Coordinate> labelPoint;

//    @Immutable
    @Concern(OlPropertyConcern.class)
    @OlSetter("setStyle")
    public Config2<OlFeature,Base>       style;

    @Immutable
    @Concern(OlPropertyConcern.class)
    // defaults to "geometry"
    public Config2<OlFeature,String>     geometryName;

    // @Mandatory
    // @Immutable
    @Concern(OlPropertyConcern.class)
    public Config2<OlFeature,String>     name;

    @Immutable
    @Mandatory
    @Concern(OlPropertyConcern.class)
    public Config2<OlFeature,String>     id;


    public OlFeature() {
        this( null, null );
    }


    public OlFeature( String id ) {
        this( id, null );
    }


    public OlFeature( String id, String geometryName ) {
        super( "ol.Feature" );
        this.id.set( StringUtils.isBlank( id ) ? UUID.randomUUID().toString() : id );
        this.geometryName.set( StringUtils.isBlank( geometryName ) ? "geometry" : geometryName );
    }
}
