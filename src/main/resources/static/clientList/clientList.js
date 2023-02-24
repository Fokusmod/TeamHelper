angular.module('index-app').controller('clientListController', function ($scope, $http, $location, $localStorage, $rootScope) {


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

});