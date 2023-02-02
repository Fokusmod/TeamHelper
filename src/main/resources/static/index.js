(function () {
    angular
    .module('index-app', ['ngRoute', 'ngStorage'])
    .config(config)
    .run(run);

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
         .when('/authorization', {
         templateUrl: 'authorization/authorization.html',
         controller: 'authorizationController'
         })
        .otherwise({
            redirectTo: '/schedule'
        });
    }

    function run($rootScope, $http, $localStorage, $location) {
        if ($localStorage.springWebUser) {
            try {
                let jwt = $localStorage.springWebUser.token;
                let payload = JSON.parse(atob(jwt.split('.')[1]));
                let currentTime = parseInt(new Date().getTime() / 1000);
                if (currentTime > payload.exp) {
                    console.log("Token is expired!!!");
                    delete $localStorage.springWebUser;
                    $http.defaults.headers.common.Authorization = '';
                }
            } catch (e) {
            }
            if ($localStorage.springWebUser) {
                $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.springWebUser.token;
            }
        } else {
            $location.path('/authorization');
        }
    }
})();


angular.module('index-app').controller('indexController', function ($rootScope, $scope, $http, $location, $localStorage) {

    $scope.tryToLogout = function () {
            $scope.clearUser();
            $scope.user = null;
            $location.path('/authorization');
        };

        $scope.clearUser = function () {
            delete $localStorage.springWebUser;
            $http.defaults.headers.common.Authorization = '';
        };

        $rootScope.isUserLoggedIn = function () {
            if ($localStorage.springWebUser) {
                return true;
            } else {
            $location.path('/authorization');
            return false;
            }
        };

        $rootScope.isUser = function () {
            if ($localStorage.springWebUser.username) {
                return $localStorage.springWebUser.username;
            } else {
                return null;
            }
        };

});




