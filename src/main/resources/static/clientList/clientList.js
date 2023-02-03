angular.module('index-app').controller('clientListController', function ($scope, $http, $location, $localStorage) {
   $rootScope.isUserLoggedIn = function () {
            if ($localStorage.springWebUser) {
                return true;
            } else {
                $location.path('/authorization');
                return false;
            }
        };
});