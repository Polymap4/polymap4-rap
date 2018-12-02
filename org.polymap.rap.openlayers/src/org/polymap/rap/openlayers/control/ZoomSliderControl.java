/*
 * polymap.org 
 * Copyright 2009, Polymap GmbH, and individual contributors as indicated
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
package org.polymap.rap.openlayers.control;

/**
 * A slider type of control for zooming.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.control.ZoomSlider.html">OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class ZoomSliderControl
        extends Control {

    /*
     * TODO
     * 
     * className string | undefined CSS class name. 
     * duration number | undefined
     * experimental Animation duration in milliseconds. Default is 200. maxResolution
     * number | undefined Maximum resolution. minResolution number | undefined
     * Minimum resolution. render function | undefined experimental Function called
     * when the control should be re-rendered. This is called in a
     * requestAnimationFrame callback.
     */
    
    public ZoomSliderControl() {
        super( "ol.control.ZoomSlider" );
    }
}
