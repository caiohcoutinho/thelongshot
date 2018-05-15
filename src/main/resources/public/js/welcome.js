var app = angular.module('longshot');

app.controller("welcome", ['$scope', '$resource', '$location', function($scope, $resource, $location){

    $scope.userLogged = true;
    window.location.href = "/stage.html";
    return;

    var userEndpoint = 'rest/user';
    var userResource = $resource(userEndpoint, {}, {
        logoff: {method: 'POST', url: userEndpoint+'/logoff'},
        logged: {method: 'GET', url: userEndpoint+'/logged'},
        token: {method: 'POST', url: userEndpoint+'/token'}
    });
    var googleAuthResource = $resource('https://accounts.google.com/o/oauth2/v2/auth');

    $scope.hasCookies = navigator.cookieEnabled;

    var redirect_uri = $location.absUrl();

    if(redirect_uri.indexOf("localhost") != -1){
        // Workaround for dev
        redirect_uri = "http://localhost:8080/rest/user/code";
    } else{
        redirect_uri = "http://longshot-pcmaker.rhcloud.com/rest/user/code";
    }

    $scope.login = function(){
        userResource.token({}, function(token){
            var url = 'https://accounts.google.com/o/oauth2/v2/auth?'+
                'client_id=405402394589-f20c532e75fn33c541jfbu5ta179bko4.apps.googleusercontent.com&'+
                'redirect_uri='+escape(redirect_uri)+'&'+
                'response_type=code&'+
                'scope=openid+profile&'+
                'state='+token.sessionToken;
            window.location.href = url;
        });
    }

    var defaultSuccessCallback = function(a, b, c){
        alert("success");
        window.a = a;
        window.b = b;
        window.c = c;
    }

    var defaultErrorCallback = function(e){
        alert("error");
        window.e = e;
    }

    userResource.logged({}, function(logged){
        var isLogged = logged.username != null;
        if(isLogged){
            window.location.href = "/stage.html"
        }
        $scope.userLogged = isLogged;
    });

}])