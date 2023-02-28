angular.module('index-app').controller('clientListController', function ($scope, $http, $location, $localStorage, $rootScope) {

    const error_color = "#ffe6e6";
    const warn_color = "#fff6e0";
    const ok_color = "#e8fcdb";

    $scope.displayMessage = function (msgText, color) {
        const html = document.querySelector('html');
        const panel = document.createElement('div');
        panel.setAttribute('class', 'msgBox');
        panel.style.backgroundColor = color;
        panel.setAttribute('id', 'msgBox');
        html.appendChild(panel);

        const msg = document.createElement('p');
        msg.setAttribute('class', 'msgBox_p');
        msg.textContent = msgText;
        panel.appendChild(msg);
    }

    document.addEventListener('click', function (event) {
        const e = document.getElementById('msgBox');
        if (e !== null) {
            if (!e.contains(event.target)) e.remove()
        }
    });

    const clients = document.getElementById("client-list")
    const waitList = document.getElementById("wait-list")

    $scope.clientPanel = function () {
        const clientItemPanel = document.getElementById('item-client');
        const scheduleItemPanel = document.getElementById('item-schedule');
        const adminItemPanel = document.getElementById('item-admin');

        clientItemPanel.style.backgroundColor = "#eff1f4";
        clientItemPanel.style.color = "#00152a";

        scheduleItemPanel.style.backgroundColor = "transparent";
        scheduleItemPanel.style.color = "#f1f1f1";

        adminItemPanel.style.backgroundColor = "transparent";
        adminItemPanel.style.color = "#f1f1f1";
    }


    $scope.clientPanel();

    let stompClient = null;

    function connect() {
        let socket = new SockJS("http://localhost:3100/websocket");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/events/message', function (responce) {
                $scope.getAllClient();
            });
        });
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    $scope.setVisibleClientPage = function () {
        clients.style.visibility = "visible"
        waitList.style.visibility = "hidden"
    }
    $scope.setVisibleWaitList = function () {
        clients.style.visibility = "hidden"
        waitList.style.visibility = "visible"
    }

    $scope.getAllClient = function () {
        return new Promise(function (resolve, reject) {
            $http.get("http://localhost:3100/clients")
                .then(function successCallback(responce) {
                    $scope.orderClient = responce.data;
                    console.log(responce.data)
                    resolve();
                }, function failCallback(responce) {
                    console.log(response.data);
                    reject();
                });
        });
    }

    $scope.sendOrder = function () {
        console.log("send button")
    }


    $scope.additionalPanel = function (order) {
        const div = document.getElementById(order.id);
        const separator = document.getElementById('separator-' + order.id)
        let sep = document.getElementsByClassName('separator-client');
        if (sep.length>=1) {
            sep[0].remove();
        }

        if (separator) {
            separator.remove();
        } else {
            const separator = document.createElement('div');
            separator.id = "separator-" + order.id
            separator.classList.add('separator-client')

            const newDiv = document.createElement("div");
            newDiv.classList.add('additionalPanel')

            const labelOne = document.createElement('div')
            labelOne.classList.add('label-client')
            labelOne.textContent = "Редактирование заказа или формата:"
            newDiv.appendChild(labelOne)


            const origin = document.createElement('textarea')
            origin.classList.add('area-client');
            if (order.originInfo === null) {
                if (order.armoryLink === null) {
                    origin.value = order.noParseInfo + "\n===";
                } else {
                    origin.value = order.noParseInfo + "\n" + order.armoryLink + "\n===";
                }

            } else {
                origin.value = order.originInfo;
            }
            newDiv.appendChild(origin)

            const format = document.createElement('button')
            format.classList.add('send-button')
            format.style.marginLeft = "50px"
            format.style.marginBottom = "15px"
            format.textContent = "Изменить"
            format.onclick = function () {
                $scope.parseOrderInfo(order, origin.value)
            }
            newDiv.appendChild(format)

            const labelTwo = document.createElement('div')
            labelTwo.classList.add('label-client')
            labelTwo.textContent = "Комментарий:"
            newDiv.appendChild(labelTwo)

            const comment = document.createElement('textarea')
            comment.classList.add("area-client")
            comment.value = order.orderComments
            newDiv.appendChild(comment)

            const ready = document.createElement('button')
            ready.classList.add('send-button')
            ready.style.marginLeft = "50px"
            ready.style.marginBottom = "15px"
            ready.textContent = "Готово"
            ready.onclick = function () {
                $scope.setComments(order, comment.value)
            }
            newDiv.appendChild(ready)

            separator.appendChild(newDiv)

            div.insertAdjacentElement("afterend", separator)

            if (order.noParseInfo !== null) {
                origin.style.backgroundColor = "#fff4f4"
            } else {
                origin.style.backgroundColor = "#f5ffee"
            }
        }

    }


    $scope.setComments = function (order, comment) {
        let textComment = "null";
        if (comment.length > 0) {
            textComment = comment;
        }
        $http.put("http://localhost:3100/clients/" + order.id + "/comment", textComment)
            .then(function successCallback(responce) {
                order.orderComments = responce.data.orderComments
                const div = document.getElementById('separator-'+order.id);
                div.remove();
            }, function failCallback(responce) {
                console.log(responce.data)
            })
    }


    $scope.parseOrderInfo = async function (order, value) {
        try {
            const response = await $http.put("http://localhost:3100/clients/" + order.id + "/order-info", value);
            await $scope.getAllClient();
            $scope.additionalPanel(response.data[0]);
        } catch (error) {
            $scope.displayMessage("Не удалось успешно завершить форматирование. Все изменения были сброшены.", error_color);
            await $scope.getAllClient();
            $scope.additionalPanel(order);
        }
    };


    connect();
    $scope.setVisibleClientPage();
    $scope.getAllClient();
});

