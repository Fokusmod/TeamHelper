angular.module('index-app').controller('clientListController', function ($scope, $http, $location, $localStorage, $rootScope) {

    const clients = document.getElementById("client-list")
    const waitList = document.getElementById("wait-list")

    //TODO это и так рутскоуп. его не нужно добавлять везде.
    $rootScope.isUserLoggedIn = function () {
        if ($localStorage.springWebUser) {
            return true;
        } else {
            $location.path('/authorization');
            return false;
        }
    };

    $scope.setVisibleClientPage = function () {
        clients.style.visibility = "visible"
        waitList.style.visibility = "hidden"
    }
    $scope.setVisibleWaitList = function () {
        clients.style.visibility = "hidden"
        waitList.style.visibility = "visible"
    }

    $scope.setVisibleClientPage();
});