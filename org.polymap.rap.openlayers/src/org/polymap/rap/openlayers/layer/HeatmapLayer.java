/*
 * polymap.org Copyright 2009, Polymap GmbH, and individual contributors as indicated
 * by the @authors tag.
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

/**
 * Layer for rendering vector data as a heatmap. Note that any property set in the
 * options is set as a ol.Object property on the layer object; for example, setting
 * title: 'My Title' in the options means that title is observable, and has get/set
 * accessors.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.layer.Heatmap.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class HeatmapLayer
        extends VectorLayer {

    public HeatmapLayer() {
        super( "ol.layer.Heatmap" );
    }
}
