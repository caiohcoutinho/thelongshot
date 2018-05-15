var app = angular.module('longshot');

app.controller("stage", ['$scope', '$element', '$resource', '$modal', function($scope, $element, $resource, $modal){

    /**
    var userEndpoint = 'rest/user';
    var userResource = $resource(userEndpoint, {}, {
        logoff: {method: 'POST', url: userEndpoint+'/logoff'},
        logged: {method: 'GET', url: userEndpoint+'/logged'},
        token: {method: 'POST', url: userEndpoint+'/token'}
    });

    $scope.logoff = function(){
        userResource.logoff({}, function(){
            window.location.href = "/"
        });
    }
    **/

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

    var isLogged = true;
    $scope.userLogged = isLogged;
    jQuery('.stageDiv').show();

    /**
    userResource.logged({}, function(logged){
        var isLogged = logged.username != null;
        $scope.userLogged = isLogged;
        if(isLogged){
            $scope.username = logged.username;
            $scope.picture = logged.picture;
            jQuery('.stageDiv').show();
        } else {
            window.location.href = "/";
        }
    });
    **/

    var sortStages = function(){
        $scope.userStages = _.sortBy($scope.userStages, "name");
        $scope.openStages = _.sortBy($scope.openStages, "name");
    }

    var stageEndpoint = "rest/stage/";
    var stageResource = $resource(stageEndpoint+':id', {id: '@id'},{
        play : {method: 'POST', url: stageEndpoint+"play/:id"}
    });

    var refreshStages = function(){
        stageResource.get(function(stages){
            var userStages = JSON.parse(stages.userStages);
            if(userStages.length == 1){
                $scope.selectedStage = userStages[0];
                $scope.isStageListHidden = true;
            }
            $scope.userStages = userStages;
            $scope.openStages = JSON.parse(stages.openStages);
            sortStages();
        });
    }

    refreshStages();

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
        stageResource.play(stage, function(json){
            var response = json.response;
            if(response == "ready"){
                window.location.href = "rest/battle/"+stage.id;
            } else{
                $scope.showInfoMessage = "A sala ainda não está completa.";
            }
        }, defaultErrorCallback)
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