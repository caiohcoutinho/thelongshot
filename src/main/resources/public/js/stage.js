var app = angular.module('longshot');


var auth2;

app.controller("stage", ['$scope', '$element', '$resource', '$modal', function($scope, $element, $resource, $modal){

    var sessionEndpoint = 'rest/session';
    var sessionResource = $resource(sessionEndpoint, {});

    $scope.loginFailed = true;

    var cookiePrefix = "userTokenId=";
    var battleCookiePrefix = "battleId=";
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

    gapi.load('auth2', function(){
        auth2 = gapi.auth2.init({
            client_id: '405402394589-f20c532e75fn33c541jfbu5ta179bko4.apps.googleusercontent.com'
        });
        var userTokenId = getTokenIdFromCookie();
        if(_.isUndefined(userTokenId)){
            killCookiesAndRedirect();
        } else{
            sessionResource.get({userTokenId: userTokenId}, function(longshotSession){
                if(_.isUndefined(longshotSession) ||
                    _.isUndefined(longshotSession.username) ||
                    _.isUndefined(longshotSession.userLogoUrl)){
                    killCookiesAndRedirect();
                }
                $scope.loginFailed = false;
                $scope.username = longshotSession.username;
                $scope.userLogoUrl = longshotSession.userLogoUrl;
                refreshStages();
            }, function(){
                killCookiesAndRedirect();
            });
        }
    });

    var killCookiesAndRedirect = function(){
        setCookie(cookiePrefix, -1, -1);
        setCookie(battleCookiePrefix, -1, -1);
        var auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut();
        window.location.href = "/index.html";
    }

    $scope.logoff = function(){
        var userTokenId = getTokenIdFromCookie();
        sessionResource.remove({userTokenId: userTokenId}, function(){
            killCookiesAndRedirect();
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

    jQuery('.stageDiv').show();

    var sortStages = function(){
        $scope.userStages = _.sortBy($scope.userStages, "name");
        $scope.openStages = _.sortBy($scope.openStages, "name");
    }

    var stageEndpoint = "rest/stage/";
    var stageResource = $resource(stageEndpoint, {});

    var refreshStages = function(){
        stageResource.get({}, function(stages){
            var userStages = stages.userStages;
            if(userStages.length == 1){
                $scope.selectedStage = userStages[0];
                $scope.isStageListHidden = true;
            }
            $scope.userStages = userStages;
            $scope.openStages = stages.openStages;
            sortStages();
        });
    }

    $scope.showUserStages = function(){
        jQuery('.openStageDiv').hide();
        jQuery('.userStageDiv').show();
        refreshStages();
    }

    $scope.showOpenStages = function(){
        jQuery('.userStageDiv').hide();
        jQuery('.openStageDiv').show();
        refreshStages();
    }

    var Stage = function(id, name){
        this.id = id;
        this.name = name;
    }

    $scope.clearMessages = function(){
        $scope.errorMessage = null;
        $scope.infoMessage = null;
        $scope.successMessage = null;
    }
    $scope.hasInfoMessage = function(){
        return $scope.infoMessage != null;
    }
    $scope.hasErrorMessage = function(){
        return $scope.errorMessage != null;
    }
    $scope.hasSuccessMessage = function(){
        return $scope.successMessage != null;
    }
    $scope.showErrorMessage = function(error){
        $scope.errorMessage = error;
    }
    $scope.showInfoMessage = function(info){
        $scope.infoMessage = info;
    }
    $scope.selectStage = function(index){
        $scope.selectedStage = $scope.userStages[index];
    }
    $scope.isStageSelected = function(){
        return $scope.selectedStage != undefined;
    }
    $scope.newStage = function(){
        // TODO: Need to internationalize strings.
        $scope.selectedStage = new Stage(null, "Nova Sala");
    }
    $scope.playStage = function(stage){
        if(_.isUndefined(stage)){
            stage = $scope.selectedStage
        }
        //window.location.href = "rest/battle/"+stage.id;
        setCookie(battleCookiePrefix, stage.id);
        window.location.href = "game.html";
        /*
        stageResource.play(stage, function(json){
            var response = json.response;
            if(response == "ready"){
                window.location.href = "rest/battle/"+stage.id;
            } else{
                $scope.showInfoMessage = "A sala ainda não está completa.";
            }
        }, defaultErrorCallback)
        */
    };
    $scope.saveStage = function(){
        stageResource.save($scope.selectedStage, function(value){
            $scope.selectedStage = value;
            var oldStage = _.find($scope.userStages, {id: value.id});
            if(_.isUndefined(oldStage)){
                $scope.userStages.push(value);
            }
            sortStages();
        }, function(error){
            $scope.showErrorMessage("Não foi possível salvar as alterações.\nContate o administrador do sistema.\nCódigo do erro: "+error.status+"\n"+error.statusText);
        });
    }

    $scope.removeStage = function() {
        var modalInstance = $modal.open({
          animation: true,
          templateUrl: 'removeStageModal.html',
          controller: 'ModalInstanceCtrl',
          size: 'lg',
          resolve: {
            stage: function () {
              return $scope.selectedStage;
            }
          }
        });

        modalInstance.result.then(function (stageToRemove) {
            stageResource.remove({}, stageToRemove, function(){
                $scope.selectedStage = undefined;
                $scope.userStages = _.without($scope.userStages, stageToRemove);
            }, defaultErrorCallback)
        }, function(){});
    };

}]);

app.controller('ModalInstanceCtrl', function ($scope, $modalInstance, stage) {

  $scope.stage = stage;

  $scope.ok = function () {
    $modalInstance.close(stage);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss(stage);
  };
});