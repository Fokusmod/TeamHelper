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
            .when('/registration', {
                templateUrl: 'registration/registration.html',
                controller: 'registrationController'
            })
            .when('/admin', {
                templateUrl: 'admin/admin.html',
                controller: 'adminController'
            })
            .otherwise({
                redirectTo: '/schedule'
            });
    }

    function run($rootScope, $http, $localStorage, $location) {
        if ($localStorage.springWebUser) {
            if ($localStorage.springWebUser) {
                $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.springWebUser.token;
            }
            // try {
            //     let jwt = $localStorage.springWebUser.token;
            //     let payload = JSON.parse(atob(jwt.split('.')[1]));
            //     let currentTime = parseInt(new Date().getTime() / 1000);
            //     if (currentTime > payload.exp) {
            //         console.log("Token is expired!!!");
            //         delete $localStorage.springWebUser;
            //         $http.defaults.headers.common.Authorization = '';
            //     }
            // } catch (e) {
            // }

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
        console.log($localStorage.springWebUser)
        return !!$localStorage.springWebUser;
    };


    $scope.showUsername = function () {
        if ($localStorage.springWebUser) {
        $scope.userlogin = $localStorage.springWebUser.login;
        schedule.style.cssText='background-color: #eff1f4; color:#00152a;';
        } else {
        $location.path('/authorization');
        }
    };

    $scope.menuStyle = function (href) {
    var notActive = 'background-color: transparent; color:#f1f1f1;';
    var active = 'background-color: #eff1f4; color:#00152a;';
        if (href === 'clientList') {
        clientList.style.cssText=active;
        schedule.style.cssText=notActive;
        admin.style.cssText=notActive;
        }
        if (href === 'schedule') {
        clientList.style.cssText=notActive;
        schedule.style.cssText=active;
        admin.style.cssText=notActive;
        }
        if (href === 'admin') {
        clientList.style.cssText=notActive;
        schedule.style.cssText=notActive;
        admin.style.cssText=active;
        }
    };
    $scope.menuStyle();
    $scope.showUsername();

});





