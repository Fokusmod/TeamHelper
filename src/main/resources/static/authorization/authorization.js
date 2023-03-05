angular.module('index-app').controller('authorizationController', function ($scope, $http, $localStorage, $rootScope, $location,messageService) {

    $scope.displayMessage = messageService.displayMessage;

    const error_color = "#ffe6e6";
    const warn_color = "#fff6e0";
    const ok_color = "#e8fcdb";

    const contextPath = 'http://localhost:3100';


    $scope.afterRegistration = function () {
        if ($rootScope.createdUser != null) {
            document.getElementById("authLogin").value = $rootScope.createdUser.email;
        }
    }

    function authValidate() {
        const login = document.getElementById("authLogin");
        const password = document.getElementById("authPassword");

        if (login.value === '' && password.value === '') {
            $scope.displayMessage("Поля не должны быть пустыми");
            return false;
        } else if (login.value === '' || password.value === '') {
            $scope.displayMessage("Одно из полей пустое");
            return false;
        } else {
            return true;
        }
    }

    $scope.authorization = function () {
        const result = authValidate();
        if (result) {
            $http.post(contextPath + '/auth', $scope.user)
                .then(function successCallback(response) {
                    console.log($scope.user)
                    console.log(response)
                    if (response.data.token) {
                        $http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
                        $localStorage.springWebUser = {login: $scope.user.login, token: response.data.token};
                        $scope.user.login = null;
                        $scope.user.password = null;
                        $location.path("/schedule")
                    }
                }, function errorCallback(response) {
                    if (response.data.statusCode === 403) {
                        $scope.displayMessage(response.data.message, warn_color);

                    } else {
                        $scope.displayMessage(response.data.message, error_color);
                    }
                });
        }
    };

    $scope.relocateToRegistration = function () {
        $location.path("/registration")
    }

    const form = {
        login: document.getElementById("login"),
        password: document.getElementById("password")
    }
    form.login.oninput = (e) => {
        const {value} = e.target
        if (value.length < 4) {
            form.login.getElementsByClassName('msg-error')[0].style.display = 'block';
        } else {
            form.login.getElementsByClassName('msg-error')[0].style.display = 'none';
        }
        if (value) {
            form.login.classList.add('filed')
        } else {
            form.login.classList.remove('filed')
        }
    }

    form.password.oninput = (e) => {
        const {value} = e.target
        if (value.length < 4) {
            form.password.getElementsByClassName('msg-error')[0].style.display = 'block';
        } else {
            form.password.getElementsByClassName('msg-error')[0].style.display = 'none';
        }
        if (value) {
            form.password.classList.add('filed')
        } else {
            form.password.classList.remove('filed')
        }
    }

    $scope.afterRegistration();

});

