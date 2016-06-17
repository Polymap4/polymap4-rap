/*
 * polymap.org and individual contributors as indicated by the @authors tag.
 * Copyright (C) 2009-2015 All rights reserved.
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

import java.util.List;

import org.eclipse.rap.rwt.widgets.WidgetUtil;
import org.polymap.core.runtime.config.Check;
import org.polymap.core.runtime.config.Concern;
import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.DefaultString;
import org.polymap.core.runtime.config.Immutable;
import org.polymap.core.runtime.config.Mandatory;
import org.polymap.core.runtime.config.NumberRangeValidator;
import org.polymap.rap.openlayers.base.OlMap;
import org.polymap.rap.openlayers.base.OlPropertyConcern;
import org.polymap.rap.openlayers.types.Attribution;

/**
 * Source for WMS servers providing single, untiled images.
 *
 * @see <a href="http://openlayers.org/en/master/apidoc/ol.source.ImageWMS.html">
 *      OpenLayers Doc</a>
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class ImageWMSSource
        extends ImageSource {

    /**
     * WMS service URL.
     */
    @Mandatory
    @Immutable
    @Concern( OlPropertyConcern.class )
    public Config2<ImageWMSSource,String>           url;

    /**
     * 
     * The crossOrigin attribute for loaded images. Note that you must provide a
     * crossOrigin value if you are using the WebGL renderer or if you want to access
     * pixel data with the Canvas renderer. See
     * https://developer.mozilla.org/en-US/docs/Web/HTML/CORS_enabled_image for more
     * detail.
     */
    @Concern( OlPropertyConcern.class )
    @DefaultString( "Anonymous" )
    public Config2<ImageWMSSource,String>           crossOrigin;

    /**
     * WMS request parameters. At least a LAYERS param is required. STYLES is '' by
     * default. VERSION is 1.3.0 by default. WIDTH, HEIGHT, BBOX and CRS (SRS for WMS
     * version < 1.3.0) will be set dynamically.
     */
    @Mandatory
    @Immutable
    // @Concern( OlPropertyConcern.class )
    public Config2<ImageWMSSource,WMSRequestParams> params;

    /**
     * Resolutions. If specified, requests will be made for these resolutions only.
     */
    @Concern( OlPropertyConcern.class )
    public Config2<ImageWMSSource,List<Float>>      resolutions;

    /**
     * Ratio. 1 means image requests are the size of the map viewport, 2 means twice
     * the size of the map viewport, and so on. Default is 1.5.
     */
    @Check( value = NumberRangeValidator.class, args = { "1", "2" } )
    @Concern( OlPropertyConcern.class )
    public Config2<ImageWMSSource,Float>            ratio;

    /**
     * Optional attributions for the source. If provided, these will be used instead
     * of any attribution data advertised by the server. If not provided, any
     * attributions advertised by the server will be used.
     */
    @Concern( OlPropertyConcern.class )
    public Config2<VectorSource,List<Attribution>>  attributions;


    public ImageWMSSource() {
        super( "ol.source.ImageWMS" );
    }


    @Override
    public void onSetMap( OlMap map ) {
        call( "var progress=this.objs['" + WidgetUtil.getId( map.getControl() )
                + "p']; this.obj.on('imageloadstart', function() { progress.addLoading();});" );
        call( "var progress=this.objs['" + WidgetUtil.getId( map.getControl() )
                + "p'];this.obj.on('imageloadend', function() { progress.addLoaded();});" );
        call( "var progress=this.objs['" + WidgetUtil.getId( map.getControl() )
                + "p'];this.obj.on('imageloaderror', function() { progress.addLoaded();});" );
        super.onSetMap( map );
    }
}
