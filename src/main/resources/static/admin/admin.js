angular.module('index-app').controller('adminController', function ($scope, $http, $location, $localStorage, $rootScope) {
const contextPath = 'http://localhost:3100';


    $rootScope.isUserLoggedIn = function () {
        if ($localStorage.springWebUser) {
            return true;
        } else {
            $location.path('/authorization');
            return false;
        }
    };


    $scope.loadUsers = function () {
        $http.get(contextPath + '/api/v1/user')
        .then(function (response) {
        $scope.usersList = response.data;
        });
    };


    $scope.loadNotApprovedUsers = function () {
     var user_status ='not_approved';
        $http({
            url: contextPath + '/api/v1/user/status',
            method: 'GET',
            params: {
                status: user_status
            }
        }).then(function (response) {
            $scope.notApprovedUsersList = response.data;
        });
    };


    $scope.deleteUser = function (userId) {
        $http.post(contextPath + '/api/v1/user/delete/' + userId)
        .then(function (response) {
            $scope.loadUsers();
            $scope.loadNotApprovedUsers();
        });
    };

    $scope.approveUser = function (userId) {
        $http.post(contextPath + '/user_approved/' + userId)
        .then(function (response) {
            $scope.loadUsers();
            $scope.loadNotApprovedUsers();
        });
    };

    $scope.changeRole = function (userId) {
        $http.post(contextPath + '/user_approved/' + userId)
        .then(function (response) {
            $scope.loadUsers();
            $scope.loadNotApprovedUsers();
        });
    };

    $scope.loadUsers();
    $scope.loadNotApprovedUsers();
});