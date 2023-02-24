angular.module('index-app').controller('scheduleController', function ($scope, $http, $location, $localStorage, $rootScope) {


    $scope.selectFunction = function () {
        var select = 'background-color: #eff1f4; color:#00152a; cursor: pointer;';
        document.getElementById('team-selected').style.cssText=select;
        document.getElementById('type-selected').style.cssText=select;
    };

    $scope.getTeamsForSettings = function () {
        $http.get("http://localhost:3100/teams/all")
            .then(function succesCallback(responce) {
                responce.data.push({title: 'select-all'})
                $scope.teams = responce.data;
/*                $scope.selectedTeam = $scope.teams[$scope.teams.length - 1];*/
            }, function failCallback(responce) {

            })
    };

    $scope.getEventTypeForSettings = function () {
        $http.get("http://localhost:3100/eventType/all")
            .then(function succesCallback(responce) {
                responce.data.push({title: 'select-all'})
                $scope.eventType = responce.data;
/*                $scope.selectedType = $scope.eventType[$scope.eventType.length - 1];*/
            }, function failCallback(responce) {

            })
    };


    $scope.getTeams = function () {
        $http.get("http://localhost:3100/teams/all")
            .then(function successCallback(responce) {
                $scope.listTeams = responce.data;
                $scope.listTeamSelected = $scope.listTeams[0];
            }, function failCallback(responce) {

            })
    };

    $scope.getEventType = function () {
        $http.get("http://localhost:3100/eventType/all")
            .then(function succesCallback(responce) {
                $scope.listEventType = responce.data;
                $scope.listSelectedType = $scope.listEventType[$scope.listEventType.length - 2];
            }, function failCallback(responce) {

            })
    };


    $scope.filterByTeamAndType = function () {
        let selectedTeam = $scope.selectedTeam.title;
        let selectedType = $scope.selectedType.title;
        if (selectedType === 'select-all' && selectedTeam !== 'select-all') {
            $scope.onlyTeamSchedule(selectedTeam);
            return;
        }
        if (selectedTeam === 'select-all' && selectedType !== 'select-all') {
            $scope.onlyTypeSchedule(selectedType);
            return;
        }
        if (selectedTeam === 'select-all' && selectedType === 'select-all') {
            $scope.defaultSchedule();
            return;
        }

        $http.get("http://localhost:3100/events/filter/" + selectedTeam + "/" + selectedType)
            .then(function succesCallback(responce) {
                $scope.events = responce.data;
                return $scope.events;
            }, function failCallback(responce) {

            })
    }

    $scope.onlyTeamSchedule = function (selectedTeam) {
        $http.get("http://localhost:3100/events/byTeam/" + selectedTeam)
            .then(function successCallback(responce) {
                $scope.events = responce.data;
                return $scope.events;
            }, function failCallback(responce) {

            })
    }

    $scope.onlyTypeSchedule = function (selectedType) {
        $http.get("http://localhost:3100/events/byType/" + selectedType)
            .then(function successCallback(responce) {
                $scope.events = responce.data;
                return $scope.events;
            }, function failCallback(responce) {

            })
    }

    $scope.defaultSchedule = function () {
        $http.get("http://localhost:3100/events/all")
            .then(function succesCallback(responce) {
                $scope.events = responce.data;
                return $scope.events;
            }, function failCallback(responce) {

            })
    }


    $scope.addEvent = function () {
        var raidEvents = document.getElementById('event').outerHTML
        var button = document.getElementById('button');
        button.insertAdjacentHTML('beforebegin', raidEvents)

    };

    $scope.cancelScheduleList = function () {
        $scope.clearScheduleList();
        var div = document.getElementById('schedule-table-form');
        div.style.display = 'none';
    }

    $scope.clearScheduleList = function () {
        let allElement = document.getElementsByClassName('add-schedule-list');
        var countDeletingElement = allElement.length - 1
        for (let i = 0; i < countDeletingElement; i++) {
            document.getElementById('event').remove();
        }
        for (let i = 0; i < 2; i++) {
            let l = document.getElementsByClassName('input-schedule')[i].value = '';
        }
    }


    $scope.createScheduleList = function () {
        var j = 0;
        $scope.content = []
        $scope.schedule = {};
        let elements = document.getElementsByClassName('input-schedule');
        let tags = document.getElementsByClassName('add-schedule-list')
        for (let i = 0; i < tags.length; i++) {
            $scope.schedule.type = $scope.listSelectedType.title;
            $scope.schedule.team = $scope.listTeamSelected.title;
            $scope.schedule.date = document.getElementsByClassName("input-schedule")[j].value;
            $scope.schedule.time = document.getElementsByClassName("input-schedule")[j + 1].value;
            if ($scope.schedule.date === '' || $scope.schedule.time === '') {
                alert("Поля обязательны для заполнения")
                break;
            } else {
                $scope.content.push($scope.schedule);
                $scope.schedule = {};
                j = (elements.length - elements.length + 2) * (i + 1);
            }
        }
        if ($scope.content.length === tags.length) {
            $scope.postScheduleList($scope.content);
            $scope.clearScheduleList();
        }
    }

    $scope.postScheduleList = function (content) {
        console.log(content)
        $http.post("http://localhost:3100/events/createList", content)
            .then(function succesCallback(responce) {
                alert("События успешно добавлены")
                $scope.defaultSchedule();
                $scope.filterByTeamAndType();
            }, function failCallback(responce) {
                alert("Что то пошло не так")
            })
        var div = document.getElementById('schedule-table-form');
        div.style.display = 'none';
    }

    $scope.postScheduleText = function (content) {
        $http.post("http://localhost:3100/events/createText", content)
            .then(function succesCallback(responce) {
                alert("События успешно добавлены")
                $scope.defaultSchedule();
                $scope.filterByTeamAndType();
            }, function failCallback(responce) {
                alert("Что то пошло не так")
            })
        var div = document.getElementById('schedule-text-form');
        div.style.display = 'none';
    }


    $scope.getTableForm = function () {
        const div = document.getElementById('schedule-table-form');
        const another = document.getElementById('schedule-text-form');
        if (another.style.display === 'block') {
            another.style.display = 'none'
            $scope.clearTextField()
        }
        div.style.display = 'block';
    };

    $scope.chooseScheduleForm = function () {
        let scheduleForm = $scope.selectedScheduleForm;
        if (scheduleForm === 'вручную') {
            $scope.getTableForm();
        }
        if (scheduleForm === 'текстом') {
        $scope.getTextForm();
        }
    };

    $scope.getTextForm = function () {
        var div = document.getElementById('schedule-text-form');
        const another = document.getElementById('schedule-table-form');
        if (another.style.display === 'block') {
            another.style.display = 'none'
            $scope.clearScheduleList();
        }
        div.style.display = 'block';
    };


    $scope.clearTextField = function () {
        const textField = document.getElementById("text-events");
        textField.value = '';
    }

    $scope.closeTextField = function () {
        const textForm = document.getElementById("schedule-text-form");
        textForm.style.display = 'none';
        $scope.clearTextField();
    }

    $scope.getTextSchedule = function () {
        const textField = document.getElementById("text-events");
        let text = textField.value
        if (text.length === 0) {
            alert("Поле ввода не должно быть пустым")
        } else {
            $scope.postScheduleText(text);
            $scope.clearTextField();
        }
    }
    $scope.changeEvent = function (id) {
        const currentNode = document.getElementById(id)
        let elements = currentNode.getElementsByClassName("event-data")
        $scope.changeEventTeam(elements)
        $scope.changeEventType(elements)
        $scope.changeEventDate(elements)
        $scope.changeEventTime(elements)
        $scope.changeEventChangeButton(elements)
    }

    $scope.changeAcceptButton = function (eventId) {
        const currentNode = document.getElementById(eventId)
        let selectNodes = currentNode.getElementsByClassName('button')

        const teamContent = selectNodes.team.value
        const newTeamNode = $scope.teamNode;
        newTeamNode.textContent = teamContent


        const typeContent = selectNodes.type.value
        const newTypeNode = $scope.typeNode;
        newTypeNode.textContent = typeContent


        let eventDate = currentNode.getElementsByClassName('event-date-input')
        const dateContent = eventDate.date.value
        const newDateNode = $scope.dateNode;
        newDateNode.textContent = dateContent


        let eventTime = currentNode.getElementsByClassName('event-time-input')
        const timeContent = eventTime.time.value
        const newTimeNode = $scope.timeNode;
        newTimeNode.textContent = timeContent


        let buttons = currentNode.getElementsByTagName('button')
        const newButton = $scope.changeNode

        if (!$scope.checkDate(dateContent)) {
            alert("Неправильный формат даты.")
            return
        }
        if (!$scope.checkTime(timeContent)) {
            alert("Неправильный формат времени. Возможно присутствуют русские символы")
            return
        }


        $scope.request = []
        $scope.changeSchedule = {}
        $scope.changeSchedule.type = typeContent
        $scope.changeSchedule.team = teamContent
        $scope.changeSchedule.date = dateContent
        $scope.changeSchedule.time = timeContent
        $scope.request.push($scope.changeSchedule)
        console.log($scope.request)


        $scope.changeEventById = function (eventId, content) {
            $http.put("http://localhost:3100/events/changeList/" + eventId, content)
                .then(function successCallback(responce) {
                    selectNodes.team.replaceWith(newTeamNode)
                    selectNodes.type.replaceWith(newTypeNode)
                    eventDate.date.replaceWith(newDateNode)
                    eventTime.time.replaceWith(newTimeNode)
                    buttons.change.replaceWith(newButton)
                    return $scope.events
                }, function failCallback(responce) {
                    alert("Событие не было изменено. Проверьте корректность ввода данных")
                    window.location.reload();
                })
        }
        $scope.changeEventById(eventId, $scope.request)

    }

    document.addEventListener(`keyup`, (e) => {
        if (e.keyCode === 27) { // если нажали на ESC
            window.location.reload();
        }
    });


    $scope.checkTime = function (timeContent) {
        var timePattern = "([01]?[0-9]|2[0-3]):[0-5][0-9][ ][A-Z][A-Z][A-Z]"
        return timeContent.match(timePattern) !== null
    }

    $scope.checkDate = function (dateContent) {
        return dateContent.match("[0-3][0-9]\.[0-1][0-9]\.[0-9][0-9]") !== null;
    }

    $scope.changeEventChangeButton = function (elements) {
        let elementsDiv = elements.eventButton.children
        $scope.changeNode = elementsDiv.change;
        const changeButton = elementsDiv.change;
        const newButton = document.createElement('button')
        newButton.id = 'change'
        newButton.className = "fa fa-floppy-o fa-2 safe_icon_button "
     /*   newButton.textContent = <i class="fa fa-floppy-o" aria-hidden="true"></i>*/
        newButton.onclick = function () {
            const eventId = elements.id.textContent
            $scope.changeAcceptButton(eventId);
        }
        changeButton.replaceWith(newButton)
    }

    $scope.changeEventDate = function (elements) {
        $scope.dateNode = elements.date;
        const dateNode = elements.date;
        const dateValue = elements.date.textContent;
        const newDateNode = document.createElement('input');
        newDateNode.id = "date"
        newDateNode.value = dateValue;
        newDateNode.className = "event-date-input"
        dateNode.replaceWith(newDateNode)
    }

    $scope.changeEventTime = function (elements) {
        $scope.timeNode = elements.time;
        const timeNode = elements.time;
        const timeValue = elements.time.textContent;
        const newTimeNode = document.createElement('input');
        newTimeNode.id = "time"
        newTimeNode.value = timeValue;
        newTimeNode.className = "event-time-input"
        timeNode.replaceWith(newTimeNode)
    }

    $scope.changeEventType = function (elements) {
        $scope.typeNode = elements.type;
        const typeNode = elements.type;
        const typeValue = elements.type.textContent;
        const newTypeNode = document.createElement('select');
        for (let i = 0; i < $scope.listEventType.length; i++) {
            const opt = document.createElement('option');
            opt.value = $scope.listEventType[i].title;
            opt.innerHTML = $scope.listEventType[i].title;
            newTypeNode.appendChild(opt)
            if (typeValue === $scope.listEventType[i].title) {
                newTypeNode.selectedIndex = i
            }
        }
        newTypeNode.id = "type"
        newTypeNode.className = "button"
        typeNode.replaceWith(newTypeNode);
    }

    $scope.changeEventTeam = function (elements) {
        $scope.teamNode = elements.team;
        const teamNode = elements.team;
        const teamValue = elements.team.textContent;
        const newTeamNode = document.createElement('select');

        for (let i = 0; i < $scope.listTeams.length; i++) {
            const opt = document.createElement('option');
            opt.value = $scope.listTeams[i].title;
            opt.innerHTML = $scope.listTeams[i].title;
            newTeamNode.appendChild(opt)
            if (teamValue === $scope.listTeams[i].title) {
                newTeamNode.selectedIndex = i
            }
        }
        newTeamNode.id = "team"
        newTeamNode.className = "button"
        teamNode.replaceWith(newTeamNode);
    }

    $scope.deleteEvent = function (eventId) {
        $http.delete("http://localhost:3100/events/delete/" + eventId)
            .then(function succesCallback(responce) {
               alert("Событие " + eventId + " было удалено.")
                window.location.reload()
            }, function failCallback(responce) {
                alert("wrong")
            })
    }

    $scope.schedulePanel = function () {
        const clientItemPanel = document.getElementById('item-client');
        const scheduleItemPanel = document.getElementById('item-schedule');
        const adminItemPanel = document.getElementById('item-admin');

        scheduleItemPanel.style.backgroundColor = "#eff1f4";
        scheduleItemPanel.style.color = "#00152a";

        clientItemPanel.style.backgroundColor = "transparent";
        clientItemPanel.style.color = "#f1f1f1";

        adminItemPanel.style.backgroundColor = "transparent";
        adminItemPanel.style.color = "#f1f1f1";
    };

    $scope.schedulePanel();
    $scope.defaultSchedule();
    $scope.getTeamsForSettings();
    $scope.getEventTypeForSettings();
    $scope.getTeams();
    $scope.getEventType();

});