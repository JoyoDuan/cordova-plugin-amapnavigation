var exec = require('cordova/exec');

// 这里的命令AMapNavigation与plugin.xml无关，用abc都可以，module.exports用于require('')
function AMapNavigation() {}

AMapNavigation.prototype.navigation = function(startPoint, endPoint, naviType, pageType, successCallback, errorCallback) {
  exec(
  	successCallback, 
  	errorCallback, 
  	"AMapNavigationPlugin", 
  	'navigation', 
  	[
  		startPoint.lng, 
  		startPoint.lat, 
  		startPoint.address, 
  		endPoint.lng, 
  		endPoint.lat, 
  		endPoint.address,
		  naviType.toString(),
      pageType.toString()
  	]
  );
};

module.exports = new AMapNavigation();
