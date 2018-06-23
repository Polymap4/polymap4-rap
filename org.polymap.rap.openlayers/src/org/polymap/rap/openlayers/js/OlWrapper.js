/*
 * polymap.org
 * Copyright 2009-2018, Polymap GmbH. All rights reserved.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

/**
 * JavaScript Part for the OpenLayers RAP Widget
 */
function loadCSS(url) { 
    var link = document.createElement("link");
    link.type = "text/css";
    link.rel = "stylesheet";
    link.href = url; 
    document.getElementsByTagName("head")[0].appendChild(link); 
}

/**
 * 
 */
function log( msg ) {
    console.log( msg );
}

(function() {
    'use strict';

    loadCSS('rwt-resources/ol/css/ol.css');
    loadCSS('rwt-resources/ol/css/progress.css');
    
    rap.registerTypeHandler("org.polymap.rap.openlayers.OlWidget", {

        factory : function(properties) {
            log( "factory(): " + properties );
            return new map.OlWidget(properties);
        },

        destructor : "destroy",

        properties : [],

        methods : [ "call", "addListener", "removeListener"]

    });

    if (!window.map) {
        window.map = {};
        log( "window.map created" );
    }

    map.OlWidget = function(properties) {
        log( "map.OlWidget(): ...");
        this.objs = {};
        this.listenerKeys = {};
        this.events = {};
        bindAll(this, [ "layout", "onRender", "createDiv" ]);
        rap.on("render", this.onRender);
        this.remoteObject;
    };

    map.OlWidget.prototype = {
        ready : false,
        repaint : false,

        createDiv : function(id) {
            log( 'createDiv():' + id);
            var parent = rap.getObject(id);
            var element = document.createElement('div');
            element.setAttribute("id", id + id);
            parent.append(element);

            var progress = document.createElement('div');
            progress.setAttribute("id", id + 'p');
            progress.setAttribute("class", "progress");
            parent.append(progress);

            this.objs[id + 'p'] = new Progress(progress );
//            parent.addListener("Resize", function() {
//                var area = parent.getClientArea();
//                console.log("layout " + parent + ", " + element + "," + area);
//                element.style.left = area[0] + "px";
//                element.style.top = area[1] + "px";
//                element.style.width = area[2] + "px";
//                element.style.height = area[3] + "px";
//            });
//            
//            element.addEventListener("Resize", function() {console.log("element resize ");});
            
//            console.log('element id is ' + element.getAttribute("id"));
            // this.remoteObject = rap.getRemoteObject(this);
            return element;
        },

        onRender : function() {
//            console.log("onRender");
            // this.remoteObject = rap.getRemoteObject(this);
            // rwt.ui.core.Widget.flushGlobalQueues();
            // if (this.element.parentNode) {
            rap.off("render", this.onRender);
            // if (this._map == null) {
//            console.log("onRender creating map");
            this.ready = true;
            // this.layout();
            // // OpenLayers.ImgPath = "/rwt-resources/map/img/";
            // this.objs=[];
            // this.objs['map1'] = new OpenLayers.Map(this.element, {
            // numZoomLevels : 20
            // });
            // //this._map.events.register('moveend', this, this._moveEnd);
            // //this._map.events.register('zoomend', this, this._zoomEnd);
            // // this._map.events.register('changelayer', this,
            // // this._changeLayer);
            // OpenLayers.Control.Click = OpenLayers
            // .Class(
            // OpenLayers.Control,
            // {
            // defaultHandlerOptions : {
            // 'single' : true,
            // 'double' : false,
            // 'pixelTolerance' : 0,
            // 'stopSingle' : false,
            // 'stopDouble' : false
            // },
            //
            // initialize : function(options) {
            // this.handlerOptions = OpenLayers.Util
            // .extend(
            // {},
            // this.defaultHandlerOptions);
            // OpenLayers.Control.prototype.initialize
            // .apply(this, arguments);
            // // this.handler = new OpenLayers.Handler.Click(
            // // this, {
            // // 'click' : this.trigger
            // // }, this.handlerOptions);
            // },
            //
            // // trigger : function(e) {
            // // this.widget._click(e.xy);
            // // },
            //
            // setWidget : function(w) {
            // this.widget = w;
            // }
            //
            // });
            // // var click = new OpenLayers.Control.Click();
            // // // pass the map and widget to control
            // // click.setMap(this._map);
            // // click.setWidget(this);
            // // // add and activate click control
            // // this._map.addControl(click);
            // // click.activate();
            rap.getRemoteObject(this).call("handleOnRender", {});
            // }
            // this._map.updateSize();

            // this._synchronizeResizeWithServer();
            // }
        },

        /**
         * Load addins after OpenLayer.js is loaded.
         * 
         * FIXME: does not work correctly yet. Can be called *after*
         * Openlayer.js is loaded.
         */
        /*
         * load_addin : function(lib_url) { loadScript(lib_url,
         * function(context) { // alert( 'Addin loaded: ' + lib_url ); }, this); },
         */
        layout : function(parent, element) {
            log( "layout " + this.ready + ", " + parent + ", " + element );
//            if (this.ready) {
//                var area = parent.getClientArea();
//                 element.style.left = area[0] + "px";
//                 element.style.top = area[1] + "px";
//                // this.element.style.width = area[2] + "px";
//                // this.element.style.height = area[3] + "px";
//                // // this.editor.resize( area[ 2 ], area[ 3 ] );
//            }
        },
        destroy : function() {
            this.element.parentNode.removeChild(this.element);
        },

        call : function(properties) {
            log( 'call(): ' + properties.code );
            eval( properties.code );
        },
        
        addListener : function(properties) {
            //log( 'addListener(): ' + properties );
            //log( 'obj' + this.objs[properties.src] );
            var key = this.objs[properties.src].on(properties.event, function( theEvent ) {
                //log( "Event properties: " + this.objs[properties.src].getProperties() );
                //log( "theEvent: " + theEvent );
                
                var oldTimeOut = this.events[theEvent + theEvent.type];
                if (oldTimeOut) {
                    window.clearTimeout(oldTimeOut);
                }
                var that = this;
                var ev = theEvent;
                
                // wait 100ms for sending the event, newer events will remove this call
                var newTimeOut = window.setTimeout( function() {
                    log( "Code: " + properties.code );
                	eval( properties.code );
                },100);
                this.events[theEvent + theEvent.type] = newTimeOut;
            }, this);
            //log( 'key: ' + key );
            // listeners stored by key, do later remove them from the global goog context
            this.listenerKeys[properties.hashCode] = key;
        },

        removeListener : function(properties) {
            log( 'removeListener:  ' + properties );
            var key = this.listenerKeys[properties.hashCode];
            log( 'removeListener:  ' + key);
            this.objs[properties.src].unByKey(key);
        }
    };
    
    var bind = function(context, method) {
        return function() {
            return method.apply(context, arguments);
        };
    };

    var bindAll = function(context, methodNames) {
        for (var i = 0; i < methodNames.length; i++) {
            var method = context[methodNames[i]];
            context[methodNames[i]] = bind(context, method);
        }
    };
    
}());