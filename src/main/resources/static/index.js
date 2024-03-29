(function () {
    angular
        .module('index-app', ['ngRoute', 'ngStorage'])
        .config(config)
        .factory('messageService', function () {
            return {
                displayMessage: function (msgText, color) {
                    const html = document.querySelector('html');
                    const panel = document.createElement('div');
                    panel.setAttribute('class', 'msgBox');
                    panel.style.backgroundColor = color;
                    panel.setAttribute('id', 'msgBox');
                    html.appendChild(panel);
                    const msg = document.createElement('p');
                    msg.setAttribute('class', 'msgBox_p');
                    msg.textContent = msgText;
                    panel.appendChild(msg);
                }

            }
        })
        .run(run);

    document.addEventListener('click', function (event) {
        const e = document.getElementById('msgBox');
        if (e !== null) {
            if (!e.contains(event.target)) e.remove()
        }
    });

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
        return !!$localStorage.springWebUser;
    };


    $scope.showUsername = function () {
        if ($localStorage.springWebUser) {
            $scope.userlogin = $localStorage.springWebUser.login;
        } else {
            $location.path('/authorization');
        }
    };

    $scope.clientPage = function () {
        $location.path('/clientList');

    };
    $scope.schedulePage = function () {
        $location.path('/schedule');
    };

    $scope.adminPage = function () {
        $location.path('/admin');
    };

    $scope.showUsername();

});





