	// test's variables

	// for test using eXoPlatform webdav
	var webdav = new Webdav('localhost','8080');
	webdav.setSynchronous();
	webdav.username = 'root';
	webdav.password = 'exo';
	var test_folder = '/rest/jcr/repository/collaboration/тест';
/*	
	// for testing by using Apache webdav
	var webdav = new Webdav('localhost','80');
	webdav.setSynchronous();
	webdav.username = 'wampp';
	webdav.password = 'xampp';
	var test_folder = '/webdav/тест';
*/

	/**
	 * Serialize an XML Document or Element and return it as a string.
	 */
	function XMLtoString(node) {
		if (typeof node != 'object') return node; 
	    if (typeof XMLSerializer != "undefined")
	        return decodeURI( (new XMLSerializer()).serializeToString(node) );
	    else if (node.xml) return node.xml;
	    else throw "XML.serialize is not supported or can't serialize " + node;
	};