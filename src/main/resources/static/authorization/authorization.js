angular.module('app', []).controller('authorizationController', function ($scope, $http) {
const contextPath = 'http://localhost:8080/users';

    $scope.authorization = function () {
            $http.post(contextPath + '/auth', $scope.userForm).
            then(function (response) {
            if (response.data) {
                $scope.displayMessage('welcome!');
            } else {
                $scope.displayMessage('Sorry, your registration application has not yet been approved.');
            }

            });
    }

    $scope.displayMessage = function (msgText) {
        var html = document.querySelector('html');
        var panel = document.createElement('div');
        panel.setAttribute('class', 'msgBox');
        html.appendChild(panel);

        var msg = document.createElement('p');
        msg.setAttribute('class', 'msgBox_p');
        msg.textContent = msgText;
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