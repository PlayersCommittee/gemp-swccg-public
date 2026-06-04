var GempSwccgCommunication = Class.extend({
    url:null,
    failure:null,

    init:function (url, failure) {
        this.url = url;
        this.apiBase = url + "/api";
        this.failure = failure;
        this._gameWs = null;
        this._gameUpdateCallback = null;
        this._gameErrorMap = null;
        this._pendingGameUpdate = null;
        this._gameChannelNumber = null;
        this._gameSnapshotReceived = false;
        this._gameWsReconnectTimer = null;
        this._gameWsReconnectAttempts = 0;
        this._gameWsMaxReconnectAttempts = 6;
        this._gameWsBaseDelayMs = 1000;
        this._gameWsMaxDelayMs = 15000;
        this._gameWsEnabled = false;
        this._gameWsDisabled = false;
        this._chatSockets = {};
        this._chatCallbacks = {};
        this._chatErrorMaps = {};
        this._chatReconnectTimers = {};
        this._chatReconnectAttempts = {};
        this._chatUsers = {};
        this._chatWsEnabled = {};
        this._chatWsDisabled = {};
        this._hallSocket = null;
        this._hallCallback = null;
        this._hallErrorMap = null;
        this._hallReconnectTimer = null;
        this._hallReconnectAttempts = 0;
        this._hallWsEnabled = false;
        this._hallWsDisabled = false;
        this._hallPendingUpdates = [];
        this._hallState = {};
        this._hallChannelNumber = null;
        this._hallKnownQueues = {};
        this._hallKnownTournaments = {};
        this._hallKnownTables = {};
        this._authTokenSnapshot = null;
        this._authTokenChangeHandler = null;
    },

    errorCheck:function (errorMap) {
        var that = this;
        return function (xhr, status, request) {
            var errorStatus = "" + xhr.status;
            if (errorMap != null && errorMap[errorStatus] != null)
                errorMap[errorStatus](xhr, status, request);
            else if (""+xhr.status != "200")
                that.failure(xhr, status, request);
        };
    },

    logout:function (callback, errorMap) {
        var that = this;
        var authHeaders = this.buildAuthHeaders();
        $.ajax({
            type:"POST",
            url:this.apiBase + "/auth/logout",
            cache:false,
            async:false,
            headers:authHeaders,
            xhrFields:{ withCredentials:true },
            success:function (payload, status, request) {
                that.clearAuthToken();
                if (callback != null)
                    callback(payload, status, request);
            },
            error:function (xhr, status, request) {
                that.clearAuthToken();
                that.errorCheck(errorMap)(xhr, status, request);
            },
            dataType:"json"
        });
    },

    getDelivery:function (callback) {
        $.ajax({
            type:"GET",
            url:this.url + "/delivery",
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:callback,
            error:null,
            dataType:"xml"
        });
    },

    deliveryCheck:function (callback) {
        var that = this;
        return function (xml, status, request) {
            var delivery = request.getResponseHeader("Delivery-Service-Package");
            if (delivery == "true" && window.deliveryService != null)
                that.getDelivery(window.deliveryService);
            callback(xml);
        };
    },
    
    deliveryCheckStatus:function (callback) {
        var that = this;
        return function (xml, status, request) {
            var delivery = request.getResponseHeader("Delivery-Service-Package");
            if (delivery == "true" && window.deliveryService != null)
                that.getDelivery(window.deliveryService);
            callback(xml, request.status);
        };
    },

    getGameHistory:function (start, count, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/gameHistory",
            cache:false,
            data:{
                start:start,
                count:count,
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    getStats:function (startDay, length, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/stats",
            cache:false,
            data:{
                startDay:startDay,
                length:length,
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    getPlayerStats:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/playerStats",
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    getCollectionStats:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/playerCollectionStats",
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getLiveTournaments:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/tournament",
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    getHistoryTournaments:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/tournament/history",
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    getTournament:function (tournamentId, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/tournament/" + tournamentId,
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    getLeagues:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/league",
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    getLeague:function (type, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/league/" + type,
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    joinLeague:function (code, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/league/" + code,
            cache:false,
            data:{
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    getReplay:function (replayId, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/replay/" + replayId,
            cache:false,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    startGameSession:function (callback, errorMap) {
        var that = this;
        this._gameErrorMap = errorMap;
        this.startGameSessionHttp(function (xml) {
            that.captureGameChannelFromXml(xml);
            that._gameSnapshotReceived = true;
            callback(xml);
            if (that.supportsWebSockets() && !that._gameWsDisabled) {
                that._gameWsEnabled = true;
                that.ensureGameSocket();
            } else {
                that._gameWsEnabled = false;
            }
        }, errorMap);
    },
    startGameSessionHttp:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/game/" + getUrlParam("gameId"),
            cache:false,
            data:{ participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    updateGameState:function (channelNumber, callback, errorMap) {
        this._gameErrorMap = errorMap;
        if (channelNumber != null && channelNumber !== "")
            this._gameChannelNumber = channelNumber;

        // WS-first mode: modern clients stay on websocket transport and do not re-enter HTTP long polling.
        if (this.supportsWebSockets()) {
            if (this._gameWsDisabled)
                return;
            this._gameWsEnabled = true;
            this._gameUpdateCallback = callback;
            this.flushPendingGameUpdate();
            this.ensureGameSocket();
            return;
        }

        this._gameWsEnabled = false;
        this.updateGameStateHttp(channelNumber, callback, errorMap);
    },
    updateGameStateHttp:function (channelNumber, callback, errorMap) {
        var that = this;
        $.ajax({
            type:"POST",
            url:this.url + "/game/" + getUrlParam("gameId"),
            cache:false,
            data:{
                channelNumber:channelNumber,
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(function (xml) {
                that.captureGameChannelFromXml(xml);
                callback(xml);
            }),
            timeout: 20000,
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getGameCardModifiers:function (cardId, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/game/" + getUrlParam("gameId") + "/cardInfo",
            cache:false,
            data:{
                cardId:cardId,
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    gameDecisionMade:function (decisionId, response, channelNumber, callback, errorMap) {
        this._gameErrorMap = errorMap;
        if (channelNumber != null && channelNumber !== "")
            this._gameChannelNumber = channelNumber;

        if (this.supportsWebSockets()) {
            if (this._gameWsDisabled)
                return;
            this._gameWsEnabled = true;
            this.ensureGameSocket();

            if (this.sendGameDecisionWs(decisionId, response, channelNumber)) {
                callback(this.createEmptyGameUpdateXml(channelNumber));
                return;
            }
        }

        this.gameDecisionMadeHttp(decisionId, response, channelNumber, callback, errorMap);
    },
    gameDecisionMadeHttp:function (decisionId, response, channelNumber, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/game/" + getUrlParam("gameId"),
            cache:false,
            data:{
                channelNumber:channelNumber,
                participantId:getUrlParam("participantId"),
                decisionId:decisionId,
                decisionValue:response },
            success:this.deliveryCheck(callback),
            timeout: 20000,
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    captureGameChannelFromXml:function (xml) {
        if (xml == null || xml.documentElement == null)
            return;
        var root = xml.documentElement;
        if (root.tagName == "gameState" || root.tagName == "update") {
            var channelNumber = root.getAttribute("cn");
            if (channelNumber != null && channelNumber !== "")
                this._gameChannelNumber = channelNumber;
        }
    },
    concede:function (errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/game/" + getUrlParam("gameId") + "/concede",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    cancel:function (errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/game/" + getUrlParam("gameId") + "/cancel",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    extendGameTimer:function (minutesToExtend, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/game/" + getUrlParam("gameId") + "/extendGameTimer",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                minutesToExtend:minutesToExtend},
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    disableActionTimer:function (errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/game/" + getUrlParam("gameId") + "/disableActionTimer",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getDecks:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/deck/list",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getPrettyDeckLink: function(deckName) {
        var cacheBreaker = Math.round(Math.random()*1000000);
        return this.url + "/deck?deckName="+deckName+"&cacheBreaker=" + cacheBreaker + "&pretty=true";
    },
    getDeck:function (deckName, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/deck",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                deckName:deckName },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getLibraryDecks:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/deck/libraryList",
            cache:false,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getLibraryDeck:function (deckName, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/deck/library",
            cache:false,
            data:{
                deckName:deckName },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getCollectionTypes:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/collection",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getMerchant:function (filter, ownedCompareSelect, ownedMin, start, count, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/merchant",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                filter:filter,
                ownedCompareSelect:ownedCompareSelect,
                ownedMin:ownedMin,
                start:start,
                count:count},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    buyItem:function (blueprintId, price, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/merchant/buy",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                blueprintId:blueprintId,
                price:price},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    sellItem:function (blueprintId, price, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/merchant/sell",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                blueprintId:blueprintId,
                price:price},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    sellAll:function (blueprintId, price, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/merchant/sellAll",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                blueprintId:blueprintId,
                price:price},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    tradeInFoil:function (blueprintId, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/merchant/tradeFoil",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                blueprintId:blueprintId},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getCollection:function (collectionType, filter, start, count, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/collection/" + collectionType,
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                filter:filter,
                start:start,
                count:count},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    openPack:function (collectionType, pack, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/collection/" + collectionType,
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                pack:pack},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    openSelectionPack:function (collectionType, pack, selection, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/collection/" + collectionType,
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                pack:pack,
                selection:selection},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    saveDeck:function (deckName, contents, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/deck",
            cache:false,
            async:false,
            data:{
                participantId:getUrlParam("participantId"),
                deckName:deckName,
                deckContents:contents},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    renameDeck:function (oldDeckName, deckName, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/deck/rename",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                oldDeckName:oldDeckName,
                deckName:deckName},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    deleteDeck:function (deckName, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/deck/delete",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                deckName:deckName},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    getDeckStats:function (contents, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/deck/stats",
            cache:false,
            data:{
                participantId:getUrlParam("participantId"),
                deckContents:contents},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    // Charlie Code
    loadShields:function (shieldUrl, callback, errorMap) {
        $.ajax({
            type:"GET",
            url: shieldUrl,
            cache:false,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
       });
    },
    startChat:function (room, callback, errorMap) {
        this._chatCallbacks[room] = callback;
        this._chatErrorMaps[room] = errorMap;

        if (this.supportsWebSockets()) {
            if (this._chatWsDisabled[room])
                return;
            this._chatWsEnabled[room] = true;
            this.ensureChatSocket(room);
            return;
        }

        this._chatWsEnabled[room] = false;
        this.startChatHttp(room, callback, errorMap);
    },
    startChatHttp:function (room, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/chat/" + room,
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:callback,
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    chatErrorCheckWithRetry:function (room, latestMsgId, callback, errorMap, tryNum, maxTries) {
        var that = this;
        return function (xhr, status, request) {
            var errorStatus = "" + xhr.status;
            if (errorStatus == "0") {
                // Try again
                setTimeout(function() {
                    that.updateChat(room, latestMsgId, callback, errorMap, tryNum + 1, maxTries);
                });
                return;
            }
            if (errorMap != null && errorMap[errorStatus] != null)
                errorMap[errorStatus](xhr, status, request);
            else if (errorStatus != "200")
                that.failure(xhr, status, request);
        };
    },
    updateChat:function (room, latestMsgId, callback, errorMap, tryNum, maxTries) {
        this._chatCallbacks[room] = callback;
        this._chatErrorMaps[room] = errorMap;

        // WS-first mode: modern clients stay on websocket transport and do not re-enter HTTP long polling.
        if (this.supportsWebSockets()) {
            if (this._chatWsDisabled[room])
                return;
            this._chatWsEnabled[room] = true;
            this.ensureChatSocket(room);
            return;
        }

        this.updateChatHttp(room, latestMsgId, callback, errorMap, tryNum, maxTries);
    },
    updateChatHttp:function (room, latestMsgId, callback, errorMap, tryNum, maxTries) {
        $.ajax({
            type:"POST",
            url:this.url + "/chat/" + room,
            cache:false,
            async:true,
            data:{
                participantId:getUrlParam("participantId"),
                latestMsgIdRcvd:latestMsgId},
            success:callback,
            timeout: 5000,
            error:this.chatErrorCheckWithRetry(room, latestMsgId, callback, errorMap, tryNum, maxTries),
            dataType:"xml"
        });
    },
    sendChatMessage:function (room, messages, errorMap) {
        if (this._chatWsEnabled[room]) {
            var socket = this._chatSockets[room];
            if (socket != null && socket.readyState === 1) {
                socket.send(JSON.stringify({ type:"message", text:messages }));
                return;
            }
            this.ensureChatSocket(room);
        }

        this.sendChatMessageHttp(room, messages, errorMap);
    },
    sendChatMessageHttp:function (room, messages, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/chat/" + room,
            cache:false,
            async:false,
            data:{
                participantId:getUrlParam("participantId"),
                message:messages},
            traditional:true,
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    supportsWebSockets:function () {
        return window != null && typeof window.WebSocket != "undefined";
    },
    closeSocketSafely:function (socket) {
        if (socket == null)
            return;
        try {
            socket.close();
        } catch (e) {
        }
    },
    closeRealtimeConnections:function () {
        this._gameWsEnabled = false;
        this._gameUpdateCallback = null;
        this.closeSocketSafely(this._gameWs);
        this._gameWs = null;
        if (this._gameWsReconnectTimer != null) {
            clearTimeout(this._gameWsReconnectTimer);
            this._gameWsReconnectTimer = null;
        }

        this._hallWsEnabled = false;
        this._hallCallback = null;
        this.closeSocketSafely(this._hallSocket);
        this._hallSocket = null;
        if (this._hallReconnectTimer != null) {
            clearTimeout(this._hallReconnectTimer);
            this._hallReconnectTimer = null;
        }

        for (var room in this._chatSockets) {
            if (this._chatSockets.hasOwnProperty(room)) {
                this._chatWsEnabled[room] = false;
                this.closeSocketSafely(this._chatSockets[room]);
                this._chatSockets[room] = null;
            }
        }
        for (var timerRoom in this._chatReconnectTimers) {
            if (this._chatReconnectTimers.hasOwnProperty(timerRoom) && this._chatReconnectTimers[timerRoom] != null) {
                clearTimeout(this._chatReconnectTimers[timerRoom]);
                this._chatReconnectTimers[timerRoom] = null;
            }
        }
    },
    getAuthToken:function () {
        if (typeof localStorage == "undefined")
            return null;
        try {
            return localStorage.getItem("gemp.jwt");
        } catch (e) {
            return null;
        }
    },
    storeAuthToken:function (token) {
        if (token == null || token === "" || typeof localStorage == "undefined")
            return;
        try {
            localStorage.setItem("gemp.jwt", token);
        } catch (e) {
        }
    },
    clearAuthToken:function () {
        if (typeof localStorage == "undefined")
            return;
        try {
            localStorage.removeItem("gemp.jwt");
        } catch (e) {
        }
    },
    getAuthSubjectFromToken:function (token) {
        if (token == null || token === "")
            return null;

        var parts = token.split(".");
        if (parts.length !== 3)
            return null;

        try {
            var payload = parts[1].replace(/-/g, "+").replace(/_/g, "/");
            while (payload.length % 4 !== 0) {
                payload += "=";
            }
            var decoded = JSON.parse(atob(payload));
            return decoded != null ? decoded.sub : null;
        } catch (e) {
            return null;
        }
    },
    watchAuthTokenChanges:function (onChanged) {
        this.unwatchAuthTokenChanges();

        if (typeof window == "undefined" || typeof window.addEventListener != "function")
            return;
        if (typeof localStorage == "undefined")
            return;

        var that = this;
        this._authTokenSnapshot = this.getAuthToken();
        if (this._authTokenSnapshot == null || this._authTokenSnapshot === "")
            return;

        this._authTokenChangeHandler = function (event) {
            if (event == null || event.storageArea !== localStorage)
                return;
            if (event.key !== "gemp.jwt")
                return;

            var currentToken = that.getAuthToken();
            if (currentToken === that._authTokenSnapshot)
                return;

            if (onChanged != null)
                onChanged(
                    that._authTokenSnapshot,
                    currentToken,
                    that.getAuthSubjectFromToken(that._authTokenSnapshot),
                    that.getAuthSubjectFromToken(currentToken)
                );
            that._authTokenSnapshot = currentToken;
        };
        window.addEventListener("storage", this._authTokenChangeHandler);
    },
    unwatchAuthTokenChanges:function () {
        if (this._authTokenChangeHandler != null && typeof window != "undefined" && typeof window.removeEventListener == "function")
            window.removeEventListener("storage", this._authTokenChangeHandler);
        this._authTokenChangeHandler = null;
        this._authTokenSnapshot = null;
    },
    buildAuthHeaders:function () {
        var token = this.getAuthToken();
        if (token != null && token !== "")
            return { Authorization:"Bearer " + token };
        return {};
    },
    parseJsonSafely:function (payload) {
        if (payload == null || payload === "")
            return null;
        if (typeof payload === "object")
            return payload;
        try {
            return JSON.parse(payload);
        } catch (e) {
            return null;
        }
    },
    parseXmlSafely:function (payload) {
        if (payload == null || payload === "")
            return null;
        try {
            if (typeof $ != "undefined" && $.parseXML)
                return $.parseXML(payload);
        } catch (e) {
        }
        if (typeof DOMParser == "undefined")
            return null;
        try {
            return (new DOMParser()).parseFromString(payload, "text/xml");
        } catch (e2) {
            return null;
        }
    },
    buildWsBase:function () {
        var protocol = (window.location.protocol === "https:") ? "wss://" : "ws://";
        return protocol + window.location.host + "/gemp-swccg-server/ws";
    },
    flushPendingGameUpdate:function () {
        if (this._gameUpdateCallback == null || this._pendingGameUpdate == null)
            return;

        var callback = this._gameUpdateCallback;
        this._gameUpdateCallback = null;
        callback(this._pendingGameUpdate);
        this._pendingGameUpdate = null;
    },
    ensureGameSocket:function () {
        if (!this._gameWsEnabled)
            return;

        if (this._gameWs != null && (this._gameWs.readyState === 0 || this._gameWs.readyState === 1))
            return;

        var gameId = getUrlParam("gameId");
        if (gameId == null || gameId === "")
            return;

        var wsUrl = this.buildWsBase() + "?channel=game&gameId=" + encodeURIComponent(gameId);

        var participantId = getUrlParam("participantId");
        if (participantId != null && participantId !== "")
            wsUrl += "&participantId=" + encodeURIComponent(participantId);

        if (this._gameChannelNumber != null && this._gameChannelNumber !== "")
            wsUrl += "&channelNumber=" + encodeURIComponent(this._gameChannelNumber);

        var that = this;
        this._gameWs = new WebSocket(wsUrl);

        this._gameWs.onopen = function () {
            that._gameWsReconnectAttempts = 0;
            if (that._gameWsReconnectTimer != null) {
                clearTimeout(that._gameWsReconnectTimer);
                that._gameWsReconnectTimer = null;
            }
        };

        this._gameWs.onmessage = function (event) {
            that.handleGameWsMessage(event.data);
        };

        this._gameWs.onerror = function () {
            // Reconnect/fallback is handled from onclose.
        };

        this._gameWs.onclose = function (event) {
            that._gameWs = null;
            if (!that._gameWsEnabled)
                return;

            if (that.isAuthClose(event)) {
                that.handleGameError("401");
                return;
            }
            if (that.isConcurrentClose(event)) {
                that.handleGameError("409");
                return;
            }
            if (that.isGoneClose(event)) {
                that.handleGameError("404");
                return;
            }
            that.scheduleGameReconnect();
        };
    },
    scheduleGameReconnect:function () {
        if (!this._gameWsEnabled)
            return;
        if (this._gameWsReconnectTimer != null)
            return;

        if (this._gameWsReconnectAttempts >= this._gameWsMaxReconnectAttempts) {
            this.enableGamePollingFallback();
            return;
        }

        var delay = Math.min(this._gameWsMaxDelayMs, this._gameWsBaseDelayMs * Math.pow(2, this._gameWsReconnectAttempts));
        this._gameWsReconnectAttempts += 1;

        var that = this;
        this._gameWsReconnectTimer = setTimeout(function () {
            that._gameWsReconnectTimer = null;
            that.ensureGameSocket();
        }, delay);
    },
    enableGamePollingFallback:function () {
        if (this._gameWsDisabled)
            return;

        this._gameWsEnabled = false;
        this._gameWsDisabled = true;

        if (this._gameWsReconnectTimer != null) {
            clearTimeout(this._gameWsReconnectTimer);
            this._gameWsReconnectTimer = null;
        }

        this.closeSocketSafely(this._gameWs);
        this._gameWs = null;

        // In WS-only mode we surface an error instead of switching back to HTTP polling.
        this.handleGameError("0");
    },
    handleGameWsMessage:function (data) {
        var payload = this.parseJsonSafely(data);
        if (payload != null && payload.type === "game") {
            if (payload.event === "error") {
                this.handleGameError("" + (payload.status || "0"));
                return;
            }
            if (payload.event === "ack")
                return;
        }

        var xml = this.parseXmlSafely(data);
        if (xml == null || xml.documentElement == null)
            return;

        var rootName = xml.documentElement.tagName;
        if (rootName !== "gameState" && rootName !== "update")
            return;

        this.captureGameChannelFromXml(xml);
        if (rootName === "gameState")
            this._gameSnapshotReceived = true;

        if (this._gameUpdateCallback != null) {
            var callback = this._gameUpdateCallback;
            this._gameUpdateCallback = null;
            callback(xml);
            return;
        }

        this.queuePendingGameUpdate(xml);
    },
    queuePendingGameUpdate:function (xml) {
        if (xml == null || xml.documentElement == null)
            return;

        if (this._pendingGameUpdate == null) {
            this._pendingGameUpdate = xml;
            return;
        }

        var pendingRoot = this._pendingGameUpdate.documentElement;
        var incomingRoot = xml.documentElement;
        if (pendingRoot == null || incomingRoot == null) {
            this._pendingGameUpdate = xml;
            return;
        }

        // Keep queue behavior from polling mode by appending incremental update nodes.
        if (pendingRoot.tagName !== "update" || incomingRoot.tagName !== "update") {
            this._pendingGameUpdate = xml;
            return;
        }

        var incomingChannel = incomingRoot.getAttribute("cn");
        if (incomingChannel != null && incomingChannel !== "")
            pendingRoot.setAttribute("cn", incomingChannel);

        for (var i = 0; i < incomingRoot.childNodes.length; i++) {
            var child = incomingRoot.childNodes[i];
            if (child == null || child.nodeType !== 1)
                continue;
            pendingRoot.appendChild(this._pendingGameUpdate.importNode(child, true));
        }
    },
    handleGameError:function (status) {
        if (this._gameWs != null) {
            try {
                this._gameWs.close();
            } catch (e) {
            }
        }
        this._gameWs = null;
        this._gameWsEnabled = false;

        if (this._gameErrorMap != null && this._gameErrorMap[status] != null)
            this._gameErrorMap[status]();
        else if (this.failure != null)
            this.failure({ status:status }, null, null);
    },
    sendGameDecisionWs:function (decisionId, response, channelNumber) {
        if (this._gameWs == null || this._gameWs.readyState !== 1)
            return false;

        var payload = {
            type:"decision",
            decisionId:decisionId,
            decisionValue:response,
            channelNumber:channelNumber
        };
        if (payload.channelNumber == null || payload.channelNumber === "")
            payload.channelNumber = this._gameChannelNumber;

        this.applyAutoPassSettings(payload);
        this.applyParticipantOverride(payload);
        this._gameWs.send(JSON.stringify(payload));
        return true;
    },
    applyParticipantOverride:function (payload) {
        var participantId = getUrlParam("participantId");
        if (participantId != null && participantId !== "")
            payload.participantId = participantId;
    },
    applyAutoPassSettings:function (payload) {
        if (typeof $ == "undefined" || typeof $.cookie != "function")
            return;

        var autoPass = $.cookie("autoPass");
        if (autoPass === "false") {
            payload.autoPassEnabled = false;
            return;
        }

        var phases = $.cookie("autoPassPhases");
        if (phases == null || phases === "")
            return;

        var entries = phases.split("0");
        var result = [];
        for (var i = 0; i < entries.length; i++) {
            if (entries[i] != null && entries[i] !== "")
                result.push(entries[i]);
        }
        if (result.length > 0)
            payload.autoPassPhases = result;
    },
    createEmptyGameUpdateXml:function (channelNumber) {
        var doc = document.implementation.createDocument("", "", null);
        var root = doc.createElement("update");
        if (channelNumber != null && channelNumber !== "")
            root.setAttribute("cn", "" + channelNumber);
        doc.appendChild(root);
        return doc;
    },
    ensureChatSocket:function (room) {
        if (!this._chatWsEnabled[room])
            return;

        var existing = this._chatSockets[room];
        if (existing != null && (existing.readyState === 0 || existing.readyState === 1))
            return;

        var wsUrl = this.buildWsBase() + "?channel=chat&room=" + encodeURIComponent(room);

        var that = this;
        var socket = new WebSocket(wsUrl);
        this._chatSockets[room] = socket;

        socket.onopen = function () {
            that._chatReconnectAttempts[room] = 0;
            if (that._chatReconnectTimers[room] != null) {
                clearTimeout(that._chatReconnectTimers[room]);
                that._chatReconnectTimers[room] = null;
            }
        };

        socket.onmessage = function (event) {
            that.handleChatMessage(room, event.data);
        };

        socket.onerror = function () {
            // onclose manages reconnect/fallback behavior.
        };

        socket.onclose = function (event) {
            that._chatSockets[room] = null;
            if (!that._chatWsEnabled[room])
                return;

            if (that.isAuthClose(event)) {
                that.enableChatPollingFallback(room, "401");
                return;
            }
            if (that.isConcurrentClose(event)) {
                that.enableChatPollingFallback(room, "409");
                return;
            }
            if (that.isGoneClose(event)) {
                that.enableChatPollingFallback(room, "404");
                return;
            }
            that.scheduleChatReconnect(room);
        };
    },
    scheduleChatReconnect:function (room) {
        if (!this._chatWsEnabled[room])
            return;

        var reconnectAttempts = this._chatReconnectAttempts[room] || 0;
        if (reconnectAttempts >= 6) {
            this.enableChatPollingFallback(room);
            return;
        }
        if (this._chatReconnectTimers[room] != null)
            return;

        var delay = Math.min(15000, 1000 * Math.pow(2, reconnectAttempts));
        this._chatReconnectAttempts[room] = reconnectAttempts + 1;

        var that = this;
        this._chatReconnectTimers[room] = setTimeout(function () {
            that._chatReconnectTimers[room] = null;
            that.ensureChatSocket(room);
        }, delay);
    },
    enableChatPollingFallback:function (room, status) {
        if (this._chatWsDisabled[room])
            return;

        this._chatWsEnabled[room] = false;
        this._chatWsDisabled[room] = true;

        var socket = this._chatSockets[room];
        this.closeSocketSafely(socket);
        this._chatSockets[room] = null;

        if (this._chatReconnectTimers[room] != null) {
            clearTimeout(this._chatReconnectTimers[room]);
            this._chatReconnectTimers[room] = null;
        }

        // In WS-only mode we surface an error instead of switching back to HTTP polling.
        this.handleChatError(room, status || "0");
    },
    isAuthClose:function (event) {
        if (event == null)
            return false;
        var code = event.code;
        if (code === 1008 || code === 4001 || code === 4003 || code === 4401 || code === 4403)
            return true;

        var reason = (event.reason || "").toLowerCase();
        return reason.indexOf("auth") > -1 || reason.indexOf("token") > -1 || reason.indexOf("jwt") > -1;
    },
    isConcurrentClose:function (event) {
        if (event == null)
            return false;
        if (event.code === 4409)
            return true;

        var reason = (event.reason || "").toLowerCase();
        return reason.indexOf("concurrent") > -1 || reason.indexOf("replaced") > -1;
    },
    isGoneClose:function (event) {
        if (event == null)
            return false;
        if (event.code === 4404)
            return true;

        var reason = (event.reason || "").toLowerCase();
        return reason.indexOf("not found") > -1 || reason.indexOf("closed") > -1 || reason.indexOf("removed") > -1;
    },
    handleChatMessage:function (room, data) {
        var payload = null;
        try {
            payload = JSON.parse(data);
        } catch (e) {
            return;
        }

        if (payload == null || payload.type !== "chat")
            return;

        if (payload.event === "error") {
            this.handleChatError(room, "0");
            return;
        }

        var messages = [];
        var users = this._chatUsers[room] || [];

        if (payload.event === "snapshot") {
            messages = payload.messages || [];
            users = payload.users || [];
            this._chatUsers[room] = users;
        } else if (payload.event === "message") {
            if (payload.message != null)
                messages = [payload.message];
            if (payload.users != null) {
                users = payload.users;
                this._chatUsers[room] = users;
            }
        } else if (payload.event === "users") {
            users = payload.users || [];
            this._chatUsers[room] = users;
        } else {
            return;
        }

        var callback = this._chatCallbacks[room];
        if (callback != null) {
            callback(this.buildChatXml(payload.room || room, this.normalizeChatMessages(messages), users));
        }
    },
    normalizeChatMessages:function (messages) {
        var result = [];
        if (messages == null)
            return result;

        for (var i = 0; i < messages.length; i++) {
            var message = messages[i];
            if (message == null)
                continue;
            if (typeof message === "string") {
                result.push({
                    id: (new Date()).getTime() + i,
                    from: "System",
                    text: message,
                    date: (new Date()).getTime()
                });
            } else {
                result.push({
                    id: message.id,
                    from: message.from,
                    text: message.text,
                    date: message.date
                });
            }
        }

        return result;
    },
    buildChatXml:function (room, messages, users) {
        var doc = document.implementation.createDocument("", "", null);
        var root = doc.createElement("chat");
        root.setAttribute("roomName", room);

        for (var i = 0; i < messages.length; i++) {
            var message = messages[i];
            var msgElem = doc.createElement("message");
            msgElem.setAttribute("msgId", "" + message.id);
            msgElem.setAttribute("from", message.from);
            msgElem.setAttribute("date", "" + message.date);
            msgElem.appendChild(doc.createTextNode(message.text));
            root.appendChild(msgElem);
        }

        for (var j = 0; j < users.length; j++) {
            var userElem = doc.createElement("user");
            userElem.appendChild(doc.createTextNode(users[j]));
            root.appendChild(userElem);
        }

        doc.appendChild(root);
        return doc;
    },
    handleChatError:function (room, status) {
        var errorMap = this._chatErrorMaps[room];
        if (errorMap != null && errorMap[status] != null)
            errorMap[status]();
        else if (this.failure != null)
            this.failure({ status:status }, null, null);
    },
    getHall:function (callback, errorMap) {
        var that = this;
        $.ajax({
            type:"GET",
            url:this.url + "/hall",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(function (xml) {
                that.captureHallStateFromXml(xml, true);
                callback(xml);
            }),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    updateHall:function (callback, channelNumber, errorMap) {
        this._hallErrorMap = errorMap;
        this._hallChannelNumber = channelNumber;

        // WS-first mode: modern clients stay on websocket transport and do not re-enter HTTP long polling.
        if (this.supportsWebSockets()) {
            if (this._hallWsDisabled)
                return;
            this._hallWsEnabled = true;
            this._hallCallback = callback;
            this.flushPendingHallUpdate();
            this.ensureHallSocket();
            return;
        }

        this._hallWsEnabled = false;
        this.updateHallHttp(callback, channelNumber, errorMap);
    },
    updateHallHttp:function (callback, channelNumber, errorMap) {
        var that = this;
        $.ajax({
            type:"POST",
            url:this.url + "/hall/update",
            cache:false,
            data:{
                channelNumber:channelNumber,
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(function (xml) {
                that.captureHallStateFromXml(xml, false);
                callback(xml);
            }),
            timeout: 20000,
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    flushPendingHallUpdate:function () {
        if (this._hallCallback == null)
            return;
        if (this._hallPendingUpdates == null || this._hallPendingUpdates.length === 0)
            return;

        var callback = this._hallCallback;
        this._hallCallback = null;
        callback(this._hallPendingUpdates.shift());
    },
    ensureHallSocket:function () {
        if (!this._hallWsEnabled)
            return;

        if (this._hallSocket != null && (this._hallSocket.readyState === 0 || this._hallSocket.readyState === 1))
            return;

        var wsUrl = this.buildWsBase() + "?channel=hall";

        var that = this;
        this._hallSocket = new WebSocket(wsUrl);

        this._hallSocket.onopen = function () {
            that._hallReconnectAttempts = 0;
            if (that._hallReconnectTimer != null) {
                clearTimeout(that._hallReconnectTimer);
                that._hallReconnectTimer = null;
            }
        };

        this._hallSocket.onmessage = function (event) {
            that.handleHallWsMessage(event.data);
        };

        this._hallSocket.onerror = function () {
            // onclose handles reconnect and fallback.
        };

        this._hallSocket.onclose = function (event) {
            that._hallSocket = null;
            if (!that._hallWsEnabled)
                return;

            if (that.isAuthClose(event)) {
                that.handleHallError("401");
                return;
            }
            if (that.isConcurrentClose(event)) {
                that.handleHallError("409");
                return;
            }
            that.scheduleHallReconnect();
        };
    },
    scheduleHallReconnect:function () {
        if (!this._hallWsEnabled)
            return;

        if (this._hallReconnectAttempts >= 3) {
            this.enableHallPollingFallback();
            return;
        }
        if (this._hallReconnectTimer != null)
            return;

        var delay = Math.min(4000, 500 * Math.pow(2, this._hallReconnectAttempts));
        this._hallReconnectAttempts += 1;

        var that = this;
        this._hallReconnectTimer = setTimeout(function () {
            that._hallReconnectTimer = null;
            that.ensureHallSocket();
        }, delay);
    },
    enableHallPollingFallback:function () {
        if (this._hallWsDisabled)
            return;

        this._hallWsEnabled = false;
        this._hallWsDisabled = true;

        if (this._hallReconnectTimer != null) {
            clearTimeout(this._hallReconnectTimer);
            this._hallReconnectTimer = null;
        }

        this.closeSocketSafely(this._hallSocket);
        this._hallSocket = null;

        // In WS-only mode we surface an error instead of switching back to HTTP polling.
        this.handleHallError("0");
    },
    handleHallWsMessage:function (data) {
        var payload = null;
        try {
            payload = JSON.parse(data);
        } catch (e) {
            return;
        }

        if (payload == null || payload.type !== "hall")
            return;

        var xml = this.buildHallXmlFromWs(payload);
        if (xml == null)
            return;

        if (this._hallCallback != null) {
            var callback = this._hallCallback;
            this._hallCallback = null;
            callback(xml);
            return;
        }

        this._hallPendingUpdates.push(xml);
        if (this._hallPendingUpdates.length > 200) {
            this._hallPendingUpdates.shift();
        }
    },
    handleHallError:function (status) {
        if (this._hallErrorMap != null && this._hallErrorMap[status] != null)
            this._hallErrorMap[status]();
        else if (this.failure != null)
            this.failure({ status:status }, null, null);
    },
    captureHallStateFromXml:function (xml, resetKnown) {
        if (xml == null || xml.documentElement == null)
            return;

        var root = xml.documentElement;
        if (root.tagName !== "hall")
            return;

        if (resetKnown) {
            this._hallKnownQueues = {};
            this._hallKnownTournaments = {};
            this._hallKnownTables = {};
        }

        this.captureHallAttribute(root, "channelNumber");
        this.captureHallAttribute(root, "currency");
        this.captureHallAttribute(root, "privateGamesEnabledBoolean");
        this.captureHallAttribute(root, "aiTablesEnabledBoolean");
        this.captureHallAttribute(root, "motd");
        this.captureHallAttribute(root, "serverTime");

        this.captureHallKnownEntities(root, "queue", this._hallKnownQueues);
        this.captureHallKnownEntities(root, "tournament", this._hallKnownTournaments);
        this.captureHallKnownEntities(root, "table", this._hallKnownTables);
    },
    captureHallAttribute:function (root, name) {
        if (root == null || name == null)
            return;
        var value = root.getAttribute(name);
        if (value != null && value !== "") {
            this._hallState[name] = value;
        }
    },
    captureHallKnownEntities:function (root, tagName, targetMap) {
        if (root == null || tagName == null || targetMap == null)
            return;

        var entities = root.getElementsByTagName(tagName);
        for (var i = 0; i < entities.length; i++) {
            var entity = entities[i];
            var id = entity.getAttribute("id");
            if (id == null || id === "")
                continue;
            var action = entity.getAttribute("action");
            if (action === "remove")
                delete targetMap[id];
            else
                targetMap[id] = true;
        }
    },
    buildHallXmlFromWs:function (payload) {
        if (payload == null || payload.event == null)
            return null;

        // Keep legacy hall renderer untouched: adapt incremental WS events into hall XML snapshots.
        if (payload.channelNumber != null)
            this._hallState.channelNumber = "" + payload.channelNumber;
        if (payload.motd != null)
            this._hallState.motd = payload.motd;
        if (payload.serverTime != null)
            this._hallState.serverTime = payload.serverTime;

        var doc = document.implementation.createDocument("", "", null);
        var hall = doc.createElement("hall");

        if (this._hallState.channelNumber != null)
            hall.setAttribute("channelNumber", this._hallState.channelNumber);
        if (this._hallState.currency != null)
            hall.setAttribute("currency", this._hallState.currency);
        if (this._hallState.privateGamesEnabledBoolean != null)
            hall.setAttribute("privateGamesEnabledBoolean", this._hallState.privateGamesEnabledBoolean);
        if (this._hallState.aiTablesEnabledBoolean != null)
            hall.setAttribute("aiTablesEnabledBoolean", this._hallState.aiTablesEnabledBoolean);
        if (this._hallState.motd != null)
            hall.setAttribute("motd", this._hallState.motd);
        if (this._hallState.serverTime != null)
            hall.setAttribute("serverTime", this._hallState.serverTime);

        var event = payload.event;
        if (event === "newPlayerGame") {
            var newGame = doc.createElement("newGame");
            if (payload.gameId != null)
                newGame.setAttribute("id", payload.gameId);
            hall.appendChild(newGame);
        } else if (event === "addTournamentQueue" || event === "updateTournamentQueue" || event === "removeTournamentQueue") {
            hall.appendChild(this.buildHallEntityElement(doc, "queue", payload.id, event, payload.props, this._hallKnownQueues));
        } else if (event === "addTournament" || event === "updateTournament" || event === "removeTournament") {
            hall.appendChild(this.buildHallEntityElement(doc, "tournament", payload.id, event, payload.props, this._hallKnownTournaments));
        } else if (event === "addTable" || event === "updateTable" || event === "removeTable") {
            hall.appendChild(this.buildHallEntityElement(doc, "table", payload.id, event, payload.props, this._hallKnownTables));
        } else if (event !== "channelNumber" && event !== "motd" && event !== "serverTime") {
            return null;
        }

        doc.appendChild(hall);
        return doc;
    },
    buildHallEntityElement:function (doc, tagName, id, event, props, knownMap) {
        var elem = doc.createElement(tagName);
        if (id != null)
            elem.setAttribute("id", id);

        var action = this.resolveHallAction(event, id, knownMap);
        elem.setAttribute("action", action);

        if (props != null && action !== "remove") {
            for (var key in props) {
                if (props.hasOwnProperty(key) && props[key] != null) {
                    elem.setAttribute(key, "" + props[key]);
                }
            }
        }

        return elem;
    },
    resolveHallAction:function (event, id, knownMap) {
        if (event == null)
            return "update";
        if (event.indexOf("remove") === 0) {
            if (knownMap != null && id != null)
                delete knownMap[id];
            return "remove";
        }

        var known = knownMap != null && id != null && knownMap[id] === true;
        if (knownMap != null && id != null)
            knownMap[id] = true;

        if (!known)
            return "add";
        return "update";
    },
    joinQueue:function (queueId, deckName, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/hall/queue/" + queueId,
            cache:false,
            data:{
                deckName:deckName,
                sampleDeck:sampleDeck,
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    leaveQueue:function (queueId, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/hall/queue/" + queueId + "/leave",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    dropFromTournament:function(tournamentId, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/hall/tournament/" + tournamentId + "/leave",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    joinTable:function (tableId, deckName, sampleDeck, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/hall/" + tableId,
            cache:false,
            data:{
                deckName:deckName,
                sampleDeck:sampleDeck,
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    leaveTable:function (tableId, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/hall/"+tableId+"/leave",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    createTable:function (format, deckName, sampleDeck, tableDesc, isPrivate, playVsAi, aiSkill, aiDeckName, aiDeckSample, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/hall",
            cache:false,
            data:{
                format:format,
                deckName:deckName,
                sampleDeck:sampleDeck,
                tableDesc:tableDesc,
                isPrivate:isPrivate,
                playVsAi:playVsAi,
                aiSkill:aiSkill,
                aiDeckName:aiDeckName,
                aiDeckSample:aiDeckSample,
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    setShutdownMode:function (enabled, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/shutdown",
            cache:false,
            data:{
                enabled:enabled
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    clearServerCache:function (callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/clearcache",
            cache:false,
            data:{},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    setPrivateMode:function (enabled, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/settings/privategames",
            cache:false,
            data:{
                enabled:enabled
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },

    setAiTablesEnabled:function (enabled, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/settings/aitables",
            cache:false,
            data:{
                enabled:enabled
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    setNewAccountRegistration:function (enabled, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/settings/newaccounts",
            cache:false,
            data:{
                enabled:enabled
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    setInGameStatTracking:function (enabled, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/settings/stattracking",
            cache:false,
            data:{
                enabled:enabled
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    purgeInGameStatisticsListeners:function (callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/settings/purgestattrackers",
            cache:false,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    setBonusAbilities:function (enabled, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/settings/bonusabilities",
            cache:false,
            data:{
                enabled:enabled
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    getMOTD:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/admin/motd/get",
            cache:false,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"json"
        });
    },
    
    setMOTD:function (motd, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/motd/update",
            cache:false,
            data:{
                motd:motd
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    addItems:function (collectionType, product, players, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/collections/additems",
            cache:false,
            data:{
                collectionType:collectionType,
                product:product,
                players:players
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    addItemsToAllPlayers:function (collectionType, reason, product, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/collections/additemstoall",
            cache:false,
            data:{
                collectionType:collectionType,
                reason:reason,
                product:product
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    addCurrency:function (players, currencyAmount, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/collections/addcurrency",
            cache:false,
            data:{
                players:players,
                currencyAmount:currencyAmount
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    resetUserPassword:function (login, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/user/passwordreset",
            cache:false,
            data:{
                login:login
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    permabanUser:function (login, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/user/ban/permanent",
            cache:false,
            data:{
                login:login
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    tempbanUser:function (login, duration, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/user/ban/temporary",
            cache:false,
            data:{
                login:login,
                duration:duration
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    unbanUser:function (login, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/user/ban/acquit",
            cache:false,
            data:{
                login:login
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    susUserSearch:function (login, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/users/detailedsearch",
            cache:false,
            data:{
                login:login
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    
    banMultiple:function (logins, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/users/ban/permanent",
            cache:false,
            data:{
                logins:logins
            },
            traditional: true,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    deactivateMultiple:function (logins, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/users/deactivate",
            cache:false,
            data:{
                logins:logins
            },
            traditional: true,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    
    showUsersWithFlag:function (flag, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/users/findwithflag",
            cache:false,
            data:{
                flag:flag
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    
    showPlaytesters:function (callback, errorMap) {
      this.showUsersWithFlag("PLAYTESTER", callback, errorMap)  
    },
    
    showCommentators:function (callback, errorMap) {
      this.showUsersWithFlag("COMMENTATOR", callback, errorMap)  
    },
    
    addFlagToUser:function (login, flag, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/user/addflag",
            cache:false,
            data:{
                login:login,
                flag:flag
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    addPlaytesterToUser:function (login, callback, errorMap) {
      this.addFlagToUser(login, "PLAYTESTER", callback, errorMap)  
    },
    
    addCommentatorToUser:function (login, callback, errorMap) {
      this.addFlagToUser(login, "COMMENTATOR", callback, errorMap)  
    },
    
    removeFlagFromUsers:function (logins, flag, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/users/removeflag",
            cache:false,
            data:{
                logins:logins,
                flag:flag
            },
            traditional:true,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    removeFlagFromUser:function (logins, flag, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/users/removeflag",
            cache:false,
            data:{
                logins:logins,
                flag:flag
            },
            traditional:false,
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    removePlaytesterFromUsers:function (logins, callback, errorMap) {
      this.removeFlagFromUsers(logins, "PLAYTESTER", callback, errorMap)  
    },
    
    removeCommentatorFromUsers:function (logins, callback, errorMap) {
      this.removeFlagFromUsers(logins, "COMMENTATOR", callback, errorMap)  
    },
    
    reactivateUser:function (logins, callback, errorMap) {
      this.removeFlagFromUser(logins, "DEACTIVATED", callback, errorMap)  
    },
    
    
    
    previewSealedLeague:function (name, cost, start, format, serieDuration, maxMatches, 
                              allowTimeExtensions, allowSpectators, showPlayerNames, 
                              invitationOnly, registrationInfo, decisionTimeoutSeconds, 
                              timePerPlayerMinutes,
                              callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/league/sealed/preview",
            cache:false,
            data:{
                name:name,
                cost:cost,
                start:start,
                format:format,
                serieDuration:serieDuration,
                maxMatches:maxMatches,
                allowTimeExtensions:allowTimeExtensions,
                allowSpectators:allowSpectators,
                showPlayerNames:showPlayerNames,
                invitationOnly:invitationOnly,
                registrationInfo:registrationInfo,
                decisionTimeoutSeconds:decisionTimeoutSeconds,
                timePerPlayerMinutes:timePerPlayerMinutes
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    
    addSealedLeague:function (name, cost, start, format, serieDuration, maxMatches, 
                              allowTimeExtensions, allowSpectators, showPlayerNames, 
                              invitationOnly, registrationInfo, decisionTimeoutSeconds, 
                              timePerPlayerMinutes,
                              callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/league/sealed/create",
            cache:false,
            data:{
                name:name,
                cost:cost,
                start:start,
                format:format,
                serieDuration:serieDuration,
                maxMatches:maxMatches,
                allowTimeExtensions:allowTimeExtensions,
                allowSpectators:allowSpectators,
                showPlayerNames:showPlayerNames,
                invitationOnly:invitationOnly,
                registrationInfo:registrationInfo,
                decisionTimeoutSeconds:decisionTimeoutSeconds,
                timePerPlayerMinutes:timePerPlayerMinutes
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },


    previewSoloDraftLeague:function (name, cost, start, format, serieDuration, maxMatches,
                              allowTimeExtensions, allowSpectators, showPlayerNames,
                              invitationOnly, registrationInfo, decisionTimeoutSeconds,
                              timePerPlayerMinutes,
                              callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/league/solodraft/preview",
            cache:false,
            data:{
                name:name,
                cost:cost,
                start:start,
                format:format,
                serieDuration:serieDuration,
                maxMatches:maxMatches,
                allowTimeExtensions:allowTimeExtensions,
                allowSpectators:allowSpectators,
                showPlayerNames:showPlayerNames,
                invitationOnly:invitationOnly,
                registrationInfo:registrationInfo,
                decisionTimeoutSeconds:decisionTimeoutSeconds,
                timePerPlayerMinutes:timePerPlayerMinutes
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    addSoloDraftLeague:function (name, cost, start, format, serieDuration, maxMatches,
                              allowTimeExtensions, allowSpectators, showPlayerNames,
                              invitationOnly, registrationInfo, decisionTimeoutSeconds,
                              timePerPlayerMinutes,
                              callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/league/solodraft/create",
            cache:false,
            data:{
                name:name,
                cost:cost,
                start:start,
                format:format,
                serieDuration:serieDuration,
                maxMatches:maxMatches,
                allowTimeExtensions:allowTimeExtensions,
                allowSpectators:allowSpectators,
                showPlayerNames:showPlayerNames,
                invitationOnly:invitationOnly,
                registrationInfo:registrationInfo,
                decisionTimeoutSeconds:decisionTimeoutSeconds,
                timePerPlayerMinutes:timePerPlayerMinutes
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },


    previewConstructedLeague:function (name, cost, start, collectionType,
                              allowTimeExtensions, allowSpectators, showPlayerNames,
                              invitationOnly, registrationInfo, decisionTimeoutSeconds,
                              timePerPlayerMinutes, lockedDeckType, formats, serieDurations, maxMatches,
                              callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/league/constructed/preview",
            cache:false,
            traditional: true,
            data:{
                name:name,
                cost:cost,
                start:start,
                collectionType:collectionType,
                allowTimeExtensions:allowTimeExtensions,
                allowSpectators:allowSpectators,
                showPlayerNames:showPlayerNames,
                invitationOnly:invitationOnly,
                registrationInfo:registrationInfo,
                decisionTimeoutSeconds:decisionTimeoutSeconds,
                timePerPlayerMinutes:timePerPlayerMinutes,
                lockedDeckType:lockedDeckType,
                formats:formats,
                serieDurations:serieDurations,
                maxMatches:maxMatches
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },

    addConstructedLeague:function (name, cost, start, collectionType,
                              allowTimeExtensions, allowSpectators, showPlayerNames,
                              invitationOnly, registrationInfo, decisionTimeoutSeconds,
                              timePerPlayerMinutes, lockedDeckType, formats, serieDurations, maxMatches,
                              callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/league/constructed/create",
            cache:false,
            traditional: true,
            data:{
                name:name,
                cost:cost,
                start:start,
                collectionType:collectionType,
                allowTimeExtensions:allowTimeExtensions,
                allowSpectators:allowSpectators,
                showPlayerNames:showPlayerNames,
                invitationOnly:invitationOnly,
                registrationInfo:registrationInfo,
                decisionTimeoutSeconds:decisionTimeoutSeconds,
                timePerPlayerMinutes:timePerPlayerMinutes,
                lockedDeckType:lockedDeckType,
                formats:formats,
                serieDurations:serieDurations,
                maxMatches:maxMatches
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    addPlayersToLeague:function (leagueType, players, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/league/addplayers",
            cache:false,
            traditional: true,
            data:{
                leagueType:leagueType,
                players:players
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    
    leagueDeckCheck:function (leagueId, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/league/deckcheck",
            cache:false,
            data:{
                leagueId:leagueId
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    
    addTables:function (name, tournament, format, timer, playerones, playertwos, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/admin/addTables",
            cache:false,
            data:{
                name:name,
                tournament:tournament,
                format:format,
                timer:timer,
                playerones:playerones,
                playertwos:playertwos
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    //NEVER EVER EVER use this for actual authentication
    // This is strictly to simplify things like auto-hiding
    // of the admin panel.  If you actually need functionality
    // gated behind authorization, it goes on the server
    // and not in here.
    
    getPlayerInfo:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/playerStats/playerInfo",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")
            },
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"json"
        });
    },
    
    getFormat:function (formatCode, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/hall/format/" + formatCode,
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    getStatus:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    login:function (login, password, callback, errorMap) {
        var that = this;
        $.ajax({
            type:"POST",
            url:this.apiBase + "/auth/login",
            cache:false,
            async:false,
            data:{
                login:login,
                password:password
            },
            xhrFields:{ withCredentials:true },
            success:function (payload, status, request) {
                var statusCode = "" + request.status;
                var authPayload = that.parseJsonSafely(payload);
                if (statusCode == "200" && authPayload != null && authPayload.token != null) {
                    that.storeAuthToken(authPayload.token);
                    callback(authPayload, request.status);
                    return;
                }
                that.clearAuthToken();
                callback(authPayload, request.status);
            },
            error:function (xhr, status, request) {
                that.clearAuthToken();
                that.errorCheck(errorMap)(xhr, status, request);
            },
            dataType:"text"
        });
    },
    register:function (login, password, callback, errorMap) {
        var that = this;
        $.ajax({
            type:"POST",
            url:this.apiBase + "/auth/register",
            cache:false,
            data:{
                login:login,
                password:password
            },
            xhrFields:{ withCredentials:true },
            success:function (payload, status, request) {
                var statusCode = "" + request.status;
                var authPayload = that.parseJsonSafely(payload);
                if (statusCode == "200" && authPayload != null && authPayload.token != null) {
                    that.storeAuthToken(authPayload.token);
                    callback(authPayload, request.status);
                    return;
                }
                that.clearAuthToken();
                callback(authPayload, request.status);
            },
            error:function (xhr, status, request) {
                that.clearAuthToken();
                that.errorCheck(errorMap)(xhr, status, request);
            },
            dataType:"text"
        });
    },
    getRegistrationForm:function (callback, errorMap) {
        $.ajax({
            type:"POST",
            url:"/gemp-swccg/includes/registrationForm.html",
            cache:false,
            async:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    getDraft:function (leagueType, callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/soloDraft/"+leagueType,
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:callback,
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    makeDraftPick:function (leagueType, choiceId, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/soloDraft/"+leagueType,
            cache:false,
            data:{
                choiceId:choiceId,
                participantId:getUrlParam("participantId")},
            success:callback,
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    }
});
