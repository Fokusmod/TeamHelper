angular.module('index-app').controller('scheduleController', function ($scope, $http, $location, $localStorage) {
     $rootScope.isUserLoggedIn = function () {
        if ($localStorage.springWebUser) {
            return true;
        } else {
            $location.path('/authorization');
            return false;
        }
     };

    $scope.getTeams = function () {
        $http.get("http://localhost:3100/teams")
            .then(function succesCallback (responce) {
                console.log(responce)
                $scope.teams = responce.data;
            }, function failCallback(responce){

            })
    };

    $scope.getTeams();


});

// $scope.regRequest = function () {
//     $http.post(contextPath + '/registration', $scope.new_user)
//         .then(function successCallback(response) {
//             $scope.new_user = null;
//             alert(response.data.messages)
//             $location.path('/auth');
//         }, function failCallback(response) {
//             alert(response.data.messages)
//         })
// };
//
// $scope.tryToRegistration = function () {
//     if ($scope.new_user.username == null || $scope.new_user.password == null || $scope.new_user.email == null) {
//         alert("Поля обязательны для заполнения")
//     } else {
//         $scope.regRequest();
//     }
// }
