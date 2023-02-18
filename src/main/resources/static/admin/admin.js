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
            url: contextPath + 'api/v1/user/status',
            method: 'GET',
            params: {
                status: user_status
            }
        }).then(function (response) {
        console.log(data);
            $scope.notApprovedUsersList = response.data;
        });
    };


/*    $scope.deleteUser = function (userId) {
        $http.post(contextPath + '/api/v1/user/delete/' + userId)
        .then(function (response) {
            $scope.loadUsers();
            $scope.loadNotApprovedUsers();
        });
    };*/

    $scope.approveUser = function (userId) {
        $http.post(contextPath + '/api/v1/user/' + userId + '/status/' + 1)
        .then(function (response) {
            $scope.loadUsers();
/*            $scope.loadNotApprovedUsers();*/
        });
    };

/*    $scope.getRoles = function () {
        $http.get("http://localhost:3100/api/v1/role")
            .then(function successCallback(response) {
                $scope.roles = response.data;
                $scope.selectedRole = $scope.roles[0];
            }, function failCallback(response) {

            })
    };*/

/*     $scope.changeRole = function (userID) {
            let selectedRole = document.getElementById('role-selected').value;
            console.log(selectedRole);
            $http.put(contextPath + '/api/v1/user/' + userID + '/change_role/' + selectedRole)
                .then(function successCallback(response) {

                }, function failCallback(response) {
            })
     }*/

    $scope.loadUsers();
    $scope.loadNotApprovedUsers();
/*    $scope.getRoles();*/

});