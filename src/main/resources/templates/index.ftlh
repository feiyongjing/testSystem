<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width-device-width, initial-scale=1.0">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>考试系统</title>
</head>
<body>
<h1 align="center">考试系统</h1>
<div align="center">
    <a href=${item} target="_self">${item1}</a>
</div>
<form >
    <input type="i"/>
</form>
<div id="main-text" align="center">
    <div>
        <label>电话号码</label>
        <input id="username" type="text" name="username" required>
    </div>
    <div>
        <label>密码</label>
        <input id="password" type="password" name="password" required>
    </div>
    <div>
        <button type="button" onclick="myFunction()">${item2}</button>
    </div>
</div>
</body>
<script>
    function myFunction() {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function () {
            if (request.readyState === 4 && request.status === 200) {
                console.log('request', request)
                document.getElementById("main-text").innerHTML = request.responseText;
            } else {
            }
        }
        <#--request.open("POST",${item3} ,true);-->

        request.open("POST", '/api/v1/login', true);
        request.setRequestHeader("Content-Type", "application/json");
        var username = document.getElementById("username").value;
        var password = document.getElementById("password").value;
        var params = {
            "username": username,
            "password": password
        }
        request.send(JSON.stringify(params));
    }
</script>
</html>