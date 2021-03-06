/* 
 * polymap.org
 * Copyright (C) 2012-2015, Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
package org.polymap.rap.updownload.dnd;

import java.util.List;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.rap.rwt.service.ServiceHandler;

import org.polymap.core.ui.UIUtils;

/**
 * This service handler gets hit from the client side after the uploads are
 * completely transmitted to {@link DndUploadServlet}. From within a service handler
 * the session (and its display) is available and so it is possible to call
 * {@link DesktopDndSupport#instance()}.
 * 
 * @author <a href="http://www.polymap.de">Falko Br�utigam</a>
 */
public class DndServiceHandler
        implements ServiceHandler {

    private static Log log = LogFactory.getLog( DndServiceHandler.class );

    /** Defined in plugin.xml and used in dnd.js Javascript code. */
    public static final String          SERVICE_HANDLER_ID = "org.polymap.rap.updownload.DndServiceHandler";

    public static final String          PARAM_TEXT = "eventText";
    
    
    public DndServiceHandler() {
    }


    /**
     * Sent after uploads are completed.
     */
    @Override
    public void service( HttpServletRequest request, HttpServletResponse response ) 
            throws IOException, ServletException {
        log.debug( "Request: " + request );

        final List<DesktopDropEvent> events = DndUploadServlet.instance().uploads( request.getSession() );

        final String eventText = request.getParameter( PARAM_TEXT );
        if (eventText != null) {
            events.add( new TextDropEvent() {
                public String getText() {
                    return eventText;
                }
            });
        }
        UIUtils.sessionDisplay().asyncExec( new Runnable() {
            public void run() {
                DesktopDndSupport.instance().fireEvents( events );
            }
        });
        response.flushBuffer();
    }
    
}
