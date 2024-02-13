'use strict';

var roomPage = document.querySelector("#room-page");
var createDoc = document.getElementById("create");
var signInDoc = document.getElementById("sign-in");
var docName = document.querySelector("#docId");
var docUser = document.querySelector("#docUser");

var userSession = document.querySelector("#userSession");

var docUrl = document.querySelector("#textEditor");
var editor = document.querySelector("#edit-area");
var openDoc = document.querySelector("#open");
var saveDoc = document.querySelector("#save");
var viewPriv = document.querySelector("#viewPrivilege");
//var addUser = document.querySelector("#authentication");
var addUserForm = document.querySelector("#authentication");

var usersCon = document.querySelector("#usersConnected");
var comments = document.querySelector("#comments");

var stompClient = null;
//var docId = "test-room";
var docId = docName.value.trim();
//var user = docUser.value.trim();
var user = docUser.value.trim();
var textContent = null;
var previousTextArea = null;
//const usersConnected = [];


function generate() {
    //var roomId = document.getElementById("roomID");

    let alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    let symbols = "%!@#$^&*-+=|\\(){}:\"';,?";
    let password = "";

    for (let i = 0; i < 4; i++) {
        let randomNumber = Math.floor(Math.random() * 10);
        let lowerCaseLetter = alphabet.charAt(Math.random() * 26).toLowerCase();
        let upperCaseLetter = alphabet.charAt(Math.random() * 26).toUpperCase();
        //let specialChar = symbols.charAt(Math.random() * symbols.length);

        password += randomNumber + lowerCaseLetter + upperCaseLetter ;
    }
    console.log("this is room ", password);

    docName.value = password;
    console.log('this is testing',docName.value);
}



var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function generateRandomUrl() {
    // This is a simple example; you may want to use a more robust method
    const randomId = Math.random().toString(36).substring(2, 10);
    return randomId;
}

function connect(event) {

    console.log("docId given",docId);
    console.log("doc user given",docUser.value.trim());

    roomPage.classList.add('hidden');
    docUrl.classList.remove('hidden');
    addUserForm.classList.remove('hidden');

    var socket = new SockJS('/ws');
    console.log("websocket established");

    stompClient = Stomp.over(socket);
    stompClient.connect({},onConnected,onError);

    event.preventDefault();

}

function onConnected() {

    console.log("try to connect to socket");


    stompClient.subscribe('/topic/public', onMessageReceived);
    if (viewPriv.checked) {


        console.log("View radio is checked");
        stompClient.send('/app/controllers.subscribeToView',
            {},
            //JSON.stringify({senderId: user.value.trim(), type: 'JOIN'})
            JSON.stringify({senderId: docUser.value.trim(), type: 'JOIN', text: "", docId: docName.value.trim()})
        );
    } else {


        stompClient.send('/app/controllers.docRoomUser',
            {},
            //JSON.stringify({senderId: user.value.trim(), type: 'JOIN'})
            JSON.stringify({senderId: docUser.value.trim(), type: 'JOIN', text: "", docId: docName.value.trim()})
        );

    }

    userSessionName(docUser.value.trim());

    findAndDisplayConnected().then();

}

async function findAndDisplayConnected() {

    const connectedUserResponse = await fetch('/users');
    let connectedUsers = await connectedUserResponse.json();
    connectedUsers = connectedUsers.filter(user => user.name !== user.text);
    //const connectedUsersList = document.getElementById('connectedUsers');
    usersCon.innerHTML = '';
    //connectedUsersList.innerHTML = '';
    let li = document.createElement('p');
    let textEl = document.createTextNode("Connected Users");
    li.appendChild(textEl);
    usersCon.appendChild(li);

    connectedUsers.forEach(user => {
        console.log("connected user ",user.name);
        let li = document.createElement('li')
        li.setAttribute('id','li'+user.name);
        let span = document.createElement('span');
        let btn = document.createElement('button');
        btn.setAttribute('type','button');
        btn.setAttribute('id',user.name);

        btn.addEventListener('click',userDisconnection);
        let textLi = document.createTextNode(user.name + ' joined');
        span.appendChild(btn);
        span.appendChild(textLi);
        li.appendChild(span);
        usersCon.appendChild(li);
    });

}

function userDisconnection(event) {


    var clickedUser = event.target;
    var selectedUser = clickedUser.getAttribute('id');

    var deletedElement = document.querySelector("#li"+selectedUser);

    console.log("selected User to delete",selectedUser);
    console.log("li to deleted",document.querySelector("#li"+selectedUser).getAttribute('id'));

    usersCon.removeChild(deletedElement);

    //now must correct the message problem
    var msg = {
        //senderId: user.value.trim(),
        senderId: selectedUser,
        docId: docName.value.trim(),
        text: textContent,
        //text: '',
        type: 'LEAVE'
    }

// try to make disconnect work
// this is to send to controller to update database and send
//  a DISCONNECT message to all
    stompClient.send('/app/controllers.onDisconnect',
        {},
        JSON.stringify(msg));

    // this is for js client to disconnect
    if (stompClient !== null) {
        stompClient.disconnect(function() {
            console.log("user disconnected");
        });
        stompClient = null;
    }
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function userSessionName(name) {

    console.log("try to write user session");

    let userSessionN = document.createElement('i')
    let userSessionText = document.createTextNode(' ' + name);
    userSessionN.appendChild(userSessionText);
    userSession.appendChild(userSessionN);

}


function onError() {
    connectingElement.textContent('Could not connect to socket! Refresh and try...');
    //connectingElement.style.color = 'red';
}


function writeToEditor(event) {

    //let previous = editor.content;

    textContent = editor.value;

    //previousTextArea =  textContent;

    if (textContent && stompClient) {
        var editorMessage = {
            //senderId: user.value.trim(),
            senderId: docUser.value.trim(),
            docId: docName.value.trim(),
            text: textContent,
            type: 'TEXT'
        }

        if (!viewPriv.checked) {

        stompClient.send('/app/controllers.sendMessage', {},
            JSON.stringify(editorMessage));
        }

    }

    //console.log(previousTextArea)

    event.preventDefault();
}


async function onMessageReceived(payload) {

    var textElement = null;
    var messageText = null;
    var text = null;


    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    await findAndDisplayConnected().then();

    editor.value = message.text;
    //editor.value = previousTextArea;
    editor.scrollTop = editor.scrollHeight;

    //editor.value = previous + message.text;


}

async function addShareUser(event,form) {

    event.preventDefault();

    let btnSubmit = document.querySelector("#btnSubmit");
    btnSubmit.disabled = true;


    let data = document.querySelector("#username");
    console.log("data form:",data.value.trim());

    let viewUser = document.querySelector("#viewUser");
    let editUser = document.querySelector("#editUser");
    let role = null;

    if (viewUser.checked) {
        role = 'VIEW';
    }
    if (editUser.checked) {
        role = 'EDIT';
    }

    let userResultJson = await fetch('http://localhost:8090/editor/addShareUser', {
        method: 'POST',
        body: JSON.stringify({'name': data.value.trim(),
                                    'role': role}),
        headers:{
            'Content-Type': 'application/json'
        }
    });
    let userResult = await userResultJson.json();
    console.log(userResult);
    if (!userResultJson) {
        alert("An error occured!");
    }
}

function openDocument(event) {

 //   editor.value = text;

    if (stompClient) {
        var editorMessage = {
            //senderId: user.value.trim(),
            senderId: docUser.value.trim(),
            docId: docName.value.trim(),
            type: 'TEXT'
        }

        stompClient.send('/app/controllers.openDoc',{},
            JSON.stringify(editorMessage));

    }
}

function saveDocument(event) {

    var clicked = event.target;
    var clickedBtn = clicked.getAttribute('id');

    console.log("name of clicked button",clickedBtn);
    const response =  fetch('/save/'+docName.value.trim() + '/' + docUser.value.trim());
    if (response === '200') {
        console.log("doc saved");
    }
    else {console.log("errorrr!!!");}

}

roomPage.addEventListener('submit',connect,true);
//roomPage.addEventListener('submit',connectTwo,true);
//roomPage.addEventListener('submit',createDoc,true);
addUserForm.addEventListener('submit',addShareUser,true);


editor.addEventListener('input',writeToEditor,true);
openDoc.addEventListener('submit',openDocument,true);
saveDoc.addEventListener('click',saveDocument,true);



//editor.addEventListener('mouseout',onMessageReceived,true);
