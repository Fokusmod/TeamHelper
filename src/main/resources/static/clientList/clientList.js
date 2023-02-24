angular.module('index-app').controller('clientListController', function ($scope, $http, $location, $localStorage, $rootScope) {

    const clients = document.getElementById("client-list")
    const waitList = document.getElementById("wait-list")

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


    $scope.setVisibleClientPage();
    $scope.getAllClient();
});