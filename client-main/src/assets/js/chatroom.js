"use strict";
let sendToServer = null;
document.addEventListener("DOMContentLoaded", init);

async function init() {
    sendToServer = openSocket();
    if (localStorage.getItem("currentchattype") === "public") {
        document.querySelector("#send-button").addEventListener("click", sendPublicMessage);
    } else if (localStorage.getItem("currentchattype") === "private") {
        //get chatmessages from server and display them
        await loadPrivateChatMessages();
        const chattername = await loadChatInfo();
        document.querySelector("#username").innerHTML += `<p>${chattername}</p>`;
        document.querySelector("#send-button").addEventListener("click", sendPrivateMessage);
    }
}

function sendPublicMessage(e) {
    e.preventDefault();
    const message = document.querySelector("#chat-message");
    if (message.value !== "") {
        const user = JSON.parse(localStorage.getItem("user"));
        const data = {type: 'message', marsid: user.marsid, "message": message.value};
        sendToServer(data);
        message.value = "";
    }
}

function sendPrivateMessage(e) {
    e.preventDefault();
    const message = document.querySelector("#chat-message");
    if (message.value !== "") {
        const user = JSON.parse(localStorage.getItem("user"));
        const chatid = JSON.parse(localStorage.getItem("currentChatId"));
        const data = {type: "privatemessage", "chatid": chatid, marsid: user.marsid, message: message.value};
        sendToServer(data);
        message.value = "";
    }
}

async function loadPrivateChatMessages() {
    const api_response = await getAllChatsWithUser(localStorage.getItem("currentChatId"));
    const response = [...api_response].reverse();
    response.forEach(message => {
        let timestamp = message.timestamp;
        timestamp = timestamp.substring(0, (timestamp.length - 7));
        let user = localStorage.getItem("user");
        user = JSON.parse(user);
        if (`${message.name}` === user.name) {
            document.querySelector("#messages").insertAdjacentHTML("beforeend", `<div class="chatMessage owner">
			<p> ${message.name} </p> <p> ${message.content} </p> <p> ${timestamp} </p> 
		</div>`);
        } else {
            document.querySelector("#messages").insertAdjacentHTML("beforeend", `<div class="chatMessage friend">
			<p> ${message.name} </p> <p> ${message.content} </p> <p> ${timestamp} </p> 
		</div>`);
        }
    });
}

async function loadChatInfo() {
    const response = await getAllChats();
    let currentChatId = localStorage.getItem("currentChatId");
    currentChatId = parseInt(currentChatId);
    let result;
    response.forEach(chat => {
        if (chat.chatid === currentChatId) {
            result = chat;
        }
    });
    return result.username;
}
