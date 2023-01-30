angular.module('index-app', ['ngRoute', 'ngStorage'])
    .config(config);

function config($routeProvider) {
    $routeProvider
        .when('/clientList', {
            templateUrl: 'clientList/clientList.html',
            controller: 'clientListController'
        })
        .when('/schedule', {
            templateUrl: 'schedule/schedule.html',
            controller: 'scheduleController'
        })
        .otherwise({
            redirectTo: '/clientList'
        });
}


angular.module('index-app').controller('indexController', function ($rootScope, $scope, $http, $localStorage) {

    $scope.changeContent = function () {
        console.log(123);
    }

});




