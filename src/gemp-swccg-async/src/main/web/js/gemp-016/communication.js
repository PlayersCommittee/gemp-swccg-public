var GempSwccgCommunication = Class.extend({
    url:null,
    failure:null,

    init:function (url, failure) {
        this.url = url;
        this.failure = failure;
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
        $.ajax({
            type:"POST",
            url:this.url + "/logout",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap)
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
            dataType:"xml"
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
        $.ajax({
            type:"POST",
            url:this.url + "/game/" + getUrlParam("gameId"),
            cache:false,
            data:{
                channelNumber:channelNumber,
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
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
    startChat:function (room, callback, errorMap) {
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
    getHall:function (callback, errorMap) {
        $.ajax({
            type:"GET",
            url:this.url + "/hall",
            cache:false,
            data:{
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheck(callback),
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
    },
    updateHall:function (callback, channelNumber, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/hall/update",
            cache:false,
            data:{
                channelNumber:channelNumber,
                participantId:getUrlParam("participantId") },
            success:this.deliveryCheck(callback),
            timeout: 20000,
            error:this.errorCheck(errorMap),
            dataType:"xml"
        });
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
    createTable:function (format, deckName, sampleDeck, tableDesc, isPrivate, callback, errorMap) {
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
    
    
    previewConstructedLeague:function (name, cost, start, collectionType,  
                              allowTimeExtensions, allowSpectators, showPlayerNames, 
                              invitationOnly, registrationInfo, decisionTimeoutSeconds, 
                              timePerPlayerMinutes, formats, serieDurations, maxMatches,
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
                              timePerPlayerMinutes, formats, serieDurations, maxMatches,
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
        $.ajax({
            type:"POST",
            url:this.url + "/login",
            cache:false,
            async:false,
            data:{
                login:login,
                password:password,
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheckStatus(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
        });
    },
    register:function (login, password, callback, errorMap) {
        $.ajax({
            type:"POST",
            url:this.url + "/register",
            cache:false,
            data:{
                login:login,
                password:password,
                participantId:getUrlParam("participantId")},
            success:this.deliveryCheckStatus(callback),
            error:this.errorCheck(errorMap),
            dataType:"html"
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
    }
});
