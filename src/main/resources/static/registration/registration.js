angular.module('index-app').controller('registrationController', function ($scope, $http, $localStorage, $rootScope, $location) {

    const contextPath = 'http://localhost:3100';
    const reg = /^([A-Za-z\d_\-.])+@([A-Za-z\d_\-.])+\.([A-Za-z]{2,4})$/;

    $scope.registration = function () {
        const result = validation();
        console.log(result)
        if (result) {
            $http.post(contextPath + '/registration', $scope.user)
                .then(function successCallback(response) {
                    $rootScope.displayMessage("Пользователь был успешно создан, но для продолжения работы с " +
                        "приложением Вы должны быть одобрены администратором.");
                    $localStorage.createdUser = response.data;
                    $location.path('/authorization');
                }, function errorCallback(response) {
                    $rootScope.displayMessage(response.data.message);
                    // $scope.user = undefined
                });
        }
    };

    function validation() {
        const login = document.getElementById("inputEmail");
        const password = document.getElementById("inputPassword");
        const username = document.getElementById("inputUsername");

        if (!reg.test(login.value)) {
            $rootScope.displayMessage("Поле E-mail должно быть в формате example@mail.ru");
            return false;
        }
        if (login.value === '' && password.value === '' && username.value === '') {
            $rootScope.displayMessage("Поля не должны быть пустыми");
            return false
        } else if (login.value === '' || password.value === '' || username.value === '') {
            $rootScope.displayMessage("Одно из полей пустое");
            return false;
        } else {
            return true
        }
    }


    const form = {
        login: document.getElementById("regEmail"),
        password: document.getElementById("regPassword"),
        username: document.getElementById("regUsername")
    }
    form.login.oninput = (e) => {
        const {value} = e.target
        if (!reg.test(value)) {
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

    form.username.oninput = (e) => {
        const {value} = e.target
        if (value.length < 4) {
            form.username.getElementsByClassName('msg-error')[0].style.display = 'block';
        } else {
            form.username.getElementsByClassName('msg-error')[0].style.display = 'none';
        }
        if (value) {
            form.username.classList.add('filed')
        } else {
            form.username.classList.remove('filed')
        }
    }

    $scope.relocateToAuthorization = function () {
        $location.path("/authorization")
    }

});