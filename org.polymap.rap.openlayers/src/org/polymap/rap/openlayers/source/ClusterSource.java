/*
 * <<<<<<< HEAD polymap.org Copyright (C) 2009-2014, Polymap GmbH. All rights
 * reserved. ======= polymap.org Copyright (C) @year@ individual contributors as
 * indicated by the @authors tag. All rights reserved. >>>>>>>
 * 6a8ac5780fb40f90177477dc3c07d601caabbe90
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
package org.polymap.rap.openlayers.source;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;
import org.polymap.rap.openlayers.base.OlPropertyConcern;

/**
 * Layer source to cluster vector data. Works out of the box with point geometries.
 * For other geometry types, or if not all geometries should be considered for
 * clustering, a custom geometryFunction can be defined.
 * 
 * A clusterSource could be added into a vector layer. Then the vector layer needs a
 * style function similar to:
 * 
 * <pre>
 * var size = feature.get('features').length;
 * if (size == 1 && feature.get('features')[0].getStyle() != null) {
 *   return [feature.get('features')[0].getStyle()];
 * }
 * return [new ol.style.Style({
 *   image: new ol.style.Circle({
 *       radius: 10,
 *     stroke: new ol.style.Stroke({
 *       color: '#fff'
 *     }),
 *     fill: new ol.style.Fill({
 *       color: '#3399CC'
 *     })
 *   }),
 *   text: new ol.style.Text({
 *     text: size.toString(),
 *     fill: new ol.style.Fill({
 *     color: '#fff'
 *     })
 *   })
 * })];
 * </pre>
 * 
 * This function styles aggregated features and uses the individual feature of single
 * nodes, if they exists.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.source.Cluster.html">
 *      OpenLayers Doc</a>
 * 
 * @author <a href="mailto:joerg@mapzone.io">Joerg Reichert</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class ClusterSource
        extends VectorSource {

    /**
     * Source. Required.
     */
    @Immutable
    @Mandatory
    @Concern(OlPropertyConcern.class)
    public Config2<ClusterSource,VectorSource> source;

    /**
     * Minimum distance in pixels between clusters. Default is 20.
     */
    @Immutable
    @Concern(OlPropertyConcern.class)
    public Config2<ClusterSource,Double>       distance;


    public ClusterSource() {
        super( "ol.source.Cluster" );
    }
}
