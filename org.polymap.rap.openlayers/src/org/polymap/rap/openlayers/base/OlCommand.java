/*
 * polymap.org Copyright 2009, Polymap GmbH, and individual contributors as indicated
 * by the @authors tag.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
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
 * 
 * $Id$
 */

package org.polymap.rap.openlayers.base;

import org.eclipse.rap.json.JsonObject;

public class OlCommand {

    private String command;


    /**
     * some commands need to be executed in the context of the right widget ( e.g.
     * creation of a Map ) if this is null it can be executed on all widgets
     * **/

    // private OlWidget assigned_widget;

    public OlCommand( String cmd ) {
        this.command = cmd;
        // this.assigned_widget=null;
    }


    //
    // public OlCommand(String cmd, OlWidget widget) {
    // this.command=cmd;
    // this.assigned_widget=widget;
    // }

    public String getCommand() {
        return command;
    }


    //
    // public Object[] getCommandForWriter() {
    // Object[] res=new Object[1];
    // res[0]=command;
    // return res;
    // }
    // public void setCommand( String command ) {
    // this.command = command;
    // }
    //
    //
    // public OlWidget getAssigned_widget() {
    // return assigned_widget;
    // }
    //
    //
    // public void setAssigned_widget( OlWidget assignedWidget ) {
    // this.assigned_widget = assignedWidget;
    // }
    //
    // public boolean isSuitableFor(OlWidget widget) {
    // return ((assigned_widget==null) || (assigned_widget==widget));
    // }

    public JsonObject getJson() {
        return new JsonObject().add( "code", command );
    }

}
