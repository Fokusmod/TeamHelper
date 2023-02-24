angular.module('index-app').controller('clientListController', function ($scope, $http, $location, $localStorage, $rootScope) {

    var stompClient = null;

    $scope.clientPanel = function () {
        const clientItemPanel = document.getElementById('item-client');
        const scheduleItemPanel = document.getElementById('item-schedule');
        const adminItemPanel = document.getElementById('item-admin');

        clientItemPanel.style.backgroundColor = "#eff1f4";
        clientItemPanel.style.color = "#00152a";

        scheduleItemPanel.style.backgroundColor = "transparent";
        scheduleItemPanel.style.color = "#f1f1f1";

        adminItemPanel.style.backgroundColor = "transparent";
        adminItemPanel.style.color = "#f1f1f1";
    }

    $scope.connect = function () {
        var socket = new SockJS("http://localhost:8181/market-core/web-socket")
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected:' + frame);
            stompClient.subscribe('/topic/file', function (message) {
                $scope.download(message.body);
            })
        })
    };

    $scope.connect = function () {
        var socket = new SockJS("http://localhost:8181/market-core/web-socket")
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected:' + frame);
            stompClient.subscribe('/topic/file', function (message) {
                $scope.download(message.body);
            })
        })
    };

    $scope.disconnect = function () {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    };

    $scope.sendMessage = function () {
        stompClient.send("/app/web-socket", {},
            "productInfo.xlsx");
    };

    $scope.download = function (fileUrl) {
        window.location.href = fileUrl;
    };

    window.onload = new function () {
        $scope.connect();
    };


    $scope.clientPanel();

});