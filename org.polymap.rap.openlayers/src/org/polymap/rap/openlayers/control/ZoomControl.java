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
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this software; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

package org.polymap.rap.openlayers.control;

import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Config2;
import org.polymap.rap.openlayers.base.OlProperty;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.style.StrokeStyle;
import org.polymap.rap.openlayers.style.Style;

/**
 * A control with 2 buttons, one for zoom in and one for zoom out. This control is
 * one of the default controls of a map. To style this control use css selectors
 * .ol-zoom-in and .ol-zoom-out.
 * 
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.control.Zoom.html">OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class ZoomControl
        extends Control {

    @Immutable
    @Concern(OlPropertyConcern.class)
    @OlProperty("className")
    /**
     * className string | undefined CSS class name. Default is ol-zoom.
     */
    public Config2<ZoomControl,String> cssClass;
    /*
     * TODO
     * 
     * duration number | undefined Animation duration in milliseconds. Default is
     * 250. 
     * zoomInLabel string | Node | undefined Text label to use for the zoom-in
     * button. Default is +. Instead of text, also a Node (e.g. a span element) can
     * be used. zoomOutLabel string | Node | undefined Text label to use for the
     * zoom-out button. Default is -. Instead of text, also a Node (e.g. a span
     * element) can be used. zoomInTipLabel string | undefined Text label to use for
     * the button tip. Default is Zoom in zoomOutTipLabel string | undefined Text
     * label to use for the button tip. Default is Zoom out delta number | undefined
     * The zoom delta applied on each click. target Element | undefined Target.
     */

    public ZoomControl() {
        super( "ol.control.Zoom" );
    }
}
