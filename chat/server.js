var db = require("./database");
var app = require("express")();
var http = require("http").Server(app);
var io = require("socket.io")(http,{ origins: '*:*'});
var moment = require('moment');
var base64Img = require('base64-img');
const fs = require('fs');
var hat = require("hat").rack();
const ORIGIN_SERVER = 0;
const ADMIN_ID = 1;
var active_users_id = new Map();
var active_users_socket_id = new Map();
let IMAGEPATH=__dirname+'/images/';
 let URL='http://192.168.25.100:3000'
//let URL='http://message.thebrosltd.tk/'

server_datetime =function()
{
  // var today = new Date();
  // var server_date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();
  // var server_time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
  // return server_dateTime = server_date+' '+server_time;
  return moment().format('YYYY-MM-DD HH:mm:ss');
}
app.get("/images/:file", function(req, res) {

  fs.access(__dirname + "/images/"+req.params.file, fs.F_OK, (err) => {
    if (err) {
      console.error(err)
      return res.status(404).json({ error: 'Unauthorized access' });
    }
    res.sendFile(__dirname + "/images/"+req.params.file);
  });
  
});
const admin = io.of("/admin").on("connection", function(socket) {
  console.log(`An Admin connected id-> ${socket.id}`);
  admin.emit("log",JSON.stringify(`An Admin connected id-> ${socket.id}`));
  socket.on("message", function(val,to) {
    var data = JSON.parse(val);
    data.time=server_datetime();
    admin.emit("admin",JSON.stringify(data),to);
    db.addChat(
      function(id) {
          if(active_users_id.has(to))
            {//console.log(active_users_id.get(to));
              android.to(active_users_id.get(to)).emit("admin",JSON.stringify(data));
            }
      },
      [ADMIN_ID, to, data.message, data.msg_type, data.origin,data.time]
    );
    
  });
  socket.on("update",function(){
    android.emit("update");
  })
  socket.on("delete",function(msg){
    var id=JSON.parse(msg).data
    console.log("deleting user "+id);
    db.deleteuser(id,function(){
      if(active_users_id.has(id))
      {
        var soc=active_users_id.get(id);
        android.to(soc).emit("delete");
        android.connected[soc].disconnect();
      }
      admin.emit("refesh",JSON.stringify({text:"User deleted",type:"success",title:"Chat Server",song:null}));
    })
  });
  socket.on("reset_user_pending",function(id)
  {
    
    db.reset_pending(id,function(){
      admin.emit("reset_pending",id);
    })
  })

  generateFilename = function(ext,val)
  {
    let name=hat()+'.'+ext;
    fs.access(IMAGEPATH+name, fs.F_OK, (err) => {
      if (err) {
        val(name);
        return;
      }
      val("fallback_"+name)
      // val(generateFilename(ext));
    });
  }
  socket.on("imagemsg", function(val, to) {
    var data = JSON.parse(val);
    data.time=server_datetime();
    // console.log(data);
    if(data.message.includes('image/')){
    let mime=data.message.substring("data:image/".length, data.message.indexOf(";base64"));
      if(mime.includes('+'))
      {
        mime=mime.substring(0,mime.indexOf('+'));
      }
    let base64Image = data.message.split(';base64,').pop();
    generateFilename(mime,function(name)
    {
      fs.writeFile(IMAGEPATH+name, base64Image, {encoding: 'base64'}, function(err) {
        data.message=URL+"/images/"+name;
        db.addChat(
          function(id) {
            //console.log(data);
            data.id=id;
            data.pending=0;
            admin.emit("admin", JSON.stringify(data),to);
            console.log(data);
            if(active_users_id.has(to))
            {//console.log(active_users_id.get(to));
              android.to(active_users_id.get(to)).emit("admin",JSON.stringify(data));
            }
            
          },
          [ADMIN_ID, to, data.message, data.msg_type, data.origin,data.time]
        );
      });
    });
    
    }else{
      Console.log("sorry not an image");
    }
    });
  
});
const android = io.of("/android").on("connection", function(socket) {
  console.log("an android user connected  id-> " + socket.id);
  admin.emit("log",JSON.stringify(`An android user connected id-> ${socket.id}`));
  var messenger_id=active_users_socket_id.get(socket.id);
  socket.on("message", function(val, status) {
    var data = JSON.parse(val);
    data.time=server_datetime();
    // console.log(data);
    db.addChat(
      function(id) {
        //console.log(data);
        data.id=id;
        data.pending=0;
        admin.emit("admin", JSON.stringify(data),messenger_id);
        //console.log(data);
        status(JSON.stringify(data));
        db.pending_increase(messenger_id,function(){
          // admin.emit("increase",messenger_id);
        })
      },
      [messenger_id, ADMIN_ID, data.message, data.msg_type, data.origin,data.time]
    );
  });
  socket.on("imagemsg", function(val, status) {
    var data = JSON.parse(val);
    data.time=server_datetime();
    let base64Image = data.message.split(';base64,').pop();
    let name=hat()+'.jpeg';
    fs.writeFile(IMAGEPATH+name, base64Image, {encoding: 'base64'}, function(err) {
      data.message=URL+"/images/"+name;
      db.addChat(
        function(id) {
          //console.log(data);
          data.id=id;
          data.pending=0;
          admin.emit("admin", JSON.stringify(data),messenger_id);
          console.log(data);
          status(JSON.stringify(data));
          
        },
        [messenger_id, ADMIN_ID, data.message, data.msg_type, data.origin,data.time]
      );
    });
  });
  socket.on("timechat",function(msg,and_callback){
    let data=JSON.parse(msg);
    //console.log(data);
    db.getTimeChat(active_users_socket_id.get(socket.id),data,function(value)
    {
      //console.log(moment(value.time).format('YYYY-MM-DD HH:mm:ss'));
      value.forEach(element => {
        element.time=moment(element.time).format('YYYY-MM-DD HH:mm:ss');
      });
       //console.log(JSON.stringify({"dataPackets": value}));
      and_callback(JSON.stringify({"dataPackets": value}));
    })
  });
  socket.on("disconnect", function() {
    console.log("an android user disconnected  id-> " + socket.id);
  admin.emit("log",JSON.stringify(`An android user disconnected id-> ${socket.id}`));
    active_users_id.delete(active_users_socket_id.get(socket.id));
    active_users_socket_id.delete(socket.id);

  });
});
android.use(function(socket, next) {
  console.log("new user on android");
  let token = socket.handshake.query.token;
  if (token == undefined) {
    console.log("Token not found");
    return next(new Error("undefined_token"));
  }
  db.isValid(token, function(id, success) {
    if (success) {
      console.log(`Identified as: ${id}`);
      active_users_id.set(id, socket.id);
      active_users_socket_id.set(socket.id,id);
      return next();
    } else {
      console.log("Auth error");
      return next(new Error("auth_error"));
    }
  });
});
const register = io.on("connection", function(socket) {
  console.log("New client for register")
  admin.emit("log",JSON.stringify(`New client for register`));

  socket.on("register", function(callback) {
    db.register(function(id, token) {
      //console.log(id,token);
      admin.emit("log",JSON.stringify(`New client registered: ${id} -> ${token}`));
      admin.emit("refesh",JSON.stringify({text:"A new User just registered!!",type:"success",title:"Chat Server",song:"newuser.mp3"}));
      callback(token,server_datetime());
    });
  });
  socket.on("disconnect",function(socket){
    console.log("A user disconnect");
  });
});
http.listen(3000, function() {
  console.log("listening on 80");
});
