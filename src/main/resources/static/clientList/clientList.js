angular.module('index-app').controller('clientListController', function ($scope, $http, $location, $localStorage, $rootScope, messageService, $window) {

    let socket = new SockJS("http://localhost:3100/websocket");
    let stompClient = null;

    const error_color = "#ffe6e6";
    const warn_color = "#fff6e0";
    const ok_color = "#e8fcdb";

    let classList = ['Paladin', 'Warrior', 'Hunter', 'Rogue', 'Priest', 'Shaman', 'Mage', 'Warlock', 'Monk', 'Druid',
        'Demon hunter', 'Death knight', 'Evoker']
    let zenithToken = ['Evoker', 'Monk', 'Rogue', 'Warrior']
    let dreadfulToken = ['Death knight', 'Warlock', 'Demon hunter']
    let veneratedToken = ['Paladin', 'Priest', 'Shaman']
    let mysticToken = ['Mage', 'Druid', 'Hunter']

    let cloth = ['Mage', 'Priest', 'Warlock']
    let plate = ['Paladin', 'Death knight', 'Warrior']
    let mail = ['Hunter', 'Shaman', 'Evoker']
    let leather = ['Druid', 'Demon hunter', 'Monk', 'Rogue']

    let attentionId = [];

    $scope.displayMessage = messageService.displayMessage;

    const clients = document.getElementById("client-list")
    const waitList = document.getElementById("wait-list")
    const activeEvents = document.getElementById('active-events')
    const allEvents = document.getElementById('all-events')

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


    addEventListener('click', (event) => {

        let divs = document.getElementsByClassName('panel-item-title')
        for (const div of divs) {
            if (div === event.target && div.textContent !== "Клиенты") {
                disconnect()
            }
        }
    });


    window.addEventListener('load', function () {
        var scrollHeight = Math.max(
            document.documentElement.scrollHeight,
            document.body.scrollHeight
        );
        var clientHeight = document.documentElement.clientHeight;
        var footer = document.querySelector('footer');

        if (scrollHeight <= clientHeight) {
            footer.classList.add('visible');
        }
    });

    var scroll = document.getElementsByClassName("page")[0];


    scroll.addEventListener('scroll', function () {
        var scrollHeight = scroll.scrollHeight;
        var scrollTop = scroll.scrollTop;
        var clientHeight = scroll.clientHeight;
        var footer = document.querySelector('footer');

        if (scrollHeight <= clientHeight) {
            footer.classList.add('visible');
        } else if (scrollTop + clientHeight >= scrollHeight) {
            footer.classList.add('visible');
        } else {
            footer.classList.remove('visible');
        }
    });


    $window.addEventListener('beforeunload', function () {
        disconnect()
    });

    $scope.$on('$destroy', function () {
        disconnect()
    });

    $scope.clientPanel();

    let message = null;

    function connect() {
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            stompClient.subscribe('/events/message', function (responce) {
                if (responce.body.includes("MESSAGE")) {
                    message = responce.body.split("/")
                    attentionId.push(message[1]);
                    messageService.displayMessage(message[2] + message[3], warn_color);
                } else if (responce.body.includes("ACTIVE")) {
                    $scope.getActiveEvents(responce.body)
                } else if (responce.body.includes("EVENT")) {
                    $scope.changeEvent(responce.body);
                } else {
                    message = responce.body.split("/")
                    $scope.getNewClients(message)
                        .then(r => $scope.checkBundle())
                        .then(a => $scope.attentionOrder())
                }
            });
        });
    }

    $scope.changeEvent = async function (message) {
        var eventId = message.split("/")[1];
        var orderId = message.split("/")[2];
        var order = await $scope.getClient(orderId);

        if ($scope.activeEvents !== undefined) {
            for (let i = 0; i < $scope.activeEvents.length; i++) {
                if ($scope.activeEvents[i].id === Number(eventId)) {
                    $scope.changeClientInEvent($scope.activeEvents[i], order)
                    break;
                }
            }
        }

        if ($scope.eventsWithClient !== undefined) {
            for (let i = 0; i < $scope.eventsWithClient.length; i++) {
                if ($scope.eventsWithClient[i].id === Number(eventId)) {
                    $scope.changeAllEventsList($scope.eventsWithClient[i], order)
                    break;
                }
            }
        }
    }

    $scope.changeAllEventsList = function (event, order) {
        var existClient = false;
        for (let i = 0; i < event.clientList.length; i++) {
            if (event.clientList[i].id === Number(order.id)) {
                existClient = true;
                break;
            }
        }
        if (!existClient) {
            for (let i = 0; i < $scope.eventsWithClient.length; i++) {
                if ($scope.eventsWithClient[i].id === Number(event.id)) {
                    $scope.eventsWithClient[i].clientList.push(order);
                }
            }
        }
    }

    $scope.changeClientInEvent = function (event, order) {
        var existClient = false;
        for (let i = 0; i < event.clientList.length; i++) {
            if (event.clientList[i].id === Number(order.id)) {
                existClient = true;
                break;
            }
        }
        if (!existClient) {
            for (let i = 0; i < $scope.activeEvents.length; i++) {
                if ($scope.activeEvents[i].id === Number(event.id)) {
                    $scope.activeEvents[i].clientList.push(order);
                }
            }
        }
    }

    $scope.getClient = function (id) {
        return $http.get("http://localhost:3100/clients/" + id)
            .then(function successCallback(response) {
                return response.data;
            }, function errorCallback(response) {
                console.log(response);
                throw new Error("Failed to fetch client");
            });
    };

    $scope.getActiveEvents = function (message) {
        if (message === undefined) {
            $http.get("http://localhost:3100/events/active")
                .then(function successCallback(response) {
                    console.log(response)
                    if ($scope.activeEvents === undefined) {
                        $scope.activeEvents = response.data;
                        $scope.setEventTimer();
                    } else {
                        $scope.filterActiveEvents(response.data)
                    }
                }, function failCallback(response) {
                    console.log(response)
                })
        } else {
            let messages = message.split("/");
            $http.post("http://localhost:3100/events/active", messages[1])
                .then(function successCallback(response) {
                    console.log(response)
                    if ($scope.activeEvents === undefined) {
                        $scope.activeEvents = response.data;
                    } else {
                        $scope.filterActiveEvents(response.data)
                    }
                }, function failCallback(response) {
                    console.log(response)
                })
        }
    }

    $scope.getCurrentMoscowTime = function () {
        return new Promise(function (resolve, reject) {
            var xhr = new XMLHttpRequest();
            xhr.open('GET', 'http://worldtimeapi.org/api/timezone/Europe/Moscow', false);
            xhr.onload = function () {
                if (xhr.status === 200) {
                    let time = xhr.responseText;
                    let z = JSON.parse(time).utc_datetime;
                    let time1 = new Date(z).getTime();
                    let timestampPlus = time1 + (60 * 60 * 1000);
                    resolve(new Date(timestampPlus));
                } else {
                    reject(xhr.status + ': ' + xhr.statusText);
                }
            };
            xhr.onerror = function () {
                reject('Network Error');
            };
            xhr.send();
        });
    }


    $scope.setEventTimer = async function () {
        let dateString
        await $scope.getCurrentMoscowTime().then(function (result) {
            dateString = result

        }).catch(function (error) {
            console.log(error);
        });
        let moscowTime = new Date(Date.parse(dateString));
        moscowTime.setHours(moscowTime.getHours() - 1)

        for (let i = 0; i < $scope.activeEvents.length; i++) {
            if ($scope.activeEvents[i].timeLeft === undefined) {
                var dateStr = $scope.activeEvents[i].eventDate;
                var timeStr = $scope.activeEvents[i].startedAt;

                var timeParts = timeStr.split(':');
                var hours = parseInt(timeParts[0]);
                var minutes = parseInt(timeParts[1]);

                var dateParts = dateStr.split('.');
                var year = 2000 + parseInt(dateParts[2]);
                var month = parseInt(dateParts[1]) - 1;
                var day = parseInt(dateParts[0]);

                var date = new Date(year, month, day, hours, minutes);

                var timeLeft = date.getTime() - moscowTime.getTime();
                if (timeLeft < 0) {
                    $scope.activeEvents[i].timeLeft = "Рейд начался..."
                } else {
                    $scope.timerEvent($scope.activeEvents[i], timeLeft);
                }
            }
        }
    }

    $scope.timerEvent = function (event, timeLeft) {
        var time = timeLeft;
        var intervalId = setInterval(function () {

            var days = Math.floor(time / (1000 * 60 * 60 * 24));
            var hours = Math.floor((time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            var minutes = Math.floor((time % (1000 * 60 * 60)) / (1000 * 60));
            var seconds = Math.floor((time % (1000 * 60)) / 1000);

            event.timeLeft = "Старт через: " + minutes + " мин. " + seconds + " сек.";
            $scope.$applyAsync();
            time -= 1000;

            if (time < 0) {
                clearInterval(intervalId);
                event.timeLeft = "Рейд начался...";
            }
        }, 1000);
    }


    $scope.filterActiveEvents = function (response) {
        if ($scope.activeEvents !== undefined) {
            $scope.checkNewEvents(response)
            $scope.checkChangeEvent(response)
            $scope.setEventTimer();
        }
    }

    $scope.checkNewEvents = function (response) {
        for (let i = 0; i < response.length; i++) {
            var exists = false;
            for (let j = 0; j < $scope.activeEvents.length; j++) {
                if (response[i].id === $scope.activeEvents[j].id) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                $scope.activeEvents.push(response[i]);
            }
        }
    }
    $scope.checkRemoveEvents = function (response) {
        var itemsToRemove = [];
        for (let i = 0; i < $scope.activeEvents.length; i++) {
            var exist = false;
            for (let j = 0; j < response.length; j++) {
                if ($scope.activeEvents[i].id === response[j].id) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                itemsToRemove.push(i);
            }
        }
        if (itemsToRemove.length !== 0) {
            for (let k = itemsToRemove.length - 1; k >= 0; k--) {
                $scope.activeEvents.splice(itemsToRemove[k], 1);
            }
        }
    }

    $scope.checkChangeEvent = function (response) {
        $scope.checkRemoveEvents(response);
    }

    $scope.attentionOrder = function () {
        if (message === null) {
            return;
        }
        const info = `
        <div class="not-bundle-info" style="background-color: yellow; margin: 5px 20px; border-radius: 5px; padding: 5px">
        <i class="fa fa-bell"></i>${message[2]}</div>`;
        for (const id of attentionId) {
            console.log(id)
            var div = document.getElementById(id);
            console.log(div)
            if (div === null) {
                setTimeout(function () {
                    $scope.attentionOrder();
                }, 100);
                break;
            } else {
                div.classList.add('attention')
                let order = div.getElementsByClassName("order-info")[0]
                order.insertAdjacentHTML('beforebegin', info);
            }
        }
    }

    $scope.closeAttention = function () {
        for (const id of attentionId) {
            var div = document.getElementById(id);
            if (div !== null) {
                div.classList.remove('attention')
                div.getElementsByClassName('not-bundle-info')[0].remove();
                const index = attentionId.indexOf(id);
                if (index !== -1) {
                    attentionId.splice(index, 1);
                }
            }
        }
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
    }


    $scope.setClientPage = function () {
        $scope.getNewClients();
        clients.style.display = "block"
        waitList.style.display = "none"
        activeEvents.style.display = "none"
        allEvents.style.display = "none"
    }
    $scope.setWaitListPage = function () {
        clients.style.display = "none"
        waitList.style.display = "block"
        activeEvents.style.display = "none"
        allEvents.style.display = "none"
    }
    $scope.setActiveEventPage = function () {
        clients.style.display = "none"
        waitList.style.display = "none"
        activeEvents.style.display = "block"
        allEvents.style.display = "none"
        $scope.getActiveEvents();
    }
    $scope.setAllEventsPage = function () {
        $scope.getEventsWithClients();
        clients.style.display = "none"
        waitList.style.display = "none"
        activeEvents.style.display = "none"
        allEvents.style.display = "block"
    }


    $scope.showFindResult = function () {
        let currentSendContainer = document.getElementsByClassName("send-order-container")
        if (currentSendContainer.length >= 1) {
            currentSendContainer[0].remove();
        }

        var input = $scope.findInput;
        if (input.length !== 0) {
            var findPage = document.getElementById("find-list");
            findPage.style.display = "block"
            var span = document.getElementById("result-span");
            $http.post("http://localhost:3100/clients/find", input)
                .then(function successCallback(responce) {
                    $scope.result = responce.data;
                    span.textContent = "Результаты поиска по запросу <" + input + "> : " + $scope.result.length + " найдено";
                }, function failCallback(responce) {
                    console.log(responce)
                })
        }
    }

    $scope.closeFindResult = function () {
        $scope.findInput = undefined;
        var findPage = document.getElementById("find-list");
        findPage.style.display = "none"

        let currentSendContainer = document.getElementsByClassName("send-order-container")
        if (currentSendContainer.length >= 1) {
            currentSendContainer[0].remove();
        }
    }

    $scope.getEventsWithClients = function () {
        $http.get("http://localhost:3100/events/with-clients")
            .then(function successCallback(responce) {
                $scope.eventsWithClient = responce.data;
                console.log(responce)
            }, function failCallback(responce) {
                console.log(responce)
            })
    }

    $scope.checkNewOrder = function (orderClient, response) {
        for (let i = 0; i < response.length; i++) {
            var exists = false;
            for (let j = 0; j < orderClient.length; j++) {
                if (response[i].id === orderClient[j].id) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                $scope.orderClient.push(response[i]);
            }

        }
    };

    $scope.checkChangeOrder = function (orderClient, response, message) {
        var itemsToRemove = [];
        $scope.changeOrder = [];
        let ids = [];
        if (message !== undefined) {
            if (message[0] === "CHANGE") {
                ids = message[1].split(" ");
            }
        }

        console.log("айди измененного " + ids)

        for (let i = 0; i < orderClient.length; i++) {
            var exist = false;
            for (let j = 0; j < response.length; j++) {
                if (orderClient[i].id === response[j].id) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                itemsToRemove.push(i);
            }
        }
        if (itemsToRemove.length !== 0) {
            for (let k = itemsToRemove.length - 1; k >= 0; k--) {
                $scope.orderClient.splice(itemsToRemove[k], 1);
            }
        }
        if (ids.length !== 0) {
            for (let i = 0; i < response.length; i++) {
                for (let j = 0; j < ids.length; j++) {
                    if (response[i].id === Number(ids[j])) {
                        console.log("change " + response[i].id)
                        $scope.changeOrder.push(response[i])
                    }
                }
            }
        }
        for (let i = 0; i < $scope.changeOrder.length; i++) {
            for (let j = 0; j < $scope.orderClient.length; j++) {
                if ($scope.orderClient[j].id === $scope.changeOrder[i].id) {
                    $scope.orderClient[j] = $scope.changeOrder[i];
                }
            }
        }
    }


    $scope.updateOrderClient = function (response, message) {
        if ($scope.orderClient !== undefined) {
            $scope.checkChangeOrder($scope.orderClient, response, message)
            $scope.checkNewOrder($scope.orderClient, response)
        }

    }

    $scope.getNewClients = async function (message) {
        return new Promise(function (resolve, reject) {
            $http.get("http://localhost:3100/clients")
                .then(function successCallback(responce) {
                    console.log(responce.data)
                    if ($scope.orderClient !== undefined) {
                        $scope.updateOrderClient(responce.data, message);
                    } else {
                        $scope.orderClient = responce.data;
                    }
                    $scope.checkBundle()
                    resolve();
                }, function failCallback(responce) {
                    console.log(response.data);
                    reject();
                });
        });
    }

    $scope.showAddNewOrder = function () {
        var overlay = document.getElementsByClassName("overlay-new-order")[0];
        var content = document.getElementsByClassName("add-new-order")[0];
        overlay.classList.add('show')
        content.classList.add("zoomIn")
    }

    $scope.cancelNewOrder = function () {
        var overlay = document.getElementsByClassName("overlay-new-order")[0];
        var settingsBundle = document.getElementsByClassName("add-new-order")[0];
        var span = document.getElementsByClassName("info-span")[0];
        $scope.textareaText = undefined;
        span.textContent = "При добавлении стороннего заказа убедитесь что вы дописали символьный разделитель " +
            "'===' на новой строке. Без него заказ не будет добавлен."
        overlay.classList.add('hide')
        settingsBundle.classList.add("zoomOut")
        setTimeout(function () {
            overlay.classList.remove("show")
            overlay.classList.remove("hide")
            settingsBundle.classList.remove("zoomIn")
            settingsBundle.classList.remove("zoomOut")
        }, 350);
    }

    $scope.autoResizeTextarea = function () {
        var textarea = document.querySelector("textarea");
        textarea.style.height = "auto";
        textarea.style.height = textarea.scrollHeight + "px";
    }
    $scope.saveNewOrder = function () {
        var span = document.getElementsByClassName("info-span")[0];
        const defaultValue = span.textContent;
        if ($scope.textareaText === undefined) {
            span.textContent = "Введите информацио и заказе."
        } else if ($scope.textareaText.indexOf("===") === -1) {
            span.textContent = "В введенной вами заказе отсутствует символьный разделитель ===."
        } else {
            $http.post("http://localhost:3100/clients", $scope.textareaText)
                .then(function successCallback(responce) {
                    span.textContent = defaultValue;
                    $scope.cancelNewOrder();
                    setTimeout(function () {
                        messageService.displayMessage("Введенный вами заказ был добавлен", ok_color)
                    }, 350);
                }, function failCallback(responce) {
                    span.textContent = defaultValue;
                    $scope.cancelNewOrder();
                    setTimeout(function () {
                        messageService.displayMessage(responce.data.message, error_color)
                    }, 350);
                })
        }
    }


    $scope.checkBundle = function () {
        for (let i = 0; i < $scope.orderClient.length; i++) {
            if ($scope.orderClient[i].bundle === "true" && $scope.orderClient[i].bundleType === null
                || $scope.orderClient[i].orderCount !== null && $scope.orderClient[i].bundleType === null) {
                var id = $scope.orderClient[i].id;
                var orderContainer = document.getElementById(id);
                if (orderContainer === null) {
                    setTimeout(function () {
                        $scope.checkBundle()
                    }, 10);
                    break;
                } else {
                    $scope.setButton(orderContainer, $scope.orderClient[i])
                }
            }
        }
    }


    $scope.setButton = function (orderContainer, order) {
        var sendButton = orderContainer.getElementsByClassName("send-button")[1];
        var selectBundle = orderContainer.getElementsByClassName("select-bundle")[0];
        if (order.bundleType === null) {
            sendButton.style.display = "none"
            selectBundle.style.display = "block"
        } else {
            sendButton.style.display = "block"
            selectBundle.style.display = "none"
        }
    }

    $scope.getBundle = function () {
        return $http.get("http://localhost:3100/bundle")
            .then(function successCallback(response) {
                return response.data;
            }, function errorCallback(response) {
                throw new Error(response.statusText);
            });
    };


    $scope.showSettings = function () {
        var overlay = document.getElementsByClassName("overlay")[0];
        var settingsBundle = document.getElementsByClassName("settings-bundle")[0];
        overlay.classList.add('show')
        settingsBundle.classList.add("zoomIn")
    };

    var bundleOrderId = null;

    $scope.addBundleScope = function () {
        $scope.bundleType = null;
        $scope.getBundle().then(function (data) {
            $scope.bundleType = data;
            if ($scope.bundleType !== null) {
                $scope.bundles = {
                    availableOptions: $scope.bundleType.bundleDtoList,
                    selectedBundle: $scope.bundleType.bundleDtoList[0]
                }
                if ($scope.bundles.availableOptions.length === 1) {
                    $scope.getBundleStages($scope.bundles.availableOptions[0]);
                } else {
                    $scope.getBundleStages($scope.bundles.availableOptions[0]);
                }
            }
        }).catch(function (error) {
            console.error(error);
        });
    }

    $scope.setBundle = function (id) {
        bundleOrderId = id
        $scope.showSettings()
        $scope.addBundleScope()
    }

    $scope.getBundleStages = function (bundles) {
        $scope.stages = bundles.stages
    }

    $scope.cancelBundleButton = function () {
        var overlay = document.getElementsByClassName("overlay")[0];
        var settingsBundle = document.getElementsByClassName("settings-bundle")[0];
        overlay.classList.add('hide')
        settingsBundle.classList.add("zoomOut")
        setTimeout(function () {
            overlay.classList.remove("show")
            overlay.classList.remove("hide")
            settingsBundle.classList.remove("zoomIn")
            settingsBundle.classList.remove("zoomOut")
        }, 350);
    }

    $scope.saveBundleButton = function () {
        console.log($scope.bundles.selectedBundle.stages)
        console.log(bundleOrderId)
        $http.post("http://localhost:3100/clients/" + bundleOrderId + "/client-stage/", $scope.bundles.selectedBundle)
            .then(function successCallback(responce) {
                $scope.cancelBundleButton();
                setTimeout(function () {
                    messageService.displayMessage("Бандл для заказа с id " + bundleOrderId + " установлен.", ok_color)
                }, 350);
            }, function failCallback(responce) {
                $scope.cancelBundleButton();
                setTimeout(function () {
                    messageService.displayMessage(responce.data.message, error_color)
                }, 350);
            })
    }

    $scope.deleteBundle = function (bundle) {
        var confirmed = confirm("Вы уверены, что хотите удалить этот бандл?");
        if (confirmed) {
            $http.delete("http://localhost:3100/bundle/" + bundle.id)
                .then(function successCallback(responce) {
                    $scope.addBundleScope()
                    setTimeout(function () {
                        messageService.displayMessage("Бандл " + bundle.title + " удалён!.", ok_color)
                    }, 100);
                }, function failCallback(responce) {
                    $scope.addBundleScope()
                    setTimeout(function () {
                        messageService.displayMessage(responce.data.message, error_color)
                    }, 100);
                })
        }
    }

    $scope.showAddSettings = function () {
        var overlay = document.getElementsByClassName("overlay")[1];
        var settingsBundle = document.getElementsByClassName("settings-bundle")[1];
        overlay.classList.add('show')
        settingsBundle.classList.add("zoomIn")
    }

    $scope.cancelAddBundleButton = function () {
        var content = document.getElementById("add-content");
        let inputs = content.getElementsByClassName("add-input")

        while (inputs.length !== 2) {
            inputs[inputs.length - 1].remove();
        }

        for (let i = 0; i < inputs.length; i++) {
            if (i === 0) {
                inputs[i].value = '';
            }
            if (i === 1) {
                inputs[i].value = '';
            }
        }

        let oldSpans = content.getElementsByClassName("validate-span")
        while (oldSpans.length !== 0) {
            oldSpans[0].remove();
        }

        var overlay = document.getElementsByClassName("overlay")[1];
        var settingsBundle = document.getElementsByClassName("settings-bundle")[1];
        overlay.classList.add('hide')
        settingsBundle.classList.add("zoomOut")
        setTimeout(function () {
            overlay.classList.remove("show")
            overlay.classList.remove("hide")
            settingsBundle.classList.remove("zoomIn")
            settingsBundle.classList.remove("zoomOut")
        }, 350);


    }
    $scope.addMoreInput = function () {
        var content = document.getElementById("add-content");
        let inputs = content.getElementsByClassName("add-input")

        var newInput = document.createElement("input")
        newInput.classList.add("add-input")
        newInput.placeholder = "Название этапа"

        for (let i = 0; i < inputs.length; i++) {
            if (i === inputs.length - 1) {
                inputs[i].insertAdjacentElement('afterend', newInput)
            }

        }
        var newContent = document.getElementById("add-content");
        let spans = newContent.getElementsByClassName("validate-span")
        while (spans.length !== 0) {
            spans[0].remove();
        }
    }

    $scope.createBundle = function () {
        $scope.showAddSettings();
    }

    $scope.changeBundle = function (id) {
        $scope.setBundle(id);
    }


    $scope.sendOrder = function (order, mainId) {
        $scope.closeAttention()
        const main = document.getElementById(mainId)
        let divsSendContainer = main.getElementsByClassName("new-client")
        let sep = document.getElementsByClassName('separator-client');
        if (sep.length >= 1) {
            sep[0].remove();
        }
        var div = null;
        for (const divElement of divsSendContainer) {
            if (String(divElement.id) === String(order.id)) {
                div = divElement;
                break;
            }
        }
        var existSendContainer = document.getElementById("send-container-" + order.id);
        let currentSendContainer = document.getElementsByClassName("send-order-container")
        if (currentSendContainer.length >= 1) {
            console.log(currentSendContainer[0])
            currentSendContainer[0].remove();
        }
        if (existSendContainer) {
            existSendContainer.className += ' slideInUp';
            setTimeout(function () {
                existSendContainer.classList.remove("slideInDown")
                existSendContainer.classList.remove("slideInUp")
                existSendContainer.remove();
            }, 250);
        } else {
            var sendContainer = document.createElement('div')
            sendContainer.id = "send-container-" + order.id;
            sendContainer.classList.add("slideInDown")
            sendContainer.classList.add("send-order-container")

            var content = document.createElement('div');
            content.classList.add("send-order-content")
            sendContainer.appendChild(content)

            if (div !== null) {
                div.insertAdjacentElement("afterend", sendContainer)
                div.scrollIntoView({
                    behavior: "smooth",
                    block: "start",
                });
            }
            $scope.regionSelector(order);
        }
    }

    $scope.regionSelector = async function (order) {
        var orderRegion = null;
        if (order.region !== null) {
            orderRegion = order.region.replace("(", "").split("-")[0].toLowerCase();
        }
        var content = document.getElementsByClassName("send-order-content")[0];

        var mode = null;
        if (order.bundle === "false") {
            mode = order.mode;
        }

        var timeCet = null
        if (order.orderDateTime !== null) {
            if (order.orderDateTime.includes("@")) {
                timeCet = order.orderDateTime.split("@")[0].replace("(", "").trim();
            }
        }

        let events = await getEvents();
        let filteredEventsByRegion = [];
        let filteredEventsByMode = [];
        let filteredEventsByDate = [];

        if (orderRegion !== null) {
            for (let i = 0; i < events.length; i++) {
                if (events[i].team.teamRegion.title.toLowerCase().includes(orderRegion.toLowerCase())) {
                    filteredEventsByRegion.push(events[i])
                }
            }
        } else {
            filteredEventsByRegion = events;
        }

        if (mode !== null) {
            for (let i = 0; i < filteredEventsByRegion.length; i++) {
                if (filteredEventsByRegion[i].wowEventType.title.toLowerCase().includes(mode.toLowerCase())) {
                    filteredEventsByMode.push(filteredEventsByRegion[i])
                }
            }
            if (filteredEventsByMode.length === 0) {
                filteredEventsByMode = filteredEventsByRegion;
            }
        } else {
            filteredEventsByMode = filteredEventsByRegion;
        }

        if (timeCet !== null) {
            var months = {
                "Jan": "01",
                "Feb": "02",
                "Mar": "03",
                "Apr": "04",
                "May": "05",
                "Jun": "06",
                "Jul": "07",
                "Aug": "08",
                "Sep": "09",
                "Oct": "10",
                "Nov": "11",
                "Dec": "12"
            };
            for (let i = 0; i < filteredEventsByMode.length; i++) {
                var eventDateString = filteredEventsByMode[i].eventDate;
                var eventParts = eventDateString.split(".");
                var eventFormattedDate = "20" + eventParts[2] + "-" + eventParts[1] + "-" + eventParts[0];

                var orderDateString = timeCet;
                var orderDateParts = orderDateString.split(" ");
                var month = months[orderDateParts[2]];
                var day = orderDateParts[1];
                var year = new Date().getFullYear().toString();
                var orderFormatted = year + "-" + month + "-" + day.padStart(2, "0");

                if (eventFormattedDate === orderFormatted) {
                    filteredEventsByDate.push(filteredEventsByMode[i])
                }
            }
            if (filteredEventsByDate.length === 0) {
                filteredEventsByDate = filteredEventsByMode
            }
        } else {
            filteredEventsByDate = filteredEventsByMode
        }


        filteredEventsByDate.sort(function (a, b) {
            var dateA = new Date('20' + a.eventDate.split('.').reverse().join('-'));
            var dateB = new Date('20' + b.eventDate.split('.').reverse().join('-'));
            return dateA - dateB;
        });

        var filterContainer = document.createElement('div');
        filterContainer.setAttribute('class', 'find-events-container');
        filterContainer.setAttribute('id', 'filter-events');
        content.appendChild(filterContainer)

        var findEventsContainer = document.createElement('div');
        findEventsContainer.setAttribute('class', 'find-events-container');
        findEventsContainer.setAttribute('id', 'find-events');
        var span = document.createElement('span')
        span.textContent = "Выберите событие: ";
        span.classList.add("span-events")
        findEventsContainer.appendChild(span)


        var eventsSelector = document.createElement('select');
        eventsSelector.setAttribute('class', 'send-order-selector');
        eventsSelector.setAttribute('id', 'event-selector');

        $scope.eventList = {}
        $scope.eventList.availableEvents = filteredEventsByDate;
        $scope.eventList.selectedEvents = " | " + filteredEventsByDate[0].eventDate + " | "
            + filteredEventsByDate[0].startedAt + " | "
            + filteredEventsByDate[0].wowEventType.title + " | "
            + filteredEventsByDate[0].team.title + " | ";

        for (var y = 0; y < filteredEventsByDate.length; y++) {
            var eventsOption = document.createElement('option');
            eventsOption.value = " | " + filteredEventsByDate[y].id + " | " + filteredEventsByDate[y].eventDate + " | " + filteredEventsByDate[y].startedAt + " | " + filteredEventsByDate[y].wowEventType.title + " | " + filteredEventsByDate[y].team.title + " | ";
            eventsOption.text = " | " + filteredEventsByDate[y].eventDate + " | " + filteredEventsByDate[y].startedAt + " | " + filteredEventsByDate[y].wowEventType.title + " | " + filteredEventsByDate[y].team.title + " | ";
            if (filteredEventsByDate[y].id === $scope.eventList.selectedEvents.id) {
                eventsOption.setAttribute('selected', true);
                flag = true
            }
            eventsSelector.appendChild(eventsOption);
        }

        eventsSelector.addEventListener('change', function () {
            var selectedIndex = eventsSelector.selectedIndex;
            var selectedOption = eventsSelector.options[selectedIndex];
            var selectedEventData = selectedOption.value.split(" | ");
            var id = selectedEventData[1];
            $scope.definedOrder(id, order, content)
        })

        findEventsContainer.appendChild(eventsSelector);
        content.appendChild(findEventsContainer)


        var filterSpan = document.createElement("span")
        filterSpan.classList.add("span-events")
        filterSpan.textContent = "Фильтры:"
        filterContainer.appendChild(filterSpan)

        var allEventsBth = document.createElement('button');
        allEventsBth.classList.add("send-button")
        allEventsBth.style.marginRight = "10px";
        allEventsBth.textContent = "Сброс"
        allEventsBth.onclick = function () {
            getAllEvents(events, eventsSelector, order);
        };
        filterContainer.appendChild(allEventsBth)

        var byRegionEventsBth = document.createElement('button');
        byRegionEventsBth.classList.add("send-button")
        byRegionEventsBth.textContent = "Регион"
        byRegionEventsBth.style.marginRight = "10px";
        byRegionEventsBth.onclick = function () {
            getAllEvents(filteredEventsByRegion, eventsSelector, order);
        };
        filterContainer.appendChild(byRegionEventsBth)

        var byModeEventsBth = document.createElement('button');
        byModeEventsBth.classList.add("send-button")
        byModeEventsBth.textContent = "Сложность"
        byModeEventsBth.style.marginRight = "10px";
        byModeEventsBth.onclick = function () {
            getAllEvents(filteredEventsByMode, eventsSelector, order);
        };
        filterContainer.appendChild(byModeEventsBth)

        var byDateEventsBth = document.createElement('button');
        byDateEventsBth.classList.add("send-button")
        byDateEventsBth.textContent = "Дата"
        byDateEventsBth.onclick = function () {
            getAllEvents(filteredEventsByDate, eventsSelector, order);
        };
        filterContainer.appendChild(byDateEventsBth)

        var selectedIndex = eventsSelector.selectedIndex;
        var selectedOption = eventsSelector.options[selectedIndex];
        var selectedEventData = selectedOption.value.split(" | ");
        var id = selectedEventData[1];
        $scope.definedOrder(id, order, content)
    }

    $scope.definedOrder = function (id, order, content) {
        var container = document.getElementById("send-events");

        if (!container) {
            var sendContainer = document.createElement('div');
            sendContainer.setAttribute('class', 'find-events-container');
            sendContainer.setAttribute('id', 'send-events');
            content.appendChild(sendContainer);

            var sendBtn = document.createElement('button');
            sendBtn.classList.add("send-button");
            sendBtn.textContent = "Готово";
            sendContainer.appendChild(sendBtn);
            sendContainer.getElementsByClassName("send-button")[0].onclick = function () {
                setTimeout(function () {
                    $scope.sendOrderInEvents(id, order);
                }, 100);
            };
        } else {
            container.getElementsByClassName("send-button")[0].onclick = function () {
                setTimeout(function () {
                    $scope.sendOrderInEvents(id, order);
                }, 100);
            };
        }
    }


    $scope.sendOrderInEvents = async function (eventId, order) {
        var event = await getEventsById(eventId);
        let intersection = [];

        var correctClass = $scope.splitAndReplaceClass(order)
        console.log("Корректный класс " + correctClass)
        if (correctClass === undefined) {
            var confirmed = confirm("У заказа не определен класс. Вы уверены что хотите опеределить его в текущее сорбытие?");
            if (confirmed) {
                $http.post("http://localhost:3100/events/" + eventId + "/" + order.id)
                    .then(function successCallback(responce) {
                    }, function failCallback(responce) {
                        messageService.displayMessage(responce.data.message, error_color)
                    })
            }
            return;
        }


        if (order.orderType === "Advanced" || order.orderType === "Premium") {
            let token = $scope.checkToken(correctClass);
            let currentIntersectionClass = $scope.checkArmorType(correctClass)
            $scope.uniqueValues = [];
            $scope.uniqueValues = $scope.uniqueValues.concat(token, currentIntersectionClass)
                .filter((value, index, self) => self.indexOf(value) === index);
            console.log($scope.uniqueValues)
            for (let i = 0; i < event.clientList.length; i++) {
                var eventClass = $scope.splitAndReplaceClass(event.clientList[i]);
                if (eventClass === undefined) continue;
                for (let j = 0; j < $scope.uniqueValues.length; j++) {
                    if ($scope.uniqueValues[j].replace(" ", "").toLowerCase() === eventClass.toLowerCase()) {
                        intersection.push(event.clientList[i])
                    }
                }
            }
        } else {
            var existClass = null;
            var existToken = null;
            var intersectionClass = null;
            $scope.uniqueValues = [];
            for (let i = 0; i < event.clientList.length; i++) {
                if (event.clientList[i].orderType === "Advanced" || event.clientList[i].orderType === "Premium") {
                    existClass = $scope.splitAndReplaceClass(event.clientList[i]);
                    if (existClass === undefined) continue;
                    existToken = $scope.checkToken(existClass);
                    intersectionClass = $scope.checkArmorType(existClass)
                    $scope.uniqueValues = $scope.uniqueValues.concat(intersectionClass, existToken)
                        .filter((value, index, self) => self.indexOf(value) === index);
                    var orderClass = $scope.splitAndReplaceClass(order)
                    console.log(orderClass)
                    console.log($scope.uniqueValues)
                    for (let j = 0; j < $scope.uniqueValues.length; j++) {
                        if (orderClass.toLowerCase() === $scope.uniqueValues[j].toLowerCase().replace(" ", "")) {
                            intersection.push(event.clientList[i]);
                        }
                    }
                }
            }
        }

        if (intersection.length === 0) {
            $http.post("http://localhost:3100/events/" + eventId + "/" + order.id)
                .then(function successCallback(responce) {
                }, function failCallback(responce) {
                    messageService.displayMessage(responce.data.message, error_color)
                })

        } else {
            var currentContainer = document.getElementsByClassName("send-order-content")[0];
            var content = currentContainer.getElementsByClassName("find-events-container")[1];

            let oldIntersectionElements = document.getElementsByClassName("intersection");
            if (oldIntersectionElements !== undefined) {
                while (oldIntersectionElements.length !== 0) {
                    oldIntersectionElements[0].remove();
                }
            }
            var info = document.createElement('div');
            for (let i = 0; i < intersection.length; i++) {
                var spanClass = $scope.splitAndReplaceClass(intersection[i]);
                if (spanClass !== undefined) {
                    var message = document.createElement('div');
                    message.classList.add("intersection")
                    message.textContent = spanClass + " " + intersection[i].nickname + "-" +
                        intersection[i].realm + " " + intersection[i].battleTag + " " + intersection[i].orderCode
                    message.style.color = "red"
                    content.insertAdjacentElement("afterend", message)
                }
            }
            info.classList.add("intersection")
            info.textContent = "Пересечения"
            info.style.textAlign = "center"
            content.insertAdjacentElement("afterend", info)

            setTimeout(function () {
                var confirmed2 = confirm("В заказе есть пересечение с клиентами. Вы уверены что хотите опеределить его в текущее событие?");
                if (confirmed2) {
                    $http.post("http://localhost:3100/events/" + eventId + "/" + order.id)
                        .then(function successCallback(responce) {
                        }, function failCallback(responce) {
                            messageService.displayMessage(responce.data.message, error_color)
                        })
                }
            }, 100);


        }
    }

    $scope.checkArmorType = function (correctClass) {
        for (let i = 0; i < cloth.length; i++) {
            if (cloth[i].toLowerCase().replaceAll(" ", "") === correctClass.toLowerCase().replaceAll(" ", "")) {
                return cloth;
            }
        }
        for (let i = 0; i < plate.length; i++) {
            if (plate[i].toLowerCase().replaceAll(" ", "") === correctClass.toLowerCase().replaceAll(" ", "")) {
                return plate;
            }
        }
        for (let i = 0; i < mail.length; i++) {
            if (mail[i].toLowerCase().replaceAll(" ", "") === correctClass.toLowerCase().replaceAll(" ", "")) {
                return mail;
            }
        }
        for (let i = 0; i < leather.length; i++) {
            if (leather[i].toLowerCase().replaceAll(" ", "") === correctClass.toLowerCase().replaceAll(" ", "")) {
                return leather;
            }
        }

    }


    $scope.checkToken = function (correctClass) {
        for (let i = 0; i < zenithToken.length; i++) {
            if (correctClass.toLowerCase().replaceAll(" ", "") === zenithToken[i].toLowerCase().replaceAll(" ", "")) {
                return zenithToken;
            }
        }

        for (let i = 0; i < dreadfulToken.length; i++) {
            if (correctClass.toLowerCase().replaceAll(" ", "") === dreadfulToken[i].toLowerCase().replaceAll(" ", "")) {
                return dreadfulToken;
            }
        }

        for (let i = 0; i < veneratedToken.length; i++) {
            if (correctClass.toLowerCase().replaceAll(" ", "") === veneratedToken[i].toLowerCase().replaceAll(" ", "")) {
                return veneratedToken;
            }
        }

        for (let i = 0; i < mysticToken.length; i++) {
            if (correctClass.toLowerCase().replaceAll(" ", "") === mysticToken[i].toLowerCase().replaceAll(" ", "")) {
                return mysticToken;
            }
        }

    }

    $scope.splitAndReplaceClass = function (order) {
        var eventClass = null;
        if (order.characterClass !== null) {
            eventClass = order.characterClass;
        } else if (order.blizzardApiClass !== null) {
            eventClass = order.blizzardApiClass;
        }

        if (eventClass !== null) {
            let strings = eventClass.replaceAll(" ", "").toLowerCase();

            for (let i = 0; i < classList.length; i++) {
                if (strings.includes(classList[i].replace(" ", "").toLowerCase())) {
                    if (classList[i].toLowerCase() === "hunter") {
                        var result = correctClass(classList[i], strings)
                        if (!result) {
                            return classList[i];
                        } else {
                            return result;
                        }
                    }
                    return classList[i];
                }
            }
        }

    }

    function correctClass(duoClass, eventClass) {
        if (eventClass.includes("demonhunter")) {
            for (let i = 0; i < classList.length; i++) {
                var eachClass = classList[i].replaceAll(" ", "").toLowerCase();
                if (eachClass === "demonhunter") {
                    return classList[i];
                }
            }
        }
        return false;
    }

    function getAllEvents(events, selector, order) {
        selector.innerHTML = '';

        let currentEvents = events;
        currentEvents.sort(function (a, b) {
            var dateA = new Date('20' + a.eventDate.split('.').reverse().join('-'));
            var dateB = new Date('20' + b.eventDate.split('.').reverse().join('-'));
            return dateA - dateB;
        });

        $scope.eventList = {}
        $scope.eventList.availableEvents = currentEvents;
        $scope.eventList.selectedEvents = " | " + currentEvents[0].eventDate + " | "
            + currentEvents[0].startedAt + " | "
            + currentEvents[0].wowEventType.title + " | "
            + currentEvents[0].team.title + " | ";

        for (var y = 0; y < events.length; y++) {
            var eventsOption = document.createElement('option');
            eventsOption.value = " | " + currentEvents[y].id + " | " + currentEvents[y].eventDate + " | " + currentEvents[y].startedAt + " | " + currentEvents[y].wowEventType.title + " | " + currentEvents[y].team.title + " | ";
            eventsOption.text = " | " + currentEvents[y].eventDate + " | " + currentEvents[y].startedAt + " | " + currentEvents[y].wowEventType.title + " | " + currentEvents[y].team.title + " | ";
            if (currentEvents[y].id === $scope.eventList.selectedEvents.id) {
                eventsOption.setAttribute('selected', true);
                flag = true
            }
            selector.appendChild(eventsOption);
        }

        var selectedIndex = selector.selectedIndex;
        var selectedOption = selector.options[selectedIndex];
        var selectedEventData = selectedOption.value.split(" | ");
        var id = selectedEventData[1];
        $scope.definedOrder(id, order)
    }


    async function getEvents() {
        try {
            const response = await $http.get("http://localhost:3100/events/with-clients");
            return response.data;
        } catch (error) {
            console.log(error);
        }
    }

    async function getEventsById(id) {
        try {
            const response = await $http.get("http://localhost:3100/events/" + id);
            return response.data;
        } catch (error) {
            console.log(error);
        }
    }


    $scope.sendCreateBundle = function () {
        var content = document.getElementById("add-content");
        let inputs = content.getElementsByClassName("add-input")
        let oldSpans = content.getElementsByClassName("validate-span")
        while (oldSpans.length !== 0) {
            oldSpans[0].remove();
        }

        for (let i = 0; i < inputs.length; i++) {
            var span = document.createElement('span')
            span.classList.add("validate-span")
            inputs[i].insertAdjacentElement("afterend", span);
        }

        let spans = content.getElementsByClassName("validate-span");
        $scope.newBundle = {};
        $scope.bundle = [];


        var validate = true;
        for (let i = 0; i < inputs.length; i++) {
            if (i === 0) {
                if (inputs[i].value === "") {
                    spans[i].textContent = "Поле не должно быть пустым"
                    spans[i].style.marginBottom = "10px";
                    validate = false
                } else {
                    spans[i].textContent = undefined
                }
                $scope.newBundle.title = inputs[i].value
            } else {
                if (inputs[i].value === "") {
                    spans[i].textContent = "Поле не должно быть пустым"
                    spans[i].style.marginBottom = "10px";
                    validate = false
                } else if (inputs[i].value !== "Normal Raid" && inputs[i].value !== "Heroic Raid"
                    && inputs[i].value !== "Mythic Raid") {
                    spans[i].textContent = "Поле содержит неверный фармат"
                    spans[i].style.marginBottom = "10px";
                    validate = false
                } else {
                    spans[i].textContent = undefined
                }
                $scope.bundle.push(inputs[i].value)
            }
        }
        if (!validate) {
            return
        }


        $scope.newBundle.bundleStages = $scope.bundle
        console.log($scope.newBundle)
        $http.post("http://localhost:3100/bundle", $scope.newBundle)
            .then(function successCallback(responce) {
                $scope.addBundleScope()
                $scope.cancelAddBundleButton();
                setTimeout(function () {
                    messageService.displayMessage("Бандл " + $scope.newBundle.title + " создан!.", ok_color)
                }, 350);
            }, function failCallback(responce) {
                $scope.cancelAddBundleButton();
                setTimeout(function () {
                    messageService.displayMessage(responce.data.message, error_color)
                }, 350);
            })
    }


    $scope.additionalPanel = function (order, mainId) {
        $scope.closeAttention()
        const main = document.getElementById(mainId)
        let divs = main.getElementsByClassName("new-client");
        var div = null;
        for (const divElement of divs) {
            if (String(divElement.id) === String(order.id)) {
                div = divElement;
                break;
            }
        }
        let currentSendContainer = document.getElementsByClassName("send-order-container")
        if (currentSendContainer.length >= 1) {
            currentSendContainer[0].remove();
        }
        const separator = document.getElementById('separator-' + order.id)
        let sep = document.getElementsByClassName('separator-client');

        if (sep.length >= 1) {
            sep[0].remove();
        }
        if (separator) {
            separator.className += ' slideInUp';
            setTimeout(function () {
                separator.classList.remove("slideInDown")
                separator.classList.remove("slideInUp")
                separator.remove();
            }, 250);
        } else {
            console.log(div)
            const separator = document.createElement('div');
            separator.id = "separator-" + order.id
            separator.classList.add("slideInDown")
            separator.classList.add('separator-client')

            const newDiv = document.createElement("div");
            newDiv.classList.add('additionalPanel')

            const labelOne = document.createElement('div')
            labelOne.classList.add('label-client')
            labelOne.textContent = "Редактирование заказа или формата:"
            newDiv.appendChild(labelOne)


            const origin = document.createElement('textarea')
            origin.classList.add('area-client');
            origin.addEventListener('input', () => {
                origin.style.height = 'auto';
                origin.style.height = origin.scrollHeight + 'px';
            });
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
                $scope.parseOrderInfo(order, origin.value, mainId)
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

            comment.addEventListener('input', () => {
                comment.style.height = 'auto';
                comment.style.height = comment.scrollHeight + 'px';
            });

            const ready = document.createElement('button')
            ready.classList.add('send-button')
            ready.style.marginLeft = "50px"
            ready.style.marginBottom = "15px"
            ready.textContent = "Готово"
            ready.onclick = function () {
                $scope.setComments(order, comment.value, mainId)
            }
            newDiv.appendChild(ready)

            separator.appendChild(newDiv)

            div.insertAdjacentElement("afterend", separator)
            div.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });

            if (order.noParseInfo !== null) {
                origin.style.backgroundColor = "#fff4f4"
            } else {
                origin.style.backgroundColor = "#f5ffee"
            }
            origin.style.height = origin.scrollHeight + 'px';
        }

    }

    $scope.setComments = async function (order, comment, id) {
        let textComment = "null";
        if (comment.length > 0) {
            textComment = comment;
        }
        try {
            const response = await $http.put("http://localhost:3100/clients/" + order.id + "/comment", textComment);
            message = "CHANGE/" + order.id;
            $scope.getNewClients(message.split("/"))
        } catch (error) {
            console.log(error.data)
            throw error;
        }
    }


    $scope.parseOrderInfo = async function (order, value, id) {
        try {
            const response = await $http.put("http://localhost:3100/clients/" + order.id + "/order-info", value);
            message = "CHANGE/" + order.id;
            $scope.getNewClients(message.split("/"))
                .then(r => $scope.checkBundle())
                .then(a => $scope.attentionOrder())
                .then(s => $scope.additionalPanel(response.data[0], id))
        } catch (error) {
            $scope.displayMessage("Не удалось успешно завершить форматирование. Все изменения были сброшены.", error_color);
            await $scope.getNewClients("NEW");
            $scope.additionalPanel(order, id);
        }
    };


    connect();
    $scope.setClientPage()
});

