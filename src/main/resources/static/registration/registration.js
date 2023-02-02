angular.module('index-app', []).controller('registrationController', function ($scope, $http) {
const contextPath = 'http://localhost:8080/users';

    $scope.registration = function () {
        $http.post(contextPath + '/add', $scope.userForm)
        .then(function (response) {

        });
    }
// tany@mail.ru tany 101

    $scope.displayMessage = function () {
        var html = document.querySelector('html');
        var panel = document.createElement('div');
        panel.setAttribute('class', 'msgBox');
        html.appendChild(panel);

        var msg = document.createElement('p');
        msg.setAttribute('class', 'msgBox_p');
        msg.textContent = 'Your application for registration has been sent for consideration. Please wait for an email response';
        panel.appendChild(msg);

        var closeBtn = document.createElement('button');
        closeBtn.setAttribute('class', 'msgBox_button');
        closeBtn.textContent = 'x';
        panel.appendChild(closeBtn);

        closeBtn.onclick = function () {
        panel.parentNode.removeChild(panel);
        }
    }
});