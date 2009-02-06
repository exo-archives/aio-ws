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

// start FireUnit tests library "exowebdavtests" ================================================================             

// JS Framework for the asynchronous testing
  function testSuite(){}

  // prepare to test
  testSuite.prototype.start = function(webdavProvider, test_folder) {
    // define initial values of properties
    this.test_folder = test_folder;
    this.getWebdavObject = webdavProvider;
    this.defaultTimeout = 200; //millisec
    this.tests = [];

    // define test plan "tests"
    // -- Set Up
    this.addTest('SetUpTearDown', 'SetUp', this.defaultTimeout);    

    // -- Modification Of Http Methods Tests
    this.addTest('testOPTIONS', 'optionsTest', this.defaultTimeout);
    this.addTest('testMKCOL', 'createCollectionTest', this.defaultTimeout); // to create collection 'приклад'
    this.addTest('testPUT', 'createResourceTest', this.defaultTimeout); // to create resource 'приклад/приклад.txt'
    this.addTest('testGET', 'getResourceTest', this.defaultTimeout * 4);
    this.addTest('testHEAD', 'headResourceTest', 21000); // Error with response time of request HEAD to the resource
    this.addTest('testCOPY', 'copyResourceTest', this.defaultTimeout * 2);
    this.addTest('testCOPY', 'copyCollectionTest', this.defaultTimeout);
    this.addTest('testMOVE', 'moveResourceTest', this.defaultTimeout);
    this.addTest('testMOVE', 'moveCollectionTest', this.defaultTimeout * 3);
    this.addTest('testExtensionMethod', 'MKCOL', this.defaultTimeout);
    this.addTest('testExtensionMethod', 'PUT', this.defaultTimeout);
    this.addTest('testExtensionMethod', 'GET', this.defaultTimeout * 4);
    this.addTest('testExtensionMethod', 'DELETE', this.defaultTimeout);   
    this.addTest('testDELETE', 'deleteResourceTest', this.defaultTimeout); // to remove resource 'приклад/приклад.txt'
    this.addTest('testDELETE', 'deleteCollectionTest', this.defaultTimeout); // to remove collection 'приклад'

    // -- Property Operations Tests
    this.addTest('testMKCOL', 'createCollectionTest', this.defaultTimeout); // to create collection 'приклад'
    this.addTest('testPUT', 'createResourceTest', this.defaultTimeout); // to create resource 'приклад/приклад.txt'
    this.addTest('testPROPFIND', 'PROPFINDCollectionTest', this.defaultTimeout * 2);
        
    // -- Lock Operations Tests

    // -- Versioning Extensions Tests

    // -- Search Tests

    // -- Tear Down    
    this.addTest('SetUpTearDown', 'TearDown', this.defaultTimeout * 3);       
        
    // start tests
    this.currentTestCaseNumber = 0;
    this.currentTestNumber = -1;
    this.testWrapper();    
  }
  
  testSuite.prototype.addTest = function(suiteName, testName, testDuration) {
    var currentTestCaseNumber = this.tests.length - 1;
    if ( 
        // test if this.tests is empty 
        currentTestCaseNumber === -1 
        ||
        // test if this is new suite name
        this.tests[ currentTestCaseNumber ][0] !== suiteName
      ) {
      // add new suite name
      this.tests.push([suiteName, []]);
      currentTestCaseNumber = this.tests.length - 1;
    }
   
    this.tests[ currentTestCaseNumber ][1].push( [ testName, testDuration || 0 ] );
  }
  
  // method that go across the test plan "tests" 
  testSuite.prototype.testWrapper = function() {
    // first check if the test plan "tests" is ended
    if (typeof this.tests[this.currentTestCaseNumber] == "undefined") {
      // finish executing of the test plan "tests"
      fireunit.testDone();
      return;
    }

    // go to the next test or test suite
    this.currentTestNumber ++;
    if ( this.currentTestNumber == this.tests[ this.currentTestCaseNumber ][1].length ) {
      this.currentTestCaseNumber ++;
      this.currentTestNumber = 0;
    }

    // second check if the test plan "tests" is ended
    if (typeof this.tests[this.currentTestCaseNumber] == "undefined") {
      // finish executing of the test plan "tests"
      fireunit.testDone();
      return;
    }

    // perform current test of the test plan "tests"
    currentTestCaseName = this.tests[ this.currentTestCaseNumber ][0];
    currentTestName = this.tests[ this.currentTestCaseNumber ][1][ this.currentTestNumber ][0];
    currentTestDuration = this.tests[ this.currentTestCaseNumber ][1][ this.currentTestNumber ][1];
    console.log('Start test ' + currentTestCaseName + '::' + currentTestName + ' (duration: ' + currentTestDuration + 'ms) at ' + new Date().toString() ); // write info about test into console of Firebug
    eval( "this." + currentTestCaseName + "('" + currentTestName + "', " + currentTestDuration + ")" );
  }
  
  // Test SetUp and TearDown
  testSuite.prototype.SetUpTearDown = function( testName, timeout ){    
    switch (testName) {
      case 'SetUp':
        // clean of the test folder (twice delete the folder)
        var webdav = this.getWebdavObject(); // to create new webdav object
        webdav.DELETE({}, this.test_folder + '/');           
        // create folder for testing
        setTimeout(function(){
            var webdav = this.testSuite.getWebdavObject();  // to create new webdav object
            webdav.MKCOL({}, this.testSuite.test_folder);
            this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"           
            setTimeout(function(){             
              var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
              fireunit.compare(201, webdav.result.status, 'test ' + this.testSuite.tests[this.testSuite.currentTestCaseNumber][0] + ' :: ' + this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][0]);
              this.testSuite.testWrapper(); // have to be at the end of the each test
            }, timeout);
        }, timeout);
        break;

      case 'TearDown':
        // remove folder for testing
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.DELETE({}, this.test_folder + '/');
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            204,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
          );
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, timeout);
        break;

      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    }
  }

  // Modification Of Http Methods Tests
  // -- test suite "testMKCOL" with tests of the MKCOL WebDAV method 
  testSuite.prototype.testMKCOL = function(testName, testDuration){
    switch (testName) {
      case 'createCollectionTest':
          // test of collection creating 
          var webdav = this.getWebdavObject();  // to create new webdav object
          webdav.MKCOL({}, this.test_folder + '/приклад');
          this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
          setTimeout(function(){
            var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
            fireunit.compare(
              201,
              webdav.result.status,
              'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0] 
              + ' | test of creating of collection ' + this.testSuite.test_folder + '/приклад'
            );
            fireunit.ok( 
              webdav.isSuccess(webdav.result.status),
              'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
              + ' | test of creating of collection - test of webdav.isSuccess()' 
            );            
            this.testSuite.testWrapper(); // have to be at the end of the each test
          }, testDuration);
          break;
    } 
  }

  // -- test suite "testDELETE" with tests of the DELETE WebDAV method 
  testSuite.prototype.testDELETE = function(testName, testDuration){
    switch (testName) {       
      case 'deleteCollectionTest':
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.DELETE({}, this.test_folder + '/приклад/');
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            204,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of deleting of collection ' + this.testSuite.test_folder + '/приклад'
          );
          this.testSuite.testWrapper();  // have to be at the end of the each test
        }, testDuration);
        break;
      
      case 'deleteResourceTest':
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.DELETE({}, this.test_folder + '/приклад/приклад.txt');
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            204,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of removing of resource ' + this.testSuite.test_folder + '/приклад/приклад.txt'
          );           
          this.testSuite.testWrapper();  // have to be at the end of the each test
        }, testDuration);
        break;        

      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    } 
  }
  
  // -- test suite "testPUT" with tests of the PUT WebDAV method 
  testSuite.prototype.testPUT = function(testName, testDuration){
    switch (testName) {
      case 'createResourceTest':
        // test of resource creating with UTF-8 encoded name and save UTF-8 encoded contents 
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.PUT({}, this.test_folder + '/приклад/приклад.txt', {content: 'приклад'});
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            201,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of creating of resource ' + this.testSuite.test_folder + '/приклад/приклад.txt with UTF-8 encoded name and save UTF-8 encoded contents');
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);
        break;

      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    } 
  }
  
  // -- test suite "testGET" with tests of the GET WebDAV method 
  testSuite.prototype.testGET = function(testName, testDuration){
    switch (testName) {
      case 'getResourceTest':
        // get UTF-8 encoded content of resource 
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.GET({}, this.test_folder + '/приклад/приклад.txt');
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            200,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of geting of UTF-8 encoded content of resource ' + this.testSuite.test_folder + '/приклад/приклад.txt - test of status'
          );

          fireunit.compare(
            'приклад',
            webdav.result.content,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]           
            + ' | test of geting of UTF-8 encoded content of resource ' + this.testSuite.test_folder + '/приклад/приклад.txt - test of content'
          );

          fireunit.ok(
            ( typeof webdav.result.headers['Content-Type'] != "undefined" ) && webdav.result.headers['Content-Type'].indexOf('text/plain') >= 0,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]           
            + ' | test of geting of UTF-8 encoded content of resource ' + this.testSuite.test_folder + '/приклад/приклад.txt - test of existing of Response Header "Content-Type" = "text/plain"'
          );          
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);
        break;

      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    } 
  }

  // -- test suite "testHEAD" with tests of the HEAD WebDAV method 
  testSuite.prototype.testHEAD = function(testName, testDuration){
    switch (testName) {
      case 'headResourceTest':
        // get UTF-8 encoded content of resource 
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.HEAD( { 
          onComplete: function(){
            console.log('Finish test testHEAD::headResourceTest at ' + new Date().toString() );
          } 
        }, this.test_folder + '/приклад/приклад.txt' );        
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"        

        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            200,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of geting of UTF-8 encoded content of resource ' + this.testSuite.test_folder + '/приклад/приклад.txt - test of status'
          );

          fireunit.ok(
            ( typeof webdav.result.headers['Content-Type'] != "undefined" ) && webdav.result.headers['Content-Type'].indexOf('text/plain') >= 0,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]           
            + ' | test of geting of UTF-8 encoded content of resource ' + this.testSuite.test_folder + '/приклад/приклад.txt - test of existing of Response Header "Content-Type" = "text/plain"'
          );          
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);
        break;

      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    } 
  }

  // -- test suite "testOPTIONS" with tests of the OPTIONS WebDAV method 
  testSuite.prototype.testOPTIONS = function(testName, testDuration){
    switch (testName) {
      case 'optionsTest':
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.OPTIONS({}, this.test_folder + '/');
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            200,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of status'
          );        

          if (webdav.result.headers.hasOwnProperty('allow')) {
            fireunit.ok(
              webdav.result.headers['Allow'].indexOf('OPTIONS')>=0,
              'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]           
              + ' | test if response header "allow" consists "OPTIONS"'
            );          
          } else {
            if (webdav.result.headers.hasOwnProperty('Allow')) {
              fireunit.ok(
                webdav.result.headers['Allow'].indexOf('OPTIONS')>=0,
                'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]           
                + ' | test if response header "Allow" consists "OPTIONS"'
              );          
            } else {
              fireunit.ok(
                false,
                'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]           
                + ' | test if response header "Allow", or response header "allow" are existed'
              );          
            }
          }
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);
        break;
 
      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    } 
  }

  // -- test suite "testCOPY" with tests of the COPY WebDAV method 
  testSuite.prototype.testCOPY = function(testName, testDuration){
    switch (testName) {
      case 'copyResourceTest':
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.MKCOL({}, this.test_folder + '/цільова_for_copy_1');        
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"        

        // create folder for testing
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            201,
            webdav.result.status,              
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of creating of collection ' + this.testSuite.test_folder + '/цільова_for_copy_1' 
          );

          // test copy of resource
          webdav = this.testSuite.getWebdavObject();  // to create new webdav object
          var options = { 
            depth: 'infinity',
            destination: this.testSuite.test_folder + '/цільова_for_copy_1/приклад_for_copy_1.txt'
          };
          webdav.COPY({}, this.testSuite.test_folder + '/приклад/приклад.txt', options);
          this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

          setTimeout(function(){
            var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
            fireunit.compare(
              201,
              webdav.result.status,              
              'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
              + ' | test of copy of file ' + this.testSuite.test_folder + '/приклад/приклад.txt to ' + this.testSuite.test_folder + '/цільова_for_copy_1/приклад_for_copy_1.txt'
            );
            this.testSuite.testWrapper(); // have to be at the end of the each test
          }, testDuration);
        }, testDuration);
        break;

      case 'copyCollectionTest':
        // test of COPY of folder without options.depth and options.overwrite 
        var webdav = this.getWebdavObject();  // to create new webdav object
        var options = { 
          destination: this.test_folder + '/цільова_for_copy_2'
        };
        webdav.COPY({}, this.test_folder + '/цільова_for_copy_1/', options);
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            201,
            webdav.result.status,              
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of copy of folder ' + this.testSuite.test_folder + '/цільова_for_copy_1/ to ' + this.testSuite.test_folder + '/цільова_for_copy_2 without options.depth and options.overwrite' 
          );

          // test of COPY of folder by getting file '/цільова_for_copy_2/приклад_for_copy_1.txt'
          webdav = this.testSuite.getWebdavObject();  // to create new webdav object
          webdav.GET({}, this.testSuite.test_folder + '/цільова_for_copy_2/приклад_for_copy_1.txt');
          this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

          setTimeout(function(){
            var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
            fireunit.compare(
              'приклад',
              webdav.result.content,              
              'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
              + ' | test of geting of UTF-8 encoded content - test of content'
            );
            this.testSuite.testWrapper(); // have to be at the end of the each test
          }, testDuration);

        }, testDuration);
        break;

      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    }
  }

  // -- test suite "testMOVE" with tests of the MOVE WebDAV method 
  testSuite.prototype.testMOVE = function(testName, testDuration){
    switch (testName) {
      case 'moveResourceTest':
        var webdav = this.getWebdavObject();  // to create new webdav object
        webdav.MKCOL({}, this.test_folder + '/цільова_for_move_1');
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
                
        // create folder for testing
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            201,
            webdav.result.status,              
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of creating of collection ' + this.testSuite.test_folder + '/цільова_for_move_1' 
          );

          // test move of resource
          webdav = this.testSuite.getWebdavObject();  // to create new webdav object
          var options = {
            depth: 'infinity',
            destination: this.testSuite.test_folder + '/цільова_for_move_1/приклад_for_move_1.txt'
          };
          webdav.MOVE({}, this.testSuite.test_folder + '/цільова_for_copy_1/приклад_for_copy_1.txt', options);
          this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

          setTimeout(function(){
            var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
            // according to the [rfc2518, 8.9.4] expected result.status = 201:Created ("Test  MOVE.1")
            fireunit.compare(
              201,
              webdav.result.status,              
              'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
              + ' | test of move of file ' + this.testSuite.test_folder + '/цільова_for_copy_1/приклад_for_copy_1.txt to ' + this.testSuite.test_folder + '/цільова_for_move_1/приклад_for_move_1.txt ("Test  MOVE.1")'
            );        
            this.testSuite.testWrapper(); // have to be at the end of the each test
          }, testDuration);

        }, testDuration);
        break;

      case 'moveCollectionTest':
        // test MOVE folder with options.depth = 1
        var webdav = this.getWebdavObject();  // to create new webdav object
        var options = { 
          depth: 1,
          destination: this.test_folder + '/цільова_for_move_2/'
        };
        webdav.MOVE({}, this.test_folder + '/цільова_for_move_1/', options);
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            400,
            webdav.result.status,              
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of move of folder ' + this.testSuite.test_folder + '/цільова_for_move_1/ to ' + this.testSuite.test_folder + '/цільова_for_move_2 with options.depth = 1' 
          );

          // test of MOVE of folder without options.depth and options.overwrite 
          webdav = this.testSuite.getWebdavObject();  // to create new webdav object
          var options = { 
            destination: this.testSuite.test_folder + '/цільова_for_move_2'
          };
          webdav.MOVE({}, this.testSuite.test_folder + '/цільова_for_move_1/', options);
          this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
          
          setTimeout(function(){
            var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
            // according to the [rfc2518, 8.9.4] expected result.status = 201:Created ("Test MOVE.2")
            fireunit.compare(
              201,
              webdav.result.status,              
              'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
              + ' | test of move of folder ' + this.testSuite.test_folder + '/цільова_for_move_1/ to ' + this.testSuite.test_folder + '/цільова_for_move_2 without options.depth and options.overwrite ("Test MOVE.2")'
            );        
  
            // test of MOVE of folder by getting file '/цільова_for_move_2/приклад_for_move_1.txt'
            webdav = this.testSuite.getWebdavObject();  // to create new webdav object
            webdav.GET({}, this.testSuite.test_folder + '/цільова_for_move_2/приклад_for_move_1.txt');
            this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
  
            setTimeout(function(){
              var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
              fireunit.compare(
                'приклад',
                webdav.result.content,              
                'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
                + ' | test of MOVE of folder by getting file ' + this.testSuite.test_folder + '/цільова_for_move_2/приклад_for_move_1.txt'
              );  

              // test of MOVE of folder by trying to get the moved file /цільова_for_move_1/приклад_for_move_1.txt'
              webdav = this.testSuite.getWebdavObject();  // to create new webdav object
              webdav.GET({}, this.testSuite.test_folder + '/цільова_for_move_1/приклад_for_move_1.txt');   
              this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
              
              setTimeout(function(){
                var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
                fireunit.compare(
                  404,
                  webdav.result.status,              
                  'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
                  + ' | test of MOVE of folder by trying to get the moved file ' + this.testSuite.test_folder + '/цільова_for_move_1/приклад_for_move_1.txt'
                );        
    
                this.testSuite.testWrapper(); // have to be at the end of the each test
              }, testDuration);             

            }, testDuration);
  
          }, testDuration);

        }, testDuration);
        break;

      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    }
  }

  // -- test suite "testExtensionMethod" with tests of the ExtensionMethod method - an particular duplicate PUT_GET_HEAD test using an ExtensionMethod 
  testSuite.prototype.testExtensionMethod = function(testName, testDuration){
    switch (testName) {
      case 'MKCOL':
        // create a collection by using an ExtensionMethod
        var webdav = this.getWebdavObject();  // to create new webdav object
        var options = {
          method: 'MKCOL'
        }
        webdav.ExtensionMethod({}, this.test_folder + '/цільова_for_testExtensionMethod', options);
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
        
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            201,
            webdav.result.status,              
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of creating of collection ' + this.test_folder + '/цільова_for_testExtensionMethod by using ExtensionMethod'
          );        
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);             
        break;
        
      case 'PUT':
        // create a file with UTF-8 encoded name and save UTF-8 encoded contents by using an ExtensionMethod 
        var webdav = this.getWebdavObject();  // to create new webdav object
        var options = {
          method: 'PUT',
          headers: new Array,
          body: 'приклад'
        }
        options.headers['Content-Type'] = 'text/plain; charset=UTF-8';
        webdav.ExtensionMethod({}, this.test_folder + '/цільова_for_testExtensionMethod/приклад.txt', options);
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
        
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            201,
            webdav.result.status,              
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of creating of resource ' + this.testSuite.test_folder + '/цільова_for_testExtensionMethod/приклад.txt with UTF-8 encoded name and save UTF-8 encoded contents by using an ExtensionMethod'
          );        
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);    
        break;
              
      case 'GET':
        // get UTF-8 encoded content by using an ExtensionMethod 
        var webdav = this.getWebdavObject();  // to create new webdav object
        var options = {
          method: 'GET'
        }
        webdav.ExtensionMethod({}, this.test_folder + '/цільова_for_testExtensionMethod/приклад.txt', options);
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
        
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            200,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of geting of UTF-8 encoded content of resource ' + this.testSuite.test_folder + '/цільова_for_testExtensionMethod/приклад.txt by using an ExtensionMethod - test of status'
          );

          fireunit.compare(
            'приклад',
            webdav.result.content,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]           
            + ' | test of geting of UTF-8 encoded content of resource ' + this.testSuite.test_folder + '/цільова_for_testExtensionMethod/приклад.txt by using an ExtensionMethod - test of content'
          );

          fireunit.ok(
            ( typeof webdav.result.headers['Content-Type'] != "undefined" ) && webdav.result.headers['Content-Type'].indexOf('text/plain') >= 0,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]           
            + ' | test of geting of UTF-8 encoded content of resource ' + this.testSuite.test_folder + '/цільова_for_testExtensionMethod/приклад.txt by using an ExtensionMethod - test of existing of Response Header "Content-Type" = "text/plain"'
          );          
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);
        break;

      case 'DELETE':
        // delete the collection by using an ExtensionMethod
        var webdav = this.getWebdavObject();  // to create new webdav object
        var options = {
          method: 'DELETE'
        }
        webdav.ExtensionMethod({}, this.test_folder + '/цільова_for_testExtensionMethod/', options);
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"

        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            204,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of removing of collection ' + this.testSuite.test_folder + '/цільова_for_testExtensionMethod by using an ExtensionMethod'
          );
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);
        break;

      default:
        alert('There is no such test "' + testName + '" in the "' + this.tests[ this.currentTestCaseNumber ][0] + '" test suite' );
        this.testWrapper(); // have to be at the end of the each test
    }
  }

  // Property Operations Tests
  // -- test suite "testPROPFIND" with tests of the PROPFIND WebDAV method 
  testSuite.prototype.testPROPFIND = function(testName, testDuration){
    switch (testName) {
      case 'PROPFINDCollectionTest':
        var webdav = this.getWebdavObject();  // to create new webdav object        
        var options = {
          depth: 1,
          operation: 'propname'
        };      
        webdav.PROPFIND({}, this.test_folder + '/приклад', options);
        this.tests[this.currentTestCaseNumber][1][this.currentTestNumber][2] = webdav;  // to save webdav object into the testplan "tests"
        
        setTimeout(function(){
          var webdav = this.testSuite.tests[this.testSuite.currentTestCaseNumber][1][this.testSuite.currentTestNumber][2];  // to get webdav object from the testplan "tests"
          fireunit.compare(
            207,
            webdav.result.status,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0] 
            + ' | test of PROPFIND to the ' + this.testSuite.test_folder + '/приклад'
          );

          fireunit.ok( 
            webdav.result.content.toLowerCase().indexOf( encodeURI( this.testSuite.test_folder + '/приклад').toLowerCase() ) >= 0
            && webdav.result.content.indexOf('<D:displayname/>') >= 0,
            'test ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][0] + ' :: ' + this.testSuite.tests[ this.testSuite.currentTestCaseNumber ][1][ this.testSuite.currentTestNumber ][0]
            + ' | test of PROPFIND to the ' + this.testSuite.test_folder + '/приклад - verifying of presents of ' + this.testSuite.test_folder + '/приклад and property "displayname" in the response.content' 
          );            
          this.testSuite.testWrapper(); // have to be at the end of the each test
        }, testDuration);
        break;
    } 
  }
  
  gadgets.exowebdav.testSuite = testSuite;

// finish FireUnit tests library "exo-webdav-tests" ===============================================================
