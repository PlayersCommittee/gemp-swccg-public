var ChatBoxUI = Class.extend({
    name:null,
    div:null,
    communication:null,

    chatMessagesDiv:null,
    chatTalkDiv:null,
    chatListDiv:null,

    showTimestamps:false,
    maxMessageCount:500,
    talkBoxHeight:25,

    chatUpdateInterval:750,
    latestMsgIdRcvd:-1,
    maxRetries:50,

    playerListener:null,
    hiddenClasses:null,

    hideSystemButton:null,
    lockButton:null,

    lockChat:false,
    stopUpdates: false,

    allPlayerIds: [],

    init:function (name, div, url, showList, playerListener, showHideSystemButton, showLockButton, allPlayerIds) {
        var that = this;
        this.hiddenClasses = new Array();
        this.playerListener = playerListener;
        this.name = name;
        this.div = div;
        this.communication = new GempSwccgCommunication(url, function (xhr, ajaxOptions, thrownError) {
            that.appendMessage("Unknown chat problem occurred (error=" + xhr.status + ")", "warningMessage");
        });

        this.chatMessagesDiv = $("<div class='chatMessages'></div>");
        this.div.append(this.chatMessagesDiv);

        this.allPlayerIds = allPlayerIds || [];

        if (this.name != null) {
            this.chatTalkDiv = $("<input type='text' class='chatTalk'>");

            if (showHideSystemButton) {
                this.hideSystemButton = $("<button id='showSystemMessages'>Toggle system messages</button>").button(
                {icons:{
                    primary:"ui-icon-zoomin"
                }, text:false});
                this.hideSystemButton.click(
                        function () {
                            if (that.isShowingMessageClass("systemMessage")) {
                                $('#showSystemMessages').button("option", "icons", {primary:'ui-icon-zoomin'});
                                that.hideMessageClass("systemMessage");
                            } else {
                                $('#showSystemMessages').button("option", "icons", {primary:'ui-icon-zoomout'});
                                that.showMessageClass("systemMessage");
                            }
                        });
                this.hideMessageClass("systemMessage");
            }

            if (showLockButton) {
                this.lockButton = $("<button id='lockChatButton'>Toggle lock chat</button>").button(
                {icons:{
                    primary:"ui-icon-locked"
                }, text:false});
                this.lockButton.click(
                        function () {
                            if (that.lockChat) {
                                $('#lockChatButton').button("option", "icons", {primary:'ui-icon-locked'});
                                that.lockChat = false;
                            } else {
                                $('#lockChatButton').button("option", "icons", {primary:'ui-icon-unlocked'});
                                that.lockChat = true;
                            }
                        });
            }

            if (showList) {
                this.chatListDiv = $("<div class='userList'></div>");
                this.div.append(this.chatListDiv);
            }
            if (this.hideSystemButton != null)
                this.div.append(this.hideSystemButton);
            if (this.lockButton != null)
                this.div.append(this.lockButton);
            this.div.append(this.chatTalkDiv);

            this.communication.startChat(this.name,
                    function (xml) {
                        that.processMessages(xml, true);
                    }, this.chatErrorMap());

            this.chatTalkDiv.bind("keypress", function (e) {
                var code = (e.keyCode ? e.keyCode : e.which);
                if (code == 13) {
                    var value = $(this).val();
                    if (value != "")
                        that.sendMessage(value);
                    $(this).val("");
                }
            });
        } else {
            this.talkBoxHeight = 0;
        }
    },

    hideMessageClass:function (msgClass) {
        this.hiddenClasses.push(msgClass);
        $("div.message." + msgClass, this.chatMessagesDiv).hide();
    },

    isShowingMessageClass:function (msgClass) {
        var index = $.inArray(msgClass, this.hiddenClasses);
        return index == -1;
    },

    showMessageClass:function (msgClass) {
        var index = $.inArray(msgClass, this.hiddenClasses);
        if (index > -1) {
            this.hiddenClasses.splice(index, 1);
            $("div.message." + msgClass, this.chatMessagesDiv).show();
        }
    },

    escapeHtml:function (text) {
        return $('<div/>').text(text).html();
    },

    setBounds:function (x, y, width, height) {
        var talkBoxPadding = 3;

        var userListWidth = 150;
        if (this.chatListDiv == null)
            userListWidth = 0;

        if (this.chatListDiv != null)
            this.chatListDiv.css({ position:"absolute", left:x + width - userListWidth + "px", top:y + "px", width:userListWidth, height:height - this.talkBoxHeight - 3 * talkBoxPadding, overflow:"auto" });
        this.chatMessagesDiv.css({ position:"absolute", left:x + "px", top:y + "px", width:width - userListWidth, height:height - this.talkBoxHeight - 3 * talkBoxPadding, overflow:"auto" });
        if (this.chatTalkDiv != null) {
            var leftTextBoxPadding = 0;

            if (this.hideSystemButton != null) {
                this.hideSystemButton.css({position:"absolute", left:x + width - talkBoxPadding - this.talkBoxHeight + "px", top:y - 2 * talkBoxPadding + (height - this.talkBoxHeight) + "px", width:this.talkBoxHeight, height:this.talkBoxHeight});
                leftTextBoxPadding += this.talkBoxHeight + talkBoxPadding;
            }
            if (this.lockButton != null) {
                this.lockButton.css({position:"absolute", left:x + width - talkBoxPadding - this.talkBoxHeight - leftTextBoxPadding + "px", top:y - 2 * talkBoxPadding + (height - this.talkBoxHeight) + "px", width:this.talkBoxHeight, height:this.talkBoxHeight});
                leftTextBoxPadding += this.talkBoxHeight + talkBoxPadding;
            }

            this.chatTalkDiv.css({ position:"absolute", left:x + talkBoxPadding + "px", top:y - 2 * talkBoxPadding + (height - this.talkBoxHeight) + "px", width:width - 3 * talkBoxPadding - leftTextBoxPadding, height:this.talkBoxHeight });
        }
    },

    appendMessage:function (message, msgClass) {
        if (msgClass == undefined)
            msgClass = "chatMessage";
        var messageDiv = $("<div class='message " + msgClass + "'>" + message + "</div>");

        this.chatMessagesDiv.append(messageDiv);
        var index = $.inArray(msgClass, this.hiddenClasses);
        if (!this.isShowingMessageClass(msgClass)) {
            messageDiv.hide();
        }

        if ($("div.message", this.chatMessagesDiv).length > this.maxMessageCount) {
            $("div.message", this.chatMessagesDiv).first().remove();
        }
        if (!this.lockChat)
            this.chatMessagesDiv.prop({ scrollTop:this.chatMessagesDiv.prop("scrollHeight") });
    },

    monthNames:["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],

    formatToTwoDigits:function (no) {
        if (no < 10)
            return "0" + no;
        else
            return no;
    },

    processMessages:function (xml, processAgain) {
        var root = xml.documentElement;
        if (root.tagName == 'chat') {
            var messages = root.getElementsByTagName("message");
            for (var i = 0; i < messages.length; i++) {
                var message = messages[i];
                var from = message.getAttribute("from");
                var text = message.childNodes[0].nodeValue;
                var msgId = Number(message.getAttribute("msgId"));

                var fromObserver = false;
                if (from && (-1 == this.allPlayerIds.indexOf(from))) {
                    fromObserver = true;
                }

                // Only process messages that have a higher message id than messages already received
                if (msgId > this.latestMsgIdRcvd) {
                    this.latestMsgIdRcvd = msgId;

                    var msgClass = "chatMessage";
                    if (from == "System") {
                        msgClass = "systemMessage";
                    }
                    if (fromObserver) {
                        msgClass += " fromObserver";
                    }

                    if (this.showTimestamps) {
                        var date = new Date(parseInt(message.getAttribute("date")));
                        var dateStr = this.monthNames[date.getMonth()] + " " + date.getDate() + " " + this.formatToTwoDigits(date.getHours()) + ":" + this.formatToTwoDigits(date.getMinutes()) + ":" + this.formatToTwoDigits(date.getSeconds());
                        this.appendMessage("<div class='timestamp'>[" + dateStr + "]</div> <b>" + from + ":</b> " + text, msgClass);
                    } else {
                        this.appendMessage("<b>" + from + ":</b> " + text, msgClass);
                    }
                }
            }

            var users = root.getElementsByTagName("user");
            if (this.playerListener != null) {
                var players = new Array();
                for (var i = 0; i < users.length; i++) {
                    var user = users[i];
                    var userName = user.childNodes[0].nodeValue;
                    players.push(userName);
                }
                this.playerListener(players);
            }

            if (this.chatListDiv != null) {
                this.chatListDiv.html("");
                var players = new Array();
                for (var i = 0; i < users.length; i++) {
                    var user = users[i];
                    var userName = user.childNodes[0].nodeValue;
                    players.push(userName);
                    this.chatListDiv.append("<div class='chatUser'>" + userName + "</div>");
                }
            }

            var that = this;

            if (processAgain)
                setTimeout(function () {
                    that.updateChatMessages();
                }, that.chatUpdateInterval);
        }
    },

    updateChatMessages:function () {
        var that = this;

        this.communication.updateChat(this.name, this.latestMsgIdRcvd, function (xml) {
            that.processMessages(xml, true);
        }, this.chatErrorMap(), 1, this.maxRetries);
    },

    sendMessage:function (message) {
        var that = this;
        this.communication.sendChatMessage(this.name, message, this.chatErrorMap());
    },

    chatMalfunction: function() {
        this.stopUpdates = true;
        this.chatTalkDiv.prop('disabled', true);
        this.chatTalkDiv.css({"background-color": "#ff9999"});
    },

    chatErrorMap:function() {
        var that = this;
        return {
            "0":function() {
                that.chatMalfunction();
                that.appendMessage("Chat server has been closed or there was a problem with your internet connection.", "warningMessage");
            },
            "401":function() {
                that.chatMalfunction();
                that.appendMessage("You are not logged in.", "warningMessage");
            },
            "403": function() {
                that.chatMalfunction();
                that.appendMessage("You have no permission to participate in this chat.", "warningMessage");
            },
            "404": function() {
                that.chatMalfunction();
                that.appendMessage("Chat room is closed.", "warningMessage");
            },
            "410": function() {
                that.chatMalfunction();
                that.appendMessage("You have been inactive for too long and were removed from the chat room. Refresh the page if you wish to re-enter.", "warningMessage");
            }
        };
    }
});
