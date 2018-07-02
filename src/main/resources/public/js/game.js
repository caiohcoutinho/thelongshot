var app = angular.module('longshot');

app.controller("game", ['$scope', '$element', 'hotkeys', '$resource', function($scope, $element, hotkeys, $resource){

    var battleEndpoint = 'rest/battle';
    var battleResource = $resource(battleEndpoint, {}, {});

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
    var getStageIdFromCookie = function(){
        var cookie = _.find(decodeURIComponent(document.cookie).split(';'), function(cookie){
            return cookie.trim().startsWith(battleCookiePrefix);
        });
        if(!_.isUndefined(cookie)){
            return cookie.trim().substring(battleCookiePrefix.length);
        }
    }

    function setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays*24*60*60*1000));
        var expires = "expires="+ d.toUTCString();
        document.cookie = cname + cvalue + ";" + expires + ";path=/";
    }

    var killCookiesAndRedirect = function(){
        setCookie(cookiePrefix, -1, -1);
        setCookie(battleCookiePrefix, -1, -1);
        var auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut();
        window.location.href = "/index.html";
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
            }, function(){
                killCookiesAndRedirect();
            });
        }
    });

    $scope.logoff = function(){
        var userTokenId = getTokenIdFromCookie();
        sessionResource.remove({userTokenId: userTokenId}, function(){
            killCookiesAndRedirect();
        });
    }

    $scope.shot = {};
    $scope.shot.fx = "X0+((V0-X0)/100)*t";
    $scope.shot.fy = "Y0+((4*V3-3*Y0-V1)/100)*t+((V1+Y0-2*V3)/5000)*t*t";
    $scope.shot.fz = "Z0+((V2-Z0)/100)*t";
    $scope.shot.variables = {};
    _.each(_.range(10), function(i){
        $scope.shot.variables[i] = 0;
    });
    $scope.shot.points = [];
    $scope.lastShotScore = 0;
    $scope.totalScore = 0;
    $scope.cameras = [];

    $scope.nextCamera = function(){
        var lastCamera;
        var notFound = _.every($scope.cameras, function(camera){
            if(!_.isUndefined(lastCamera) && lastCamera.selected){
                $scope.selectCamera(camera);
                return false;
            }
            lastCamera = camera;
            return true;
        });
        if(notFound){
            $scope.selectCamera($scope.cameras[0]);
        }
    }

    $scope.VEL = 0.5;
    $scope.ANG_VEL = Math.PI/64;

    $scope.endTurn = function(){
        alert('acabou o turno');
    }

    $scope.gasBarValue = function(){
        if(isMyTurn()){
            return $scope.tank.fuel;
        }
        return 0;
    }

    $scope.gasBarMax = function(){
        //TODO: variable max fuel
        return 80;
    }

    $scope.gasBarText = function(){
        if(isMyTurn()){
            var tank = $scope.tank;
            return 100*tank.fuel / tank.FUEL_MAX;
        }
        return 0;
    }

    $scope.setFuel = function(fuel){
        $scope.tank.fuel = fuel;
        if($scope.tank.fuel <= 0 ){
            $scope.tank.fuel = 0;
            $scope.tank.turn.hasGas = false;
            if($scope.tank.turn.hasShot == false){
                $scope.endTurn();
            }
        }
    }

    $scope.useGas = function(times){
        var times = times || 1;
        $scope.tank.fuel = $scope.tank.fuel - times;
        if($scope.tank.fuel <= 0 ){
            $scope.tank.fuel = 0;
            $scope.tank.turn.hasGas = false;
            if($scope.tank.turn.hasShot == false){
                $scope.endTurn();
            }
        }
    }

    $scope.hasGas = function(){
        var res = $scope.tank.fuel > 0;
        if(res == false){
            console.log("No Gas");
        }
        return res;
    }

    $scope.findActivePlayerName = function(){
        if(!$scope.stage){
            return "???";
        }
        var player = _.findWhere($scope.stage.players, {id : $scope.stage.activePlayerId});
        if(!!player){
            return player.userName;
        }
        return "???"
    }

    $scope.hasDoubleGas = function(){
        var res = $scope.tank.fuel > 1;
        if(res == false){
            console.log("No double gas");
        }
        return res;
    }

    var moveSelectedPlayer = function(){
        // does it?
    }

    $scope.moveTankForward = function(){
        if($scope.hasGas() && isMyTurn()){
            $scope.packets++;
            battleResource.post(JSON.stringify({move: "forward", playerId: $scope.stage.selectedPlayerId}, moveSelectedPlayer);
        }
    }

    $scope.moveTankBackward = function(){
        if($scope.hasGas() && isMyTurn()){
        $scope.packets++;
        }
            battleResource.post(JSON.stringify({move: "forward", playerId: $scope.stage.selectedPlayerId}, moveSelectedPlayer);
        }
    }

    $scope.spinTankLeft = function(){
        if($scope.hasGas() && isMyTurn()){
                var t = $scope.tank;
                var rot = t.rotation;
                var a = rot.y - $scope.ANG_VEL;
		$scope.packets++;
                $scope.subSocketBattle.push(JSON.stringify({move: {rotation: t.rotation, newRotation: {x: rot.x, y: a, z: rot.z}, position: t.position, newPosition: t.position}}));
        }
    }

    $scope.spinTankRight = function(){
        if($scope.hasGas() && isMyTurn()){
                var t = $scope.tank;
                var rot = t.rotation;
                var a = rot.y + $scope.ANG_VEL;
		$scope.packets++;
                $scope.subSocketBattle.push(JSON.stringify({move: {rotation: t.rotation, newRotation: {x: rot.x, y: a, z: rot.z}, position: t.position, newPosition: t.position}}));
        }
    }

    var HotkeyOption = function(combo, description, callback){
        this.combo = combo;
        this.description = description;
        this.callback = callback;
    }

    $scope.refuel = function(){
        $scope.tank.fuel = $scope.tank.FUEL_MAX;
    }

    $scope.drawShotLastRound = false;

    hotkeys.bindTo($scope)
    .add(new HotkeyOption('q', 'Mudar opção de camara', $scope.nextCamera))
    .add(new HotkeyOption('w', 'Move veículo para frente', $scope.moveTankForward))
    .add(new HotkeyOption('s', 'Move veículo para trás', $scope.moveTankBackward))
    .add(new HotkeyOption('a', 'Gira veículo para esquerda', $scope.spinTankLeft))
    .add(new HotkeyOption('d', 'Gira veículo para direita', $scope.spinTankRight))
    /*
    .add(new HotkeyOption('w+a', 'Move veículo para frente e para esquerda', $scope.moveTankForwardLeft))
    .add(new HotkeyOption('w+d', 'Move veículo para frente e para direita', $scope.moveTankForwardRight))
    .add(new HotkeyOption('s+a', 'Move veículo para trás e para esquerda', $scope.moveTankBackwardLeft))
    .add(new HotkeyOption('s+d', 'Move veículo para trás e para direita', $scope.moveTankBackwardRight))
    */
    ;

    $scope.selectCamera = function(camera){
        _.each($scope.cameras, function(item){
            var found = camera == item;
            item.selected = found;
            if(found){
                $scope.scene.activeCamera = item;
                item.attachControl($scope.canvas);
                if(_.isFunction(item.setTarget)){
                    item.setTarget($scope.tank.position);
                }
            } else{
                item.detachControl($scope.canvas);
            }
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

    $scope.packets = 0;
    $scope.received = 0;

    $scope.shoot = function(){
        if(isMyTurn() && $scope.tank.turn.hasShot == true){
            var fx = $scope.shot.fx || "X0";
            var fy = $scope.shot.fy || "Y0";
            var fz = $scope.shot.fz || "Z0";

	    $scope.packets++;

	        // TODO: Here I have to send this shot to the server.
            console.log(JSON.stringify({shot: {
                variables: _.values($scope.shot.variables),
                stageId: $scope.stage.id,
                fx: fx, fy: fy, fz: fz,
                position: $scope.tank.position
            }}));
        }
    }

    function isMyTurn(){
        var res = !!$scope.stage && !!$scope.stage.selectedPlayer && $scope.stage.selectedPlayer.id == $scope.stage.activePlayerId;
        if(res == false){
            //console.log("Not my turn");
        }
        return res;
    }

    function isTouchSupported() {
        var msTouchEnabled = window.navigator.msMaxTouchPoints;
        var generalTouchEnabled = "ontouchstart" in document.createElement("div");

        if (msTouchEnabled || generalTouchEnabled) {
            return true;
        }
        return false;
    }

    var startGame = function(){
        var stageId = getStageIdFromCookie();
        battleResource.get({stageId: stageId, action: "startGame"},{}, function(stage){
            $scope.stage = stage;
            var canvas = $element.find('canvas')[0];
            $scope.canvas = canvas;
            var engine = new BABYLON.Engine(canvas, true);

            // create a basic BJS Scene object
            var scene = new BABYLON.Scene(engine);

            var player = stage.selectedPlayer;
            var players = $scope.stage.players;
            var p1pos = player.position;
            var p1rot = player.rotation;

            var camDist = 50;
            if(isTouchSupported()){
                var camera = new BABYLON.TouchCamera('camera1', new BABYLON.Vector3(p1pos.x + camDist, p1pos.y + camDist, p1pos.z + camDist), scene);
                $scope.cameras.push(camera);
                camera.attachControl(canvas, false);
            } else{
                var freeCamera = new BABYLON.FreeCamera('camera1', new BABYLON.Vector3(p1pos.x - camDist, p1pos.y + camDist, p1pos.z + camDist), scene);
                freeCamera.attachControl(canvas, false);
                freeCamera.name = "Livre"
                freeCamera.selected = true;
                $scope.cameras.push(freeCamera);

                var followCamera = new BABYLON.FollowCamera('followCamera', new BABYLON.Vector3(p1pos.x, p1pos.y, p1pos.z), scene);
                followCamera.radius = 15;
                followCamera.name = "1ª Pessoa";
                followCamera.selected = false;
                $scope.cameras.push(followCamera);

                var arcCamera = new BABYLON.ArcRotateCamera('camera1', 0, Math.PI/4, 50, new BABYLON.Vector3(p1pos.x, p1pos.y, p1pos.z), scene);
                arcCamera.name = "3ª Pessoa";
                arcCamera.selected = false;
                $scope.cameras.push(arcCamera);
            }

            //$scope.$apply();

            // create a basic light, aiming 0,1,0 - meaning, to the sky
            //var light = new BABYLON.HemisphericLight('light1', new BABYLON.Vector3(0,1,0), scene);
            var sun = new BABYLON.PointLight("Omni0", new BABYLON.Vector3(100, 200, -125), scene);

            var camouflageMaterial = new BABYLON.StandardMaterial("camouflage", scene);
            camouflageMaterial.diffuseTexture = new BABYLON.Texture("jpg/material/camouflage/bluecamou.jpg", scene);
            camouflageMaterial.uScale = 10;
            camouflageMaterial.vScale = 10;

            var enemyCamouflageMaterial = new BABYLON.StandardMaterial("camouflage", scene);
            enemyCamouflageMaterial.diffuseTexture = new BABYLON.Texture("jpg/material/camouflage/redcamou.jpg", scene);
            enemyCamouflageMaterial.uScale = 10;
            enemyCamouflageMaterial.vScale = 10;

            var blackMaterial = new BABYLON.StandardMaterial("black", scene);
            blackMaterial.diffuseColor = new BABYLON.Color3(0,0,0);

            var createTank = function(name, theScene, camouflage, cannonMaterial){
                var core = new BABYLON.Mesh.CreateBox(name+'core', 1, theScene);
                core.position = new BABYLON.Vector3(0,0,0);
                core.userId = name;
                var topBox = new BABYLON.Mesh.CreateBox(name+'topBox', 1, theScene);
                topBox.position = new BABYLON.Vector3(1,1,1);
                topBox.scaling = new BABYLON.Vector3(4,2,7);
                topBox.material = camouflage;
                topBox.parent = core;
                var bottomBox = new BABYLON.Mesh.CreateBox(name+'bottomBox', 1, theScene);
                bottomBox.position = new BABYLON.Vector3(1,-1,1);
                bottomBox.scaling = new BABYLON.Vector3(7,2,9);
                bottomBox.material = camouflage;
                bottomBox.parent = core;
                var cannonBox = new BABYLON.Mesh.CreateBox(name+'cannonBox', 1, theScene);
                cannonBox.position = new BABYLON.Vector3(1,1,-5);
                cannonBox.scaling = new BABYLON.Vector3(1,1,5);
                cannonBox.material = blackMaterial;
                cannonBox.parent = core;
                return core;
            }

            var findSkin = function(skin){
                if(skin == "red"){
                    return enemyCamouflageMaterial;
                }
                return camouflageMaterial;
            }

            $scope.enemyTanks = [];
            _.each(_.filter(players, function(p){
                return p.id != player.id;
            }), function(p){
                var tank = createTank(p.id, scene, findSkin(p.skin), blackMaterial);
                var pos = p.position;
                var rot = p.rotation;
                tank.position = tank.position.add(new BABYLON.Vector3(pos.x, pos.y, pos.z));
                tank.rotation = tank.rotation.add(new BABYLON.Vector3(rot.x, rot.y, rot.z));
                $scope.enemyTanks.push(tank);
            });

            $scope.tank = createTank(player.id, scene, findSkin(player.skin), blackMaterial);
            $scope.tank.FUEL_MAX = 80;
            $scope.tank.fuel = player.fuel;
            $scope.tank.turn = {
                hasGas : true,
                hasShot : true
            }
            $scope.tank.position = $scope.tank.position.add(new BABYLON.Vector3(p1pos.x, p1pos.y, p1pos.z));
            $scope.tank.rotation = $scope.tank.rotation.add(new BABYLON.Vector3(p1rot.x, p1rot.y, p1rot.z));
            freeCamera.setTarget($scope.tank.position);
            followCamera.target = $scope.tank;
            arcCamera.target = $scope.tank;

            var skyBox = BABYLON.Mesh.CreateBox("skyBox", 1000.0, scene);
            var skyBoxMaterial = new BABYLON.StandardMaterial("skyBox", scene);
            skyBoxMaterial.backFaceCulling = false;
            skyBoxMaterial.reflectionTexture = new BABYLON.CubeTexture("jpg/material/skybox/skybox", scene);
            skyBoxMaterial.reflectionTexture.coordinatesMode = BABYLON.Texture.SKYBOX_MODE;
            skyBoxMaterial.diffuseColor = new BABYLON.Color3(0,0,0);
            skyBoxMaterial.specularColor = new BABYLON.Color3(0,0,0);
            skyBox.material = skyBoxMaterial;

            var extraGround = BABYLON.Mesh.CreateGround("extraGround", 600, 600, 1, scene, false);
            var extraGroundMaterial = new BABYLON.StandardMaterial("extraGround", scene);
            extraGroundMaterial.diffuseTexture = new BABYLON.Texture("jpg/material/ground/grass.jpg", scene);
            extraGroundMaterial.diffuseTexture.uScale = 60;
            extraGroundMaterial.diffuseTexture.vScale = 60;
            extraGround.position.y = -2;
            extraGround.material = extraGroundMaterial;

            var beforeRender = function(){
                var BETA_MIN = 0.1;
                var BETA_MAX = 15*Math.PI/32;
                var RAD_MIN = 10;
                var RAD_MAX = 200;

                if(!_.isUndefined(arcCamera)){
                    if(arcCamera.beta < BETA_MIN){
                        arcCamera.beta = BETA_MIN;
                    } else if(arcCamera.beta > BETA_MAX ){
                        arcCamera.beta = BETA_MAX;
                    }

                    if(arcCamera.radius > RAD_MAX){
                        arcCamera.radius = RAD_MAX;
                    } else if(arcCamera.radius < RAD_MIN){
                        arcCamera.radius = RAD_MIN;
                    }
                }
            }

            scene.registerBeforeRender(beforeRender);

            engine.runRenderLoop(function(){
                scene.render();
            })

            window.addEventListener('resize', function(){
                engine.resize();
            });

            //var transport = "websocket";

            //var urlChat = "ws://"+location.hostname+":8000/ws/chat/"+stage.id;
            //if(location.hostname.indexOf("localhost") >= 0){
            //    urlChat = "/ws/chat/"+stage.id;
            //}

            //var socket = $.atmosphere;
            //var requestChat = {
            //    url: urlChat,
            //    contentType: "application/json",
            //    transport: 'websocket',
            //    trackMessageLength : true,
            //    fallbackTransport: 'long-polling'
            //};

            //requestChat.onOpen = function(response){
            //    console.log("Conexão aberta chat: "+response.transport);
            //}

            //requestChat.onError = function(response){
            //    console.log("Erro chat: "+response.transport);
            //}

            //$scope.posts = [];
            //requestChat.onMessage = function(response){
            //    $scope.posts.push(JSON.parse(response.responseBody));
            //    $scope.$apply();
            //}

            //var subSocketChat = socket.subscribe(requestChat);
            //$scope.subSocketChat = subSocketChat;

            //var urlBattle = "ws://"+location.hostname+":8000/ws/battle/"+stage.id;

            //if(location.hostname.indexOf("localhost") >= 0){
            //    urlBattle = "/ws/battle/"+stage.id;
            //}

            //var requestBattle = {
            //    url: urlBattle,
            //    contentType: "application/json",
            //    transport: 'websocket',
            //    trackMessageLength : true,
            //    fallbackTransport: 'long-polling'
            //};

            //requestBattle.onOpen = function(response){
            //    console.log("Conexão aberta battle: "+response.transport);
            //}

            //requestBattle.onError = function(response){
            //    console.log("Erro battle: "+response.transport);
            //}

            //requestBattle.onMessage =
            var handleMessage = function(response){
                $scope.received++;
                var turn = JSON.parse(response.responseBody);
                if(turn.message){
                    console.log(turn.message);
                } else{

                    var stage = turn;

                    var activePlayer;

                    _.each(stage.players, function(p){
                        var userId = p.id;
                        var newPos = p.position;
                        var newRot = p.rotation;
                        var pos = new BABYLON.Vector3(newPos.x, newPos.y, newPos.z);
                        var rot = new BABYLON.Vector3(newRot.x, newRot.y, newRot.z);
                        if(userId == stage.activePlayerId){
                            $scope.stage.activePlayerId = userId;
                        }
                        if($scope.stage.selectedPlayer.id == userId){
                            $scope.tank.position = pos;
                            $scope.tank.rotation = rot;
                            $scope.setFuel(p.fuel);
                            $scope.$apply();
                        } else{
                            var enemyTank = _.findWhere($scope.enemyTanks, {userId : userId});
                            if(enemyTank){
                                enemyTank.position = pos;
                                enemyTank.rotation = rot;
                                $scope.$apply();
                            }
                        }
                    });


                    var lastShot = stage.lastShot;

                    if(!!lastShot){

                        var fx = stage.lastShot.fx;
                        var fy = stage.lastShot.fy;
                        var fz = stage.lastShot.fz;

                        $scope.shot.X0 = stage.lastShot.start.x;
                        $scope.shot.Y0 = stage.lastShot.start.y;
                        $scope.shot.Z0 = stage.lastShot.start.z;
                        _.each($scope.shot.variables, function(item, index){
                            $scope.shot['V'+index] = item;
                        });

                        if(!_.isUndefined($scope.shot.lastShot)){
                            $scope.shot.lastShot.dispose();
                        }

                        $scope.shot.points = [];

                        var shotResult = stage.lastShot;

                        var lastPoint;

                        _.every(_.range(shotResult.t), function(iteration){
                            $scope.shot.t = iteration;
                            var x = math.eval(fx, $scope.shot);
                            var y = math.eval(fy, $scope.shot);
                            var z = math.eval(fz, $scope.shot);
                            var point = new BABYLON.Vector3(x, y, z);
                            $scope.shot.points.push(point);
                            lastPoint = point;
                            return true;
                        });

                        var endPoint = new BABYLON.Vector3(shotResult.end.x, shotResult.end.y, shotResult.end.z);
                        if(_.isUndefined(lastPoint)){
                            lastPoint = endPoint;
                        }

                        $scope.shot.points.push(new BABYLON.Vector3(shotResult.end.x, shotResult.end.y, shotResult.end.z));

                        var shotLine = BABYLON.Mesh.CreateLines("shot", $scope.shot.points, $scope.scene);

                        var fireMaterial = new BABYLON.StandardMaterial("fire", $scope.scene);
                        fireMaterial.emissiveTexture = new BABYLON.Texture("jpg/material/fire/fire.jpg", $scope.scene);
                        fireMaterial.uScale = 10;
                        fireMaterial.vScale = 10;

                        var explosion = BABYLON.Mesh.CreateSphere("explosion", 20.0, 20.0, $scope.scene);
                        explosion.position = lastPoint;
                        explosion.parent = shotLine;
                        explosion.material = fireMaterial;

                        $scope.lastShotScore = shotResult.points;
                        $scope.totalScore = parseFloat($scope.totalScore) + parseFloat(shotResult.points);

                        $scope.shot.lastShot = shotLine;
                    }

                }
            }

            //var subSocketBattle = socket.subscribe(requestBattle);
            //$scope.subSocketBattle = subSocketBattle;

            $scope.scene = scene;
        }, defaultErrorCallback)
    }

    //$scope.postMessage = function(){
    //    $scope.subSocketChat.push(JSON.stringify({author: 'blah', message: $scope.post}));
    //}

    gapi.load('auth2', function(){
        auth2 = gapi.auth2.init({
            client_id: '405402394589-f20c532e75fn33c541jfbu5ta179bko4.apps.googleusercontent.com'
        });
        var userTokenId = getTokenIdFromCookie();
        if(_.isUndefined(userTokenId)){
            killCookiesAndRedirect();
        } else{
            $scope.loginFailed = false;
            $scope.$apply();
            sessionResource.get({userTokenId: userTokenId}, function(longshotSession){
                $scope.username = longshotSession.username;
                $scope.userLogoUrl = longshotSession.userLogoUrl;
                startGame();
            });
        }
    });

}])
