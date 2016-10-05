/* 
 * polymap.org
 * Copyright (C) 2015, Falko Bräutigam. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.updownload.upload;

import java.util.concurrent.ConcurrentMap;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.service.ServiceHandler;

import org.polymap.core.runtime.ConcurrentReferenceHashMap;
import org.polymap.core.runtime.ConcurrentReferenceHashMap.ReferenceType;
import org.polymap.core.ui.ServiceUriBuilder;

/**
 * General HTTP upload service.
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class UploadService
        implements ServiceHandler {

    private static Log log = LogFactory.getLog( UploadService.class );

    private static final String         SERVICE_HANDLER_ID = "org.polymap.rap.updownload.UploadService";

    /**
     * The parameter used to identify the upload handler.
     * <p/>
     * Because of a bug in SWL Label this must not be "id" :( Otherwise the created
     * URLs could not be used in SWT Labels with {@link RWT#MARKUP_ENABLED}.
     */
    private static final String         ID_REQUEST_PARAM = "id";
    
    private static ConcurrentMap<String,IUploadHandler> handlers = 
            new ConcurrentReferenceHashMap( ReferenceType.STRONG, ReferenceType.WEAK );

    
    /**
     * Registers the given upload handler. An unique id of the newly registered
     * download is build out of the hashcode of the handler.
     * 
     * @param handler The handler to register. The handler is stored in a
     *        <b>WEAKLY</b> reference.
     * @return The URL to use for the given handler.
     * @throws IllegalArgumentException If a handler is already registered for the
     *         given handlerId.
     */
    public static String registerHandler( IUploadHandler handler ) {
        return registerHandler( String.valueOf( handler.hashCode() ), handler );
    }
    
    
    /**
     * Registers the given upload handler.
     * 
     * @param The id that identifies this handler. Internally used to build the URL.
     * @param handler The handler to register. The handler is stored in a
     *        <b>WEAKLY</b> reference.
     * @return The URL to use for the given handler.
     * @throws IllegalArgumentException If a handler is already registered for the
     *         given handlerId.
     */
    public static String registerHandler( String handlerId, IUploadHandler handler ) {
        IUploadHandler previous = handlers.put( handlerId, handler );
        if (previous != null) {
            if (previous == handler) {
                log.warn( "Upload handler already registered for id: " + handlerId );
            }
            else {
                throw new IllegalArgumentException( "Upload handler already registered for id: " + handlerId );
            }
        }
        
        ServiceUriBuilder uri = ServiceUriBuilder.forServiceId( SERVICE_HANDLER_ID );
        uri.appendParam( ID_REQUEST_PARAM, handlerId );
        return uri.encodedRelativeUri();

    }
    
    
    public static boolean removeHandler( IUploadHandler handler ) {
        String handlerId = String.valueOf( handler.hashCode() );
        return handlers.remove( handlerId ) != null;
    }
    
    
    public static boolean removeUploadHandler( String handlerId ) {
        return handlers.remove( handlerId ) != null;
    }
    
    
    // instance *******************************************
    
    private ServletFileUpload   fileUpload = new ServletFileUpload();

    
    @Override
    public void service( HttpServletRequest request, HttpServletResponse response ) 
            throws IOException, ServletException {
        try {
            FileItemIterator it = fileUpload.getItemIterator( request );
            while (it.hasNext()) {
                FileItemStream item = it.next();
                
                try (
                    InputStream in = item.openStream()
                ){
                    if (item.isFormField()) {
                        log.info( "Form field " + item.getFieldName() + " with value " + Streams.asString( in ) + " detected.");
                    } 
                    else {
                        log.info( "File field " + item.getFieldName() + " with file name " + item.getName() + " detected.");
                        
                        String handlerId = request.getParameter( ID_REQUEST_PARAM );
                        assert handlerId != null;
                        IUploadHandler handler = handlers.get( handlerId );
                        ClientFile clientFile = new ClientFile() {
                            @Override
                            public String getName() {
                                return item.getName();
                            }
                            @Override
                            public String getType() {
                                return item.getContentType();
                            }
                            @Override
                            public long getSize() {
                                // for the upload field we always get just one item (which has the length of the request!?)
                                return request.getContentLength();
                            }
                        };
                        handler.uploadStarted( clientFile, in );
                    }
                }
            }
        }
        catch (Exception e) {
            log.warn( "", e );
            throw new RuntimeException( e );
        }
    }
    
}
