angular.module('index-app').controller('clientListController', function ($scope, $http, $location, $localStorage, $rootScope) {

    const clients = document.getElementById("client-list")
    const waitList = document.getElementById("wait-list")

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

    $scope.clientPanel();

    let stompClient = null;

    function connect() {
        let socket = new SockJS("http://localhost:3100/websocket");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/events/new-clients', function (message) {
                $scope.getAllClient();
            });
        });
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    }









    $scope.setVisibleClientPage = function () {
        clients.style.visibility = "visible"
        waitList.style.visibility = "hidden"
    }
    $scope.setVisibleWaitList = function () {
        clients.style.visibility = "hidden"
        waitList.style.visibility = "visible"
    }

    $scope.getAllClient = function () {
        $http.get("http://localhost:3100/clients")
            .then(function successCallback(responce) {
                $scope.orderClient = responce.data;
                console.log(responce.data)
            }, function failCallback(responce) {

            })
    }

    connect();
    $scope.setVisibleClientPage();
    $scope.getAllClient();
});