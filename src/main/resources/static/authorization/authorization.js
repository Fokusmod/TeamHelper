angular.module('index-app').controller('authorizationController', function ($scope, $http, $localStorage, $rootScope) {
const contextPath = 'http://localhost:3100';
    $scope.authorization = function () {
        $http.post(contextPath + '/auth', $scope.user)
            .then(function successCallback(response) {
                if (response.data.token) {
                $http.defaults.headers.common.Authorization = 'Bearer ' + response.data.token;
                console.log($scope.user.username);
                console.log(response.data.token);

                $localStorage.springWebUser = {username: $scope.user.username, token: response.data.token};
                $scope.user.username = null;
                $scope.user.password = null;

                window.location.replace('http://localhost:3100/index.html?#!/schedule');
                } else {
                $scope.displayMessage('Sorry, your registration application has not yet been approved.');
                }
            }, function errorCallback(response) {
            });
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