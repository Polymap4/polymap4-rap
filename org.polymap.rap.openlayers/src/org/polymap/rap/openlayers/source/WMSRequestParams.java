/*
 * polymap.org and individual contributors as indicated by the @authors tag.
 * Copyright (C) 2009-2015 
 * All rights reserved.
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

import org.polymap.core.runtime.config.Config2;
import org.polymap.core.runtime.config.ConfigurationFactory;
import org.polymap.core.runtime.config.DefaultString;
import org.polymap.core.runtime.config.Mandatory;

import org.polymap.rap.openlayers.base.Jsonable;
import org.polymap.rap.openlayers.base.OlProperty;
import org.polymap.rap.openlayers.base.OlPropertyConcern;

/**
 * WMS request parameters. At least a LAYERS param is required. STYLES is '' by
 * default. VERSION is 1.3.0 by default. WIDTH, HEIGHT, BBOX and CRS (SRS for WMS
 * version < 1.3.0) will be set dynamically.
 * 
 * Documentation is based on
 * <a href="http://docs.geoserver.org/stable/en/user/services/wms/reference.html">
 * geoserver.org</a>
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 *
 */
public class WMSRequestParams
        implements Jsonable {

    /**
     * Constructs a new instance.
     */
    public WMSRequestParams() {
        ConfigurationFactory.inject( this );
    }

    /**
     * Layers to display on map. Value is a comma-separated list of layer names.
     */
    @Mandatory
    @OlProperty("LAYERS")
    public Config2<WMSRequestParams,String>  layers;

    /**
     * Styles in which layers are to be rendered. Value is a comma-separated list of
     * style names, or empty if default styling is required. Style names may be empty
     * in the list, to use default layer styling.
     */
    @DefaultString("")
    @OlProperty("STYLES")
    public Config2<WMSRequestParams,String>  styles;

    /**
     * Service version. Value is one of 1.0.0, 1.1.0, 1.1.1, 1.3.
     */
    @DefaultString("1.3.0")
    @OlProperty("VERSION")
    public Config2<WMSRequestParams,String>  version;

    /**
     * Format for the map output. See <a href=
     * "http://docs.geoserver.org/stable/en/user/services/wms/outputformats.html#wms-output-formats">
     * WMS output formats</a> for supported values.
     */
    @OlProperty("FORMAT")
    public Config2<WMSRequestParams,String>  format;

    /**
     * Whether the map background should be transparent. Values are true or false.
     * Default is false
     */
    @OlProperty("TRANSPARENT")
    public Config2<WMSRequestParams,Boolean> transparent;

    /**
     * Background color for the map image. Value is in the form RRGGBB. Default is
     * FFFFFF (white).
     */
    @OlProperty("BGCOLOR")
    public Config2<WMSRequestParams,String>  bgcolor;

    /**
     * Format in which to report exceptions. Default value is
     * application/vnd.ogc.se_xml.
     */
    @OlProperty("EXCEPTIONS")    
    public Config2<WMSRequestParams,String>  exceptions;

    /**
     * Time value or range for map data. See <a href=
     * "http://docs.geoserver.org/stable/en/user/services/wms/time.html#wms-time">
     * Time Support</a> in GeoServer WMS for more information.
     */
    @OlProperty("TIME")
    public Config2<WMSRequestParams,String>  time;

    /**
     * A URL referencing a StyledLayerDescriptor XML file which controls or enhances
     * map layers and styling-
     */
    @OlProperty("SLD")
    public Config2<WMSRequestParams,String>  sld;

    /**
     * A URL-encoded StyledLayerDescriptor XML document which controls or enhances
     * map layers and styling
     */
    @OlProperty("SLD_BODY")
    public Config2<WMSRequestParams,String>  sld_body;


    @Override
    public Object toJson() {
        return OlPropertyConcern.propertiesAsJson( this );
    }
}