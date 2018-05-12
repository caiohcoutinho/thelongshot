(function(a){if(typeof define==="function"&&define.amd){define(["jquery"],a)
}else{a(jQuery)
}}(function(c){c(window).bind("unload.atmosphere",function(){c.atmosphere.debug(new Date()+" Atmosphere: unload event");
c.atmosphere.unsubscribe()
});
c(window).bind("beforeunload.atmosphere",function(){c.atmosphere.debug(new Date()+" Atmosphere: beforeunload event");
c.atmosphere._beforeUnloadState=true;
setTimeout(function(){c.atmosphere.debug(new Date()+" Atmosphere: beforeunload event timeout reached. Reset _beforeUnloadState flag");
c.atmosphere._beforeUnloadState=false
},5000)
});
c(window).bind("offline",function(){c.atmosphere.offline=true;
var d=[].concat(c.atmosphere.requests);
for(var f=0;
f<d.length;
f++){var e=d[f];
e.close();
clearTimeout(e.response.request.id);
if(e.heartbeatTimer){clearTimeout(e.heartbeatTimer)
}}});
c(window).bind("online",function(){c.atmosphere.offline=false;
if(c.atmosphere.requests.length>0){for(var d=0;
d<c.atmosphere.requests.length;
d++){c.atmosphere.requests[d].init();
c.atmosphere.requests[d].execute()
}}});
c(window).keypress(function(d){if(d.keyCode===27){d.preventDefault()
}});
var a=function(e){var d,g=/^(.*?):[ \t]*([^\r\n]*)\r?$/mg,f={};
while(d=g.exec(e)){f[d[1]]=d[2]
}return f
};
c.atmosphere={version:"2.2.12-jquery",uuid:0,offline:false,requests:[],callbacks:[],onError:function(d){},onClose:function(d){},onOpen:function(d){},onMessage:function(d){},onReconnect:function(e,d){},onMessagePublished:function(d){},onTransportFailure:function(e,d){},onLocalMessage:function(d){},onClientTimeout:function(d){},onFailureToReconnect:function(e,d){},WebsocketApiAdapter:function(e){var d,f;
e.onMessage=function(g){f.onmessage({data:g.responseBody})
};
e.onMessagePublished=function(g){f.onmessage({data:g.responseBody})
};
e.onOpen=function(g){f.onopen(g)
};
f={close:function(){d.close()
},send:function(g){d.push(g)
},onmessage:function(g){},onopen:function(g){},onclose:function(g){},onerror:function(g){}};
d=new $.atmosphere.subscribe(e);
return f
},AtmosphereRequest:function(Z){var l={timeout:300000,method:"GET",headers:{},contentType:"",callback:null,url:"",data:"",suspend:true,maxRequest:-1,reconnect:true,maxStreamingLength:10000000,lastIndex:0,logLevel:"info",requestCount:0,fallbackMethod:"GET",fallbackTransport:"streaming",transport:"long-polling",webSocketImpl:null,webSocketBinaryType:null,dispatchUrl:null,webSocketPathDelimiter:"@@",enableXDR:false,rewriteURL:false,attachHeadersAsQueryString:true,executeCallbackBeforeReconnect:false,readyState:0,withCredentials:false,trackMessageLength:false,messageDelimiter:"|",connectTimeout:-1,reconnectInterval:0,dropHeaders:true,uuid:0,shared:false,readResponsesHeaders:false,maxReconnectOnClose:5,enableProtocol:true,pollingInterval:0,heartbeat:{client:null,server:null},ackInterval:0,closeAsync:false,reconnectOnServerError:true,onError:function(aD){},onClose:function(aD){},onOpen:function(aD){},onMessage:function(aD){},onReopen:function(aE,aD){},onReconnect:function(aE,aD){},onMessagePublished:function(aD){},onTransportFailure:function(aE,aD){},onLocalMessage:function(aD){},onFailureToReconnect:function(aE,aD){},onClientTimeout:function(aD){}};
var al={status:200,reasonPhrase:"OK",responseBody:"",messages:[],headers:[],state:"messageReceived",transport:"polling",error:null,request:null,partialMessage:"",errorHandled:false,closedByClientTimeout:false,ffTryingReconnect:false};
var ap=null;
var ac=null;
var v=null;
var j=null;
var T=null;
var q=true;
var ar=0;
var F=0;
var ag="X";
var aB=false;
var M=null;
var d;
var aq=null;
var N=c.now();
var u;
var aA;
var U=false;
aj(Z);
function af(){q=true;
aB=false;
ar=0;
ap=null;
ac=null;
v=null;
j=null
}function Q(){g();
af()
}function s(aD){if(aD=="debug"){return l.logLevel==="debug"
}else{if(aD=="info"){return l.logLevel==="info"||l.logLevel==="debug"
}else{if(aD=="warn"){return l.logLevel==="warn"||l.logLevel==="info"||l.logLevel==="debug"
}else{if(aD=="error"){return l.logLevel==="error"||l.logLevel==="warn"||l.logLevel==="info"||l.logLevel==="debug"
}else{return false
}}}}}function aC(aD){if(s("debug")){c.atmosphere.debug(new Date()+" Atmosphere: "+aD)
}}function aj(aD){Q();
l=c.extend(l,aD);
l.mrequest=l.reconnect;
if(!l.reconnect){l.reconnect=true
}}function an(){return l.webSocketImpl!=null||window.WebSocket||window.MozWebSocket
}function am(){function aG(aH){var aI=document.createElement("div");
aI.innerHTML='<a href="'+aH+'"/>';
return encodeURI(decodeURI(aI.firstChild.href))
}var aE=aG(l.url.toLowerCase());
var aF=/^([\w\+\.\-]+:)(?:\/\/([^\/?#:]*)(?::(\d+))?)?/.exec(aE);
var aD=!!(aF&&(aF[1]!=window.location.protocol||aF[2]!=window.location.hostname||(aF[3]||(aF[1]==="http:"?80:443))!=(window.location.port||(window.location.protocol==="http:"?80:443))));
return window.EventSource&&(!aD||!c.browser.safari||c.browser.vmajor>=7)
}function W(){if(l.shared){aq=ay(l);
if(aq!=null){if(s("debug")){c.atmosphere.debug("Storage service available. All communication will be local")
}if(aq.open(l)){return
}}if(s("debug")){c.atmosphere.debug("No Storage service available.")
}aq=null
}l.firstMessage=c.atmosphere.uuid==0?true:false;
l.isOpen=false;
l.ctime=c.now();
if(l.uuid===0){l.uuid=c.atmosphere.uuid
}l.closedByClientTimeout=false;
if(l.transport!=="websocket"&&l.transport!=="sse"){H(l)
}else{if(l.transport==="websocket"){if(!an()){at("Websocket is not supported, using request.fallbackTransport ("+l.fallbackTransport+")")
}else{ab(false)
}}else{if(l.transport==="sse"){if(!am()){at("Server Side Events(SSE) is not supported, using request.fallbackTransport ("+l.fallbackTransport+")")
}else{z(false)
}}}}}function ay(aH){var aK,aE,aG,aF="atmosphere-"+aH.url,aD={storage:function(){if(!c.atmosphere.supportStorage()){return
}var aN=window.localStorage,aL=function(aO){return c.parseJSON(aN.getItem(aF+"-"+aO))
},aM=function(aO,aP){aN.setItem(aF+"-"+aO,c.stringifyJSON(aP))
};
return{init:function(){aM("children",aL("children").concat([N]));
c(window).on("storage.socket",function(aO){aO=aO.originalEvent;
if(aO.key===aF&&aO.newValue){aJ(aO.newValue)
}});
return aL("opened")
},signal:function(aO,aP){aN.setItem(aF,c.stringifyJSON({target:"p",type:aO,data:aP}))
},close:function(){var aO,aP=aL("children");
c(window).off("storage.socket");
if(aP){aO=c.inArray(aH.id,aP);
if(aO>-1){aP.splice(aO,1);
aM("children",aP)
}}}}
},windowref:function(){var aL=window.open("",aF.replace(/\W/g,""));
if(!aL||aL.closed||!aL.callbacks){return
}return{init:function(){aL.callbacks.push(aJ);
aL.children.push(N);
return aL.opened
},signal:function(aM,aN){if(!aL.closed&&aL.fire){aL.fire(c.stringifyJSON({target:"p",type:aM,data:aN}))
}},close:function(){function aM(aP,aO){var aN=c.inArray(aO,aP);
if(aN>-1){aP.splice(aN,1)
}}if(!aG){aM(aL.callbacks,aJ);
aM(aL.children,N)
}}}
}};
function aJ(aL){var aN=c.parseJSON(aL),aM=aN.data;
if(aN.target==="c"){switch(aN.type){case"open":R("opening","local",l);
break;
case"close":if(!aG){aG=true;
if(aM.reason==="aborted"){D()
}else{if(aM.heir===N){W()
}else{setTimeout(function(){W()
},100)
}}}break;
case"message":h(aM,"messageReceived",200,aH.transport);
break;
case"localMessage":B(aM);
break
}}}function aI(){var aL=new RegExp("(?:^|; )("+encodeURIComponent(aF)+")=([^;]*)").exec(document.cookie);
if(aL){return c.parseJSON(decodeURIComponent(aL[2]))
}}aK=aI();
if(!aK||c.now()-aK.ts>1000){return
}aE=aD.storage()||aD.windowref();
if(!aE){return
}return{open:function(){var aL;
u=setInterval(function(){var aM=aK;
aK=aI();
if(!aK||aM.ts===aK.ts){aJ(c.stringifyJSON({target:"c",type:"close",data:{reason:"error",heir:aM.heir}}))
}},1000);
aL=aE.init();
if(aL){setTimeout(function(){R("opening","local",aH)
},50)
}return aL
},send:function(aL){aE.signal("send",aL)
},localSend:function(aL){aE.signal("localSend",c.stringifyJSON({id:N,event:aL}))
},close:function(){if(!aB){clearInterval(u);
aE.signal("close");
aE.close()
}}}
}function az(){var aE,aD="atmosphere-"+l.url,aI={storage:function(){if(!c.atmosphere.supportStorage()){return
}var aJ=window.localStorage;
return{init:function(){c(window).on("storage.socket",function(aK){aK=aK.originalEvent;
if(aK.key===aD&&aK.newValue){aF(aK.newValue)
}})
},signal:function(aK,aL){aJ.setItem(aD,c.stringifyJSON({target:"c",type:aK,data:aL}))
},get:function(aK){return c.parseJSON(aJ.getItem(aD+"-"+aK))
},set:function(aK,aL){aJ.setItem(aD+"-"+aK,c.stringifyJSON(aL))
},close:function(){c(window).off("storage.socket");
aJ.removeItem(aD);
aJ.removeItem(aD+"-opened");
aJ.removeItem(aD+"-children")
}}
},windowref:function(){var aJ=aD.replace(/\W/g,""),aK=(c('iframe[name="'+aJ+'"]')[0]||c('<iframe name="'+aJ+'" />').hide().appendTo("body")[0]).contentWindow;
return{init:function(){aK.callbacks=[aF];
aK.fire=function(aL){var aM;
for(aM=0;
aM<aK.callbacks.length;
aM++){aK.callbacks[aM](aL)
}}
},signal:function(aL,aM){if(!aK.closed&&aK.fire){aK.fire(c.stringifyJSON({target:"c",type:aL,data:aM}))
}},get:function(aL){return !aK.closed?aK[aL]:null
},set:function(aL,aM){if(!aK.closed){aK[aL]=aM
}},close:function(){}}
}};
function aF(aJ){var aL=c.parseJSON(aJ),aK=aL.data;
if(aL.target==="p"){switch(aL.type){case"send":p(aK);
break;
case"localSend":B(aK);
break;
case"close":D();
break
}}}M=function aH(aJ){aE.signal("message",aJ)
};
function aG(){document.cookie=aA+"="+encodeURIComponent(c.stringifyJSON({ts:c.now()+1,heir:(aE.get("children")||[])[0]}))+"; path=/"
}aE=aI.storage()||aI.windowref();
aE.init();
if(s("debug")){c.atmosphere.debug("Installed StorageService "+aE)
}aE.set("children",[]);
if(aE.get("opened")!=null&&!aE.get("opened")){aE.set("opened",false)
}aA=encodeURIComponent(aD);
aG();
u=setInterval(aG,1000);
d=aE
}function R(aF,aI,aE){if(l.shared&&aI!=="local"){az()
}if(d!=null){d.set("opened",true)
}aE.close=function(){D()
};
if(ar>0&&aF==="re-connecting"){aE.isReopen=true;
m(al)
}else{if(al.error==null){al.request=aE;
var aG=al.state;
al.state=aF;
var aD=al.transport;
al.transport=aI;
var aH=al.responseBody;
ae();
al.responseBody=aH;
al.state=aG;
al.transport=aD
}}}function av(aF){aF.transport="jsonp";
var aE=l;
if((aF!=null)&&(typeof(aF)!=="undefined")){aE=aF
}var aD=aE.url;
if(aE.dispatchUrl!=null){aD+=aE.dispatchUrl
}var aG=aE.data;
if(aE.attachHeadersAsQueryString){aD=k(aE);
if(aG!==""){aD+="&X-Atmosphere-Post-Body="+encodeURIComponent(aG)
}aG=""
}T=c.ajax({url:aD,type:aE.method,dataType:"jsonp",error:function(aH,aJ,aI){al.error=true;
if(aE.openId){clearTimeout(aE.openId)
}if(aE.heartbeatTimer){clearTimeout(aE.heartbeatTimer)
}if(aE.reconnect&&ar++<aE.maxReconnectOnClose){R("re-connecting",aE.transport,aE);
ai(T,aE,aE.reconnectInterval);
aE.openId=setTimeout(function(){V(aE)
},aE.reconnectInterval+1000)
}else{P(aH.status,aI)
}},jsonp:"jsonpTransport",success:function(aI){if(aE.reconnect){if(aE.maxRequest===-1||aE.requestCount++<aE.maxRequest){C(T,aE);
var aK=aI.message;
if(aK!=null&&typeof aK!=="string"){try{aK=c.stringifyJSON(aK)
}catch(aJ){}}var aH=n(aK,aE,al);
if(!aE.executeCallbackBeforeReconnect){ai(T,aE,aE.pollingInterval)
}if(!aH){h(al.responseBody,"messageReceived",200,aE.transport)
}if(aE.executeCallbackBeforeReconnect){ai(T,aE,aE.pollingInterval)
}f(aE)
}else{c.atmosphere.log(l.logLevel,["JSONP reconnect maximum try reached "+l.requestCount]);
P(0,"maxRequest reached")
}}},data:aE.data,beforeSend:function(aH){I(aH,aE,false)
}})
}function ax(aG){var aE=l;
if((aG!=null)&&(typeof(aG)!=="undefined")){aE=aG
}var aD=aE.url;
if(aE.dispatchUrl!=null){aD+=aE.dispatchUrl
}var aH=aE.data;
if(aE.attachHeadersAsQueryString){aD=k(aE);
if(aH!==""){aD+="&X-Atmosphere-Post-Body="+encodeURIComponent(aH)
}aH=""
}var aF=typeof(aE.async)!=="undefined"?aE.async:true;
T=c.ajax({url:aD,type:aE.method,error:function(aI,aK,aJ){al.error=true;
if(aI.status<300){ai(T,aE)
}else{P(aI.status,aJ)
}},success:function(aK,aL,aJ){if(aE.reconnect){if(aE.maxRequest===-1||aE.requestCount++<aE.maxRequest){if(!aE.executeCallbackBeforeReconnect){ai(T,aE,aE.pollingInterval)
}var aI=n(aK,aE,al);
if(!aI){h(al.responseBody,"messageReceived",200,aE.transport)
}if(aE.executeCallbackBeforeReconnect){ai(T,aE,aE.pollingInterval)
}}else{c.atmosphere.log(l.logLevel,["AJAX reconnect maximum try reached "+l.requestCount]);
P(0,"maxRequest reached")
}}},beforeSend:function(aI){I(aI,aE,false)
},crossDomain:aE.enableXDR,async:aF})
}function ao(aD){if(l.webSocketImpl!=null){return l.webSocketImpl
}else{if(window.WebSocket){return new WebSocket(aD)
}else{return new MozWebSocket(aD)
}}}function r(){var aD=k(l);
return decodeURI(c('<a href="'+aD+'"/>')[0].href.replace(/^http/,"ws"))
}function O(){var aD=k(l);
return aD
}function z(aE){al.transport="sse";
var aD=O(l.url);
if(s("debug")){c.atmosphere.debug("Invoking executeSSE");
c.atmosphere.debug("Using URL: "+aD)
}if(aE&&!l.reconnect){if(ac!=null){g()
}return
}try{ac=new EventSource(aD,{withCredentials:l.withCredentials})
}catch(aF){P(0,aF);
at("SSE failed. Downgrading to fallback transport and resending");
return
}if(l.connectTimeout>0){l.id=setTimeout(function(){if(!aE){g()
}},l.connectTimeout)
}ac.onopen=function(aG){aC("websocket.onopen");
f(l);
if(s("debug")){c.atmosphere.debug("SSE successfully opened")
}if(!l.enableProtocol){if(!aE){R("opening","sse",l)
}else{R("re-opening","sse",l)
}}else{if(l.isReopen){l.isReopen=false;
R("re-opening",l.transport,l)
}}aE=true;
if(l.method==="POST"){al.state="messageReceived";
ac.send(l.data)
}};
ac.onmessage=function(aH){aC("websocket.onmessage");
f(l);
if(!l.enableXDR&&aH.origin!==window.location.protocol+"//"+window.location.host){c.atmosphere.log(l.logLevel,["Origin was not "+window.location.protocol+"//"+window.location.host]);
return
}al.state="messageReceived";
al.status=200;
aH=aH.data;
var aG=n(aH,l,al);
if(!aG){ae();
al.responseBody="";
al.messages=[]
}};
ac.onerror=function(aG){aC("websocket.onerror");
clearTimeout(l.id);
if(l.heartbeatTimer){clearTimeout(l.heartbeatTimer)
}if(al.closedByClientTimeout){return
}aa(aE);
g();
if(aB){c.atmosphere.log(l.logLevel,["SSE closed normally"])
}else{if(!aE){at("SSE failed. Downgrading to fallback transport and resending")
}else{if(l.reconnect&&(al.transport==="sse")){if(ar++<l.maxReconnectOnClose){R("re-connecting",l.transport,l);
if(l.reconnectInterval>0){l.reconnectId=setTimeout(function(){z(true)
},l.reconnectInterval)
}else{z(true)
}al.responseBody="";
al.messages=[]
}else{c.atmosphere.log(l.logLevel,["SSE reconnect maximum try reached "+ar]);
P(0,"maxReconnectOnClose reached")
}}}}}
}function ab(aE){al.transport="websocket";
var aD=r(l.url);
if(s("debug")){c.atmosphere.debug("Invoking executeWebSocket");
c.atmosphere.debug("Using URL: "+aD)
}if(aE&&!l.reconnect){if(ap!=null){g()
}return
}ap=ao(aD);
if(l.webSocketBinaryType!=null){ap.binaryType=l.webSocketBinaryType
}if(l.connectTimeout>0){l.id=setTimeout(function(){if(!aE){var aH={code:1002,reason:"",wasClean:false};
ap.onclose(aH);
try{g()
}catch(aI){}return
}},l.connectTimeout)
}ap.onopen=function(aI){aC("websocket.onopen");
f(l);
if(s("debug")){c.atmosphere.debug("Websocket successfully opened")
}c.atmosphere.offline=false;
var aH=aE;
if(ap!=null){ap.canSendMessage=true
}if(!l.enableProtocol){aE=true;
if(aH){R("re-opening","websocket",l)
}else{R("opening","websocket",l)
}}if(ap!=null){if(l.method==="POST"){al.state="messageReceived";
ap.send(l.data)
}}};
ap.onmessage=function(aJ){aC("websocket.onmessage: "+aJ);
f(l);
if(l.enableProtocol){aE=true
}al.state="messageReceived";
al.status=200;
aJ=aJ.data;
var aH=typeof(aJ)==="string";
if(aH){var aI=n(aJ,l,al);
if(!aI){ae();
al.responseBody="";
al.messages=[]
}}else{aJ=o(l,aJ);
if(aJ===""){return
}al.responseBody=aJ;
ae();
al.responseBody=null
}};
ap.onerror=function(aH){aC("websocket.onerror");
clearTimeout(l.id);
if(l.heartbeatTimer){clearTimeout(l.heartbeatTimer)
}};
ap.onclose=function(aH){aC("websocket.onclose");
if(al.state==="closed"){return
}clearTimeout(l.id);
var aI=aH.reason;
if(aI===""){switch(aH.code){case 1000:aI="Normal closure; the connection successfully completed whatever purpose for which it was created.";
break;
case 1001:aI="The endpoint is going away, either because of a server failure or because the browser is navigating away from the page that opened the connection.";
break;
case 1002:aI="The endpoint is terminating the connection due to a protocol error.";
break;
case 1003:aI="The connection is being terminated because the endpoint received data of a type it cannot accept (for example, a text-only endpoint received binary data).";
break;
case 1004:aI="The endpoint is terminating the connection because a data frame was received that is too large.";
break;
case 1005:aI="Unknown: no status code was provided even though one was expected.";
break;
case 1006:aI="Connection was closed abnormally (that is, with no close frame being sent).";
break
}}if(s("warn")){c.atmosphere.warn("Websocket closed, reason: "+aI);
c.atmosphere.warn("Websocket closed, wasClean: "+aH.wasClean)
}if(al.closedByClientTimeout||c.atmosphere.offline){if(l.reconnectId){clearTimeout(l.reconnectId);
delete l.reconnectId
}return
}aa(aE);
al.state="closed";
if(aB){c.atmosphere.log(l.logLevel,["Websocket closed normally"])
}else{if(!aE){at("Websocket failed. Downgrading to Comet and resending")
}else{if(l.reconnect&&al.transport==="websocket"){g();
if(ar++<l.maxReconnectOnClose){R("re-connecting",l.transport,l);
if(l.reconnectInterval>0){l.reconnectId=setTimeout(function(){al.responseBody="";
al.messages=[];
ab(true)
},l.reconnectInterval)
}else{al.responseBody="";
al.messages=[];
ab(true)
}}else{c.atmosphere.log(l.logLevel,["Websocket reconnect maximum try reached "+l.requestCount]);
if(s("warn")){c.atmosphere.warn("Websocket error, reason: "+aH.reason)
}P(0,"maxReconnectOnClose reached")
}}}}};
var aF=navigator.userAgent.toLowerCase();
var aG=aF.indexOf("android")>-1;
if(aG&&ap.url===undefined){ap.onclose({reason:"Android 4.1 does not support websockets.",wasClean:false})
}}function o(aH,aG){var aF=aG;
if(aH.transport==="polling"){return aF
}if(aH.enableProtocol&&aH.firstMessage&&c.trim(aG).length!==0){var aI=aH.trackMessageLength?1:0;
var aE=aG.split(aH.messageDelimiter);
if(aE.length<=aI+1){return aF
}aH.firstMessage=false;
aH.uuid=c.trim(aE[aI]);
if(aE.length<=aI+2){c.atmosphere.log("error",["Protocol data not sent by the server. If you enable protocol on client side, be sure to install JavascriptProtocol interceptor on server side.Also note that atmosphere-runtime 2.2+ should be used."])
}F=parseInt(c.trim(aE[aI+1]),10);
ag=aE[aI+2];
b=false;
if(aH.transport!=="long-polling"){V(aH)
}c.atmosphere.uuid=aH.uuid;
aF="";
aI=aH.trackMessageLength?4:3;
if(aE.length>aI+1){for(var aD=aI;
aD<aE.length;
aD++){aF+=aE[aD];
if(aD+1!==aE.length){aF+=aH.messageDelimiter
}}}if(aH.ackInterval!==0){setTimeout(function(){p("...ACK...")
},aH.ackInterval)
}}else{if(aH.enableProtocol&&aH.firstMessage&&c.browser.msie&&+c.browser.version.split(".")[0]<10){c.atmosphere.log(l.logLevel,["Receiving unexpected data from IE"])
}else{V(aH)
}}return aF
}function f(aD){clearTimeout(aD.id);
if(aD.timeout>0&&aD.transport!=="polling"){aD.id=setTimeout(function(){aw(aD);
y();
g()
},aD.timeout)
}}function aw(aD){al.closedByClientTimeout=true;
al.state="closedByClient";
al.responseBody="";
al.status=408;
al.messages=[];
ae()
}function P(aD,aE){g();
clearTimeout(l.id);
al.state="error";
al.reasonPhrase=aE;
al.responseBody="";
al.status=aD;
al.messages=[];
ae()
}function n(aH,aG,aD){aH=o(aG,aH);
if(aH.length===0){return true
}aD.responseBody=aH;
if(aG.trackMessageLength){aH=aD.partialMessage+aH;
var aF=[];
var aE=aH.indexOf(aG.messageDelimiter);
if(aE!=-1){while(aE!==-1){var aJ=aH.substring(0,aE);
var aI=parseInt(aJ,10);
if(isNaN(aI)){throw'message length "'+aJ+'" is not a number'
}aE+=aG.messageDelimiter.length;
if(aE+aI>aH.length){aE=-1
}else{aF.push(aH.substring(aE,aE+aI));
aH=aH.substring(aE+aI,aH.length);
aE=aH.indexOf(aG.messageDelimiter)
}}aD.partialMessage=aH;
if(aF.length!==0){aD.responseBody=aF.join(aG.messageDelimiter);
aD.messages=aF;
return false
}else{aD.responseBody="";
aD.messages=[];
return true
}}}aD.responseBody=aH;
aD.messages=[aH];
return false
}function at(aD){c.atmosphere.log(l.logLevel,[aD]);
if(typeof(l.onTransportFailure)!=="undefined"){l.onTransportFailure(aD,l)
}else{if(typeof(c.atmosphere.onTransportFailure)!=="undefined"){c.atmosphere.onTransportFailure(aD,l)
}}l.transport=l.fallbackTransport;
var aE=l.connectTimeout===-1?0:l.connectTimeout;
if(l.reconnect&&l.transport!=="none"||l.transport==null){l.method=l.fallbackMethod;
al.transport=l.fallbackTransport;
l.fallbackTransport="none";
if(aE>0){l.reconnectId=setTimeout(function(){W()
},aE)
}else{W()
}}else{P(500,"Unable to reconnect with fallback transport")
}}function k(aF,aD){var aE=l;
if((aF!=null)&&(typeof(aF)!=="undefined")){aE=aF
}if(aD==null){aD=aE.url
}if(!aE.attachHeadersAsQueryString){return aD
}if(aD.indexOf("X-Atmosphere-Framework")!==-1){return aD
}aD+=(aD.indexOf("?")!==-1)?"&":"?";
aD+="X-Atmosphere-tracking-id="+aE.uuid;
aD+="&X-Atmosphere-Framework="+c.atmosphere.version;
aD+="&X-Atmosphere-Transport="+aE.transport;
if(aE.trackMessageLength){aD+="&X-Atmosphere-TrackMessageSize=true"
}if(aE.heartbeat!==null&&aE.heartbeat.server!==null){aD+="&X-Heartbeat-Server="+aE.heartbeat.server
}if(aE.contentType!==""){aD+="&Content-Type="+(aE.transport==="websocket"?aE.contentType:encodeURIComponent(aE.contentType))
}if(aE.enableProtocol){aD+="&X-atmo-protocol=true"
}c.each(aE.headers,function(aG,aI){var aH=c.isFunction(aI)?aI.call(this,aE,aF,al):aI;
if(aH!=null){aD+="&"+encodeURIComponent(aG)+"="+encodeURIComponent(aH)
}});
return aD
}function V(aD){if(!aD.isOpen){aD.isOpen=true;
R("opening",aD.transport,aD)
}else{if(aD.isReopen){aD.isReopen=false;
R("re-opening",aD.transport,aD)
}else{return
}}x(aD)
}function x(aE){if(aE.heartbeatTimer!=null){clearTimeout(aE.heartbeatTimer)
}if(!isNaN(F)&&F>0){var aD=function(){if(s("debug")){c.atmosphere.debug("Sending heartbeat")
}p(ag);
aE.heartbeatTimer=setTimeout(aD,F)
};
aE.heartbeatTimer=setTimeout(aD,F)
}}function H(aH){var aE=l;
if((aH!=null)||(typeof(aH)!=="undefined")){aE=aH
}aE.lastIndex=0;
aE.readyState=0;
if((aE.transport==="jsonp")||((aE.enableXDR)&&(c.atmosphere.checkCORSSupport()))){av(aE);
return
}if(aE.transport==="ajax"){ax(aH);
return
}if(c.browser.msie&&+c.browser.version.split(".")[0]<10){if((aE.transport==="streaming")){if(aE.enableXDR&&window.XDomainRequest){L(aE)
}else{au(aE)
}return
}if((aE.enableXDR)&&(window.XDomainRequest)){L(aE);
return
}}var aG=function(aJ){aE.lastIndex=0;
if(aJ||(aE.reconnect&&ar++<aE.maxReconnectOnClose)){al.ffTryingReconnect=true;
R("re-connecting",aH.transport,aH);
ai(aF,aE,aH.reconnectInterval)
}else{P(0,"maxReconnectOnClose reached")
}};
var aI=function(aJ){if(c.atmosphere._beforeUnloadState){c.atmosphere.debug(new Date()+" Atmosphere: reconnectF: execution delayed due to _beforeUnloadState flag");
setTimeout(function(){aG(aJ)
},5000)
}else{aG(aJ)
}};
var aD=function(){al.errorHandled=true;
g();
aI(false)
};
if(aE.reconnect&&(aE.maxRequest===-1||aE.requestCount++<aE.maxRequest)){var aF=c.ajaxSettings.xhr();
aF.hasData=false;
I(aF,aE,true);
if(aE.suspend){v=aF
}if(aE.transport!=="polling"){al.transport=aE.transport;
aF.onabort=function(){aC("ajaxrequest.onabort");
aa(true)
};
aF.onerror=function(){aC("ajaxrequest.onerror");
al.error=true;
al.ffTryingReconnect=true;
try{al.status=XMLHttpRequest.status
}catch(aJ){al.status=500
}if(!al.status){al.status=500
}if(!al.errorHandled){g();
aI(false)
}}
}aF.onreadystatechange=function(){aC("ajaxRequest.onreadystatechange, new state: "+aF.readyState);
if(aB){return
}al.error=null;
var aK=false;
var aQ=false;
if(aE.transport==="streaming"&&aE.readyState>2&&aF.readyState===4){g();
aI(false);
return
}aE.readyState=aF.readyState;
if(aE.transport==="streaming"&&aF.readyState>=3){aQ=true
}else{if(aE.transport==="long-polling"&&aF.readyState===4){aQ=true
}}f(l);
if(aE.transport!=="polling"){var aJ=200;
if(aF.readyState===4){aJ=aF.status>1000?0:aF.status
}if(!aE.reconnectOnServerError&&(aJ>=300&&aJ<600)){P(aJ,aF.statusText);
return
}if(aJ>=300||aJ===0){aD();
return
}if((!aE.enableProtocol||!aH.firstMessage)&&aF.readyState===2){if(c.browser.mozilla&&al.ffTryingReconnect){al.ffTryingReconnect=false;
setTimeout(function(){if(!al.ffTryingReconnect){V(aE)
}},500)
}else{V(aE)
}}}else{if(aF.readyState===4){aQ=true
}}if(aQ){var aN=aF.responseText;
if(aE.transport==="long-polling"&&c.trim(aN).length===0){if(!aF.hasData){aI(true)
}else{aF.hasData=false
}return
}aF.hasData=true;
C(aF,l);
if(aE.transport==="streaming"){if(!c.browser.opera){var aM=aN.substring(aE.lastIndex,aN.length);
aK=n(aM,aE,al);
aE.lastIndex=aN.length;
if(aK){return
}}else{c.atmosphere.iterate(function(){if(al.status!==500&&aF.responseText.length>aE.lastIndex){try{al.status=aF.status;
al.headers=a(aF.getAllResponseHeaders());
C(aF,l)
}catch(aS){al.status=404
}f(l);
al.state="messageReceived";
var aR=aF.responseText.substring(aE.lastIndex);
aE.lastIndex=aF.responseText.length;
aK=n(aR,aE,al);
if(!aK){ae()
}if(E(aF,aE)){G(aF,aE);
return
}}else{if(al.status>400){aE.lastIndex=aF.responseText.length;
return false
}}},0)
}}else{aK=n(aN,aE,al)
}var aP=E(aF,aE);
try{al.status=aF.status;
al.headers=a(aF.getAllResponseHeaders());
C(aF,aE)
}catch(aO){al.status=404
}if(aE.suspend){al.state=al.status===0?"closed":"messageReceived"
}else{al.state="messagePublished"
}var aL=!aP&&aH.transport!=="streaming"&&aH.transport!=="polling";
if(aL&&!aE.executeCallbackBeforeReconnect){ai(aF,aE,aE.pollingInterval)
}if(al.responseBody.length!==0&&!aK){ae()
}if(aL&&aE.executeCallbackBeforeReconnect){ai(aF,aE,aE.pollingInterval)
}if(aP){G(aF,aE)
}}};
aF.send(aE.data);
q=true
}else{if(aE.logLevel==="debug"){c.atmosphere.log(aE.logLevel,["Max re-connection reached."])
}P(0,"maxRequest reached")
}}function G(aE,aD){al.messages=[];
aD.isReopen=true;
D();
aB=false;
ai(aE,aD,500)
}function I(aF,aG,aE){var aD=aG.url;
if(aG.dispatchUrl!=null&&aG.method==="POST"){aD+=aG.dispatchUrl
}aD=k(aG,aD);
aD=c.atmosphere.prepareURL(aD);
if(aE){aF.open(aG.method,aD,true);
if(aG.connectTimeout>0){aG.id=setTimeout(function(){if(aG.requestCount===0){g();
h("Connect timeout","closed",200,aG.transport)
}},aG.connectTimeout)
}}if(l.withCredentials&&l.transport!=="websocket"){if("withCredentials" in aF){aF.withCredentials=true
}}if(!l.dropHeaders){aF.setRequestHeader("X-Atmosphere-Framework",c.atmosphere.version);
aF.setRequestHeader("X-Atmosphere-Transport",aG.transport);
if(aG.heartbeat!==null&&aG.heartbeat.server!==null){aF.setRequestHeader("X-Heartbeat-Server",aF.heartbeat.server)
}if(aG.trackMessageLength){aF.setRequestHeader("X-Atmosphere-TrackMessageSize","true")
}aF.setRequestHeader("X-Atmosphere-tracking-id",aG.uuid);
c.each(aG.headers,function(aH,aJ){var aI=c.isFunction(aJ)?aJ.call(this,aF,aG,aE,al):aJ;
if(aI!=null){aF.setRequestHeader(aH,aI)
}})
}if(aG.contentType!==""){aF.setRequestHeader("Content-Type",aG.contentType)
}}function ai(aE,aF,aG){if(al.closedByClientTimeout){return
}if(aF.reconnect||(aF.suspend&&q)){var aD=0;
if(aE.readyState>1){aD=aE.status>1000?0:aE.status
}al.status=aD===0?204:aD;
al.reason=aD===0?"Server resumed the connection or down.":"OK";
clearTimeout(aF.id);
if(aF.reconnectId){clearTimeout(aF.reconnectId);
delete aF.reconnectId
}if(aG>0){setTimeout(function(){l.reconnectId=H(aF)
},aG)
}else{H(aF)
}}}function m(aD){aD.state="re-connecting";
ah(aD)
}function L(aD){if(aD.transport!=="polling"){j=Y(aD);
j.open()
}else{Y(aD).open()
}}function Y(aF){var aE=l;
if((aF!=null)&&(typeof(aF)!=="undefined")){aE=aF
}var aK=aE.transport;
var aJ=0;
var aD=new window.XDomainRequest();
var aH=function(){if(aE.transport==="long-polling"&&(aE.reconnect&&(aE.maxRequest===-1||aE.requestCount++<aE.maxRequest))){aD.status=200;
R("re-connecting",aF.transport,aF);
L(aE)
}};
var aI=aE.rewriteURL||function(aM){var aL=/(?:^|;\s*)(JSESSIONID|PHPSESSID)=([^;]*)/.exec(document.cookie);
switch(aL&&aL[1]){case"JSESSIONID":return aM.replace(/;jsessionid=[^\?]*|(\?)|$/,";jsessionid="+aL[2]+"$1");
case"PHPSESSID":return aM.replace(/\?PHPSESSID=[^&]*&?|\?|$/,"?PHPSESSID="+aL[2]+"&").replace(/&$/,"")
}return aM
};
aD.onprogress=function(){aG(aD)
};
aD.onerror=function(){if(aE.transport!=="polling"){g();
if(ar++<aE.maxReconnectOnClose){if(aE.reconnectInterval>0){aE.reconnectId=setTimeout(function(){R("re-connecting",aF.transport,aF);
L(aE)
},aE.reconnectInterval)
}else{R("re-connecting",aF.transport,aF);
L(aE)
}}else{P(0,"maxReconnectOnClose reached")
}}};
aD.onload=function(){};
var aG=function(aL){clearTimeout(aE.id);
var aN=aL.responseText;
aN=aN.substring(aJ);
aJ+=aN.length;
if(aK!=="polling"){f(aE);
var aM=n(aN,aE,al);
if(aK==="long-polling"&&c.trim(aN).length===0){return
}if(aE.executeCallbackBeforeReconnect){aH()
}if(!aM){h(al.responseBody,"messageReceived",200,aK)
}if(!aE.executeCallbackBeforeReconnect){aH()
}}};
return{open:function(){var aL=aE.url;
if(aE.dispatchUrl!=null){aL+=aE.dispatchUrl
}aL=k(aE,aL);
aD.open(aE.method,aI(aL));
if(aE.method==="GET"){aD.send()
}else{aD.send(aE.data)
}if(aE.connectTimeout>0){aE.id=setTimeout(function(){if(aE.requestCount===0){g();
h("Connect timeout","closed",200,aE.transport)
}},aE.connectTimeout)
}},close:function(){aD.abort()
}}
}function au(aD){j=X(aD);
j.open()
}function X(aG){var aF=l;
if((aG!=null)&&(typeof(aG)!=="undefined")){aF=aG
}var aE;
var aH=new window.ActiveXObject("htmlfile");
aH.open();
aH.close();
var aD=aF.url;
if(aF.dispatchUrl!=null){aD+=aF.dispatchUrl
}if(aF.transport!=="polling"){al.transport=aF.transport
}return{open:function(){var aI=aH.createElement("iframe");
aD=k(aF);
if(aF.data!==""){aD+="&X-Atmosphere-Post-Body="+encodeURIComponent(aF.data)
}aD=c.atmosphere.prepareURL(aD);
aI.src=aD;
aH.body.appendChild(aI);
var aJ=aI.contentDocument||aI.contentWindow.document;
aE=c.atmosphere.iterate(function(){try{if(!aJ.firstChild){return
}if(aJ.readyState==="complete"){try{c.noop(aJ.fileSize)
}catch(aP){h("Connection Failure","error",500,aF.transport);
return false
}}var aM=aJ.body?aJ.body.lastChild:aJ;
var aO=function(){var aR=aM.cloneNode(true);
aR.appendChild(aJ.createTextNode("."));
var aQ=aR.innerText;
aQ=aQ.substring(0,aQ.length-1);
return aQ
};
if(!c.nodeName(aM,"pre")){var aL=aJ.head||aJ.getElementsByTagName("head")[0]||aJ.documentElement||aJ;
var aK=aJ.createElement("script");
aK.text="document.write('<plaintext>')";
aL.insertBefore(aK,aL.firstChild);
aL.removeChild(aK);
aM=aJ.body.lastChild
}if(aF.closed){aF.isReopen=true
}aE=c.atmosphere.iterate(function(){var aR=aO();
if(aR.length>aF.lastIndex){f(l);
al.status=200;
al.error=null;
aM.innerText="";
var aQ=n(aR,aF,al);
if(aQ){return""
}h(al.responseBody,"messageReceived",200,aF.transport)
}aF.lastIndex=0;
if(aJ.readyState==="complete"){aa(true);
R("re-connecting",aF.transport,aF);
if(aF.reconnectInterval>0){aF.reconnectId=setTimeout(function(){au(aF)
},aF.reconnectInterval)
}else{au(aF)
}return false
}},null);
return false
}catch(aN){al.error=true;
R("re-connecting",aF.transport,aF);
if(ar++<aF.maxReconnectOnClose){if(aF.reconnectInterval>0){aF.reconnectId=setTimeout(function(){au(aF)
},aF.reconnectInterval)
}else{au(aF)
}}else{P(0,"maxReconnectOnClose reached")
}aH.execCommand("Stop");
aH.close();
return false
}})
},close:function(){if(aE){aE()
}aH.execCommand("Stop");
aa(true)
}}
}function p(aD){if(aq!=null){A(aD)
}else{if(v!=null||ac!=null){K(aD)
}else{if(j!=null){e(aD)
}else{if(T!=null){w(aD)
}else{if(ap!=null){S(aD)
}else{P(0,"No suspended connection available");
c.atmosphere.error("No suspended connection available. Make sure atmosphere.subscribe has been called and request.onOpen invoked before invoking trying to push data")
}}}}}}function A(aD){aq.send(aD)
}function ak(aE){if(aE.length===0){return
}try{if(aq){aq.localSend(aE)
}else{if(d){d.signal("localMessage",c.stringifyJSON({id:N,event:aE}))
}}}catch(aD){c.atmosphere.error(aD)
}}function K(aE){var aD=t(aE);
H(aD)
}function e(aE){if(l.enableXDR&&c.atmosphere.checkCORSSupport()){var aD=t(aE);
aD.reconnect=false;
av(aD)
}else{K(aE)
}}function w(aD){K(aD)
}function J(aD){var aE=aD;
if(typeof(aE)==="object"){aE=aD.data
}return aE
}function t(aE){var aF=J(aE);
var aD={connected:false,timeout:60000,method:"POST",url:l.url,contentType:l.contentType,headers:l.headers,reconnect:true,callback:null,data:aF,suspend:false,maxRequest:-1,logLevel:"info",requestCount:0,withCredentials:l.withCredentials,transport:"polling",isOpen:true,attachHeadersAsQueryString:true,enableXDR:l.enableXDR,uuid:l.uuid,dispatchUrl:l.dispatchUrl,enableProtocol:false,messageDelimiter:"|",trackMessageLength:l.trackMessageLength,maxReconnectOnClose:l.maxReconnectOnClose,heartbeatTimer:l.heartbeatTimer,heartbeat:l.heartbeat};
if(typeof(aE)==="object"){aD=c.extend(aD,aE)
}return aD
}function S(aD){var aG=c.atmosphere.isBinary(aD)?aD:J(aD);
var aE;
try{if(l.dispatchUrl!=null){aE=l.webSocketPathDelimiter+l.dispatchUrl+l.webSocketPathDelimiter+aG
}else{aE=aG
}if(!ap.canSendMessage){c.atmosphere.error("WebSocket not connected.");
return
}ap.send(aE)
}catch(aF){ap.onclose=function(aH){};
g();
at("Websocket failed. Downgrading to Comet and resending "+aD);
K(aD)
}}function B(aE){var aD=c.parseJSON(aE);
if(aD.id!==N){if(typeof(l.onLocalMessage)!=="undefined"){l.onLocalMessage(aD.event)
}else{if(typeof(c.atmosphere.onLocalMessage)!=="undefined"){c.atmosphere.onLocalMessage(aD.event)
}}}}function h(aG,aD,aE,aF){al.responseBody=aG;
al.transport=aF;
al.status=aE;
al.state=aD;
ae()
}function C(aD,aF){if(!aF.readResponsesHeaders){if(!aF.enableProtocol){aF.uuid=c.atmosphere.guid()
}}else{try{var aE=aD.getResponseHeader("X-Atmosphere-tracking-id");
if(aE&&aE!=null){aF.uuid=aE.split(" ").pop()
}}catch(aG){}}}function ah(aD){i(aD,l);
i(aD,c.atmosphere)
}function i(aE,aF){switch(aE.state){case"messageReceived":aC("Firing onMessage");
ar=0;
if(typeof(aF.onMessage)!=="undefined"){aF.onMessage(aE)
}break;
case"error":aC("Firing onError");
if(typeof(aF.onError)!=="undefined"){aF.onError(aE)
}break;
case"opening":aC("Firing onOpen");
delete l.closed;
if(typeof(aF.onOpen)!=="undefined"){aF.onOpen(aE)
}break;
case"messagePublished":aC("Firing onMessagePublished");
if(typeof(aF.onMessagePublished)!=="undefined"){aF.onMessagePublished(aE)
}break;
case"re-connecting":aC("Firing onReconnect");
if(typeof(aF.onReconnect)!=="undefined"){aF.onReconnect(l,aE)
}break;
case"closedByClient":aC("Firing onClientTimeout");
if(typeof(aF.onClientTimeout)!=="undefined"){aF.onClientTimeout(l)
}break;
case"re-opening":aC("Firing onReopen");
delete l.closed;
if(typeof(aF.onReopen)!=="undefined"){aF.onReopen(l,aE)
}break;
case"fail-to-reconnect":aC("Firing onFailureToReconnect");
if(typeof(aF.onFailureToReconnect)!=="undefined"){aF.onFailureToReconnect(l,aE)
}break;
case"unsubscribe":case"closed":var aD=typeof(l.closed)!=="undefined"?l.closed:false;
if(!aD){aC("Firing onClose");
if(typeof(aF.onClose)!=="undefined"){aF.onClose(aE)
}}else{aC("Closed but not firing onClose")
}l.closed=true;
break
}}function aa(aD){if(al.state!=="closed"){al.state="closed";
al.responseBody="";
al.messages=[];
al.status=!aD?501:200;
ae()
}}function ae(){var aF=function(aI,aJ){aJ(al)
};
if(aq==null&&M!=null){M(al.responseBody)
}l.reconnect=l.mrequest;
var aD=typeof(al.responseBody)==="string";
var aG=(aD&&l.trackMessageLength)?(al.messages.length>0?al.messages:[""]):new Array(al.responseBody);
for(var aE=0;
aE<aG.length;
aE++){if(aG.length>1&&aG[aE].length===0){continue
}al.responseBody=(aD)?c.trim(aG[aE]):aG[aE];
if(aq==null&&M!=null){M(al.responseBody)
}if((al.responseBody.length===0||(aD&&ag===al.responseBody))&&al.state==="messageReceived"){continue
}ah(al);
if(c.atmosphere.callbacks.length>0){if(s("debug")){c.atmosphere.debug("Invoking "+c.atmosphere.callbacks.length+" global callbacks: "+al.state)
}try{c.each(c.atmosphere.callbacks,aF)
}catch(aH){c.atmosphere.log(l.logLevel,["Callback exception"+aH])
}}if(typeof(l.callback)==="function"){if(s("debug")){c.atmosphere.debug("Invoking request callbacks")
}try{l.callback(al)
}catch(aH){c.atmosphere.log(l.logLevel,["Callback exception"+aH])
}}}}function E(aE,aD){if(al.partialMessage===""&&(aD.transport==="streaming")&&(aE.responseText.length>aD.maxStreamingLength)){return true
}return false
}function y(){if(l.enableProtocol&&!l.firstMessage){var aE="X-Atmosphere-Transport=close&X-Atmosphere-tracking-id="+l.uuid;
c.each(l.headers,function(aF,aH){var aG=c.isFunction(aH)?aH.call(this,l,l,al):aH;
if(aG!=null){aE+="&"+encodeURIComponent(aF)+"="+encodeURIComponent(aG)
}});
var aD=l.url.replace(/([?&])_=[^&]*/,aE);
aD=aD+(aD===l.url?(/\?/.test(l.url)?"&":"?")+aE:"");
if(l.connectTimeout>0){c.ajax({url:aD,async:l.closeAsync,timeout:l.connectTimeout,cache:false,crossDomain:l.enableXDR})
}else{c.ajax({url:aD,async:l.closeAsync,cache:false,crossDomain:l.enableXDR})
}}}function D(){if(l.reconnectId){clearTimeout(l.reconnectId);
delete l.reconnectId
}if(l.heartbeatTimer){clearTimeout(l.heartbeatTimer)
}l.reconnect=false;
aB=true;
al.request=l;
al.state="unsubscribe";
al.responseBody="";
al.status=408;
ae();
y();
g()
}function g(){al.partialMessage="";
if(l.id){clearTimeout(l.id)
}if(l.heartbeatTimer){clearTimeout(l.heartbeatTimer)
}if(l.reconnectId){clearTimeout(l.reconnectId);
delete l.reconnectId
}if(j!=null){j.close();
j=null
}if(T!=null){T.abort();
T=null
}if(v!=null){v.abort();
v=null
}if(ap!=null){if(ap.canSendMessage){ap.close()
}ap=null
}if(ac!=null){ac.close();
ac=null
}ad()
}function ad(){if(d!=null){clearInterval(u);
document.cookie=aA+"=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
d.signal("close",{reason:"",heir:!aB?N:(d.get("children")||[])[0]});
d.close()
}if(aq!=null){aq.close()
}}this.subscribe=function(aD){aj(aD);
W()
};
this.execute=function(){W()
};
this.invokeCallback=function(){ae()
};
this.close=function(){D()
};
this.disconnect=function(){y()
};
this.getUrl=function(){return l.url
};
this.push=function(aF,aE){if(aE!=null){var aD=l.dispatchUrl;
l.dispatchUrl=aE;
p(aF);
l.dispatchUrl=aD
}else{p(aF)
}};
this.getUUID=function(){return l.uuid
};
this.pushLocal=function(aD){ak(aD)
};
this.enableProtocol=function(aD){return l.enableProtocol
};
this.init=function(){af()
};
this.request=l;
this.response=al
},subscribe:function(d,g,f){if(typeof(g)==="function"){c.atmosphere.addCallback(g)
}if(typeof(d)!=="string"){f=d
}else{f.url=d
}c.atmosphere.uuid=((typeof(f)!=="undefined")&&typeof(f.uuid)!=="undefined")?f.uuid:0;
var e=new c.atmosphere.AtmosphereRequest(f);
e.execute();
c.atmosphere.requests[c.atmosphere.requests.length]=e;
return e
},addCallback:function(d){if(c.inArray(d,c.atmosphere.callbacks)===-1){c.atmosphere.callbacks.push(d)
}},removeCallback:function(e){var d=c.inArray(e,c.atmosphere.callbacks);
if(d!==-1){c.atmosphere.callbacks.splice(d,1)
}},unsubscribe:function(){if(c.atmosphere.requests.length>0){var d=[].concat(c.atmosphere.requests);
for(var f=0;
f<d.length;
f++){var e=d[f];
e.close();
clearTimeout(e.response.request.id);
if(e.heartbeatTimer){clearTimeout(e.heartbeatTimer)
}}}c.atmosphere.requests=[];
c.atmosphere.callbacks=[]
},unsubscribeUrl:function(e){var d=-1;
if(c.atmosphere.requests.length>0){for(var g=0;
g<c.atmosphere.requests.length;
g++){var f=c.atmosphere.requests[g];
if(f.getUrl()===e){f.close();
clearTimeout(f.response.request.id);
if(f.heartbeatTimer){clearTimeout(f.heartbeatTimer)
}d=g;
break
}}}if(d>=0){c.atmosphere.requests.splice(d,1)
}},publish:function(e){if(typeof(e.callback)==="function"){c.atmosphere.addCallback(e.callback)
}e.transport="polling";
var d=new c.atmosphere.AtmosphereRequest(e);
c.atmosphere.requests[c.atmosphere.requests.length]=d;
return d
},checkCORSSupport:function(){if(c.browser.msie&&!window.XDomainRequest&&+c.browser.version.split(".")[0]<11){return true
}else{if(c.browser.opera&&+c.browser.version.split(".")[0]<12){return true
}else{if(c.trim(navigator.userAgent).slice(0,16)==="KreaTVWebKit/531"){return true
}else{if(c.trim(navigator.userAgent).slice(-7).toLowerCase()==="kreatel"){return true
}}}}var d=navigator.userAgent.toLowerCase();
var e=d.indexOf("android")>-1;
if(e){return true
}return false
},S4:function(){return(((1+Math.random())*65536)|0).toString(16).substring(1)
},guid:function(){return(c.atmosphere.S4()+c.atmosphere.S4()+"-"+c.atmosphere.S4()+"-"+c.atmosphere.S4()+"-"+c.atmosphere.S4()+"-"+c.atmosphere.S4()+c.atmosphere.S4()+c.atmosphere.S4())
},prepareURL:function(e){var f=c.now();
var d=e.replace(/([?&])_=[^&]*/,"$1_="+f);
return d+(d===e?(/\?/.test(e)?"&":"?")+"_="+f:"")
},param:function(d){return c.param(d,c.ajaxSettings.traditional)
},supportStorage:function(){var f=window.localStorage;
if(f){try{f.setItem("t","t");
f.removeItem("t");
return window.StorageEvent&&!c.browser.msie&&!(c.browser.mozilla&&c.browser.version.split(".")[0]==="1")
}catch(d){}}return false
},iterate:function(f,e){var g;
e=e||0;
(function d(){g=setTimeout(function(){if(f()===false){return
}d()
},e)
})();
return function(){clearTimeout(g)
}
},log:function(f,e){if(window.console){var d=window.console[f];
if(typeof d==="function"){d.apply(window.console,e)
}}},warn:function(){c.atmosphere.log("warn",arguments)
},info:function(){c.atmosphere.log("info",arguments)
},debug:function(){c.atmosphere.log("debug",arguments)
},error:function(){c.atmosphere.log("error",arguments)
},isBinary:function(d){return/^\[object\s(?:Blob|ArrayBuffer|.+Array)\]$/.test(Object.prototype.toString.call(d))
}};
(function(){var d,e;
c.uaMatch=function(g){g=g.toLowerCase();
var f=/(chrome)[ \/]([\w.]+)/.exec(g)||/(opera)(?:.*version|)[ \/]([\w.]+)/.exec(g)||/(msie) ([\w.]+)/.exec(g)||/(trident)(?:.*? rv:([\w.]+)|)/.exec(g)||g.indexOf("android")<0&&/version\/(.+) (safari)/.exec(g)||g.indexOf("compatible")<0&&/(mozilla)(?:.*? rv:([\w.]+)|)/.exec(g)||[];
if(f[2]==="safari"){f[2]=f[1];
f[1]="safari"
}return{browser:f[1]||"",version:f[2]||"0"}
};
d=c.uaMatch(navigator.userAgent);
e={};
if(d.browser){e[d.browser]=true;
e.version=d.version;
e.vmajor=e.version.split(".")[0]
}if(e.trident){e.msie=true
}c.browser=e;
c.sub=function(){function f(i,j){return new f.fn.init(i,j)
}c.extend(true,f,this);
f.superclass=this;
f.fn=f.prototype=this();
f.fn.constructor=f;
f.sub=this.sub;
f.fn.init=function h(i,j){if(j&&j instanceof c&&!(j instanceof f)){j=f(j)
}return c.fn.init.call(this,i,j,g)
};
f.fn.init.prototype=f.fn;
var g=f(document);
return f
}
})();
(function(h){var j=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,g={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"};
function d(f){return'"'+f.replace(j,function(k){var l=g[k];
return typeof l==="string"?l:"\\u"+("0000"+k.charCodeAt(0).toString(16)).slice(-4)
})+'"'
}function e(f){return f<10?"0"+f:f
}function i(o,n){var m,l,f,k,q=n[o],p=typeof q;
if(q&&typeof q==="object"&&typeof q.toJSON==="function"){q=q.toJSON(o);
p=typeof q
}switch(p){case"string":return d(q);
case"number":return isFinite(q)?String(q):"null";
case"boolean":return String(q);
case"object":if(!q){return"null"
}switch(Object.prototype.toString.call(q)){case"[object Date]":return isFinite(q.valueOf())?'"'+q.getUTCFullYear()+"-"+e(q.getUTCMonth()+1)+"-"+e(q.getUTCDate())+"T"+e(q.getUTCHours())+":"+e(q.getUTCMinutes())+":"+e(q.getUTCSeconds())+'Z"':"null";
case"[object Array]":f=q.length;
k=[];
for(m=0;
m<f;
m++){k.push(i(m,q)||"null")
}return"["+k.join(",")+"]";
default:k=[];
for(m in q){if(Object.prototype.hasOwnProperty.call(q,m)){l=i(m,q);
if(l){k.push(d(m)+":"+l)
}}}return"{"+k.join(",")+"}"
}}}h.stringifyJSON=function(f){if(window.JSON&&window.JSON.stringify){return window.JSON.stringify(f)
}return i("",{"":f})
}
}(c))
}));