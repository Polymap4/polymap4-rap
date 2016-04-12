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
package org.polymap.rap.openlayers;

import java.io.InputStream;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.rap.rwt.RWT;

import org.polymap.core.runtime.Closer;

import org.osgi.framework.BundleContext;
import org.polymap.rap.openlayers.base.OlMap;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author <a href="http://stundzig.it">Steffen Stundzig</a>
 */
public class OlPlugin
        extends Plugin {

    // The plug-in ID
    public static final String PLUGIN_ID       = "org.polymap.rap.openlayers";

    public static final String RESOURCE_PREFIX = "ol/";

    private static OlPlugin    plugin;


    public static OlPlugin instance() {
        return plugin;
    }


    public void start( final BundleContext context ) throws Exception {
        super.start( context );

        plugin = this;
    }


    public void stop( BundleContext context ) throws Exception {

        plugin = null;
        super.stop( context );
    }


    public static void registerResource( String resourceName, String fileName ) {
        String resource = RESOURCE_PREFIX + fileName;
        if (!RWT.getResourceManager().isRegistered( resource )) {
            ClassLoader classLoader = OlMap.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream( resourceName );
            if (inputStream == null) {
                throw new IllegalStateException( resourceName + " could not be found" );
            }
            try {
                RWT.getResourceManager().register( resource, inputStream );
            }
            finally {
                Closer.create().close( inputStream ).rethrowOrWrap( RuntimeException.class );
            }
        }
    }


    public static String resourceLocation( String resource ) {
        return RWT.getResourceManager().getLocation( RESOURCE_PREFIX + resource );
    }

}
