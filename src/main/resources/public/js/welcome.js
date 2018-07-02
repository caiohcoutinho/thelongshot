var app = angular.module('longshot');

var onSignIn;

app.controller("welcome", ['$scope', '$resource', function($scope, $resource){

    var sessionEndpoint = 'rest/session';
    var sessionResource = $resource(sessionEndpoint, {});

    var cookiePrefix = "userTokenId="
    var getTokenIdFromCookie = function(){
        var cookie = _.find(decodeURIComponent(document.cookie).split(';'), function(cookie){
            return cookie.trim().startsWith(cookiePrefix);
        });
        if(!_.isUndefined(cookie)){
            return cookie.trim().substring(cookiePrefix.length);
        }
    }
    function setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays*24*60*60*1000));
        var expires = "expires="+ d.toUTCString();
        document.cookie = cname + cvalue + ";" + expires + ";path=/";
    }

    $scope.loginFailed = _.isUndefined(getTokenIdFromCookie());

    onSignIn = function(googleUser){
        var tokenId = googleUser.getAuthResponse().id_token;
        var profile = googleUser.getBasicProfile();
        var userId = profile.getId();
        var username = profile.getName();
        var userLogoUrl = profile.getImageUrl();

        setCookie(cookiePrefix, tokenId, 1);
        sessionResource.save({userId: userId, userTokenId: tokenId, username: username, userLogoUrl: userLogoUrl}, function(){
            window.location.href = "/stage.html";
        });
    }

}])


