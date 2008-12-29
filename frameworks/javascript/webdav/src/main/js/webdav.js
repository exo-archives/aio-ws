/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>
*/

/**
 * @classDescription Client library of WebDAV
 * Created by The eXo Platform SAS.
 * @author: <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $ Dec 09, 2008
 * @copyright Copyright (C) 2003-2008 eXo Platform SAS.
 * @license http://www.gnu.org/licenses/ GNU General Public License
 * @constructor
 * @param {String} host - host where the WebDAV folder is, e.g. localhost
 * @param {String} port - port where the WebDAV folder is, e.g. 8080
 */
function Webdav(host, port) {
	if (port) {
		port = ':' + port
	}
	/**
	 * url
	 */
	this.url = 'http://' + host + port;
	this.request = null;
	
	/**
	 * mode of request processing: if asynchronous = true then 'mode of processing = asynchronous', else : 'synchronous'.
	 */ 
	this.asynchronous = true;
	
	/**
	 * username which will be used in the header 'Authorization' of XMLHttp request
	 */
	this.username = '';
	
	/**
	 * password which will be used in the header 'Authorization' of XMLHttp request if username would not be empty 
	 */
	this.password = '';
}

/**
 * set synchronous mode of XMLHttp interchange
 */
Webdav.prototype.setSynchronous = function() {
	this.asynchronous = false;
}

/**
 * defines if this mode of XMLHttp interchange is asynchronous
 */
Webdav.prototype.isAsynchronous = function() {
	return this.asynchronous;
}

/**
 * sets an asynchronous mode of XMLHttp interchange
 */
Webdav.prototype.setAsynchronous = function () {
	this.asynchronous = true;
}

/**
 * returns XMLHttpRequest object
 * @private
 */
Webdav.prototype.getRequest = function () {
	// define the Ajax library 
	try {
	 	// Firefox, Opera 8.0+, Safari
	 	return new XMLHttpRequest();
	}
	catch (e) {
	 	//Internet Explorer
	 	try {
	  		return new ActiveXObject("Msxml2.XMLHTTP");
	  	}
	 	catch (e) {
	  		return new ActiveXObject("Microsoft.XMLHTTP");
	  	}
	}
	
	alert ("Browser does not support HTTP Request");
	return null;
}

/**
 * opens XMLHttp request and sets its header 'Authorization' in case of this.username is not empty
 * @private
 * @param {String} name - the name of webdav method, e.g. "PROPFIND"
 * @param {Function} client - client side handler-method of asynchronous process 
 * @param {String} path - path, e.g. '/foo/example.txt' (on default = '')
 * @param {Object} path - path, e.g. '/foo/example.txt' (on default = '')
 * @return {Object} request
 */
Webdav.prototype.openRequest = function(handler, method, path, additionalHeaders) {
    var request = this.getRequest();
	if (request === null) {
		return null;
	}

   	/* prepare a request */
    request.onreadystatechange = this.wrapperHandler(handler, request);
	var url = encodeURI(this.url + path);

	request.open(method, url, this.asynchronous);

    // refuse all encoding, since the browsers don't seem to support it...
    request.setRequestHeader('Accept-Encoding', ' ');

	// set header for authorization
	if (this.username) {
		var auth = make_base_auth(this.username, this.password);
		request.setRequestHeader('Authorization', auth);
	}
	
	// set additional request headers
	if ( additionalHeaders ) {
		this.setAdditionalHeaders(request, additionalHeaders);
	}

	return request;
}

/**
 * wraps the handler with a callback
 * @private 
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {Object} request - XMLHttp request object
 * @return {Object} Handler object
 */
Webdav.prototype.wrapperHandler = function(handler, request) {
    var self = this;
    function Handler() {
		handler = {
			onSuccess: handler.onSuccess || function(){},
			onError: handler.onError || function(){},
			onComplete: handler.onComplete || function(){}
		}
        this.execute = function() {
            if (request.readyState == 4) {
				var result = self.parseResponse(request);
				request = null;
				
				// Check to see if the request was successful
				if ( self.isSuccess(result.status) ) {
					handler.onSuccess.call('', result);
				} else {
					handler.onError.call('', result);
				}
                
				// Call the completion callback
				handler.onComplete.call('', result);
            };
        };
    };
    return (new Handler().execute);
};

/**
 * parses the request and returns result-object
 * @private 
 * @param {Object} request - XMLHttpRequest object
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise
 */
Webdav.prototype.parseResponse = function(request){
	// define source of response content
	if ( request.getResponseHeader("content-type") &&
			request.getResponseHeader("content-type").indexOf("xml") >= 0 )
	{
		var content = request.responseXML;					
	} else {
		var content = request.responseText;					
	}
	
	var result = {
		status: request.status,
		statusstring: this.STATUS_CODES[request.status.toString()],
		headers: this.parseHeaders(request.getAllResponseHeaders()),
		content: content
	};
	
	return result; 
}

/**
 * parses of XMLHttpRequest.getAllResponseHeaders()
 * @private
 * @param {String} headers - result of XMLHttpRequest.getAllResponseHeaders()
 * @return {Object} hash of Headers (e.g. header "Content-Type: test/plain" => headers['Content-Type'] = 'test/plain')
 */
Webdav.prototype.parseHeaders = function(headers) {
	var headerPattern = /([^:]+):.(.+)/;
	var lines = headers.split('\n');
    var hash = {}; 
	var name = ''; 
	var value = '';

    for (var i = 0; i < lines.length; i++) {
        var header = lines[i].match(headerPattern);
		if ( header != null && 
				typeof(header[1]) != 'undefined') 
		{
			name = header[1];
			if (typeof(header[2]) != 'undefined') {
				value = header[2];
			} else {
				value = '';
			}
    	    hash[name] = value;
		}
    };
    
    return hash;
};

/**
 * sets additional headers of response
 * @param {Object} request
 * @param {Object} additionalHeaders
 * @private
 */
Webdav.prototype.setAdditionalHeaders = function (request, additionalHeaders) {
    for( var headerName in additionalHeaders ) {
        if (!additionalHeaders.hasOwnProperty(headerName)) continue;
        var headerValue = additionalHeaders[headerName];
		request.setRequestHeader(headerName, headerValue);
    }
}

/**
 * determines if HTTP response is success
 * @private 
 * @param {Number} status of response
 * @return {Boolean} True if response is success, or False otherwise 
 */
Webdav.prototype.isSuccess = function(status){
	return (status >= 200 && status < 300 ) ||
				  status == 304;
}

/**
 * Array with statuses of codes
 * @private
 */
Webdav.prototype.STATUS_CODES = {
    '100': 'Continue',
    '101': 'Switching Protocols',
    '102': 'Processing',
    '200': 'OK',
    '201': 'Created',
    '202': 'Accepted',
    '203': 'None-Authoritive Information',
    '204': 'No Content',
    '1223': 'No Content',
    '205': 'Reset Content',
    '206': 'Partial Content',
    '207': 'Multi-Status',
    '300': 'Multiple Choices',
    '301': 'Moved Permanently',
    '302': 'Found',
    '303': 'See Other',
    '304': 'Not Modified',
    '305': 'Use Proxy',
    '307': 'Redirect',
    '400': 'Bad Request',
    '401': 'Unauthorized',
    '402': 'Payment Required',
    '403': 'Forbidden',
    '404': 'Not Found',
    '405': 'Method Not Allowed',
    '406': 'Not Acceptable',
    '407': 'Proxy Authentication Required',
    '408': 'Request Time-out',
    '409': 'Conflict',
    '410': 'Gone',
    '411': 'Length Required',
    '412': 'Precondition Failed',
    '413': 'Request Entity Too Large',
    '414': 'Request-URI Too Large',
    '415': 'Unsupported Media Type',
    '416': 'Requested range not satisfiable',
    '417': 'Expectation Failed',
    '422': 'Unprocessable Entity',
    '423': 'Locked',
    '424': 'Failed Dependency',
    '500': 'Internal Server Error',
    '501': 'Not Implemented',
    '502': 'Bad Gateway',
    '503': 'Service Unavailable',
    '504': 'Gateway Time-out',
    '505': 'HTTP Version not supported',
    '507': 'Insufficient Storage'
};

/**
 * returns string "Basic " + Base64 encoded pair "user: password"
 * @private
 * @param {String} user
 * @param {String} password
 */
function make_base_auth(user, password) {
  var tok = user + ':' + password;
  var hash = Base64.encode(tok);
  return "Basic " + hash;
}

/**
*  Base64 encode / decode
*  http://www.webtoolkit.info/
* @private
**/

var Base64 = {

    // private property
    _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

    // public method for encoding
    encode : function (input) {
        var output = "";
        var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        var i = 0;

        input = Base64._utf8_encode(input);

        while (i < input.length) {

            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
            this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
            this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

        }

        return output;
    },

    // public method for decoding
    decode : function (input) {
        var output = "";
        var chr1, chr2, chr3;
        var enc1, enc2, enc3, enc4;
        var i = 0;

        input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

        while (i < input.length) {

            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 != 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 != 64) {
                output = output + String.fromCharCode(chr3);
            }

        }

        output = Base64._utf8_decode(output);

        return output;

    },

    // private method for UTF-8 encoding
    _utf8_encode : function (string) {
        string = string.replace(/\r\n/g,"\n");
        var utftext = "";

        for (var n = 0; n < string.length; n++) {

            var c = string.charCodeAt(n);

            if (c < 128) {
                utftext += String.fromCharCode(c);
            }
            else if((c > 127) && (c < 2048)) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }

        }

        return utftext;
    },

    // private method for UTF-8 decoding
    _utf8_decode : function (utftext) {
        var string = "";
        var i = 0;
        var c = c1 = c2 = 0;

        while ( i < utftext.length ) {

            c = utftext.charCodeAt(i);

            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            }
            else if((c > 191) && (c < 224)) {
                c2 = utftext.charCodeAt(i+1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            }
            else {
                c2 = utftext.charCodeAt(i+1);
                c3 = utftext.charCodeAt(i+2);
                string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
                i += 3;
            }

        }

        return string;
    }
}


/**
 * the constructor of the user-defined WebDAV request.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.method - name of requested method; must be defined. 
 * @param {Object} options.headers - Hash Object, which have to consist the pairs like options.headers[header_name] = header_value, and will be used as the headers of request, e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}.
 * @param {String} options.body - string, which will be placed in the body of request.   
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.ExtensionMethod = function (handler, path, options) {
	if (typeof(options) == 'undefined' || ! options.method ) return;
	
	var options = {
			method: options.method,
			headers: options.headers || {},
			body: options.body || ''			
		};
	
	var request = this.openRequest(handler, options.method, path, options.headers);
	request.send( options.body );

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}


// -------- WebDAV Modifications to HTTP --------

/**
 * performs a GET method - to retrieve the content of a resource 
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'} 
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise */
Webdav.prototype.GET = function(handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = { 
		additional_headers: options.additional_headers || {}
	};
	
    var request = this.openRequest(handler, 'GET', path || '', options.additional_headers);
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
};

/**
 * performs a PUT method - to save the content of a resource to the server
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path  (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.content - content of resource (on default = '')
 * @param {String} options.content_type - MIME type of content + encoding language (e.g. 'text/plain; charset=UTF-8')
 * @param {String} options.locktoken - the opaque lock token, as can be retrieved from 'Lock-Token' header of the LOCK request (on default = '')
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}   
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.PUT = function(handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			content: options.content || '',
			content_type: options.content_type || 'text/plain; charset=UTF-8',
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || '' 						
		};

    var request = this.openRequest(handler, 'PUT', path || '', options.additional_headers);
    request.setRequestHeader("Content-type", options.content_type);	
    if (options.locktoken) {
        request.setRequestHeader('If', '(<' + options.locktoken + '>)');
    };
    request.send(options.content);

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
};

/**
 * performs a DELETE method - to remove a resource or collection. 
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path  (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.locktoken - the opaque lock token, as can be retrieved from 'Lock-Token' header of the LOCK request (on default = ''). 
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise */
Webdav.prototype.DELETE = function(handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = { 
		locktoken: options.locktoken || '',
		additional_headers: options.additional_headers || {}
	};

    var request = this.openRequest(handler, 'DELETE', path || '', options.additional_headers);
    if (options.locktoken) {
        request.setRequestHeader('If', '(<' + options.locktoken + '>)');
    };
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
};

/**
 * performs an OPTIONS method - to return the HTTP methods that the server supports for specified URL. 
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path  (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.locktoken - the opaque lock token, as can be retrieved from 'Lock-Token' header of the LOCK request (on default = ''). 
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} b>result.content</b> = XMLHttp response.responseText otherwise 
*/
Webdav.prototype.OPTIONS = function(handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = { 
		additional_headers: options.additional_headers || {}
	};

    var request = this.openRequest(handler, 'OPTIONS', path || '', options.additional_headers);
    request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
};

/**
 * performs a MKCOL method - to create a collection
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.locktoken - the opaque lock token, as can be retrieved from 'Lock-Token' header of the LOCK request (on default = '')
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.MKCOL = function(handler, path, options) {
	if (typeof(options) == 'undefined') options = {};
	
	var options = { 
		locktoken: options.locktoken || '',
		additional_headers: options.additional_headers || {}
	};


	var request = this.openRequest(handler, 'MKCOL', path || '', options.additional_headers);
    if (options.locktoken) {
        request.setRequestHeader('If', '(<' + options.locktoken + '>)');
    };
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	} 
}

/**
 * performs a COPY method - to copy a resource from one URI to another. 
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path  (on default = '')
 * @param {Object} options - collection of options:
 * @param {0|1|Infinity} options.depth - depth
 * @param {String} options.destination - the relative path to copy the resource to (on default = '').
 * @param {Boolean} options.overwrite - whether or not to fail when the resource already exists (on default = true).
 * @param {String} options.locktoken - the opaque lock token, as can be retrieved from 'Lock-Token' header of the LOCK request (on default = ''). 
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise */
Webdav.prototype.COPY = function(handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			depth: options.depth,
			destination: options.destination || '',
			overwrite: ! options.hasOwnProperty('overwrite') || options.overwrite,
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || {}			
		};
		
    var request = this.openRequest(handler, 'COPY', path || '', options.additional_headers);
    if ( typeof(options.depth) != 'undefined' ) request.setRequestHeader('Depth', options.depth);
	request.setRequestHeader('Destination', encodeURI(this.url + options.destination));
    if (options.overwrite) {
        request.setRequestHeader('Overwrite', 'T');
    } else {
        request.setRequestHeader('Overwrite', 'F');		
	};
    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
};

/**
 * performs a MOVE method - to move a resource from one URI to another. 
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path  (on default = '')
 * @param {Object} options - collection of options:
 * @param {0|1|Infinity} options.depth - depth
 * @param {String} options.destination - the relative path to move the resource to (on default = '').
 * @param {Boolean} options.overwrite - whether or not to fail when the resource already exists (on default = true).
 * @param {String} options.locktoken - the opaque lock token, as can be retrieved from 'Lock-Token' header of the LOCK request (on default = ''). 
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise */
Webdav.prototype.MOVE = function(handler, path, options) {
	if (typeof(options) == 'undefined') options = {};
	
	var options = {
			depth: options.depth,
			destination: options.destination || '',
			overwrite: ! options.hasOwnProperty('overwrite') || options.overwrite,
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || {}			
		};
		
    var request = this.openRequest(handler, 'MOVE', path || '', options.additional_headers);
    if ( typeof(options.depth) != 'undefined' ) request.setRequestHeader('Depth', options.depth);
	request.setRequestHeader('Destination', encodeURI(this.url + options.destination));
    if (options.overwrite) {
        request.setRequestHeader('Overwrite', 'T');
    } else {
        request.setRequestHeader('Overwrite', 'F');		
	};
    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
};

/**
 * performs a HEAD method - to ask for the response identical to the one that would correspond to a HEAD request, but without the response body. This is useful for retrieving meta-information written in response headers, without having to transport the entire content 
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'} 
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise */
Webdav.prototype.HEAD = function(handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = { 
		additional_headers: options.additional_headers || {}
	};
	
    var request = this.openRequest(handler, 'HEAD', path || '', options.additional_headers);
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
};

// -------- WebDAV Property Operations --------

/**
 * performs a PROPFIND method - to retrieve properties, stored as XML, from a resource. It is also overloaded to allow one to retrieve the collection structure (a.k.a. directory hierarchy) of a remote system.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {0|1|Infinity} options.depth - depth
 * @param {String} options.operation - may be 'allprop', 'propname', 'specified_properties' (on default = 'allprop') 
 * @param {Array} options.properties_list - flat list of string of target properties (e.g. list of properties Array[0]='getetag', Array[1]='getlastmodified') (on default = '')
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.PROPFIND = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			depth: options.depth,
			operation: options.operation || 'allprop',
			properties_list: options.properties_list || new Array(),
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'PROPFIND', path, options.additional_headers);
	
	var xml = '<?xml version="1.0" encoding="UTF-8" ?>\n';
	switch(options.operation) {
	case 'propname':
		xml += '<propfind xmlns="DAV:">\n';
	    xml += '	<propname/>\n';
        xml += '</propfind>\n';
		break;
	case 'properties_list':
		xml += '<D:propfind xmlns:D="DAV:">\n';
		xml += '	<D:prop>\n';
		for (var i = 0; i < options.properties_list.length; i++) {
			xml += '<D:' + options.properties_list[i].replace(/\s/g, "") + '/>\n';
		}
		xml += '	</D:prop>\n';
        xml += '</D:propfind>\n';
		break;
	case 'allprop':
	default:
		xml += '<D:propfind xmlns:D="DAV:">\n';
	    xml += '	<D:allprop/>\n';
        xml += '</D:propfind>\n';
		break;
	}

    if ( typeof(options.depth) != 'undefined' ) request.setRequestHeader('Depth', options.depth);
    request.setRequestHeader('Content-type', 'text/xml; charset=UTF-8');
	request.send( xml );

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}

/**
 * performs a PROPPATCH method - to change and delete multiple properties on a resource in a single atomic act.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {Object} options.set_properties_list - collection of pairs "options.set_properties_list[property_name] = property_value" which have to be set (e.g. options.set_properties_list['publish']='true', options.set_properties_list['author']='noname') (on default = {})
 * @param {Array} options.remove_properties_list - flat array of properties' names which have to be removed (e.g. options.remove_properties_list[0]='publish', options.remove_properties_list[0]='author') (on default = [])
 * @param {String} options.locktoken - value of 'if: <locktoken>' request header  
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.PROPPATCH = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			set_properties_list: options.set_properties_list || {},
			remove_properties_list: options.remove_properties_list || new Array(),
			locktoken: options.locktoken || '',			
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'PROPPATCH', path, options.additional_headers);
	
	var xml = '<?xml version="1.0" encoding="UTF-8" ?>\n';
	xml += '<D:propertyupdate xmlns:D="DAV:">\n';
	
	// properties to set
	xml += '	<D:set>\n';
	xml += '		<D:prop>\n';
    for( var propName in options.set_properties_list ) {
        if (!options.set_properties_list.hasOwnProperty(propName)) continue;
        var propValue = options.set_properties_list[propName];
		xml += '			<D:' + propName.replace(/\s/g, "") + '>\n';
		xml += '				' + propValue + '\n';
		xml += '			</D:' + propName.replace(/\s/g, "") + '>\n';
    }
	xml += '		</D:prop>\n';
	xml += '	</D:set>\n';

	// properties to remove	
	xml += '	<D:remove>\n';
	xml += '		<D:prop>\n';
	for (var i = 0; i < options.remove_properties_list.length; i++) {
		xml += '<D:' + options.remove_properties_list[i].replace(/\s/g, "") + '/>\n';
	}
	xml += '		</D:prop>\n';
	xml += '	</D:remove>\n';			

    xml += '</D:propertyupdate>\n';

    request.setRequestHeader('Content-type', 'text/xml; charset=UTF-8');

    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };

	request.send( xml );

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}


// -------- WebDAV Lock Operations --------

/**
 * performs a LOCK method - to put a lock on a resource.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.owner - a URL to identify the owner of the lock to be set
 * @param {exclusive|shared} options.scope - the scope of the lock, can be 'exclusive' or 'shared'
 * @param {0|1|Infinity} options.depth - can be used to lock (part of) a branch (use 'infinity' as value) or just a single target (default)
 * @param {Number} options.timeout - set the timeout in seconds (on default = 'Infinite')
 * @param {String} options.locktoken - value of 'if: <locktoken>' request header 
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.LOCK = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
            owner: options.owner || '',
            scope: options.scope || 'exclusive',
            depth: options.depth,
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || {}			
		};

    if (!options.timeout) {
        options.timeout = "Infinite, Second-60480000";
    } else {
        options.timeout = 'Second-' + options.timeout;
    }

	var request = this.openRequest(handler, 'LOCK', path, options.additional_headers);

	var xml = '<?xml version="1.0" encoding="UTF-8"?>\n'
		+ '<D:lockinfo xmlns:D="DAV:">\n'
        + '	<D:lockscope><D:' + options.scope + ' /></D:lockscope>\n'
        + '	<D:locktype><D:write/></D:locktype>\n'
        + '	<D:owner>\n'
		+ '		<D:href>\n' 
        +   		encodeURI(options.owner) + '\n' 
        + '		</D:href>\n'
		+ '	</D:owner>\n'
        + '</D:lockinfo>\n';
	
    if ( typeof(options.depth) != 'undefined' ) request.setRequestHeader('Depth', options.depth);
    request.setRequestHeader('Content-type', 'text/xml; charset=UTF-8');
    request.setRequestHeader('Timeout', options.timeout);

    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };

	request.send( xml );
	
	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
};

/**
 * performs an UNLOCK method - to remove a lock from a resource.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.locktoken - the opaque lock token, as can be retrieved from 'Lock-Token' header of the LOCK request (on default = '')
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.UNLOCK = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};
	
	var options = { 
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || {}
	};

	var request = this.openRequest(handler, 'UNLOCK', path, options.additional_headers);

	request.setRequestHeader("Lock-Token", '<' + options.locktoken + '>');
	request.send('');
	
	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}

// -------- WebDAV Versioning Extension Operations --------

/**
 * performs a VERSIONCONTROL method - can be used to create a new version-controlled resource for an existing version history. This allows the creation of version-controlled resources for the same version history in multiple workspaces.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {Array} options.version_href_list - flat list of href (e.g. list of properties Array[0]='http://repo.webdav.org/his/12/ver/V3') (on default = [])
 * @param {String} options.locktoken - value of 'if: <locktoken>' request header
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.VERSIONCONTROL = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			version_href_list: options.version_href_list || [],
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'VERSION-CONTROL', path, options.additional_headers);
	
	var xml = '<?xml version="1.0" encoding="UTF-8" ?>\n';
	xml += '<D:version-control xmlns:D="DAV:">\n';
	for (var i = 0; i < options.version_href_list.length; i++) {
		xml += '	<D:version>\n';
		xml += '		<D:href>\n';
		xml += '			' + options.version_href_list[i] + '\n';
		xml += '		</D:href>\n';
		xml += '	</D:version>\n';
	}	
    xml += '</D:version-control>\n';

    request.setRequestHeader('Content-type', 'text/xml; charset=UTF-8');

    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };
	
	request.send( xml );

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}

/**
 * performs a CHECKOUT method - can be applied to a checked-in version-controlled resource to allow modifications to the content and dead properties of that version-controlled resource.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.locktoken - value of 'if: <locktoken>' request header
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.CHECKOUT = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'CHECKOUT', path, options.additional_headers);

    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };
	
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}

/**
 * performs a CHECKIN method - can be applied to a checked-out version-controlled resource to produce a new version whose content and dead properties are copied from the checked-out resource.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.locktoken - value of 'if: <locktoken>' request header
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.CHECKIN = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'CHECKIN', path, options.additional_headers);

    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };
	
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}

/**
 * performs an UNCHECKOUT method - can be applied to a checked-out version-controlled resource to cancel the CHECKOUT and restore the pre-CHECKOUT state of the version-controlled resource.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.locktoken - value of 'if: <locktoken>' request header
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.UNCHECKOUT = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			locktoken: options.locktoken || '',
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'UNCHECKOUT', path, options.additional_headers);

    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };
	
	request.send('');

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}

/**
 * performs a REPORT method - an extensible mechanism for obtaining information about a resource.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {0|1|Infinity} options.depth - depth
 * @param {String} options.type - may be only 'version-tree' (on default = 'version-tree') 
 * @param {Array} options.properties_list - flat list of string of target properties of version-tree report (e.g. list of properties Array[0]='getetag', Array[1]='getlastmodified') (on default = [])
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.REPORT = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			depth: options.depth,
			type: options.type || 'version-tree',
			properties_list: options.properties_list || new Array(),
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'REPORT', path, options.additional_headers);
	
	var xml = '<?xml version="1.0" encoding="UTF-8" ?>\n';
	switch(options.type) {
	case 'version-tree':
	default:
		xml += '<D:version-tree xmlns:D="DAV:">\n';
		xml += '	<D:prop>\n';
		for (var i = 0; i < options.properties_list.length; i++) {
			xml += '<D:' + options.properties_list[i].replace(/\s/g, "") + '/>\n';
		}
		xml += '	</D:prop>\n';
        xml += '</D:version-tree>\n';
		break;
	}

    if ( typeof(options.depth) != 'undefined' ) request.setRequestHeader('Depth', options.depth);
    request.setRequestHeader('Content-type', 'text/xml; charset=UTF-8');
	request.send( xml );

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}

/**
 * performs an ORDERPATCH method - is used to change the ordering semantics of a collection, to change the order of the collection's members in the ordering, or both.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.ordering_type_href - value of the ordering-type href property (e.g. http://example.com/col/example.txt) (on default = '') 
 * @param {Array} options.order_members - 3-dimensional array of triples:
 * @param {String} options.order_members[i]['segment'] = segment name (e.g. first.txt),
 * @param {String} options.order_members[i]['position'] = one of the ('first' | 'last' | 'before' | 'after'),
 * @param {String} options.order_members[i]['parent_segment'] = parent segment name in case of the options.order_members[i]['position'] = 'before' or 'after' 
 * @param {String} options.locktoken - value of 'if: <locktoken>' request header 
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.ORDERPATCH = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			ordering_type_href: options.ordering_type_href || '',
			order_members: options.order_members || new Array(),
 			locktoken:  options.locktoken || '',			
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'ORDERPATCH', path, options.additional_headers);
	
	var xml = '<?xml version="1.0" encoding="UTF-8" ?>\n';
	xml += '<D:orderpatch xmlns:D="DAV:">\n';
	if ( options.ordering_type_href ) {
		xml += '	<D:ordering-type>\n';
		xml += '		<D:href>' + encodeURI(options.ordering_type_href) + '</D:href>\n';
      	xml += '	</D:ordering-type>\n';
	}
	for (var i = 0; i < options.order_members.length; i++ ) {
		xml += '	<D:order-member>\n';
		xml += '		<D:segment>' + encodeURI(options.order_members[i]['segment']) + '</D:segment>\n';	
		switch(options.order_members[i]['position']) {
		// for segment position 'first' or 'last'
		case 'first':
		case 'last':		
			xml += '		<D:position>' + options.order_members[i]['position'] + '</D:position>\n';
			break;
		// for segment position 'before' or 'after'
		case 'before':
		case 'after':		
			xml += '		<D:position>\n';
			xml += '			<D:' + options.order_members[i]['position'] + '>\n';
			xml += '				<D:segment>' + encodeURI(options.order_members[i]['parent_segment']) + '</D:segment>\n'; 
			xml += '			</D:' + options.order_members[i]['position'] + '>\n';
			xml += '		</D:position>\n';
			break;
		}
        xml += '	</D:order-member>\n';
	}
    xml += '</D:orderpatch>\n';
		
    request.setRequestHeader('Content-type', 'text/xml; charset=UTF-8');

    if (options.locktoken) {
        request.setRequestHeader('If', '<' + options.locktoken + '>');
    };

	request.send( xml );

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}


// -------- WebDAV SEARCH --------

/**
 * performs a SEARCH method - a lightweight search method to transport queries and result sets that allows clients to make use of server-side search facilities retrieve properties, stored as XML, from a resource.
 * @param {Object} handler - functions to call when the request either succeeds or fails, and completes:
 * @param {Function} handler.onSuccess - will call if the request succeeds
 * @param {Function} handler.onError - will call if the request fails
 * @param {Function} handler.onComplete - will call if the request completes
 * @param {String} path (on default = '')
 * @param {Object} options - collection of options:
 * @param {String} options.search_language - language of search; can be 'sql' or 'xpath' (default value 'xpath').
 * @param {String} options.search_request - search request which syntax, 'sql' or 'xpath', depends on value of 'options.search_language'; further information about 'sql' language at http://msdn.microsoft.com/en-us/library/ms965954.aspx. 
 * @param {Object} options.additional_headers - e.g. {If-Match: '10880-22388', ETag: '870be-8f0-39ee6a4d'}  
 * @return {Object} result :
 * <br />- {Number} <b>result.status</b> = status of XMLHttp response 
 * <br />- {Number} <b>result.statusstring</b> -  an explanation of status 
 * <br />- {Object} <b>result.headers</b> - hash of request.getAllResponseHeaders() (e.g. header "Content-Type: test/plain" => result.headers['Content-Type'] = 'test/plain')
 * <br />- {Object} <b>result.content</b> = XMLHttp response.responseXML if header['Content-Type'] consists 'xml'
 * <br />- {String} <b>result.content</b> = XMLHttp response.responseText otherwise 
 */
Webdav.prototype.SEARCH = function (handler, path, options) {
	if (typeof(options) == 'undefined') options = {};

	var options = {
			search_language: options.search_language || 'xpath',
			search_request: options.search_request || '',
			additional_headers: options.additional_headers || {}			
		};
	
	var request = this.openRequest(handler, 'SEARCH', path, options.additional_headers);
	
	var xml = '<?xml version="1.0" encoding="UTF-8" ?>\n';
		xml += '<D:searchrequest xmlns:D="DAV:">\n';

	switch(options.search_language) {
		case 'sql':
			xml += '    <D:sql>\n';
	       	xml += '    	' + options.search_request + '\n';
	    	xml += '    </D:sql>\n';
			break;
		case 'xpath':
		default:
			xml += '    <D:xpath>\n';
	       	xml += '    	' + options.search_request + '\n';
	    	xml += '    </D:xpath>\n';
			break;
	}
	xml += '</D:searchrequest>\n';

    request.setRequestHeader('Content-type', 'text/xml; charset=UTF-8');
	request.send( xml );

	if ( ! this.isAsynchronous() ) {	
		var result = this.parseResponse(request);
		request = null;
		return result;
	}
}