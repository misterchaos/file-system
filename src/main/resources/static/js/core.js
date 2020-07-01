var baseUrl = "http://localhost:8080/filesystem/api/v1/"
var webContext = "http://localhost:8080/";
var auth = null;
var userId = null;
var currentDir = null;
var currentFile = null;

function goToLogin() {
    window.location.href = "../html/login.html";
}

function login() {
    var username = $("#username").val();
    if (username == null || username.trim() === "") {
        mdui.alert('请输入用户名！', '系统提示');
        return;
    }
    var settings = {
        "url": baseUrl + "/user/login",
        "method": "POST",
        "timeout": 0,
        "data": "{\n\t\"username\":\"" + username + "\"\n}",
        "headers": {
            "Content-Type": "application/json"
        }
    };

    $.ajax(settings).done(function (response, status, request) {
        console.log(response.code)
        if (response.code !== '1') {
            mdui.alert(response.message, '系统提示');
            return;
        }
        var user = response.data;
        auth = request.getResponseHeader("Authorization")
        window.location.href = "../?auth=" + auth + "&userId=" + user.userId;
    });
}

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

function deleteReq(direntId) {
    $("#progress").attr("style", "float:right;visibility:visible")
    var settings = {
        "url": baseUrl + "/core/" + direntId,
        "method": "DELETE",
        "timeout": 0,
        "headers": {
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": auth
        }
    };

    $.ajax(settings).done(function (response) {
            console.log(response.code)
            if (response.code !== '1') {
                mdui.alert(response.message, '系统提示');
            } else {
                openReq(currentDir.direntId, openFile);
            }
        }
    );
}

function getDisk() {

    var settings = {
        "url": baseUrl + "/core/disk",
        "method": "GET",
        "timeout": 0,
        "headers": {
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": auth
        }
    };

    $.ajax(settings).done(function (response) {
        console.log(response.code)
        if (response.code !== '1') {
            mdui.alert(response.message, '系统提示');
            return;
        }
        var data = response.data;
        $("#disk").text(data.info);
        $("#capacity").attr("style", 'width: ' + data.percent + '!important;')
    });
}

function openReq(dirent_id, callback) {


    var url;
    if (dirent_id == null) {
        url = baseUrl + "/core";
    } else {
        url = baseUrl + "/core/?id=" + dirent_id;
    }


    var settings = {
        "url": url,
        "method": "GET",
        "timeout": 0,
        "headers": {
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": auth
        }
    };

    $.ajax(settings).done(function (response) {
        console.log(response.code)
        if (response.code === '3001') {
            goToLogin();
            return;
        } else if (response.code !== '1') {
            mdui.alert(response.message, '系统提示');
            $("#progress").attr("style", "float:right;visibility:hidden")
            return;
        }
        var data = response.data;
        callback(data);
        getDisk();
        $("#progress").attr("style", "float:right;visibility:hidden")
    });
}

//打开文件或者目录
function openFile(data) {
    if (data.type === 'dir') {
        currentDir = data;
        showDir();
    } else {
        currentFile = data;
        showFile();
    }
    console.log("currentDir:")
    console.log(currentDir);
    console.log("currentFile:")
    console.log(currentFile);
}


function openEdit(data) {
    currentFile = data;
    if (data.type === 'dir') {
        editDir();
    } else {
        editFile();
    }
}

function editFile() {
    var editFile =
        '<div class="mdui-dialog-title" style="text-align: center;\n' +
        'padding: 20px 24px 0px 24px;" id="edit-title">编辑 ' + currentFile.filename + '</div>\n' +
        '<div class="mdui-textfield">\n' +
        '     <textarea class="mdui-textfield-input" rows="1" placeholder="文件名" id="edit-filename">'
        + currentFile.filename + '</textarea>\n' +
        '</div>\n' +
        '<div class="mdui-textfield">\n' +
        '            <textarea class="mdui-textfield-input" rows="10" placeholder="文件内容" id="edit-data">' +
        currentFile.data + '</textarea>\n' +
        '        </div>\n' +
        '        <button id="2" onclick="updateFile()" class="mdui-btn mdui-color-orange edit-button">保存</button>\n' +
        '        <button id="1" onclick="inst.close()" class="mdui-btn mdui-color-grey edit-button">关闭</button>\n';

    $("#edit").html(editFile);
    inst.open();
}

function editDir() {
    // 单行文本框
    mdui.prompt('请输入新的文件夹名称', '重命名',
        function (value) {
            updateDir(value);
        },
        function (value) {
            closeFile();
        }
    );
}


function showFile() {
    var editFile =
        '<div class="mdui-dialog-title" style="text-align: center;\n' +
        'padding: 20px 24px 0px 24px;" id="edit-title">' + currentFile.filename + '</div>\n' +
        '<div class="mdui-textfield">\n' +
        '            <div class="mdui-textfield-title" style="overflow-y: scroll;">' +
        currentFile.data + '</div>\n' +
        '        </div>\n' +
        '        <button id="1" onclick="inst.close()" class="mdui-btn mdui-color-theme-accent edit-button">关闭</button>\n';
    $("#edit").html(editFile);
    inst.open();
}

function showDir() {
    if (currentDir == null || currentDir.files == null) {
        return;
    }
    var files = currentDir.files;

    var table = '          <thead>\n' +
        '            <tr>\n' +
        '                <th>文件名</th>\n' +
        '                <th>类型</th>\n' +
        '                <th>大小</th>\n' +
        '                <th>所有者</th>\n' +
        '                <th>创建时间</th>\n' +
        '                <th>更新时间</th>\n' +
        '                <th>操作1</th>\n' +
        '                <th>操作2</th>\n' +
        '                <th>操作3</th>\n' +
        '            </tr>\n' +
        '            </thead>'

    for (let i = 0; i < files.length; i++) {
        var file = files[i];
        var tbody = '   <tbody>\n' +
            '            <tr>\n' +
            '                <td id="filename">' + file.filename + '</td>\n' +
            '                <td id="type">' + file.type + '</td>\n' +
            '                <td id="length">' + file.length + 'KB</td>\n' +
            '                <td id="owner">' + file.owner + '</td>\n' +
            '                <td id="create_time">' + file.createTime + '</td>\n' +
            '                <td id="update_time">' + file.updateTime + '</td>\n' +
            '                <td id="op1">' +
            ' <button class="mdui-btn mdui-btn-raised mdui-ripple mdui-color-green " ' +
            'onclick="openReq(' + file.direntId + ',openFile)">\n' +
            '            打开' + '</button>' +
            '</td>\n';
        var noedit = '                <td id="op2">' +
            ' <button class="mdui-btn mdui-btn-raised mdui-ripple mdui-color-blue " ' +
            ' disabled>\n' +
            '            编辑' + '</button>' +
            '</td>\n' +
            '                <td id="op3">' +
            ' <button class="mdui-btn mdui-btn-raised mdui-ripple mdui-color-red " ' +
            ' disabled>\n' +
            '            删除' + '</button>' +
            '</td>\n';
        var edit = '                <td id="op2">' +
            ' <button class="mdui-btn mdui-btn-raised mdui-ripple mdui-color-blue " ' +
            'onclick="edit(' + file.direntId + ')">\n' +
            '            编辑' + '</button>' +
            '</td>\n' +
            '                <td id="op3">' +
            ' <button class="mdui-btn mdui-btn-raised mdui-ripple mdui-color-red " ' +
            'onclick="deleteReq(' + file.direntId + ')">\n' +
            '            删除' + '</button>' +
            '</td>\n';
        var end = '            </tr>\n' +
            '            </tbody>'
        if (file.filename === '..') {
            table = table + tbody + noedit + +end;
        } else {
            table = table + tbody + edit + end;
        }


    }

    $("#fileTable").html(table);
}

function closeFile() {
    var settings = {
        "url": baseUrl + "/core/" + currentFile.direntId,
        "method": "PUT",
        "timeout": 0,
        "headers": {
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": auth
        }
    };

    $.ajax(settings).done(function (response) {
            console.log(response.code)
            if (response.code !== '1') {
                mdui.alert(response.message, '系统提示');
            }
        }
    );
}

function createFile() {
    create("txt");
}

function createDir() {
    create("dir");
}

function create(type) {
    // 单行文本框
    mdui.prompt('请输入文件/文件夹名称', '新建',
        function (value) {
            createReq(type, value);
        },
        function (value) {
            console.log("cancel")
        }
    );
}

function createReq(type, filename) {

    $("#progress").attr("style", "float:right;visibility:visible")

    var file = {
        "filename": filename,
        "type": type,
        "ownerId": userId,
        "parent": currentDir.direntId
    };

    var settings = {
        "url": baseUrl + "/core",
        "method": "POST",
        "timeout": 0,
        "data": JSON.stringify(file),
        "headers": {
            "Content-Type": "application/json",
            "Authorization": auth
        }
    };

    $.ajax(settings).done(function (response) {
            console.log(response.code)
            if (response.code !== '1') {
                mdui.alert(response.message, '系统提示');
            } else {
                openReq(currentDir.direntId, openFile);
            }
        }
    );
}

function updateReq() {
    $("#progress").attr("style", "float:right;visibility:visible")

    var settings = {
        "url": baseUrl + "/core",
        "method": "PUT",
        "timeout": 0,
        "data": JSON.stringify(currentFile),
        "headers": {
            "Content-Type": "application/json",
            "Authorization": auth
        }
    };

    $.ajax(settings).done(function (response) {
            console.log(response.code)
            if (response.code !== '1') {
                mdui.alert(response.message, '系统提示');
            } else {
                inst.close();
                openReq(currentDir.direntId, openFile);
            }
        }
    );
}

function updateFile() {
    currentFile.filename = $("#edit-filename").val();
    currentFile.data = $("#edit-data").val();
    updateReq();
}

function updateDir(filename) {
    currentFile.filename = filename;
    updateReq();
}

function goBack() {
    openReq(currentDir.parent, openFile);
}