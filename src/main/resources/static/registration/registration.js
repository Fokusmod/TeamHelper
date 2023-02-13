angular.module('app', []).controller('registrationController', function ($scope, $http) {
const contextPath = 'http://localhost:3100';

    $scope.registration = function () {
        $http.post(contextPath + '/registration', $scope.user)
            .then(function successCallback(response) {
                $scope.displayMessage(response.data);

            }, function errorCallback(response) {
                $scope.displayMessage(response.data.message);
            });
    };

    $scope.goToAuthorization = function () {
         window.location.replace('http://localhost:3100/index.html');
    };

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