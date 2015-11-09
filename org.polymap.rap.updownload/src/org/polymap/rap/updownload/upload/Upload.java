/* 
 * polymap.org
 * Copyright (C) 2013-2015, Falko Bräutigam. All rights reserved.
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

import java.util.concurrent.Callable;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.widgets.FileUpload;

import org.polymap.core.Messages;
import org.polymap.core.runtime.i18n.IMessages;
import org.polymap.core.runtime.session.SessionContext;
import org.polymap.core.ui.FormDataFactory;
import org.polymap.core.ui.FormLayoutFactory;
import org.polymap.core.ui.UIUtils;

/**
 * 
 *
 * @author <a href="http://www.polymap.de">Falko Bräutigam</a>
 */
public class Upload
        extends Composite
        implements IUploadHandler {

    private static Log log = LogFactory.getLog( Upload.class );

    public static final int         SHOW_UPLOAD_BUTTON = 1;
    public static final int         SHOW_PROGRESS = 2;
    
    private static final IMessages  i18n = Messages.forPrefix( "Upload" );

    private FileUpload              fileUpload;

    private ProgressBar             progress;

    private Label                   progressLabel;
    
    private IUploadHandler          handler;
    
    private SessionContext          sessionContext = SessionContext.current();
    
    private Display                 display = getDisplay();
    
    
    public Upload( Composite parent, int style, int... uploadStyles ) {
        super( parent, style );
        setLayout( FormLayoutFactory.defaults().create() );

        fileUpload = new FileUpload( this, SWT.NONE );
        fileUpload.setLayoutData( FormDataFactory.filled().create() );
        fileUpload.setText( i18n.get( "select" ) );
        fileUpload.setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );
        setData( RWT.TOOLTIP_MARKUP_ENABLED, Boolean.TRUE );

        fileUpload.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected( SelectionEvent ev ) {
                UIUtils.activateCallback( "upload" );
                fileUpload.submit( UploadService.registerHandler( Upload.this ) );
            }
        });
        
        if (ArrayUtils.contains( uploadStyles, SHOW_UPLOAD_BUTTON )) {
            log.info( "SHOW_UPLOAD_BUTTON is not supported yet." );
        }
        
        if (ArrayUtils.contains( uploadStyles, SHOW_PROGRESS )) {
            fileUpload.setLayoutData( FormDataFactory.filled().noRight().create() );
            
            progress = new ProgressBar( this, SWT.HORIZONTAL );
            progress.setLayoutData( FormDataFactory.filled().left( fileUpload ).create() );
            progress.setMaximum( Integer.MAX_VALUE );

            progressLabel = new Label( this, SWT.NONE );
            progressLabel.setLayoutData( FormDataFactory.filled().top( 0, 5 ).left( fileUpload, 20 ).create() );
            progressLabel.setText( i18n.get( "progressLabel" ) );
            log.warn( "not yet ported: progressLabel.setForeground( new Color( 0x60, 0x60, 0x60 ) ) " );
            progressLabel.moveAbove( progress );
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        UploadService.removeHandler( Upload.this );
        UIUtils.deactivateCallback( "upload" );
    }

    public IUploadHandler getHandler() {
        return handler;
    }
    
    public Upload setText( String text ) {
        fileUpload.setText( text );
        return this;
    }

    public Upload setImage( Image image ) {
        fileUpload.setImage( image );
        return this;
    }
    
    public Upload setHandler( IUploadHandler handler ) {
        this.handler = handler;
        return this;
    }

    @Override
    public void uploadStarted( final ClientFile clientFile, final InputStream in ) throws Exception {
        assert handler != null : "No handler set for Upload.";

        // give the thread the proper session context (but outside UI thread)
        sessionContext.execute( new Callable() {
            public Object call() throws Exception {
                handler.uploadStarted( clientFile, new ProgressInputStream( in, clientFile.getName(), clientFile.getSize() ) );
                return null;
            }
        });
    }

    
    /**
     * 
     */
    class ProgressInputStream
            extends FilterInputStream {
    
        private String      name;
    
        private long        count = 0;

        private long        contentLength;
    
    
        private ProgressInputStream( InputStream in, String name, long contentLength ) {
            super( in );
            this.name = name;
            this.contentLength = contentLength > 0 ? contentLength : 10*1024*1024;
        }
    
    
        @Override
        public int read() throws IOException {
            final int result = super.read();
            
            handleProgress( result );
            return result;
        }


        @Override
        public int read( byte[] b, int off, int len ) throws IOException {
            final int result = super.read( b, off, len );

            // try { Thread.sleep( 1000 ); } catch (InterruptedException e) {}

            handleProgress( result );
            return result;
        }


        private void handleProgress( final int result ) {
            if (progress != null && !progress.isDisposed()) {
                display.asyncExec( new Runnable() {
                    public void run() {
                        count += result;
                        // init maximum (inside UI thread)
                        if (progress.getMaximum() == Integer.MAX_VALUE) {
                            progress.setMaximum( (int)contentLength );
                        }
                        if (result == -1) {
                            progress.setSelection( progress.getMaximum() );
                            UIUtils.deactivateCallback( "upload" );
                        }
                        else {
                            progress.setSelection( (int)count );
                        }
                        // adjust contentLength
                        if (count >= contentLength) {
                            contentLength = count * 2;
                            progress.setMaximum( (int)contentLength );
                        }
                        //int percent = 100 * progress.getSelection() / progress.getMaximum();
                        progressLabel.setText( name + " (" + FileUtils.byteCountToDisplaySize( count ) 
                                /*+ " - " + percent + "%"*/ + ")" );
                    }
                });
            }
        }
    }
}
